package eu.cryptoeuro.bankgateway.rest;

import eu.cryptoeuro.bankgateway.services.transaction.TransactionService;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

@Controller
@Slf4j
public class StatementController {
	@Autowired
	TransactionService transactionService;

	@GetMapping("/statement")
	public String uploadForm() {
		return "upload";
	}

	@PostMapping("/statement")
	public String handleFileUpload(@RequestParam("statementFile") MultipartFile file,
																 RedirectAttributes redirectAttributes) {
		try {
			File transactionFile = File.createTempFile(file.getOriginalFilename(), ".tmp");
			file.transferTo(transactionFile);
			transactionService.importTransactions(transactionFile, Transaction.Source.FILE_IMPORT);
			redirectAttributes.addFlashAttribute("message",
							"You successfully uploaded '" + file.getOriginalFilename() + "' ("+ file.getSize() +" bytes)!");
		} catch (Exception e) {
			log.error("error processing uploaded statement file", e);
			redirectAttributes.addFlashAttribute("error", "error processing uploaded file: " + e.getMessage());
		}

		return "redirect:/statement";
	}

}
