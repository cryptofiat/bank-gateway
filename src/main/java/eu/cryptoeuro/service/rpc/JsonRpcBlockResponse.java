package eu.cryptoeuro.service.rpc;

import lombok.Data;

import java.util.Date;

@Data
public class JsonRpcBlockResponse {

    public JsonRpcBlockEntity result;

    public Date getTimestamp() {
        return result.getTimestamp();
    }
}
