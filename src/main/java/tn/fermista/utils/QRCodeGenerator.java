package tn.fermista.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class QRCodeGenerator {
    
    public static String generateQRCodeBase64(String content, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    
    public static String getFormationLink(String formationId) {
        // Base Google Drive folder
        String baseFolderUrl = "https://drive.google.com/drive/folders/";
        String folderId = "1J3I6cPYY0JbFZRivLL5ssfbbZHt7b6FN";
        
        // If a specific formationId is provided, use it as the folder ID
        if (formationId != null && !formationId.isEmpty()) {
            // Vérifier si l'ID est déjà dans le bon format
            if (formationId.startsWith("1")) {
                return baseFolderUrl + formationId;
            } else {
                // Si ce n'est pas un ID Google Drive valide, utiliser l'ID de base
                return baseFolderUrl + folderId;
            }
        }
        
        return baseFolderUrl + folderId;
    }
} 