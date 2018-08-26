package eu.cryptoeuro.bankgateway.services.transaction;

import eu.cryptoeuro.bankgateway.services.transaction.model.PayoutInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayoutInstructionService {
    @Autowired
    private PayoutInstructionDao payoutInstructionDao;

    public void insertPayoutInstruction(PayoutInstruction payoutInstruction) {
        payoutInstructionDao.insert(payoutInstruction);
    }

    public void initiatePayout(String txHash, String iban) {
        payoutInstructionDao.insert(txHash, iban);
    }
}
