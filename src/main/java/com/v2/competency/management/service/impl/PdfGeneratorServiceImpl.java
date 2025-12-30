package com.v2.competency.management.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.googlecloud.vertex.ai.dto.AITestResponse;
import com.googlecloud.vertex.ai.dto.VertexResponse;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
//import com.lowagie.text.FontFactory;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.v2.competency.management.dtos.PdfHeaderFooter;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.entities.VFTestUserSession;
//import com.lowagie.text.Document;
import com.v2.competency.management.service.PdfGeneratorService;
import com.v2.competency.management.service.VFTestUserQuestionAnswerService;
import com.v2.competency.management.service.VFTestUserSessionService;


@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {
	
	@Autowired
	VFTestUserQuestionAnswerService answerService;
	
	@Autowired
	VFTestUserSessionService sessionService;
	
	private Logger logger = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);
	
	ObjectMapper mapper = new ObjectMapper();
	
	 XmlMapper xmlMapper = new XmlMapper();
	 
	 
	 
	 @Override
		public ByteArrayInputStream createPdfBasedOnActualAIResponse(String testIdentifier, String email, String companyId, Integer attempt, String token) throws DocumentException {
		    // Retrieve test answers
//		 VFTestUserSession session =  sessionService.finfVFTestUserSessionByEmail(email, companyId, testIdentifier, attempt);
//			AITestResponse testResponse = null;
//			if(session.getAiAnalysisJson() != null) {
//				 testResponse = mapper.readValue(session.getAiAnalysisJson(), AITestResponse.class);
//		 		//	
//			}
//			
//			if(testResponse == null) {
//				//go back
//				//return null;
//			}
//
//		    logger.info("PDF generation has been started");
//
//		    String title = "Test Results for: " + testIdentifier + " (Attempt: " + attempt + ")";
//
//		    ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//		    Document doc = new Document();
//		    PdfWriter writer = PdfWriter.getInstance(doc, out);
//
//		    // Set header and footer
//		    writer.setPageEvent(new PdfHeaderFooter());
//
//		    doc.open();
//
//		    // Title
//		    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30, BaseColor.DARK_GRAY);
//		    Paragraph titlePara = new Paragraph(title, titleFont);
//		    titlePara.setAlignment(Element.ALIGN_CENTER);
//		    doc.add(titlePara);
//
//		    doc.add(Chunk.NEWLINE);
//
//		    // Question and Answer Sections
//		    for (VertexResponse answer : testResponse.getResponse().getList()) {
//		    	
//		        int digit = getDigits(Integer.parseInt(answer.getQuestionId()));
//
//		        // Question
//		        Font questionFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.DARK_GRAY);
//		        String questionText = (digit == 1) ? "Question " + answer.getQid() : "Follow-up Question " + (digit - 1);
//		        Paragraph questionPara = new Paragraph(questionText, questionFont);
//		        doc.add(questionPara);
//
//		        doc.add(Chunk.NEWLINE);
//
//		        // Actual Question Text
//		        Font quesFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
//		        String quesText = answer.getQuestion();
//		        Paragraph quesPara = new Paragraph(quesText, quesFont);
//		        doc.add(quesPara);
//
//		        doc.add(Chunk.NEWLINE);
//
//		        // Answer
//		        Font answerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
//		        String ansText = "Answer: " + answer.getAnswer();
//		        Paragraph ansPara = new Paragraph(ansText, answerFont);
//		        doc.add(ansPara);
//
//		        doc.add(Chunk.NEWLINE);
//
////		        // AI Analysis
////		        if (answer.getAnalysis() != null) {
////		            Font analysisFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
////		            String analysisText = "Analysis by AI: " + answer.getAnalysis();
////		            Paragraph analysisPara = new Paragraph(analysisText, analysisFont);
////		            doc.add(analysisPara);
//	//
////		            doc.add(Chunk.NEWLINE);
////		        }
//		        
//		        
//	            String analysisText = "Analysis by AI: ";
//	            Paragraph analysisPara = new Paragraph(analysisText, quesFont);
//	            doc.add(analysisPara);
//
//	            doc.add(Chunk.NEWLINE);
//		        
//		     // Create table
//	            PdfPTable analysisTable = new PdfPTable(5);
//	            PdfPCell cell;
//	            Font analysisFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
//	            // Add column names
//	            String[] columnNames = {"Aspect", "Strengths", "Areas for Improvement", "Differentiation", "Desired Answer"};
//	            for (String columnName : columnNames) {
//	                cell = new PdfPCell(new Phrase(columnName, analysisFont));
//	                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//	                cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Set background color to sky blue for all column name cells
//	                analysisTable.addCell(cell);
//	            }
//	            // Add dummy data
//	            for (int i = 0; i < 5; i++) {
//	                analysisTable.addCell("dummy");
//	            }
//	            doc.add(analysisTable);
//	            doc.add(Chunk.NEWLINE);
//		    }
//		    
//		    Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.DARK_GRAY);
//	        String summaryText = "Overall Summary : ";
//	        Paragraph summaryPara = new Paragraph(summaryText, summaryFont);
//	        doc.add(summaryPara);
//	        
//	        doc.add(Chunk.NEWLINE);
//	        
//	        
//	        Font sumFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
//	        String sumText = "Basic Analysis : ";
//	        Paragraph sumPara = new Paragraph(sumText, sumFont);
//	        doc.add(sumPara);
//	        
//	        doc.add(Chunk.NEWLINE);
//	        
//	        
//	        PdfPTable summaryTable = new PdfPTable(2);
//	        PdfPCell cell;
//	        Font sum1Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
//	        
//	        String[] columnNames = {"Aspect", "Analysis"};
//	        for (String columnName : columnNames) {
//	            cell = new PdfPCell(new Phrase(columnName, sum1Font));
//	            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//	            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Set background color to sky blue for all column name cells
//	            summaryTable.addCell(cell);
//	        }
//	        
//	        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
//	        
//	        Phrase boldPhrase = new Phrase(" Main topics XYZ focused on : ", boldFont);
//	        summaryTable.addCell(boldPhrase);
//	        summaryTable.addCell("dummy");
//	        
//	        Phrase bold1Phrase = new Phrase(" Suggested language improvements : ", boldFont);
//	        summaryTable.addCell(bold1Phrase);
//	        summaryTable.addCell("dummy");
//	        
//	        Phrase bold2Phrase = new Phrase(" Collaboration : ", boldFont);
//	        summaryTable.addCell(bold2Phrase);
//	        summaryTable.addCell("dummy");
//	        
//	        Phrase bold3Phrase = new Phrase(" Emotions : ", boldFont);
//	        summaryTable.addCell(bold3Phrase);
//	        summaryTable.addCell("dummy");
//	        
//	        Phrase bold4Phrase = new Phrase(" Confidence : ", boldFont);
//	        summaryTable.addCell(bold4Phrase);
//	        summaryTable.addCell("dummy");
//	        
//	        Phrase bold5Phrase = new Phrase(" Articulation : ", boldFont);
//	        summaryTable.addCell(bold5Phrase);
//	        summaryTable.addCell("dummy");
//	        
//	        Phrase bold6Phrase = new Phrase("Answer Relevancy Score : ", boldFont);
//	        summaryTable.addCell(bold6Phrase);
//	        summaryTable.addCell("dummy");
//	        
//	        doc.add(summaryTable);
//	        doc.add(Chunk.NEWLINE);
//	        
//	        
//	        Font sum2Font = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
//	        String sum1Text = "Our Grading : ";
//	        Paragraph sum1Para = new Paragraph(sum1Text, sum2Font);
//	        doc.add(sum1Para);
//	        
//	        doc.add(Chunk.NEWLINE);
//	        
//	        PdfPTable gradingTable = new PdfPTable(3);
//	        PdfPCell cell1;
//	        
//	        Font sum3Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
//	        
//	        String[] columnNames1 = {"Category", "Rating (1 - 5)", "Explanation"};
//	        for (String columnName : columnNames1) {
//	            cell1 = new PdfPCell(new Phrase(columnName, sum3Font));
//	            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//	            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY); // Set background color to sky blue for all column name cells
//	            gradingTable.addCell(cell1);
//	        }
//	        
//	        Phrase gradePhrase = new Phrase("  Self-Introduction : ", boldFont);
//	        gradingTable.addCell(gradePhrase);
//	        gradingTable.addCell("4");
//	        gradingTable.addCell("dummy");
//	        
//	        Phrase grade1Phrase = new Phrase(" Challenge and Solution : ", boldFont);
//	        gradingTable.addCell(grade1Phrase);
//	        gradingTable.addCell("3");
//	        gradingTable.addCell("dummy");
//	        
//	        Phrase grade2Phrase = new Phrase(" Metric Analysis : ", boldFont);
//	        gradingTable.addCell(grade2Phrase);
//	        gradingTable.addCell("5");
//	        gradingTable.addCell("dummy");
//	        
//	        Phrase grade3Phrase = new Phrase(" Content Creation : ", boldFont);
//	        gradingTable.addCell(grade3Phrase);
//	        gradingTable.addCell("4");
//	        gradingTable.addCell("dummy");
//	        
//	        Phrase grade4Phrase = new Phrase(" Content Strategy : ", boldFont);
//	        gradingTable.addCell(grade4Phrase);
//	        gradingTable.addCell("2");
//	        gradingTable.addCell("dummy");
//	        
//	        
//	        
//	        
//	        doc.add(gradingTable);
//	        doc.add(Chunk.NEWLINE);
//	        
//
//		    doc.close();
//
//		    return new ByteArrayInputStream(out.toByteArray());
		 return null;
		}
	 

	@Override
	public ByteArrayInputStream createPdf(String testIdentifier, String email, String companyId, Integer attempt, String token) throws DocumentException {
	    // Retrieve test answers
	//  List<VFTestUserQuestionAnswer> answers = answerService.findAllQAForUser(email, companyId, attempt);
		 List<VFTestUserQuestionAnswer> answers = null;

	    logger.info("PDF generation has been started");

	    String title = "Test Results for: " + testIdentifier + " (Attempt: " + attempt + ")";

	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    Document doc = new Document();
	    PdfWriter writer = PdfWriter.getInstance(doc, out);

	    // Set header and footer
	    writer.setPageEvent(new PdfHeaderFooter());

	    doc.open();

	    // Title
	    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30, BaseColor.DARK_GRAY);
	    Paragraph titlePara = new Paragraph(title, titleFont);
	    titlePara.setAlignment(Element.ALIGN_CENTER);
	    doc.add(titlePara);

	    doc.add(Chunk.NEWLINE);

	    // Question and Answer Sections
	    for (VFTestUserQuestionAnswer answer : answers) {
	        int digit = getDigits(Integer.parseInt(answer.getQid()));

	        // Question
	        Font questionFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.DARK_GRAY);
	        String questionText = (digit == 1) ? "Question " + answer.getQid() : "Follow-up Question " + (digit - 1);
	        Paragraph questionPara = new Paragraph(questionText, questionFont);
	        doc.add(questionPara);

	        doc.add(Chunk.NEWLINE);

	        // Actual Question Text
	        Font quesFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
	        String quesText = answer.getQuestion();
	        Paragraph quesPara = new Paragraph(quesText, quesFont);
	        doc.add(quesPara);

	        doc.add(Chunk.NEWLINE);

	        // Answer
	        Font answerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
	        String ansText = "Answer: " + answer.getAnswer();
	        Paragraph ansPara = new Paragraph(ansText, answerFont);
	        doc.add(ansPara);

	        doc.add(Chunk.NEWLINE);

//	        // AI Analysis
//	        if (answer.getAnalysis() != null) {
//	            Font analysisFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
//	            String analysisText = "Analysis by AI: " + answer.getAnalysis();
//	            Paragraph analysisPara = new Paragraph(analysisText, analysisFont);
//	            doc.add(analysisPara);
//
//	            doc.add(Chunk.NEWLINE);
//	        }
	        
	        
            String analysisText = "Analysis by AI: ";
            Paragraph analysisPara = new Paragraph(analysisText, quesFont);
            doc.add(analysisPara);

            doc.add(Chunk.NEWLINE);
	        
	     // Create table
            PdfPTable analysisTable = new PdfPTable(5);
            PdfPCell cell;
            Font analysisFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
            // Add column names
            String[] columnNames = {"Aspect", "Strengths", "Areas for Improvement", "Differentiation", "Desired Answer"};
            for (String columnName : columnNames) {
                cell = new PdfPCell(new Phrase(columnName, analysisFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Set background color to sky blue for all column name cells
                analysisTable.addCell(cell);
            }
            // Add dummy data
            for (int i = 0; i < 5; i++) {
                analysisTable.addCell("dummy");
            }
            doc.add(analysisTable);
            doc.add(Chunk.NEWLINE);
	    }
	    
	    Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.DARK_GRAY);
        String summaryText = "Overall Summary : ";
        Paragraph summaryPara = new Paragraph(summaryText, summaryFont);
        doc.add(summaryPara);
        
        doc.add(Chunk.NEWLINE);
        
        
        Font sumFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
        String sumText = "Basic Analysis : ";
        Paragraph sumPara = new Paragraph(sumText, sumFont);
        doc.add(sumPara);
        
        doc.add(Chunk.NEWLINE);
        
        
        PdfPTable summaryTable = new PdfPTable(2);
        PdfPCell cell;
        Font sum1Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        
        String[] columnNames = {"Aspect", "Analysis"};
        for (String columnName : columnNames) {
            cell = new PdfPCell(new Phrase(columnName, sum1Font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Set background color to sky blue for all column name cells
            summaryTable.addCell(cell);
        }
        
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
        
        Phrase boldPhrase = new Phrase(" Main topics XYZ focused on : ", boldFont);
        summaryTable.addCell(boldPhrase);
        summaryTable.addCell("dummy");
        
        Phrase bold1Phrase = new Phrase(" Suggested language improvements : ", boldFont);
        summaryTable.addCell(bold1Phrase);
        summaryTable.addCell("dummy");
        
        Phrase bold2Phrase = new Phrase(" Collaboration : ", boldFont);
        summaryTable.addCell(bold2Phrase);
        summaryTable.addCell("dummy");
        
        Phrase bold3Phrase = new Phrase(" Emotions : ", boldFont);
        summaryTable.addCell(bold3Phrase);
        summaryTable.addCell("dummy");
        
        Phrase bold4Phrase = new Phrase(" Confidence : ", boldFont);
        summaryTable.addCell(bold4Phrase);
        summaryTable.addCell("dummy");
        
        Phrase bold5Phrase = new Phrase(" Articulation : ", boldFont);
        summaryTable.addCell(bold5Phrase);
        summaryTable.addCell("dummy");
        
        Phrase bold6Phrase = new Phrase("Answer Relevancy Score : ", boldFont);
        summaryTable.addCell(bold6Phrase);
        summaryTable.addCell("dummy");
        
        doc.add(summaryTable);
        doc.add(Chunk.NEWLINE);
        
        
        Font sum2Font = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
        String sum1Text = "Our Grading : ";
        Paragraph sum1Para = new Paragraph(sum1Text, sum2Font);
        doc.add(sum1Para);
        
        doc.add(Chunk.NEWLINE);
        
        PdfPTable gradingTable = new PdfPTable(3);
        PdfPCell cell1;
        
        Font sum3Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        
        String[] columnNames1 = {"Category", "Rating (1 - 5)", "Explanation"};
        for (String columnName : columnNames1) {
            cell1 = new PdfPCell(new Phrase(columnName, sum3Font));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY); // Set background color to sky blue for all column name cells
            gradingTable.addCell(cell1);
        }
        
        Phrase gradePhrase = new Phrase("  Self-Introduction : ", boldFont);
        gradingTable.addCell(gradePhrase);
        gradingTable.addCell("4");
        gradingTable.addCell("dummy");
        
        Phrase grade1Phrase = new Phrase(" Challenge and Solution : ", boldFont);
        gradingTable.addCell(grade1Phrase);
        gradingTable.addCell("3");
        gradingTable.addCell("dummy");
        
        Phrase grade2Phrase = new Phrase(" Metric Analysis : ", boldFont);
        gradingTable.addCell(grade2Phrase);
        gradingTable.addCell("5");
        gradingTable.addCell("dummy");
        
        Phrase grade3Phrase = new Phrase(" Content Creation : ", boldFont);
        gradingTable.addCell(grade3Phrase);
        gradingTable.addCell("4");
        gradingTable.addCell("dummy");
        
        Phrase grade4Phrase = new Phrase(" Content Strategy : ", boldFont);
        gradingTable.addCell(grade4Phrase);
        gradingTable.addCell("2");
        gradingTable.addCell("dummy");
        
        
        
        
        doc.add(gradingTable);
        doc.add(Chunk.NEWLINE);
        

	    doc.close();

	    return new ByteArrayInputStream(out.toByteArray());
	}
	public static int getDigits(int qid) {
        int count = 0;
        
        if (qid == 0) {
            return 1;
        }
        
        while (qid > 0) {
            qid = qid / 10;
            count++;
        }
        
        return count;
    }

}
