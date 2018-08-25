package eu.cryptoeuro.bankgateway.services;

import eu.cryptoeuro.bankgateway.KeyUtil;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;
import eu.cryptoeuro.service.BaseService;
import eu.cryptoeuro.service.GasPriceService;
import eu.cryptoeuro.service.HashUtils;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;
import eu.cryptoeuro.transferInfo.service.TransferInfoService;
import eu.cryptoeuro.wallet.client.WalletClientService;
import eu.cryptoeuro.wallet.client.response.ContractInfo;
import eu.cryptoeuro.wallet.client.response.SupplyIncrease;
import eu.cryptoeuro.wallet.client.service.WalletServerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ethereum.core.CallTransaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ReserveService extends BaseService implements InitializingBean {
    public static final int GAS_LIMIT = 350000;

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
    @Autowired
    private GasPriceService gasPriceService;
    @Value("${reserveBank.ethereum.address}")
    private String reserveBankAccountAddress;

    private ContractInfo contractInfo;

    private static CallTransaction.Function increaseSupplyFunction = CallTransaction.Function.fromSignature("increaseSupply", "uint256");

    @Override
    public void afterPropertiesSet() {
        contractInfo = walletServerService.getContractInfo();
    }

    public SupplyIncrease increaseSupply(Transaction transaction) throws Exception {
        Long longAmountInCents = transaction.getAmount().multiply(new BigDecimal(100)).longValue();
        ECKey reserveKey = keyUtil.getReserveKey();
        byte[] callData = increaseSupplyFunction.encode(longAmountInCents);

        String txHash = sendRawTransaction(reserveKey, callData);
        if (StringUtils.isBlank(txHash)) {
            throw new Exception("Transaction hash empty, probable nonce issue");
        }

        SupplyIncrease result = new SupplyIncrease();
        result.setId(txHash);
        result.setAmount(transaction.getAmount().longValue());
        log.info("Increased supply: " + result);
        ;
        return result;
    }

    private String sendRawTransaction(ECKey signer, byte[] callData) {
        long nextNonce = getNextNonce(HashUtils.with0x(reserveBankAccountAddress));
        byte[] nonce = ByteUtil.longToBytesNoLeadZeroes(nextNonce);
        long gasPriceWei = gasPriceService.getGasPriceInWei();
        byte[] gasPrice = ByteUtil.longToBytesNoLeadZeroes(gasPriceWei);
        byte[] gasLimit = ByteUtil.longToBytesNoLeadZeroes(GAS_LIMIT);
        byte[] toAddress = Hex.decode(HashUtils.without0x(contractInfo.reserveBank));

        org.ethereum.core.Transaction transaction = new org.ethereum.core.Transaction(nonce, gasPrice, gasLimit, toAddress, null, callData);
        transaction.sign(signer);
        String params = HashUtils.hex(transaction.getEncoded());

        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.sendRawTransaction, Arrays.asList(params));
        log.info("JSON:\n" + call.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);
        String txHash = response.getResult();
        log.info("Received transaction response: " + txHash);

        return txHash;
    }

    private long getNextNonce(String account) {
        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.nextNonce, Arrays.asList(account));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);
        String result = response.getResult();
        long responseToLong = Long.parseLong(HashUtils.without0x(result), 16);
        log.info("Next nonce for " + account + ": " + responseToLong);

        return responseToLong;
    }
}
