# RAMEditor
just a small editor and debugger for some pseudo programming language

to build this project, one needs:
- [the Apache Commons-Cli Library (I used v1.4)](https://commons.apache.org/proper/commons-cli/)
- The jxtextpane Java project
  - [either the original from Yann Richet](https://github.com/yannrichet/jxtextpane)
  - [or my modified version which contains bugfixes for some things I came across while building the RAMEditor](https://github.com/hofma103/jxtextpane)

## Using this code
- where to add further syntax highlighting entries:
  - in general: [EditorPanel.java](src/de/unipassau/fim/hofma103/EditorPanel.java#L85)
  - to the console output: [Debugger.java](src/de/unipassau/fim/hofma103/Debugger.java#L85)
- where to add further help menu entries:
  - [EditorPanel.java](src/de/unipassau/fim/hofma103/EditorPanel.java#L121)
  
- new functions need to be added to the [RAMMachine.java](src/de/unipassau/fim/hofma103/RAMMachine.java)
  - The functions do right now all need one int parameter
  - if the methods used by the programming language contain any characters which are not allowed for funtion names, one needs to replace them in [RAMMachine.inputCode(ArrayList<String> code)](src/de/unipassau/fim/hofma103/RAMMachine.java#L29) so that the method name matches your implementations name
  - to show the correct console output, one should revert the replacing in [RAMMachine.processCode()](src/de/unipassau/fim/hofma103/RAMMachine.java#L53)
