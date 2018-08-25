package eu.cryptoeuro.transferInfo.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class TransferInfoRecord {
    @NotNull
    @Size(min = 1, max = 256)
    String senderIdCode;

    @NotNull
    @Size(min = 1, max = 256)
    String receiverIdCode;

    String referenceText;
}
