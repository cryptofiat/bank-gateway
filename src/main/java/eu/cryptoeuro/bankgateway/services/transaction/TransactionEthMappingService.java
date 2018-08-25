package eu.cryptoeuro.bankgateway.services.transaction;

import eu.cryptoeuro.bankgateway.services.transaction.model.TransactionEthMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class TransactionEthMappingService {
    @Autowired
    private TransactionEthMappingDao transactionEthMappingDao;

    public void setSupplyIncreaseTxHash(long transactionId, String txHash) {
        TransactionEthMapping mapping = findOrCreateTransactionEthMapping(transactionId);
        mapping.setSupplyIncreaseTxHash(txHash);
        transactionEthMappingDao.insertOrUpdate(mapping);
    }

    public void setAccountCreditTxHash(long transactionId, String txHash) {
        TransactionEthMapping mapping = findOrCreateTransactionEthMapping(transactionId);
        mapping.setAccountCreditTxHash(txHash);
        transactionEthMappingDao.insertOrUpdate(mapping);
    }

    private TransactionEthMapping findOrCreateTransactionEthMapping(long transactionId) {
        TransactionEthMapping transactionEthMapping = null;
        try {
            transactionEthMapping = transactionEthMappingDao.findByTransactionId(transactionId);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() > 1) {
                throw e;
            } else {
                transactionEthMapping = new TransactionEthMapping();
                transactionEthMapping.setTransactionId(transactionId);
            }
        }
        return transactionEthMapping;
    }

}
