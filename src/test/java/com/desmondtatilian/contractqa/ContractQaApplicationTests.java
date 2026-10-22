package com.desmondtatilian.contractqa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ContractQaApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @Autowired
    MockMvc mvc;

    @Test
    void homePageRenders() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void answerCitesTheMatchingPassage() throws Exception {
        String documentId = upload("The payment term is net thirty days.");

        mvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"documentId":"%s","question":"What is the payment term?"}
                                """.formatted(documentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.citations[0].excerpt").value("The payment term is net thirty days."))
                .andExpect(jsonPath("$.answer").value("The payment term is net thirty days."));
    }

    @Test
    void unrelatedQuestionReturnsNoCitations() throws Exception {
        String documentId = upload("The payment term is net thirty days.");

        mvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"documentId":"%s","question":"What is the spacecraft hull warranty?"}
                                """.formatted(documentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(false))
                .andExpect(jsonPath("$.citations").isEmpty());
    }

    private String upload(String sentence) throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contract.pdf",
                "application/pdf",
                pdf(sentence));
        MvcResult result = mvc.perform(multipart("/api/documents").file(file))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    private static byte[] pdf(String sentence) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                stream.newLineAtOffset(50, 700);
                stream.showText(sentence);
                stream.endText();
            }
            document.save(output);
            return output.toByteArray();
        }
    }
}
