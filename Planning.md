# How to run code:
./launch_groceryview.sh

# Plan
- Create a window for the possibility to add a receipt image and scan it for data [x]
- Create a visualization of the table data obtained from the receipt image [x]
- Create statistics to visualize once many images have been collected [x]
- Different tabs for the two windows [x]
- Analyse the receipt, extract prices and item names ...[x]
- Fix words that were badly extracted by tesseract ocr using a matching system to the dictionary []
- Different templates for receipts can also be hard-coded in, and the ocr-extracted text can be matched against these templates. []
    - For instance, the template being used now would then be the PAM template.  []
- Make sure that all environment variables can be easily set by user or set automatically []

## Charts to show
- receipt total over time
- histogram of most common receipt items and price


