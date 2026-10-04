package com.jnotifier.helpers;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileHelper {
    private static final String PDF_MAGIC_NUMBER = "25504446";

    public boolean isValidPdf(MultipartFile file) throws IOException {
        byte[] magicBytes = new byte[4];

        try (InputStream is = file.getInputStream()) {
            if (is.read(magicBytes) != 4) {
                return false;
            }
        }

        String hexSignature = bytesToHex(magicBytes);
        return PDF_MAGIC_NUMBER.equals(hexSignature);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
