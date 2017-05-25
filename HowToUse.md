## How to use this editor
### Table of contents
1. [Command line options](#1-command-line-options)
2. [Functionality of the editor](#2-functionality-of-the-editor)
3. [Functionality of the debugger](#3-functionality-of-the-debugger)
3. [The RAM language](#4-the-ram-language)
<br><br>




#### 1. Command line options

| short option | long option | parameter | what it does |
|--------------|-------------|-----------|--------------|
| `-m` | `--enableMemDump` | `none` | prints the current content of the RAM after every change done to it |
| `-f` | `--fontSize` | `integer` | overrides the default font size of 10 (the editor and debugger window allow you to change the font size by pressing Ctrl while scrolling) |
| `-d` | `--setMethodExecDelay` | `integer` | overrides the default delay of 100 ms between executing two code lines in the debugger (slowing the program execution down makes it also a lot easier to see what it does) |
| `-h` | `--help` | `none` | shows an overview about the command line options |
#### 2. Functionality of the editor
- open and save code files using the "Datei" menu
- syntax highlighting for the RAM language functions
- autocompletition (Press Ctrl + Space to open the completition menu. One can still input text while this menu is shown)
#### 3. Functionality of the debugger
- is opened using the "Debugger" button in the editors menu bar
- fetches the latest code from the editor when debugging is started ("Debugging starten" button)
- "Abbrechen" button to interrupt the code execution (also interrupts waiting for user input)
- "Konsole leeren" button to clear the console output
- shows which command is executed (including the given paramters for the command)
- shows the memorydump after each modification of the RAM if enabled with the command line flag `-m` or `-enableMemDump`
- shows the user input
- shows error messages with the corresponding line number in your code so that you can find errors (hopefully not mine) a lot easier
- shows a basic summary of how many steps it took to execute the full code (also includes some basic summary about steps spend due to jump function calls, but this is by far not accurate at the moment)
#### 4. The RAM language
This is sort of a basic programming language using the [Random-access Machine model](https://en.wikipedia.org/wiki/Random-access_machine) (wikipedia link)
List of commands:

| command | equivalent code in other languages |
|---------|------------------------------------|
| add |  |
| add(k) | acc = acc + mem[k]; |
| add@(k) | acc = acc + mem[mem[k]]; |
| addAbs(k) | acc = acc + k; |
| sub |  |
| sub(k) | acc = acc - mem[k]; |
| sub@(k) | acc = acc - mem[mem[k]]; |
| subAbs(k) | acc = acc - k; |
| mult |  |
| mult(k) | acc = acc * mem[k]; |
| mult@(k) | acc = acc * mem[mem[k]]; |
| multAbs(k) | acc = acc * k; |
| div |  |
| div(k) | acc = acc / mem[k]; |
| div@(k) | acc = acc / mem[mem[k]]; |
| divAbs(k) | acc = acc / k; |
| load |  |
| load(k) | acc = mem[k]; |
| load@(k) | acc = mem[mem[k]]; |
| loadAbs(k) | acc = k; |
| store |  |
| store(k) | mem[k] = acc; |
| store@(k) | mem[mem[k]] = acc; |
| jumpGtz |  |
| jumpGtz(k) | if (acc > 0) { pc = mem[k]; } |
| jumpGtz@(k) | if (acc > 0) { pc = mem[mem[k]]; } |
| jumpGtzAbs(k) | if (acc > 0) { pc = k; } |
| jumpZ |  |
| jumpZ(k) | if (acc == 0) { pc = mem[k]; } |
| jumpZ@(k) | if (acc == 0) { pc = mem[mem[k]]; } |
| jumpZAbs(k) | if (acc == 0) { pc = k; } |
| I/O |  |
| read() | acc = "the next input value" |
| print() | writes acc to a file (or here the debugger console) |

explanation of names:
- `acc` is the accumulator of the RAM machine
- `pc` is the program counter (keep in mind that the program starts with line number 1)
- `mem` is the memory (the Registers of a RAM machine, basically represented by an `int[]` at this place)
- `k` is of the type `int` and is just an input parameter for the function calls
