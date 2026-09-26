package com.sece.block.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.sece.block.entity.Certificate;
import com.sece.block.entity.Student;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// This service creates a certificate PDF for each issued certificate.
@Service
public class PdfService {

    public String generateCertificatePdf(Student student, Certificate certificate) {
        try {
            String folderPath = "src/main/resources/static/certificates";
            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName = certificate.getCertificateId() + ".pdf";
            String pdfPath = folder.resolve(fileName).toString();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            Paragraph title = new Paragraph("COLLEGE / UNIVERSITY NAME", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph("CERTIFICATE OF ACHIEVEMENT", headingFont));
            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph("This is to certify that", normalFont));
            document.add(new Paragraph(" ", normalFont));

            Paragraph studentName = new Paragraph(student.getName(), titleFont);
            studentName.setAlignment(Element.ALIGN_CENTER);
            document.add(studentName);

            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph("has successfully completed", normalFont));
            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph(certificate.getCourseName(), headingFont));
            document.add(new Paragraph(" ", normalFont));

            document.add(new Paragraph("Certificate ID: " + certificate.getCertificateId(), normalFont));
            document.add(new Paragraph("Issue Date: " + certificate.getIssueDate(), normalFont));
            document.add(new Paragraph("Verification Status: Blockchain Verified", normalFont));

            document.add(new Paragraph(" ", normalFont));

            String qrFile = "src/main/resources/static/certificates/" + certificate.getCertificateId() + "-qr.png";
            Path qrPath = Paths.get(qrFile);
            if (Files.exists(qrPath)) {
                Image qrImage = Image.getInstance(qrPath.toAbsolutePath().toString());
                qrImage.scaleToFit(120, 120);
                qrImage.setAlignment(Element.ALIGN_CENTER);
                document.add(qrImage);
            }

            document.close();
            return "/certificates/" + fileName;
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("PDF certificate generation failed.", e);
        }
    }
}
