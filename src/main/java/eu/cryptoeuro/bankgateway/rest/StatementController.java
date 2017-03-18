package eu.cryptoeuro.bankgateway.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class StatementController {
	@GetMapping("/statement")
	public String uploadForm() {
		return "upload";
	}

	@PostMapping("/statement")
	public String handleFileUpload(@RequestParam("statementFile") MultipartFile file,
																 RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/upload.html";
	}

}
