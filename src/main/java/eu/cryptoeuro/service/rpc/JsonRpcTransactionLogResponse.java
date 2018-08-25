package eu.cryptoeuro.service.rpc;

import lombok.Data;

import java.util.List;

@Data
public class JsonRpcTransactionLogResponse {

    private List<JsonRpcTransactionLogEntry> result;

    public List<JsonRpcTransactionLogEntry> getResult() {
        return result;
    }
}
