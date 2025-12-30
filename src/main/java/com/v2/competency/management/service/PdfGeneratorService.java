package com.v2.competency.management.service;

import java.io.ByteArrayInputStream;

import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.DocumentException;

public interface PdfGeneratorService {
	
	public ByteArrayInputStream createPdf(@RequestParam String testIdentifier, @RequestParam String email, @RequestParam String companyId, @RequestParam Integer attempt, @RequestParam String token) throws DocumentException;

	public ByteArrayInputStream createPdfBasedOnActualAIResponse(String testIdentifier, String email, String companyId, Integer attempt, String token) throws DocumentException ;
}
