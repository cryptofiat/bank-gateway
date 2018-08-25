package eu.cryptoeuro.bankgateway.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import eu.cryptoeuro.accountIdentity.response.Account;
import eu.cryptoeuro.bankgateway.KeyUtil;
import eu.cryptoeuro.bankgateway.services.AccountIdentityService;
import eu.cryptoeuro.bankgateway.services.ReserveService;
import eu.cryptoeuro.transferInfo.command.TransferInfoRecord;
import eu.cryptoeuro.transferInfo.service.TransferInfoService;
import eu.cryptoeuro.wallet.client.CreateTransferCommand;
import eu.cryptoeuro.wallet.client.FeeConstant;
import eu.cryptoeuro.wallet.client.WalletClientService;
import eu.cryptoeuro.wallet.client.response.ContractInfo;
import eu.cryptoeuro.wallet.client.response.Transfer;
import eu.cryptoeuro.wallet.client.service.WalletServerService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.bankgateway.services.slack.SlackService;
import eu.cryptoeuro.bankgateway.services.slack.json.Attachment;
import eu.cryptoeuro.bankgateway.services.slack.json.Field;
import eu.cryptoeuro.bankgateway.services.slack.json.Message;
import eu.cryptoeuro.bankgateway.services.transaction.TransactionService;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;

@Component
@Slf4j
public class TransactionProcessingJob {

    @Autowired
    private SlackService slackService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ReserveService reserveService;

    @Scheduled(cron = "0 */5 * * * *")
    public void processTransactions() {
        log.info("Starting to process transactions");

        List<Transaction> transactions = transactionService.findUnprocessedTransactions();
        for (Transaction transaction : transactions) {
            log.info("Transaction: " + transaction.getId() + ", " + transaction.getBookingDate() + ", " + transaction.getDebtorName() + ", "
                    + transaction.getAmountSigned() + ", " + transaction.getProcessingStatus());

            if (Transaction.ProcessingStatus.NEW.equals(transaction.getProcessingStatus())) {
                sendSlackNotification(transaction, "LHV reserve account has a new transaction.");
                transaction.setProcessingStatus(Transaction.ProcessingStatus.NOTIFIED);
            }

            if (Transaction.ProcessingStatus.NOTIFIED.equals(transaction.getProcessingStatus())) {
                reserveService.increaseSupplyAndCreditRecipient(transaction);

                sendSlackNotification(transaction, "Added to total reserve.");
                sendSlackNotification(transaction, "Transferred from reserve to account.");
            }

            // update status in DB
            transactionService.updateProcessingStatus(transaction.getId(), transaction.getProcessingStatus());
        }

        log.info("Completed processing of transactions");
    }



    private void sendSlackNotification(Transaction transaction, String text) {
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
        msg.setAttachments(new Attachment[] {attachment});
        slackService.sendReserveMessage(msg);
    }

}
