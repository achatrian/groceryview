#!/bin/bash
java -Djna.library.path=/usr/local/Cellar/tesseract/5.5.0/lib \
     -cp /Users/andreachatrian/Documents/Repositories/groceryview/Tess4J/dist/tess4j-3.4.8.jar \
     @/Users/andreachatrian/Documents/Repositories/groceryview/argfile_291224.argfile \
     com.groceryview.GroceryView
