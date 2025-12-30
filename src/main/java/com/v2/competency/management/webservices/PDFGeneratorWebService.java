package com.v2.competency.management.webservices;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.MediaType;
import com.itextpdf.text.DocumentException;
import com.v2.competency.management.service.PdfGeneratorService;

@Controller
@CrossOrigin
public class PDFGeneratorWebService {
	
	@Autowired
	PdfGeneratorService service;
	
	@GetMapping("/pdfGenerate")
	public ResponseEntity<InputStreamResource> generatePdf(@RequestParam String testIdentifier, @RequestParam String email, @RequestParam String companyId, @RequestParam Integer attempt, @RequestParam String token) throws DocumentException {
		
		
		ByteArrayInputStream pdf = service.createPdf(testIdentifier, email, companyId, attempt, token);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_PDF);
		
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(pdf));
		
		
	}

}
