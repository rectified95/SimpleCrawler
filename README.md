# SimpleCrawler
This program can access a given URL and count the number of links to various domains it points to.
## Running instructions
### Option 1 - import as Maven project to an IDE of choice and run from there
  Supply the URL as a command line arg in your IDE (in Intellij IDEA, look under 'Edit Configurations' in the run taskbar)
### Option 2 - command line launch
  Sample launch command: 
  
    mvn package
    mvn exec:java -Dexec.args="http://github.com"

