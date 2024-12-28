package com.groceryview;

import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// written by Claude

public class ImageTextExtractor {
    // Adjust paths based on your environment
    public static final String TESSDATA_PATH = "/usr/local/share/tessdata";

    public static String extractText(String imagePath) {
        Tesseract tesseract = new Tesseract();
        
        // Set the tessdata path
        tesseract.setDatapath(TESSDATA_PATH);

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.err.println("Image file does not exist: " + imagePath);
            return null;
        }
        
        try {
            // Validate and read the image
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                System.err.println("The image file could not be read or is invalid: " + imagePath);
                return null;
            }

            // Perform OCR on the image
            System.out.println("Performing OCR on image: " + imagePath);
            String extractedText = tesseract.doOCR(image);
            System.out.println("OCR completed successfully");
            return extractedText;
        } catch (IOException e) {
            System.err.println("Error reading image file: " + e.getMessage());
            return null;
        } catch (TesseractException e) {
            System.err.println("Error during OCR: " + e.getMessage());
            return null;
        }
    }
}
