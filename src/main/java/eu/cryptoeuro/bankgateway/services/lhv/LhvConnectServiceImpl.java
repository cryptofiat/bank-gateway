package eu.cryptoeuro.bankgateway.services.lhv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document;
import eu.cryptoeuro.bankgateway.services.common.AccountStatementRequestUtil;
import eu.cryptoeuro.bankgateway.services.lhv.model.LhvMessage;
import eu.cryptoeuro.bankgateway.services.transaction.TransactionService;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;

@Slf4j
@Service
@Transactional
public class LhvConnectServiceImpl implements LhvConnectService {

    @Value("${lhv.reserve.iban}")
    private String lhvReserveIban;
    @Autowired
    private LhvConnectApi lhvConnectApi;
    @Autowired
    private TransactionService transactionService;

    @Override
    public boolean checkLhvHeartbeat() {
        return lhvConnectApi.isAvailable() && !lhvConnectApi.getHeartbeat().hasError();
    }

    @Override
    public void postAccountStatementRequest() {
        Calendar calendar = Calendar.getInstance();
        Date toDate = calendar.getTime();
        calendar.add(Calendar.DATE, -7); // TODO hard-coded period "last 7 days", start using latest balance.synced_to_date or if null, 2017-03-18 (account creation time)
        Date fromDate = calendar.getTime();

        postAccountStatementPeriod(lhvReserveIban, fromDate, toDate);
    }

    @Override
    public void processLhvMessages() {
        log.debug("Starting to process LHV message queue.");
        int messageCount = 0;

        List<String> errors = new ArrayList<>();
        try {
            Optional<LhvMessage<?>> nextMessage = lhvConnectApi.getNextMessage();

            while (nextMessage.isPresent()) {
                messageCount++;
                @SuppressWarnings("unchecked")
                LhvMessage<Document> message = (LhvMessage<Document>)nextMessage.get();
                // Check if it is account statement response
                if (!message.isAccountStatementEntity()) {
                    if (message.hasError()) {
                        errors.add(String.format("LHV Connect message (Request-Id: %s; Response-Id: %s) has errors! Status code: %s, errors: %s",
                                message.getMessageRequestId(), message.getMessageResponseId(), message.getStatusCode(), message.getErrors()));
                    }
                    // If not, delete, since it is not supported
                    markResponseAsRead(message);
                    nextMessage = lhvConnectApi.getNextMessage();
                    continue;
                }

                // Otherwise extract transactions
                transactionService.importTransactions(message.getEntity(), Transaction.Source.LHV_CONNECT);
                markResponseAsRead(message);
                nextMessage = lhvConnectApi.getNextMessage();
            }
        } finally {
            if (CollectionUtils.isNotEmpty(errors)) {
                log.error("processLhvMessages() errors:\n" + StringUtils.join(errors, ";\n"));
            }
            log.info("Finished processing LHV message queue. {} messages received.", messageCount);
        }
    }

    ///// PRIVATE METHODS /////

    private void postAccountStatementPeriod(String iban, Date fromDate, Date toDate) {
        LhvMessage<?> lhvMessage = requestAccountStatement(iban, fromDate, toDate);
        String requestId = lhvMessage != null ? lhvMessage.getMessageRequestId() : null;
        if (StringUtils.isBlank(requestId) || lhvMessage.hasError()) {
            log.error("Account statement request completed with error, IBAN: " + iban);
        }
    }

    private LhvMessage<?> requestAccountStatement(String iban, Date fromDate, Date toDate) {
        log.debug("Requesting statement [IBAN: {}]", iban);
        JAXBElement<eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.Document> requestDocument =
                AccountStatementRequestUtil.createRequestDocument(iban, fromDate, toDate);
        LhvMessage<?> lhvMessage = lhvConnectApi.postForMessageRequestId(requestDocument);
        String requestId = lhvMessage != null ? lhvMessage.getMessageRequestId() : null;
        log.debug("Statement [IBAN: {}] Message-Request-Id is [{}]", iban, requestId);
        return lhvMessage;
    }

    private LhvMessage<?> markResponseAsRead(LhvMessage<?> message) {
        LhvMessage<?> response = lhvConnectApi.deleteMessage(message);
        if (response.hasError()) {
            String msg = "Error deleting message! Request-Id: " + message.getMessageRequestId() + ", Response-Id: " + message.getMessageResponseId();
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return response;
    }

}
