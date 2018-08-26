package eu.cryptoeuro.bankgateway.jobs;

import eu.cryptoeuro.accountIdentity.response.Account;
import eu.cryptoeuro.bankgateway.services.AccountIdentityService;
import eu.cryptoeuro.bankgateway.services.ReserveService;
import eu.cryptoeuro.bankgateway.services.slack.SlackService;
import eu.cryptoeuro.bankgateway.services.slack.json.Attachment;
import eu.cryptoeuro.bankgateway.services.slack.json.Field;
import eu.cryptoeuro.bankgateway.services.slack.json.Message;
import eu.cryptoeuro.bankgateway.services.transaction.TransactionService;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;
import eu.cryptoeuro.service.HashUtils;
import eu.cryptoeuro.transferInfo.command.TransferInfoRecord;
import eu.cryptoeuro.transferInfo.service.TransferInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TransactionProcessingJob {

    @Autowired
    private SlackService slackService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ReserveService reserveService;
    @Autowired
    private AccountIdentityService accountIdentityService;
    @Autowired
    private TransferInfoService transferInfoService;

    @Scheduled(cron = "0 */5 * * * *")
    public void processInboundTransactions() {
        log.info("Starting to process inbound transactions");
        List<Transaction> transactions = transactionService.findUnprocessedInboundTransactions();
        log.info("Processing " + transactions.size() + " inbound transactions");

        for (Transaction transaction : transactions) {
            processInboundTransaction(transaction);
        }

        log.info("Completed processing of transactions");
    }

    private void processInboundTransaction(Transaction transaction) {
        log.info("Transaction: " + transaction.getId() + ", " + transaction.getBookingDate() + ", " + transaction.getDebtorName() + ", "
                + transaction.getAmountSigned() + ", " + transaction.getProcessingStatus());

        if (Transaction.ProcessingStatus.NEW.equals(transaction.getProcessingStatus())) {
            sendSlackNotification(transaction, "LHV reserve account has a new transaction.");
            transaction.setProcessingStatus(Transaction.ProcessingStatus.NOTIFIED);
        }
        if (Transaction.ProcessingStatus.NOTIFIED.equals(transaction.getProcessingStatus())) {
            try {
                String txHash = reserveService.increaseSupply(transaction);
                sendSlackNotification(transaction, "Added to total reserve.");
                transaction.setProcessingStatus(Transaction.ProcessingStatus.SUPPLY_INCREASED);
            } catch (Exception e) {
                log.error("Error processing transaction " + transaction, e);
            }
        }
        if (Transaction.ProcessingStatus.SUPPLY_INCREASED.equals(transaction.getProcessingStatus())) {
            Account recipientAccount = accountIdentityService.getAddress(transaction.getDebtorId());
            try {
                String txHash = reserveService.creditAccount(transaction, recipientAccount);
                sendSlackNotification(transaction, "Transferred from reserve to account.");
                transaction.setProcessingStatus(Transaction.ProcessingStatus.USER_CREDITED);
                transferInfoService.send(HashUtils.without0x(txHash), new TransferInfoRecord(transaction.getDebtorId(), transaction.getDebtorId(), transaction.getDebtorAccountIban() + " " + transaction.getRemittanceInformation()));
            } catch (Exception e) {
                log.error("Error processing transaction " + transaction, e);
            }

        }
        // update status in DB
        transactionService.updateProcessingStatus(transaction.getId(), transaction.getProcessingStatus());
    }


    private void sendSlackNotification(Transaction transaction, String text) {
        try {
            // send a Slack notification
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            List<Field> fields = new ArrayList<>();
            fields.add(new Field("Tx id", transaction.getId().toString(), true));
            fields.add(new Field("Tx date", format.format(transaction.getBookingDate()), true));
            fields.add(new Field("Debtor name", StringUtils.defaultIfBlank(transaction.getUltimateDebtorName(), transaction.getDebtorName()), true));
            fields.add(new Field("Amount", String.valueOf(transaction.getAmountSigned()) + " " + transaction.getCurrency(), true));
            fields.add(new Field("Description", transaction.getRemittanceInformation(), false));
            Attachment attachment = new Attachment();
            attachment.setColor("#00f4a3");
            attachment.setFields(fields.toArray(new Field[fields.size()]));
            Message msg = new Message();
            msg.setText(text);
            msg.setAttachments(new Attachment[]{attachment});
            slackService.sendReserveMessage(msg);
        } catch (Exception e) {
            log.error("Couldn't send Slack update", e);
        }
    }

}
