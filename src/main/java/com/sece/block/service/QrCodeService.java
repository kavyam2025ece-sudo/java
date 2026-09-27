package com.sece.block.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// This service creates a QR code image for each certificate verification link.
@Service
public class QrCodeService {

    public String generateQrCode(String verificationUrl, String certificateId) {
        try {
            String folderPath = "src/main/resources/static/certificates";
            Path folder = Paths.get(folderPath);
            Files.createDirectories(folder);

            String fileName = certificateId + "-qr.png";
            Path filePath = folder.resolve(fileName);
            Files.write(filePath, generateQrCodeBytes(verificationUrl));
            return "/certificates/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("QR code generation failed.", e);
        }
    }

    public byte[] generateQrCodeBytes(String verificationUrl) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                    verificationUrl,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            return output.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR code generation failed.", e);
        }
    }
}
