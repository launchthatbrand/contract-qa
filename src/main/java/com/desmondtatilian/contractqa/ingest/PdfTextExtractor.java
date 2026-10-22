package com.desmondtatilian.contractqa.ingest;

import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PdfTextExtractor {

    public String extract(byte[] pdf) {
        if (pdf.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The PDF is empty");
        }
        try (PDDocument document = Loader.loadPDF(pdf)) {
            String text = new PDFTextStripper().getText(document).trim();
            if (text.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The PDF has no extractable text");
            }
            return text;
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is not a readable PDF");
        }
    }
}
