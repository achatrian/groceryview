package com.groceryview;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;


public class Receipt {
    public final String receiptItemPattern = "T ((?:\\w*\\s*)*)\\s(\\d{1,2},\\s{0,1}\\d\\d\\%)\\s(\\d{1,2},\\d\\d)";
    public final String totalPaidPattern = "(?:TOTALE COMPLESSIVO|[Ii]mporto [Pp]agato)\\s*(\\d{1,2}[,.]\\d{1,2})";
    private ArrayList<ReceiptItem> items; // list of items on the receipt
    private ArrayList<String> receiptHeader; // lines of text coming before the first receipt item in the text
    private ArrayList<String> receiptFooter; // lines of text following the last receipt item in the text
    private Float totalPaid; // sum amount of the receipt items prices + any additional costs
    private String receiptDate; // field to store the date the receipt was created
    private int receiptId; // field to store the id of the receipt in the database
    
    /*  
    Regular expression to extract items from a receipt, e.g. "T BANANAS 1.99% 2.99"
    First group is the item name, second group is the VAT, third group is the total price 
    */

    // Empty constructor used to make receipts that will be filled with data later
    public Receipt() {
        items = new ArrayList<ReceiptItem>();
        totalPaid = null;
        receiptDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        receiptId = -1;
    }

    // Constructor that takes a string of text and extracts the items and total paid
    public Receipt(String text) {
        items = extractReceiptItems(text);
        for (ReceiptItem item : items) {
            System.out.println("Item: " + item.getName() + ", VAT: " + item.getVat() + ", Price: " + item.getPrice());
        }
        totalPaid = extractTotalPaid();
        if (totalPaid != null) {
            System.out.println("Total Paid: " + totalPaid);
        }
        receiptDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("Receipt Date: " + receiptDate);

        receiptId = -1;
    }

    // Extract the items from the text by matching against stored patterns
    public  ArrayList<ReceiptItem> extractReceiptItems(String text) {
        Pattern pattern = Pattern.compile(receiptItemPattern);
        String[] lineArray = text.split("\n");
        ArrayList<ReceiptItem> items = new ArrayList<ReceiptItem>();
        Boolean firstMatch = false; // all items are in a sequence, so we can use this to store the header and footer of the receipt
        receiptHeader = new ArrayList<String>();
        receiptFooter = new ArrayList<String>();
        for (String line : lineArray) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) { // this skips all the lines where nothing was found
                if (firstMatch == null) {
                    firstMatch = true;
                }
                System.out.println("Line: " + line);
                System.out.println("Group count: " + matcher.groupCount());
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + matcher.group(i));
                }
                ReceiptItem item = new ReceiptItem(
                    // group 0 is the whole region that matched the pattern (?)
                    matcher.group(1),
                    matcher.group(2).replace(",", "."), // ITA to ENG decimal separator
                    Float.parseFloat(matcher.group(3).replace(",", "."))
                );
                items.add(item);
            } else { // store extra lines before and after the items in arraylists
                if (firstMatch == true) {
                    receiptHeader.add(line);
                } else {
                    receiptFooter.add(line);
                }
            }
        }
        return items;
    }

    // Extract the total paid sum from the receipt text
    public Float extractTotalPaid() {
        Pattern pattern = Pattern.compile(totalPaidPattern);
        for (String line : receiptFooter) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String totalPaidStr = matcher.group(1).replace(",", ".");
                System.out.println("Total Paid: " + totalPaidStr);
                return Float.parseFloat(totalPaidStr);
            }
        }
        System.out.println("Total Paid not found");
        return null;
    }

    // Export items to a 2D array for display in a JTable
    public String[][] itemsToDataArray() {
        String[][] data = new String[items.size()][3];
        for (int i = 0; i < items.size(); i++) {
            ReceiptItem item = items.get(i);
            data[i][0] = item.getName();
            data[i][1] = item.getVat();
            data[i][2] = String.valueOf(item.getPrice());
        }
        return data;
    }

    // getters and setters
    public Float getTotalPaid() {
        return totalPaid;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public ArrayList<ReceiptItem> getItems() {
        return items;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setTotalPaid(Float totalPaid) {
        this.totalPaid = totalPaid;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
        for (ReceiptItem item : items) {
            item.setReceiptId(receiptId);
        }
    }
    // end of getters and setters
    
    /*
     * Class to represent receipt items
     */

    public class ReceiptItem {
        private int id; // unique identifier to separate equal items from different receipts
        private String name;
        private String vat;
        private Float price;
        private int receiptId; // stores the unique receipt identifier to trace back item to receipt

        public ReceiptItem(String name, String vat, Float price) {
            this.id = -1;
            this.name = name;
            this.vat = vat;
            this.price = price;
            this.receiptId = -1;
        }

        public ReceiptItem(int id, String name, String vat, Float price, int receiptId) {
            this.id = id;
            this.name = name;
            this.vat = vat;
            this.price = price;
            this.receiptId = receiptId;
        }

        // getters and setters
        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getVat() {
            return this.vat;
        }

        public Float getPrice() {
            return this.price;
        }

        public int getReceiptId() {
            return this.receiptId;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setVat(String vat) {
            this.vat = vat;
        }

        public void setPrice(Float price) {
            this.price = price;
        }

        public void setReceiptId(int receiptItemId) {
            this.receiptId = receiptItemId;
        }
        // end getter and setters
    }
}
