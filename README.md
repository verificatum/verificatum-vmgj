# Verificatum Multiplicative Groups Library for Java (VMGJ)


## Overview

This package allows invoking GMP's modular exponentiation, including
the extension provided by the [GMP Modular Exponentiation Extension
package (GMPMEE)](https://github.com/verificatum/verificatum-gmpmee)
for simultaneous or fixed base modular exponentiation and primality
testing, from within a Java application. This drastically improves the
speed of such operations compared to pure Java implementations.

The following assumes that you are using a release. Developers should
also read `README_DEV.md`.


## Building

The source consists of both Java and C code. The Java source
essentially provides a wrapper of the functionality implemented in C
on top of GMP using Java Native Interface (JNI).

You also need to build and install the GMPMEE package before building
this package. The `LIBRARY_PATH` must point to `libgmp.la` and
`libgmpmee.a` and `C_INCLUDE_PATH` must point to `gmp.h` and
`gmpmee.h`. This is usually the case automatically after installing
GMP and GMPMEE. Then use

        ./configure
        make

to build the library.

If you prefer to use the [Clang compiler](https://clang.llvm.org) in
place of GCC for the native code, then you may use `./configure
CC=clang` instead of the above to enable it.

**Caution: Please understand that although it seems that Clang works
as well as GCC, switching compiler is a large change for mature
software.**


## Installing

1. Use

        make install

   to install the library `libvmgj-<VERSION>.{la,a,so}` and the
   jar-file `verificatum-vmgj-<VERSION>.jar` in the standard
   locations.

2. You should also make sure that the newly installed jar-file is
   found by `java` by updating your CLASSPATH, e.g., on Ubuntu, you
   can use something similar to the following snippet in your init
   script.

        export CLASSPATH=/usr/local/share/java/verificatum-vmgj-<VERSION>.jar:${CLASSPATH}

3. You need to tell the JVM where your native library, i.e.,
   `libvmgj-<VERSION>.{la,a,so}` can be found. You may either pass the
   location using the `java.library.path` property, e.g.,

        java -Djava.library.path=/usr/local/lib/

   or you can set the shell variable `LD_LIBRARY_PATH` once and for
   all in an init file, e.g.,

        export LD_LIBRARY_PATH=/usr/local/lib:${LD_LIBRARY_PATH}

4. You can test if you managed to build correctly by executing:

        make check

   This runs a set of tests.


## Benchmarks

You can run a set of benchmarks using

        make bench

Consult Makefile.am to see how these are invoked if you are interested
in customizing this for other security parameters.


## API Documentation

You may use
 
        make api

to invoke Javadoc to build the API. The API is not installed
anywhere. You can copy it to any location.


## Reporting Bugs

Minor bugs should be reported in the repository system as issues or
bugs. Security critical bugs, vulnerabilities, etc should be reported
directly to the Verificatum Project. We will make best effort to
disclose the information in a responsible way before the finder gets
proper credit.
