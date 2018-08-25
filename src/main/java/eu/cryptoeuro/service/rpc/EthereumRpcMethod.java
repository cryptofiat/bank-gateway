package eu.cryptoeuro.service.rpc;

import java.io.Serializable;

public enum EthereumRpcMethod implements Serializable {

    gasPrice("eth_gasPrice"),
    getBalance("eth_getBalance"),
    getTransactionCount("eth_getTransactionCount"),
    sendTransaction("eth_sendTransaction"),
    sendRawTransaction("eth_sendRawTransaction"),
    call("eth_call"),
    getBlockByHash("eth_getBlockByHash"),
    getTransactionByHash("eth_getTransactionByHash"),
    blockNumber("eth_blockNumber"),
    getLogs("eth_getLogs"),
    nextNonce("parity_nextNonce");

    private final String method;

    EthereumRpcMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return this.method;
    }

}
