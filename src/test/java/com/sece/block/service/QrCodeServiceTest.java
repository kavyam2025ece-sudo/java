package com.sece.block.service;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QrCodeServiceTest {

    @Test
    void generateQrCodeBytesReturnsPngImage() {
        byte[] png = new QrCodeService().generateQrCodeBytes("http://localhost:8080/verify/CERT-2026-0001");

        assertTrue(png.length > 8);
        assertArrayEquals(new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a},
                Arrays.copyOf(png, 8));
    }
}
