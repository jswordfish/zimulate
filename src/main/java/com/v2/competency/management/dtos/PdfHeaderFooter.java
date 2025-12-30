package com.v2.competency.management.dtos;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;


import java.io.IOException;

public class PdfHeaderFooter extends PdfPageEventHelper {

    private int pageNumber;

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        // Add header (logo) on each page
        try {
            Image logo = Image.getInstance("C:\\Users\\USER\\Desktop\\Dhruv\\Images\\logo.png");
            logo.scaleToFit(100, 100); // Adjust the width and height as needed

            // Create a Paragraph to hold the logo
            Paragraph logoParagraph = new Paragraph();
            Chunk chunkWithLogo = new Chunk(logo, 0, 0); // Add the logo as a Chunk

            // Add the Chunk (with logo) to the Paragraph
            logoParagraph.add(chunkWithLogo);

            // Set alignment of the Paragraph to center
            logoParagraph.setAlignment(Element.ALIGN_CENTER);

            // Add the Paragraph (with logo) to the document
            document.add(logoParagraph);

            // Add spacing after the logo to create separation
            document.add(new Paragraph(" "));

            // Adjust margins to create space for the logo and table
            document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin(), document.bottomMargin());

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // Add footer (page number) on each page
        pageNumber++;
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER,
                new Phrase("Page " + pageNumber),
                (document.right() + document.left()) / 2,
                document.bottom() - 10,
                0);
    }
}