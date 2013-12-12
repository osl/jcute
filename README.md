jCUTE
=====

The Java [Concolic Unit Testing][wp-concolic-testing] Engine (jCUTE)
automatically generates unit tests for Java programs.  Concolic
execution combines randomized concrete execution with
[symbolic execution][wp-symbolic-execution] and
[automatic constraint solving][wp-smt-solving].  Symbolic execution
allows jCUTE to discern inputs that lead down different execution
paths; randomized concrete execution helps it overcome limitations of
the constraint solver, like the inability to analyze system calls or
[solve general systems of non-linear integer equations][wp-hilbert-10th].
Through this combination, jCUTE is able to generate test cases that
execute _many_ different execution paths in _real_ Java programs.

jCUTE supports multi-threaded programs.  It can discover race
conditions and deadlocks through systematic schedule exploration.


Running
-------

jCUTE requires an installed and working Java Development Kit (JDK)
version 1.4 or later.  It expects both `java` and `javac` to be in the
`PATH`.  To run jCUTE on a 32-bit system, simply download and unzip
the binary distribution from the [jCUTE homepage][jcute].  To run
jCUTE on a 64-bit system, you will have to build the distribution
archive from the sources in this repository; see the instructions below.

* On Linux, run the `setup` script.  Then execute the `jcutegui`
  script.
* On Windows, run the `jcutegui.bat` batch file.

The `src/` directory contains a number of examples.  See the script
`runtests` to know how to run these examples.


### Usage

* `jcutec` is the instrumentor plus compiler script for distributed
  Java programs under test.
* `jcute` is the script to concolically test the executable of the
  instrumented program under test.


#### Instrumentation

Command line: `./jcutec SOURCE_DIRECTORY MAIN_JAVA_FILE MAIN_JAVA_CLASS -sequential`

* `SOURCE_DIRECTORY` is the directory containing the sources of the
  Java program to be tested
* `MAIN_JAVA_FILE` is the name of the Java source file containing the
  main function
* `MAIN_JAVA_CLASS` is the name of the class of the Java source file
  containing the main function

For example, to compile and instrument the Demo example in the
directory src/tests in the package tests, execute

`./jcutec src/ src/dtests/DExample1.java dtests.DExample1 -sequential`


#### Concolic Execution

Command line: `./jcute class -i iterations [-?] [-help] [-d DEPTH=0] [-s SEED] [-t DEBUG_LEVEL=0] [-m PATH_MODE=0] [-r] [-q] [-p] [-v] [-a] [-n ARGUMENT=0]`

Options include:

* `-help`, `-?`: Displays help information
* `-d <decimal integer>`: Depth of search. Default is 0, which implies
  infinite depth
* `-s <decimal integer>`: Seed for random number generator in case
  `-r` option is given. Default is current system time.
* `-t <decimal integer>`: Various debug information and
  statistics. Default is 0 (no debug information printed).
  * _1_: Print trace of instrumentation function call's entry and exit.
  * _2_: Print info about instrumented function call inserted for concurrency.
  * _4_: Print input map after reading from disk.
  * _8_: Print history at every history change.
  * _16_: Print symbolic state at every state change.
  * _32_: Print path constraint whenever path constraint is updated.
  * _64_: Print old and new history at the end of execution.
  * _128_: Print old and new input map at the end of the execution.
  * _256_: Print path constraint at the end of the execution.
  * _512_: Print line number executed.
* `-m <decimal integer {0,1,2}>`:  Path selection. Default is 0.
  * _0_: Next path (depends on history),
  * _1_: Replay last execution,
  * _2_: Start fresh execution without looking at any history.
* `-r`: If specified, inputs are randomly initialized; else, inputs
  are set to 0. Objects are initialized to `null` in either cases.
* `-p`: Random search strategy is invoked
* `-q`: Record branch coverage information
* `-a`: Turn off Optimal Distributed Search
* `-v`: Verbose: logs inputs and trace of execution
* `-n <decimal integer>`: Pass a single integer argument

For example, to test the Demo example run

`./jcute dtests.DExample1 -i 100 -q`

To see the statistics about branch coverage and runtime execute

`java  -classpath jcute.jar cute.concolic.BranchCoverage`


#### Graphical User Interface

In the Graphical user Interface, try selecting the directory `src/`
and the Java program `dtests/DExample1.java`.  Then "Compile" and
"(Re)start" for testing.  Click on a Run # to get the Input and Trace
log.


Building
--------

Instead of downloading the binary distribution archive, the archive
can also be built from the sources in this repository.

### Compiling the Constraint Solving Library on Linux

jCUTE uses the [`lp_solve` linear programming library][lib-lpsolve]
for constraint solving.  It comes with pre-compiled 32-bit versions of
this C library, which it accesses via the Java Native Interface (JNI).
However, the library and the wrapper were compiled against old
standard libraries (`libstdc++5.so`) that are no longer available on
modern Linux systems.  A potential solution for this is to install an
old 32-bit Linux image into a virtual machine for running jCUTE in it.
Another solution is to compile the libraries on one's system.


### 64-bit Systems

The `lp_solve` library compiles and appears to work fine on 64-bit
systems.


### Compiling lp_solve

* Download `lp_solve_5.1_source.tar.gz` from
  http://sourceforge.net/projects/lpsolve/files/lpsolve/5.1.1.3/
* Unzip the archive into a temporary directory.
* Change into the `lp_solve_5.1/lpsolve51/` sub-directory.
* Build `liblpsolve51.so` by executing `sh ccc`.
* Copy `liblpsolve51.so` into the jCUTE root directory.


### Compiling the lp_solve Java Wrapper

* Download the Java wrapper source code archive
  `lp_solve_5.1_java.zip` from http://optimum2.mii.lt/lp2jdk/
* Unzip the archive into a temporary directory.
* Change to the `lib/linux/` directory and edit the file `build`:
  - Set the `LPSOLVE_DIR` variable to the path that contains
    `lp_lib.h`; this should be the `lp_solve_5.1/` directory from
    above.
  - Tell the linker where to find `liblpsolve51.so` by adding the
    command line option `-L/path/to/containing/dir/` to the second
    `g++` invocation in the `build` file.  Replace
    `/path/to/containing/dir` with the path containing
    `liblpsolve51.so`.
  - Set the `JDK_DIR` variable to your JDK root directory, for example
    `/opt/java6/` on Arch Linux.
* Run `sh build` to build `liblpsolve51j.so`.
* Copy `liblpsolve51j.so` into the jCUTE root directory.


### Compiling and Packaging jCUTE

Execute the `package` shell script in the project's root directory to
build the jCUTE JAR file and the distribution archive: `sh package`.


License
-------

The jCUTE software is NOT in the public domain.  However, it is freely
available without fee for education, research, and non-profit purposes
as described in the complete [jCUTE license](LICENSE.txt).  The
third-party libraries used by jCUTE are licensed as described in their
license files.


History
-------

jCUTE was designed and implemented by [Koushik Sen][contact-koushik]
around 2006.


[contact-gul]: http://osl.cs.illinois.edu/members/agha.html
[contact-koushik]: http://www.cs.berkeley.edu/~ksen/ "Homepage of Koushik Sen at UC Berkeley"
[jcute]: http://osl.cs.illinois.edu/software/jcute/index.html "jCUTE Homepage"
[lib-lpsolve]: http://lpsolve.sourceforge.net/ "Homepage of the lp_solve linear programming library"
[paper-cav]: /publications/conf/cav/SenA06.html "CUTE and jCUTE: concolic unit testing and explicit path model-checking tools. By Koushik Sen and Gul Agha. In CAV, volume 4144 of Lecture Notes in Computer Science, 419–423. Springer, 2006."
[paper-fse]: /publications/conf/sigsoft/SenMA05.html "CUTE: a concolic unit testing engine for C. By Koushik Sen, Darko Marinov, and Gul Agha. In ESEC/SIGSOFT FSE, 263–272. ACM, 2005."
[paper-haifa]: /publications/conf/hvc/SenA06.html "A race-detection and flipping algorithm for automated testing of multi-threaded programs. By Koushik Sen and Gul Agha. In Haifa Verification Conference, volume 4383 of Lecture Notes in Computer Science, 166–182. Springer, 2006."
[paper-pldi]: /publications/conf/pldi/GodefroidKS05.html "Dart: directed automated random testing. By Patrice Godefroid, Nils Klarlund, and Koushik Sen. In PLDI, 213–223. ACM, 2005."
[wp-concolic-testing]: http://en.wikipedia.org/wiki/Concolic_testing "Wikipedia article on concolic testing"
[wp-hilbert-10th]: http://en.wikipedia.org/wiki/Hilbert%27s_tenth_problem "Wikipedia article on Hilbert's 10th problem"
[wp-smt-solving]: http://en.wikipedia.org/wiki/Satisfiability_Modulo_Theories "Wikipedia article on SMT solvers"
[wp-symbolic-execution]: http://en.wikipedia.org/wiki/Symbolic_execution "Wikipedia article on symbolic execution"
