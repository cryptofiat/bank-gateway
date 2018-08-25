package eu.cryptoeuro.service.rpc;

import lombok.Data;

import java.util.List;

@Data
public class JsonRpcTransactionLogEntry {

    List<String> topics;

    String data;

    String blockHash;
    String transactionHash;
}
