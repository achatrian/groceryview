# How to run code:
./launch_groceryview.sh

# Plan
- Create a window for the possibility to add a receipt image and scan it for data
- Create a visualization of the table data obtained from the receipt image
- Create statistics to visualize once many images have been collected
- Different tabs for the two windows
- Analyse the receipt, extract prices and item names ...
- Fix words that were badly extracted by tesseract ocr using a matching system to the dictionary
- Different templates for receipts can also be hard-coded in, and the ocr-extracted text can be matched against these templates.
    - For instance, the template being used now would then be the PAM template.  

## Charts to show
- receipt total over time
- histogram of most common receipt items and price


# Info;
argfiles are created by maven containing all the dependencies
- TODO: install Tess4J jar as a local maven dependancy (since the standard pom installation is not working)

