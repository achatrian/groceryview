package com.groceryview;


import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.ListFormat.Style;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.time.TimeSeries;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * GROCERYVIEW APP:
 * Scan receipts from grocery shopping, load the prices of various good, and compute and display statistics over groups of related goods, etc.
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
    String customReceiptDate;
    float receiptTotalPaid;
    // end receipt scan tab variables

    // analysis tab variables
    String querySelectedWindow = "1 month";
    int selectedBarCharItemsNum = -1;
    // ArrayList<Receipt> receipts;
    // ArrayList<Receipt.ReceiptItem> items;
    // end analysis tab variables

    // Swing components
    JButton extractTextButton;
    JButton itemizeTextButton;
    JLabel receiptItemsLabel;
    JButton saveItemsButton;
    JTextArea textArea;
    JTable receiptItemsTable;
    JTable receiptsTable;
    DefaultTableModel receiptsTableModel;
    JTable itemsTable;
    DefaultTableModel itemsTableModel;
    JPanel chartsPanel;
    ChartPanel totalPaidChartPanel;
    ChartPanel itemsFrequencyBarChartPanel;
    JComboBox<Integer> selectNumBarChartItems;
    JButton plotChartsButton;
    JButton clearChartsButton;
    JButton clearTablesButton;

    public GroceryView() {
        setTitle("GroceryView");
        setSize(GROCERYVIEW_WIDTH, GROCERYVIEW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(StyleFormatter.darkGreen);
         
        // Holds tabs for receipt scanning window and statistics window
        JTabbedPane tabPanel = new JTabbedPane();
        
        // First tab for querying receipts from database and displaying statistics
        JPanel analysisPanel = new JPanel();
        analysisPanel.setLayout(new GridLayout(1, 2));
        
        JPanel selectQueryPanel = createSelectQueryPanel();  // Panel to select the time window for the query
        
        JPanel chartsPanel = createChartsPanel();  // Panel to display charts
        
        analysisPanel.add(selectQueryPanel);
        analysisPanel.add(chartsPanel);
        tabPanel.addTab("Analysis", analysisPanel);
        // end analysis tab

        // start Scan Receipt tab
        // Second tab for scanning receipts and displaying in table form
        JPanel receiptScanPanel = new JPanel();
        receiptScanPanel.setLayout(new GridLayout(1, 3));
        
        
        JPanel showImagePanel = createShowImagePanel(); // Panel for displaying the uploaed image
        JPanel displayTextPanel = createDisplayTextPanel();  // Panel to dispaly the extracted text
        JPanel receiptItemsPanel = createReceiptItemsPanel(); // Panel to show output receipt items in table form

        receiptScanPanel.add(showImagePanel);
        receiptScanPanel.add(displayTextPanel);
        receiptScanPanel.add(receiptItemsPanel);
        tabPanel.addTab("Scan Receipts", receiptScanPanel);
        // end Scan Receipt tab

        this.add(tabPanel);  // add tab panel to main frame

        // Styling panels
        StyleFormatter.setGlobalStyles();
        StyleFormatter.applyStylesRecursively(this);
        showImagePanel.getComponent(2).setBackground(Color.WHITE);
        getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.darkGray));

    }

    /* 
    Start methods to create panels

    */
    private JPanel createShowImagePanel() {
        // Panel for displaying the uploaed image
        JPanel showImagePanel = new JPanel();
        showImagePanel.setLayout(new BorderLayout());
        JLabel receiptImageLabel = new JLabel("Scanned receipt image:", SwingConstants.CENTER);
        receiptImageLabel.setFont(new Font("Calibri", Font.BOLD, 16));
        
        showImagePanel.add(receiptImageLabel, BorderLayout.NORTH);
        JButton uploadImageButton = new JButton("Upload Image");
        uploadImageButton.addActionListener(new ImageLoadListener());
        showImagePanel.add(uploadImageButton, BorderLayout.SOUTH);
        JPanel imagePanel = new ImagePanel();
        showImagePanel.add(imagePanel, BorderLayout.CENTER);
        return showImagePanel;
    }

    private JPanel createDisplayTextPanel() {
        // Panel to display text extracted from the receipt image
        JPanel displayTextPanel = new JPanel();
        displayTextPanel.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setEnabled(false);
        displayTextPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        JLabel extractedTextLabel = new JLabel("Extracted text - edit if necessary:", SwingConstants.CENTER);
        
        displayTextPanel.add(extractedTextLabel, BorderLayout.NORTH);
        JPanel textFormatPanel = new JPanel();
        textFormatPanel.setLayout(new GridLayout(1, 2));
        
        extractTextButton = new JButton("Extract Text");
        extractTextButton.addActionListener(new ExtractTextListener());
        itemizeTextButton = new JButton("Itemize Text");
        itemizeTextButton.addActionListener(new ItemizeTextListener());
        textFormatPanel.add(extractTextButton);
        textFormatPanel.add(itemizeTextButton);
        displayTextPanel.add(textFormatPanel, BorderLayout.SOUTH);
        if (imagePath == null || receiptImage == null) {
            extractTextButton.setEnabled(false);
            itemizeTextButton.setEnabled(false);
        }
        return displayTextPanel;
    }

    private JPanel createReceiptItemsPanel() {
        // Panel to display a table with the items interpreted from the text
        JPanel receiptItemsPanel = new JPanel();
        receiptItemsPanel.setLayout(new BorderLayout());
        JPanel centralItemsPanel = new JPanel();
        centralItemsPanel.setLayout(new BorderLayout());
        receiptItemsTable = new JTable();
        centralItemsPanel.add(new JScrollPane(receiptItemsTable), BorderLayout.CENTER);
        // Add select boxes to input the date when the receipt was obtained manually
        // For receipts not obtained on the current date
        JPanel customDatePanel = new JPanel(new GridLayout(1, 4));
        JComboBox<Integer> customDateYearBox = new JComboBox<Integer>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2020; i <= currentYear; i++) {
            customDateYearBox.addItem(i);
        }
        JComboBox<Integer> customDateMonthBox = new JComboBox<Integer>();
        for (int i = 1; i <= 12; i++) {
            customDateMonthBox.addItem(i);
        }
        JComboBox<Integer> customDateDayBox = new JComboBox<Integer>();
        for (int i = 1; i <= 31; i++) {
            customDateDayBox.addItem(i);
        }
        JButton setDateButton = new JButton("Set Receipt Date");
        setDateButton.addActionListener(e -> {
            customReceiptDate = customDateYearBox.getSelectedItem() + "-" + String.format("%02d", customDateMonthBox.getSelectedItem()) + "-" + String.format("%02d", customDateDayBox.getSelectedItem());
        });
        customDatePanel.add(customDateDayBox);
        customDatePanel.add(customDateMonthBox);
        customDatePanel.add(customDateYearBox);
        customDatePanel.add(setDateButton);
        // Finish central panel, then add it to main panel
        centralItemsPanel.add(customDatePanel, BorderLayout.SOUTH);
        receiptItemsPanel.add(centralItemsPanel, BorderLayout.CENTER);
        receiptItemsLabel = new JLabel("Receipt Items:", SwingConstants.CENTER);
        receiptItemsPanel.add(receiptItemsLabel, BorderLayout.NORTH);
        // Button to save the items extracted from the OCRed text into the database
        saveItemsButton = new JButton("Save Receipt");
        saveItemsButton.addActionListener(new SaveItemsListener());
        if (extractedText == null || extractedText.isEmpty()) {
            saveItemsButton.setEnabled(false);
        };
        receiptItemsPanel.add(saveItemsButton, BorderLayout.SOUTH);
        return receiptItemsPanel;
    }

    private JPanel createSelectQueryPanel() {
        // Make panel to display the results of querys to the database for receipts and items
        JPanel selectQueryPanel = new JPanel();
        selectQueryPanel.setLayout(new BorderLayout());

        // panel to select the time window for the query
        JPanel chooseTimePanel = new JPanel();
        chooseTimePanel.setLayout(new GridLayout(1, 2));
        JLabel queryLabel = new JLabel("Time window for receipts:", SwingConstants.CENTER);
        
        JComboBox<String> selectQueryTimeWindow = new JComboBox<String>();
        for (int i = 1; i <= 12; i++) {
            selectQueryTimeWindow.addItem(i + " months");
        }
        selectQueryTimeWindow.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            querySelectedWindow = (String) cb.getSelectedItem();
        });
        chooseTimePanel.add(queryLabel);
        chooseTimePanel.add(selectQueryTimeWindow);
        selectQueryPanel.add(chooseTimePanel, BorderLayout.NORTH);

        JPanel queryButtonsPanel = new JPanel();
        queryButtonsPanel.setLayout(new GridLayout(1, 2));
        JButton runQueryButton = new JButton("Run Query");
        runQueryButton.addActionListener(new PopulateTablesListener());
        clearTablesButton = new JButton("Clear Tables");
        clearTablesButton.addActionListener(e -> {clearTables();});
        if (receiptsTable == null || receiptsTable.getRowCount() == 0) {
            clearTablesButton.setEnabled(false);
        }
        queryButtonsPanel.add(runQueryButton);
        queryButtonsPanel.add(clearTablesButton);
        selectQueryPanel.add(queryButtonsPanel, BorderLayout.SOUTH);
        // table to display queries results for receipts and items
        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new GridLayout(2, 1));
        receiptsTable = new JTable();
        receiptsTableModel = new DefaultTableModel();
        // Receipt data fields to display in the first table
        receiptsTableModel.addColumn("Receipt ID");
        receiptsTableModel.addColumn("Date Added");
        receiptsTableModel.addColumn("Total");
        tablesPanel.add(new JScrollPane(receiptsTable));
        itemsTable = new JTable();
        itemsTableModel = new DefaultTableModel();
        // Receipt item data fields to display in the second table
        itemsTableModel.addColumn("Item ID");
        itemsTableModel.addColumn("Item Name");
        itemsTableModel.addColumn("VAT");
        itemsTableModel.addColumn("Price");
        itemsTableModel.addColumn("Receipt ID");
        tablesPanel.add(new JScrollPane(itemsTable));
        selectQueryPanel.add(tablesPanel, BorderLayout.CENTER);
        return selectQueryPanel;
    }

    private JPanel createChartsPanel() {
        // panel to display charts
        chartsPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        chartsPanel.setLayout(layout);
        // panel to display selection of items for histogram and plot charts button
        JPanel chartsCommandsPanel = new JPanel();
        chartsCommandsPanel.setLayout(new GridLayout(2, 2));
        // combo box to select the number of items to display in the histogram
        selectNumBarChartItems = new JComboBox<>();
        selectNumBarChartItems.addActionListener(e -> {
            JComboBox<Integer> cb = (JComboBox<Integer>) e.getSource();
            selectedBarCharItemsNum = (int) cb.getSelectedItem();
        });
        JLabel selectNumLabel = new JLabel("Number of frequency plot items:", SwingConstants.CENTER);
        selectNumLabel.setFont(new Font("Calibri", Font.BOLD, 16));
        // Buttons to plot charts and to clear charts
        plotChartsButton = new JButton("Plot Charts");
        plotChartsButton.addActionListener(new PlotChartListener());
        clearChartsButton = new JButton("Clear Charts");
        clearChartsButton.addActionListener(e -> {
            totalPaidChartPanel.setChart(null);
            itemsFrequencyBarChartPanel.setChart(null);
            clearChartsButton.setEnabled(false);
        });
        if (receiptsTable == null || receiptsTable.getRowCount() == 0) {
            selectNumBarChartItems.setEnabled(false);
            plotChartsButton.setEnabled(false);
            clearChartsButton.setEnabled(false);
        }
        chartsCommandsPanel.add(selectNumLabel);
        chartsCommandsPanel.add(selectNumBarChartItems);
        chartsCommandsPanel.add(plotChartsButton);
        chartsCommandsPanel.add(clearChartsButton);
        if (totalPaidChartPanel == null) {
            totalPaidChartPanel = new ChartPanel(null);
        }
        if (itemsFrequencyBarChartPanel == null) {
            itemsFrequencyBarChartPanel = new ChartPanel(null);
        }
        // Arrange the charts panel and commands panel into the gridbag layout
        c.gridheight = 50;
        c.gridy = 0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        layout.setConstraints(totalPaidChartPanel, c);
        c.gridheight = 50;
        c.gridy = 50;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        layout.setConstraints(itemsFrequencyBarChartPanel, c);
        c.gridy = 100;
        c.gridheight = 1;
        c.weighty = 0.05;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        layout.setConstraints(chartsCommandsPanel, c);
        chartsPanel.add(totalPaidChartPanel);   
        chartsPanel.add(itemsFrequencyBarChartPanel);
        chartsPanel.add(chartsCommandsPanel);
        return chartsPanel;
    }

    /* 
    End methods to create panels 
    */

    // query database for receipts
    public ArrayList<Receipt> queryForReceipts() {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        DatabaseConfig.getConnection();
        try {
            DatabaseConfig.ReceiptDAO receiptDAO = new DatabaseConfig.ReceiptDAO();
            receipts = receiptDAO.getReceiptsByDate(Integer.parseInt(querySelectedWindow.split(" ")[0]));
            if (receipts == null) {
                throw new Exception("Error retrieving receipts from database");
            }
        } catch (Exception ex) {
            System.out.println("Error running query: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error running query: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return receipts;
    }

    // query database for receipt items
    public ArrayList<Receipt.ReceiptItem> queryForReceiptItems() {
        ArrayList<Receipt> receipts = queryForReceipts();
        ArrayList<Receipt.ReceiptItem> items = new ArrayList<Receipt.ReceiptItem>();
        try {
            DatabaseConfig.ReceiptItemDAO receiptItemDAO = new DatabaseConfig.ReceiptItemDAO();
            items = new ArrayList<Receipt.ReceiptItem>();
            for (int i = 0; i < receipts.size(); i++) {
                int receiptId = receipts.get(i).getReceiptId();
                ArrayList<Receipt.ReceiptItem> oneReceiptItems = receiptItemDAO.getReceiptItemsByReceiptId(receiptId);
                oneReceiptItems.forEach(item -> item.setReceiptId(receiptId));
                items.addAll(oneReceiptItems);
            }
            System.out.println("Items retrieved successfully");
            // Display the items in a table
        } catch (Exception ex) {
            System.out.println("Error running query: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error running query: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return items;
    }

    // method to clear receipts and items table, called by pressing button or before a new query
    public void clearTables() {
        DefaultTableModel dtmReceipts = (DefaultTableModel) receiptsTable.getModel();
        dtmReceipts.setRowCount(0);
        DefaultTableModel dtmItems = (DefaultTableModel) itemsTable.getModel();
        dtmItems.setRowCount(0);
        // disable input components that use table data
        plotChartsButton.setEnabled(false);
        clearChartsButton.setEnabled(false);
        selectNumBarChartItems.setEnabled(false);  
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
            Integer parentPanelWidth = this.getParent().getWidth();
            Integer parentPanelHeight = this.getParent().getHeight();
            if (receiptImage != null && receiptImage.getWidth() > 0 && receiptImage.getHeight() > 0) {
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
            } else {
                String text = "Receipt image will be displayed here";
                FontMetrics fm = g.getFontMetrics();
                int x = (parentPanelWidth - fm.stringWidth(text)) / 2;
                int y = ((parentPanelHeight - fm.getHeight()) / 2) + fm.getAscent();
                g.drawString(text, x, y);
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
                    textArea.setEditable(true);
                    textArea.setEnabled(true);
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
                itemizeTextButton.setEnabled(true);
            } catch (Exception ex) {
                System.out.println("Error extracting text: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error extracting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
    };

    private class ItemizeTextListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try { // Crate a receipt object to extract items
                Receipt receipt = new Receipt(textArea.getText());
                receiptItemsTable.setModel(new DefaultTableModel(
                    receipt.itemsToDataArray(), 
                    new String[] {"Item", "VAT", "Price"}
                ));
                saveItemsButton.setEnabled(true);
                // save receipt total paid for display in header of receipt items table
                receiptTotalPaid = receipt.getTotalPaid();
                receiptItemsLabel.setText("Receipt items - Total Paid: " + receiptTotalPaid);
                receiptItemsLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            } catch (Exception ex) {
                System.out.println("Error extracting items: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error extracting receipt items from text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            };
        }
     }

    // Class to handle save items button click
    private class SaveItemsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Save the extracted items to the database
            System.out.println("Saving items to database");
            DatabaseConfig.getConnection();
            try {
                // Get manager objects to insert the receipt and the items into the database in the respective tables
                DatabaseConfig.ReceiptDAO receiptDAO = new DatabaseConfig.ReceiptDAO();
                receiptDAO.createTable();
                DatabaseConfig.ReceiptItemDAO receiptItemDAO = new DatabaseConfig.ReceiptItemDAO();
                receiptItemDAO.createTable();
                Receipt receipt = new Receipt(textArea.getText());
                if (customReceiptDate != null && !customReceiptDate.isEmpty()) {
                    // change receipt date from default current date value to selected custom date
                    try {
                        LocalDate parsedDate = LocalDate.parse(customReceiptDate, DateTimeFormatter.ISO_LOCAL_DATE);
                        receipt.setReceiptDate(parsedDate.toString());
                    } catch (DateTimeParseException dtpe) {
                        System.out.println("Error parsing custom receipt date: " + dtpe.getMessage());
                        JOptionPane.showMessageDialog(null, "Error parsing custom receipt date: " + dtpe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // Insert the receipt into the database
                int receiptId = receiptDAO.insertReceipt(receipt); // returns the receiptId to associate to the items
                receipt.setReceiptId(receiptId);
                if (receiptId == -1) {
                    throw new Exception("Error saving receipt to database - receiptId is -1");
                } else {
                    System.out.println("Receipt saved successfully with receiptId: " + receiptId);
                }
                // Insert the items into the database
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
    private class PopulateTablesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearTables(); // clears table before populating with new data
            System.out.println("Running query for receipts in the last " + querySelectedWindow);
            ArrayList<Receipt> receipts = queryForReceipts();
            // Display the receipts in a table
            for (Receipt receipt : receipts) {
                receiptsTableModel.addRow(new Object[] {receipt.getReceiptId(), receipt.getReceiptDate(), receipt.getTotalPaid()});
            }
            receiptsTable.setModel(receiptsTableModel);

            ArrayList<Receipt.ReceiptItem> items = queryForReceiptItems();
            // Display the items in a table
            for (Receipt.ReceiptItem item : items) {
                itemsTableModel.addRow(new Object[] {item.getId(), item.getName(), item.getVat(), item.getPrice(), item.getReceiptId()});
            }
            itemsTable.setModel(itemsTableModel);
            // set the plot charts button to enabled
            for (int i = 1; i < itemsTable.getRowCount(); i++) {
                selectNumBarChartItems.addItem(i);
            }
            // enable input components that use tables data
            clearTablesButton.setEnabled(true);
            selectNumBarChartItems.setEnabled(true);
            plotChartsButton.setEnabled(true);
        }
    }

    // Listener for plotting charts
    public class PlotChartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Plotting charts");
            // Create a chart to display the total paid for groceries over time in the selected time window
            ArrayList<Receipt> receipts = queryForReceipts();
            ArrayList<Date> dates = new ArrayList<Date>();
            ArrayList<Float> totalPaid = new ArrayList<Float>();
            // Prepare the dates and the total paid values for the chart
            for (Receipt receipt : receipts) {
                String dateString = receipt.getReceiptDate();
                Calendar calendar = Calendar.getInstance();
                String[] dateParts = dateString.split("-");
                System.out.println("Date parts: " + dateParts[0] + " " + dateParts[1] + " " + dateParts[2]);
                calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1); // months are 0-indexed in Calendar
                calendar.set(Calendar.DATE, Integer.parseInt(dateParts[2]));
                Date date = calendar.getTime();
                dates.add(date);
                totalPaid.add(receipt.getTotalPaid());
            }
            // Make charts to display in analysis tab
            // Make the grocery expenses over time chart
            TimeSeries totalPaidSeries = ChartDrawer.makeTimeSeries(dates, totalPaid);
            JFreeChart totalPaidChart = ChartDrawer.createTotalPaidChart(totalPaidSeries);
            totalPaidChartPanel.setChart(totalPaidChart);
            totalPaidChartPanel.revalidate();
            totalPaidChartPanel.repaint();
            System.out.println("Total paid chart plotted successfully");

            ArrayList<Receipt.ReceiptItem> items = queryForReceiptItems();
            ArrayList<String> itemNames = new ArrayList<String>();
            items.stream().map(item -> item.getName()).forEach(itemNames::add);
            // Create the bar chart of item purchase frequency
            JFreeChart itemsFrequencyBarChart = selectedBarCharItemsNum > 1 ? 
                ChartDrawer.createItemFrequencyBarChart(itemNames, selectedBarCharItemsNum) :
                ChartDrawer.createItemFrequencyBarChart(itemNames);
            itemsFrequencyBarChartPanel.setChart(itemsFrequencyBarChart);
            itemsFrequencyBarChartPanel.revalidate();
            itemsFrequencyBarChartPanel.repaint();
            System.out.println("Item historgram chart plotted successfully");

            chartsPanel.revalidate();
            chartsPanel.repaint();
            // enable clear charts button
            clearChartsButton.setEnabled(true);
        }
    }

    // Style class
    private class StyleFormatter {
        // Colors
        public static final Color darkGreen = Color.decode("#16821a");
        public static final Color lightGreen = Color.decode("#dbffe4");
        public static final Color darkRed = Color.decode("#6e143b");
        public static final Color lightYellow = Color.decode("#f2f1d8");
        // Fonts
        public static final Font headerFont = new Font("Calibri", Font.BOLD, 16);
        
        // Set global styles using UIManager
        private static void setGlobalStyles() {
            UIManager.put("Label.font", headerFont);
            UIManager.put("Label.foreground", lightYellow);
            UIManager.put("Label.background", darkGreen);
            UIManager.put("Label.border", new LineBorder(darkRed, 1)); // not working 
            UIManager.put("Label.opaque", true);

            UIManager.put("Button.background", lightGreen);
            UIManager.put("TextField.background", lightGreen);
            UIManager.put("CheckBox.background", lightGreen);
            UIManager.put("Panel.background", lightGreen);  // Default panel color
        }

        // Apply styles recursively
        private static void applyStylesRecursively(Container container) {
            for (Component component : container.getComponents()) {
                if (component instanceof JComponent) {
                    ((JComponent) component).updateUI();
                }
                if (component instanceof JLabel) {
                    JLabel label = (JLabel) component;
                    label.setOpaque(true);  // Ensures the background is rendered
                }
                if (component instanceof Container) {
                    applyStylesRecursively((Container) component);
                }
            }
        }
    }
    
    /*
     * End of util classes
     */
 }