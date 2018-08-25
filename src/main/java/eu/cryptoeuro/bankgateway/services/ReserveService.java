package eu.cryptoeuro.bankgateway.services;

import eu.cryptoeuro.bankgateway.KeyUtil;
import eu.cryptoeuro.bankgateway.services.transaction.TransactionEthMappingService;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;
import eu.cryptoeuro.contract.Contracts;
import eu.cryptoeuro.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ReserveService extends BaseService implements InitializingBean {
    public static final int GAS_LIMIT = 350000;
    public static final int GAS_PRICE = 3;

    @Autowired
    private KeyUtil keyUtil;
    @Autowired
    private TransactionEthMappingService ethMappingService;

    @Value("${reserveBank.ethereum.address}")
    private String reserveBankAccountAddress;
    @Value("${ethereum.node.url}")
    private String ethereumNodeUrl;


    private Contracts contracts;

    @Override
    public void afterPropertiesSet() throws Exception {
        Web3jService w3j = new HttpService(ethereumNodeUrl);
        Web3j web3j = Web3j.build(w3j);

        try {
            System.out.println(web3j.web3ClientVersion().send().getWeb3ClientVersion());
            Contracts.GasLimit = BigInteger.valueOf(GAS_LIMIT);
            Contracts.GasPrice = Convert.toWei(BigDecimal.valueOf(GAS_PRICE), Convert.Unit.GWEI).toBigInteger();
            contracts = Contracts.load(web3j, Credentials.create(keyUtil.getReserveKeyInHex()));

        } catch (Exception e) {
            log.error("Error bootstrapping Ethereum integration", e);
            throw e;
        }
    }

    public String increaseSupply(final Transaction transaction) throws Exception {
        BigInteger amountInCents = transaction.getAmount().multiply(new BigDecimal(100)).toBigInteger();

        contracts.transactionManager.onceBeforeWait = (EthSendTransaction tx) -> {
            log.info("Increasing supply for bank transaction " + transaction.getId() + ", txHash=" + tx.getTransactionHash());
            ethMappingService.setSupplyIncreaseTxHash(transaction.getId(), tx.getTransactionHash());
        };
        TransactionReceipt receipt = contracts.reserve.increaseSupply(amountInCents).send();
        return receipt.getTransactionHash();

    }
}
