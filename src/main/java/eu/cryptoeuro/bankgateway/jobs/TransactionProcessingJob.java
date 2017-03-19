package eu.cryptoeuro.bankgateway.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Scheduled(cron = "0 */5 * * * *")
    public void processTransactions() {
        log.info("Starting to process transactions");

        List<Transaction> transactions = transactionService.findUnprocessedTransactions();
        for (Transaction transaction : transactions) {
            log.info("Transaction: " + transaction.getId() + ", " + transaction.getBookingDate() + ", " + transaction.getDebtorName() + ", "
                    + transaction.getAmountSigned() + ", " + transaction.getProcessingStatus());

            if (Transaction.ProcessingStatus.NEW.equals(transaction.getProcessingStatus())) {
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
                msg.setText("LHV reserve account has a new transaction.");
                msg.setAttachments(new Attachment[] {attachment});
                slackService.sendReserveMessage(msg);

                transaction.setProcessingStatus(Transaction.ProcessingStatus.NOTIFIED);
            }

            if (Transaction.ProcessingStatus.NOTIFIED.equals(transaction.getProcessingStatus())) {
                // TODO talk to our Ethereum contract and give the money to this isikukood (transaction.getDebtorId(), can be null for "transactions outside estonian personal accounts")
            }

            // update status in DB
            transactionService.updateProcessingStatus(transaction.getId(), transaction.getProcessingStatus());
        }

        log.info("Completed processing of transactions");
    }

}
