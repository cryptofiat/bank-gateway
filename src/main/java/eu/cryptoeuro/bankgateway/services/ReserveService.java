package eu.cryptoeuro.bankgateway.services;

import eu.cryptoeuro.accountIdentity.response.Account;
import eu.cryptoeuro.bankgateway.KeyUtil;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;
import eu.cryptoeuro.transferInfo.command.TransferInfoRecord;
import eu.cryptoeuro.transferInfo.service.TransferInfoService;
import eu.cryptoeuro.wallet.client.CreateTransferCommand;
import eu.cryptoeuro.wallet.client.FeeConstant;
import eu.cryptoeuro.wallet.client.IncreaseSupplyCommand;
import eu.cryptoeuro.wallet.client.WalletClientService;
import eu.cryptoeuro.wallet.client.response.ContractInfo;
import eu.cryptoeuro.wallet.client.response.SupplyIncrease;
import eu.cryptoeuro.wallet.client.response.Transfer;
import eu.cryptoeuro.wallet.client.service.WalletServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ReserveService implements InitializingBean {

    @Autowired
    private WalletClientService walletClientService;
    @Autowired
    private AccountIdentityService accountIdentityService;
    @Autowired
    private WalletServerService walletServerService;
    @Autowired
    private TransferInfoService transferInfoService;
    @Autowired
    private KeyUtil keyUtil;
    @Value("${reserveBank.ethereum.address}")
    private String reserveBankAccountAddress;

    private ContractInfo contractInfo;

    @Override
    public void afterPropertiesSet() {
        contractInfo = walletServerService.getContractInfo();
    }

    public void increaseSupplyAndCreditRecipient(Transaction transaction) {
        eu.cryptoeuro.wallet.client.response.Account reserveBankAccount = walletServerService.getAccount(reserveBankAccountAddress);
        Long longAmountInCents = transaction.getAmount().multiply(new BigDecimal(100)).longValue();
        IncreaseSupplyCommand increaseSupplyCommand = walletClientService.createAndSignIncreaseSupplyCommand(reserveBankAccount.getNonce(), longAmountInCents, keyUtil.getReserveKey());

        SupplyIncrease supplyIncrease = walletServerService.increaseSupply(increaseSupplyCommand);
        log.info("Increased supply: " + supplyIncrease);

//        Account account = accountIdentityService.getAddress(transaction.getDebtorId());
//        CreateTransferCommand createTransferCommand = walletClientService.createAndSignCreateTransferCommand(contractInfo.reserveBank, reserveBankAccount.getNonce(), account.getAddress(), Long.MIN_VALUE, keyUtil.getReserveKey(), FeeConstant.FEE);
//
//        Transfer transfer = walletServerService.transfer(createTransferCommand);
//        result.setTransferId(transfer.getId());
//
//        TransferInfoRecord transferInfoRecord = new TransferInfoRecord();
//        transferInfoRecord.setSenderIdCode(transaction.getDebtorId());
//        transferInfoRecord.setReceiverIdCode(transaction.getDebtorId());
//        transferInfoRecord.setReferenceText("Top-up from " + transaction.getDebtorAccountIban());
//
//        transferInfoService.send(without0x(transfer.getId()), transferInfoRecord);
    }

    protected String without0x(String hex) {
        return hex.startsWith("0x") ? hex.substring(2) : hex;
    }
}
