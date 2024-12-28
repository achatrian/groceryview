# How to run code:
java -Djna.library.path=/usr/local/Cellar/tesseract/5.5.0/lib -cp /Users/andreachatrian/groceryview/Tess4J/dist/tess4j-3.4.8.jar @Users/andreachatrian/groceryview/argfile_261224.argfile com.groceryview.GroceryView

<!-- info about argfiles by chatgpt -->
<!-- The @/var/.../cp_*.argfile in your Java command is a file reference created by Visual Studio Code (VSCode) or other build tools to handle long command-line arguments. It contains the list of classpath entries (-cp) or other JVM arguments that would otherwise make the command too long for the operating system's command-line length limit.

Key Points:
Location of the .argfile:

The file is stored in a temporary directory (in your case, /var/folders/...), and its exact path and name may vary between executions.
The OS (macOS, in your case) manages this directory for temporary files.
Contents of .argfile:

The .argfile contains arguments that would normally be passed directly in the command-line invocation. For example, it might include:
All the JARs or directories required for the classpath (-cp).
Other JVM arguments or system properties. -->

# Plan
- Create a window for the possibility to add a receipt image and scan it for data
- Create a visualization of the table data obtained from the receipt image
- Create statistics to visualize once many images have been collected
- Different tabs for the two windows
- Analyse the receipt, extract prices and item names ...


