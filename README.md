# Groceryview

Application to keep track of one's spending at the supermarket by scanning receipts and memorizing items in a database.
The app can produce simple charts to display trends in one's purchase of groceries.
The user is required to take a picture of their supermarket receipts (for instance one could use the laptop webcam to take the pictures).
Optical character recognition is then used to scan the receipt picture for grocery items.
The receipt data (i.e. the total paid and date) and the items data is stored in a sqlite database.


## Launching the app through java command line:
Launch the app by running the maven shaded JAR file:
`java  -jar ./groceryview/target/groceryview-0.0.jar`

## App view:
### Analysis tab
- Analyse the results:
- Query the database for receipts and the grocery items in a certain time-window
- Display receipt total paid over time in a time-series chart
- Display most frequent items in receipts (can select how many items to display in the chart)
![alt text](analysis_tab.png)

### Scan tab
- Scan your receipt:
- Upload an image of your receipt
- Use optical character recognition to extract text from the image
- Corrext errors in case it's needed
- Save the receipt items to the database
![alt text](scan_tab.png)

## Fixes:
Launching the app requires installation of `libtesseract.dylib`, and place it in an appropriate folder so it can be found by the tesseract library:
- Install it with homebrew:
    `brew install tesseract`
- On mac one can copy it to:
    `cp /usr/local/Cellar/tesseract/5.5.0/lib /Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home/bin/./libtesseract.dylib`
    or to:
    `cp /usr/local/Cellar/tesseract/5.5.0/lib /System/Library/Frameworks/tesseract.framework/tesseract`