package com.groceryview;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;


/**
 * GROCERYVIEW APP:
 * Scan receipts from grocery shopping, load the prices of various good, and compute statistics over groups of related goods, etc.
 *
 */

 public class GroceryView extends JFrame {
    // dimensions of the frame
    public static final int GROCERYVIEW_WIDTH = 1600;
    public static final int GROCERYVIEW_HEIGHT = 800;
    
    // receipt scan tab variables
    // image of receipt to display after upload
    String imagePath;
    BufferedImage receiptImage;
    String extractedText;
    // end receipt scan tab variables

    // analysis tab variables
    String querySelectedWindow = "1 month";
    // end analysis tab variables

    // Swing components
    JButton extractTextButton;
    JButton saveItemsButton;
    JTextArea textArea;
    JTable receiptItemsTable;
    JTable receiptsTable;
    DefaultTableModel receiptsTableModel;


    public GroceryView() {
        setTitle("GroceryView");
        setSize(GROCERYVIEW_WIDTH, GROCERYVIEW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
         
        // Holds tabs for receipt scanning window and statistics window
        JTabbedPane tabPanel = new JTabbedPane();
        
        // start Scan Receipt tab
        // First panel for scanning receipts and displaying in table form
        JPanel receiptScanPanel = new JPanel();
        receiptScanPanel.setLayout(new GridLayout(1, 3));
        
        // Panel for displaying the uploaed image
        JPanel showImagePanel = new JPanel();
        showImagePanel.setLayout(new BorderLayout());
        JButton uploadImageButton = new JButton("Upload Image");
        uploadImageButton.addActionListener(new ImageLoadListener());
        showImagePanel.add(uploadImageButton, BorderLayout.SOUTH);
        JPanel imagePanel = new ImagePanel();
        showImagePanel.add(imagePanel, BorderLayout.CENTER);

        // Panel to dispaly the extracted text
        JPanel displayTextPanel = new JPanel();
        displayTextPanel.setLayout(new BorderLayout());
        textArea = new JTextArea();
        extractTextButton = new JButton("Extract Text");
        extractTextButton.addActionListener(new ExtractTextListener());
        displayTextPanel.add(extractTextButton, BorderLayout.SOUTH);
        displayTextPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        if (imagePath == null || receiptImage == null) {
            extractTextButton.setEnabled(false);
        }

        // Panel to show output receipt items in table form
        JPanel receiptItemsPanel = new JPanel();
        receiptItemsPanel.setLayout(new BorderLayout());
        receiptItemsTable = new JTable();
        receiptItemsPanel.add(new JScrollPane(receiptItemsTable), BorderLayout.CENTER);
        saveItemsButton = new JButton("Save Receipt");
        saveItemsButton.addActionListener(new SaveItemsListener());
        if (extractedText == null || extractedText.isEmpty()) {
            saveItemsButton.setEnabled(false);
        };
        receiptItemsPanel.add(saveItemsButton, BorderLayout.SOUTH);
        
        receiptScanPanel.add(showImagePanel);
        receiptScanPanel.add(displayTextPanel);
        receiptScanPanel.add(receiptItemsPanel);
        tabPanel.addTab("Scan Receipts", receiptScanPanel);
        // end Scan Receipt tab
        
        
        // Second tab for displaying statistics
        JPanel analysisPanel = new JPanel();
        analysisPanel.setLayout(new GridLayout(1, 3));
        JPanel selectQueryPanel = new JPanel();
        selectQueryPanel.setLayout(new BorderLayout());
        // String selectedValue = "1 month";
        JPanel chooseTimePanel = new JPanel();
        chooseTimePanel.setLayout(new GridLayout(1, 2));
        JLabel queryLabel = new JLabel("Time window for receipts:");
        JComboBox<String> queryComboBox = new JComboBox<String>();
        for (int i = 1; i <= 12; i++) {
            queryComboBox.addItem(i + " months");
        }
        queryComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            querySelectedWindow = (String) cb.getSelectedItem();
        });
        chooseTimePanel.add(queryLabel);
        chooseTimePanel.add(queryComboBox);
        selectQueryPanel.add(chooseTimePanel, BorderLayout.NORTH);
        JButton runQueryButton = new JButton("Run Query");
        runQueryButton.addActionListener(new ReceiptQueryListener());
        selectQueryPanel.add(runQueryButton, BorderLayout.SOUTH);
        // table to display queries results
        receiptsTable = new JTable();
        receiptsTableModel = new DefaultTableModel();
        receiptsTableModel.addColumn("Receipt ID");
        receiptsTableModel.addColumn("Date Added");
        receiptsTableModel.addColumn("Total");
        selectQueryPanel.add(new JScrollPane(receiptsTable), BorderLayout.CENTER);
        analysisPanel.add(selectQueryPanel);
        
        tabPanel.addTab("Analysis", analysisPanel);
        // end analysis tab

        this.add(tabPanel);  // add tab panel to main frame

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

     // Class to display image in panel
     private class ImagePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (receiptImage != null && receiptImage.getWidth() > 0 && receiptImage.getHeight() > 0) {
                Integer parentPanelWidth = this.getParent().getWidth();
                Integer parentPanelHeight = this.getParent().getHeight();
                System.out.println("Frame dimensions: " + parentPanelWidth + "x" + parentPanelHeight);
                // Scale image so it can be viewed in image panel
                int newWidth = parentPanelWidth;
                int newHeight = (int) ((double) receiptImage.getHeight() / receiptImage.getWidth() * newWidth);
                if (newHeight > parentPanelHeight) {
                    newHeight = parentPanelHeight - 10;
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

    // Class to handle image upload button click
    private class ImageLoadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser("/Users/andreachatrian/Documents/Repositories/groceryview/");
            fileChooser.setDialogTitle("Choose an image file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                // groceryview instance variable imagePath
                imagePath = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("Selected file: " + imagePath);
                File input = new File(imagePath);
                try {
                    receiptImage = ImageIO.read(input);
                    if (receiptImage == null || receiptImage.getWidth() <= 0 || receiptImage.getHeight() <= 0) {
                        throw new IOException("Invalid image dimensions");
                    }
                    System.out.println("Image loaded successfully: " + receiptImage.getWidth() + "x" + receiptImage.getHeight());
                    // Enable extract text button
                    extractTextButton.setEnabled(true);
                } catch (IOException ex){
                    System.out.println("Error reading image file");
                    JOptionPane.showMessageDialog(null, "Error reading image file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    // Class to handle extract text button click
    private class ExtractTextListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (imagePath == null || imagePath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please upload an image first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try { // Extract text from image
                extractedText = ImageTextExtractor.extractText(imagePath);
                System.out.println("Extracted text:");
                System.out.println(extractedText);
                textArea.setText(extractedText);
            } catch (Exception ex) {
                System.out.println("Error extracting text: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error extracting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            try { // Crate a receipt object to extract items
                Receipt receipt = new Receipt(extractedText);
                receiptItemsTable.setModel(new DefaultTableModel(
                    receipt.itemsToDataArray(), 
                    new String[] {"Item", "VAT", "Price"}
                ));
                saveItemsButton.setEnabled(true);
            } catch (Exception ex) {
                System.out.println("Error extracting items: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error extracting receipt items from text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            };
        };
    };

    // Class to handle save items button click
    private class SaveItemsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Save the extracted items to the database
            System.out.println("Saving items to database");
            DatabaseConfig.getConnection();
            try {
                DatabaseConfig.ReceiptDAO receiptDAO = new DatabaseConfig.ReceiptDAO();
                receiptDAO.createTable();
                DatabaseConfig.ReceiptItemDAO receiptItemDAO = new DatabaseConfig.ReceiptItemDAO();
                receiptItemDAO.createTable();
                Receipt receipt = new Receipt(extractedText);
                int receiptId = receiptDAO.insertReceipt(receipt);
                if (receiptId == -1) {
                    throw new Exception("Error saving receipt to database - receiptId is -1");
                } else {
                    System.out.println("Receipt saved successfully with receiptId: " + receiptId);
                }
                for (Receipt.ReceiptItem item : receipt.getItems()) {
                    receiptItemDAO.insertReceiptItem(item, receiptId);
                }
                System.out.println("Receipt items saved successfully");
                JOptionPane.showMessageDialog(null, "Receipt items saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                System.out.println("Error saving items to database: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error saving items to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Listener for querying database for receipts
    private class ReceiptQueryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Running query for receipts in the last " + querySelectedWindow);
            DatabaseConfig.getConnection();
            try {
                DatabaseConfig.ReceiptDAO receiptDAO = new DatabaseConfig.ReceiptDAO();
                ArrayList<Receipt> receipts = receiptDAO.getReceiptsByDate(Integer.parseInt(querySelectedWindow.split(" ")[0]));
                if (receipts == null) {
                    throw new Exception("Error retrieving receipts from database");
                }
                System.out.println("Receipts retrieved successfully");
                // Display the receipts in a table
                for (Receipt receipt : receipts) {
                    receiptsTableModel.addRow(new Object[] {receipt.getReceiptId(), receipt.getReceiptDate(), receipt.getTotalPaid()});
                }
                receiptsTable.setModel(receiptsTableModel);
            } catch (Exception ex) {
                System.out.println("Error running query: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error running query: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /*
     * End of util classes
     */

 }