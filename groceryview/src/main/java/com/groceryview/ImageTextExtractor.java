package com.groceryview;

import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImageTextExtractor {
    // Adjust paths based on your environment
    public static final String TESSDATA_PATH = "/usr/local/share/tessdata";

    static {
        // Set the library path via system property (alternative: set it as a JVM argument)
        String homebrewLibPath = "/usr/local/Cellar/tesseract/5.5.0/lib";
        System.setProperty("java.library.path", homebrewLibPath);
        
        try {
            // Force reload of library paths
            java.lang.reflect.Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (Exception e) {
            System.err.println("Error setting library path: " + e.getMessage());
        }
    }

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
            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) {
                System.err.println("The image file could not be read or is invalid: " + imagePath);
                return null;
            }

            // Perform OCR on the image
            System.out.println("Performing OCR on image: " + imagePath);
            String extractedText = tesseract.doOCR(img);
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


    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java com.groceryview.ImageTextExtractor <image-path>");
            return;
        }
        
        String imagePath = args[0];
        String extractedText = extractText(imagePath);

        if (extractedText != null) {
            System.out.println("Extracted text:");
            System.out.println(extractedText);
        }
    }
}
