package com.groceryview;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;



/**
 * GROCERYVIEW APP:
 * Scan receipts from grocery shopping, load the prices of various good, and compute statistics over groups of related goods, etc.
 *
 */

 public class GroceryView extends JFrame {
    // dimensions of the frame
    public static final int GROCERYVIEW_WIDTH = 1600;
    public static final int GROCERYVIEW_HEIGHT = 800;
    // Image of receipt to display after upload
    BufferedImage receiptImage;

    public GroceryView() {
        setTitle("GroceryView");
        setSize(GROCERYVIEW_WIDTH, GROCERYVIEW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
         
        // Holds tabs for receipt scanning window and statistics window
        JTabbedPane tabPanel = new JTabbedPane();
        
        // First panel for scanning receipts and displaying in table form
        JPanel receiptScanPanel = new JPanel();
        receiptScanPanel.setLayout(new GridLayout(1, 2));
        
        JPanel showImagePanel = new JPanel();
        showImagePanel.setLayout(new BorderLayout());
        JButton uploadImageButton = new JButton("Upload Image");
        uploadImageButton.addActionListener(new ImageLoadListener());
        showImagePanel.add(uploadImageButton, BorderLayout.SOUTH);
        JPanel imagePanel = new ImagePanel();
        showImagePanel.add(imagePanel, BorderLayout.CENTER);

        receiptScanPanel.add(showImagePanel);
        tabPanel.addTab("Scan Receipts", receiptScanPanel);
        
        
        // Second panel for displaying statistics
        JPanel analysisPanel = new JPanel();
        analysisPanel.add(new JLabel("Analyse the scanned receipts data here"));
        tabPanel.addTab("Analysis", analysisPanel);
        
        // Add tab panel to main frame
        this.add(tabPanel);

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GroceryView gv = new GroceryView();
            gv.setVisible(true);
        });
    }


    /*
     * Section for util classes
     */
    // Class to handle image upload button click
    private class ImageLoadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser("/Users/andreachatrian/groceryview/");
            fileChooser.setDialogTitle("Choose an image file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("Selected file: " + path);
                File input = new File(path);
                try {
                    receiptImage = ImageIO.read(input);
                    if (receiptImage == null || receiptImage.getWidth() <= 0 || receiptImage.getHeight() <= 0) {
                        throw new IOException("Invalid image dimensions");
                    }
                    System.out.println("Image loaded successfully: " + receiptImage.getWidth() + "x" + receiptImage.getHeight());
                } catch (IOException ex){
                    System.out.println("Error reading image file");
                    JOptionPane.showMessageDialog(null, "Error reading image file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class ImagePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (receiptImage != null && receiptImage.getWidth() > 0 && receiptImage.getHeight() > 0) {
                System.out.println("Frame dimensions: " + GROCERYVIEW_WIDTH + "x" + GROCERYVIEW_HEIGHT);
                // Scale image so it can be viewed in image panel
                int newWidth = GROCERYVIEW_WIDTH / 2;
                int newHeight = (int) ((double) receiptImage.getHeight() / receiptImage.getWidth() * newWidth);
                if (newHeight > GROCERYVIEW_HEIGHT) {
                    newHeight = GROCERYVIEW_HEIGHT - 50;
                    newWidth = (int) ((double) receiptImage.getWidth() / receiptImage.getHeight() * newHeight);
                }
                System.out.println("New image dimensions: " + newWidth + "x" + newHeight);
                Image scaledImage = receiptImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                if (scaledImage == null) {
                    System.out.println("Error scaling image");
                    JOptionPane.showMessageDialog(null, "Error scaling image", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    g.drawImage(scaledImage, 0, 0, this);
                }
            }
        }
    }
    /*
     * End of util classes
     */

 }