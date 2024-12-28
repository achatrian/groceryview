package com.groceryview;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;


public class Receipt {
    public final String receiptItemPattern = "T ((?:\\w*\\s*)*)\\s(\\d{1,2},\\s{0,1}\\d\\d\\%)\\s(\\d{1,2},\\d\\d)";
    ArrayList<ReceiptItem> items;
    ArrayList<String> receiptHeader;
    ArrayList<String> receiptFooter;

    /*  
    Regular expression to extract items from a receipt, e.g. "T BANANAS 1.99% 2.99"
    First group is the item name, second group is the VAT, third group is the total price 
    */

    public Receipt(String text) {
        items = extractReceiptItems(text);
        for (ReceiptItem item : items) {
            System.out.println("Item: " + item.getName() + ", VAT: " + item.getVat() + ", Price: " + item.getPrice());
        }
    }

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

    // export items to a 2D array for display in a JTable
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
    
    /*
     * Class to represent receipt items
     */

    public class ReceiptItem {
        private String name;
        private String vat;
        private Float price;

        public ReceiptItem(String name, String vat, Float price) {
            this.name = name;
            this.vat = vat;
            this.price = price;
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

        public void setName(String name) {
            this.name = name;
        }

        public void setVat(String vat) {
            this.vat = vat;
        }

        public void setPrice(Float price) {
            this.price = price;
        }
    }

}
