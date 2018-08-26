package eu.cryptoeuro.bankgateway.rest;

import eu.cryptoeuro.bankgateway.services.AccountBalanceService;
import eu.cryptoeuro.bankgateway.services.balance.model.Balance;
import eu.cryptoeuro.bankgateway.services.transaction.PayoutInstructionService;
import eu.cryptoeuro.bankgateway.services.transaction.model.PayoutInstruction;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/v1/payout")
@CrossOrigin(origins = "*")
@Slf4j
public class PayoutController {
    @Autowired
    private PayoutInstructionService payoutInstructionService;

    @ApiOperation(value = "Post payout instructions")
    @RequestMapping(path = "/initiate",
            method = POST)
    public ResponseEntity<TotalReserveResponse> postPayout(
            @RequestParam @NotNull String txHash,
            @RequestParam @NotNull String iban) {
        payoutInstructionService.initiatePayout(txHash, iban);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
