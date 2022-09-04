
# Copyright 2008-2019 Douglas Wikstrom
#
# This file is part of Verificatum Multiplicative Groups library for
# Java (VMGJ).
#
# Permission is hereby granted, free of charge, to any person
# obtaining a copy of this software and associated documentation
# files (the "Software"), to deal in the Software without
# restriction, including without limitation the rights to use, copy,
# modify, merge, publish, distribute, sublicense, and/or sell copies
# of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
# BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
# ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

.PHONY: clean cleandoc

ACLOCAL_AMFLAGS=-I m4


##################### Variables used by Automake ############################

# Installation directory of jar files.
jardir = $(datadir)/java

# Distribution file.
distfile = $(PACKAGE)-$(VERSION).tar.gz


########################## Custom variables #################################

CWD = $(shell pwd)
SRC = src
PKGPATH = $(SRC)/java
MFSRC = $(SRC)/mf
JHDROUT = native
BINDIR = bin
JARFILE = $(PACKAGE)-$(VERSION).jar
VMGJ_JAR = $(jardir)/$(JARFILE)
CLASSESROOT = classes
CLASSPATH := $(CLASSESROOT)
SRC_BINDIR = $(SRC)/bin
NATLIBS = $(CWD)/native/.libs

# Code analysis
STATANA = tools/staticanalysis
CSDIR = $(STATANA)/checkstyle
FBDIR = $(STATANA)/findbugs
PMDDIR = $(STATANA)/pmd

# Coverage analysis.
COVERAGE = $(CWD)/tools/coverage
JCOVDIR = $(COVERAGE)/jcov
COBDIR = $(COVERAGE)/cobertura

# Variables pointing to external resources.
GMPMEE_VERSION = $(shell gmpmee-info version)
JCOVJARDIR = $(HOME)/jcov_2.0
COBHOME = $(HOME)/cobertura-2.1.1
COBCLASSPATH=$(shell find $(COBHOME) | grep "\.jar" | grep -E "cobertura.*/cobertura-[^-]*\.jar|asm-|slf4j-|logback-|oro-"| tr "\n" ":")
JDK_DOC = "http://docs.oracle.com/javase/8/docs/api"

# Java sources.
JAVA_FILES := $(PKGPATH)/com/verificatum/vmgj/VMG.java \
$(PKGPATH)/com/verificatum/vmgj/TestVMG.java \
$(PKGPATH)/com/verificatum/vmgj/BenchVMG.java \
$(PKGPATH)/com/verificatum/vmgj/FpowmTab.java \
$(PKGPATH)/com/verificatum/vmgj/MillerRabin.java


########################### Compilation #####################################

all: headers.stamp

$(PKGPATH)/com/verificatum/vmgj/VMG.java: $(PKGPATH)/com/verificatum/vmgj/VMG.magic scriptmacros.m4
	cat scriptmacros.m4 $(PKGPATH)/com/verificatum/vmgj/VMG.magic | m4 > $(PKGPATH)/com/verificatum/vmgj/VMG.java

# Create the destination directory.
classesroot.stamp:
	mkdir -p $(CLASSESROOT)
	@touch classesroot.stamp

# Jar manifest generated with current version.
$(CLASSESROOT)/MANIFEST.MF: $(MFSRC)/MANIFEST.MF.src
	cat .macros.m4 $(MFSRC)/MANIFEST.MF.src | m4 > $(CLASSESROOT)/MANIFEST.MF

# Compile Java sources.
classes.stamp: classesroot.stamp $(JAVA_FILES)
	$(JAVAC) -Xlint:unchecked $(AM_JAVACFLAGS) $(JAVACFLAGS) -d $(CLASSESROOT) $(JAVA_FILES)
	@touch classes.stamp

# Build JAR file.
$(JARFILE): classes.stamp $(CLASSESROOT)/MANIFEST.MF
	cd $(CLASSESROOT); $(JAR) cfm $@ MANIFEST.MF com; cp $@ ..

# Generate header files for native code.
headers.stamp: $(JARFILE)
	$(JAVAC) -h $(JHDROUT) $(PKGPATH)/com/verificatum/vmgj/VMG.java
	@rm -f $(PKGPATH)/com/verificatum/vmgj/VMG.class
	@touch headers.stamp

scriptmacros.m4:
	@printf "define(M4_VERSION, $(VERSION))dnl\n" > scriptmacros.m4
	@printf "define(M4_GMPMEE_VERSION, $(GMPMEE_VERSION))dnl\n" >> scriptmacros.m4
	@printf "define(M4_BINDIR, $(bindir))dnl\n" >> scriptmacros.m4
	@printf "define(M4_LIBDIR, $(libdir))dnl\n" >> scriptmacros.m4
	@printf "define(M4_VMGJ_JAR, $(VMGJ_JAR))dnl\n" >> scriptmacros.m4

$(BINDIR)/vmgj-$(VERSION)-info: scriptmacros.m4 $(SRC_BINDIR)/vmgj-info.src
	mkdir -p $(BINDIR)
	cat scriptmacros.m4 $(SRC_BINDIR)/vmgj-info.src | m4 > $(BINDIR)/vmgj-$(VERSION)-info
	chmod +x $(BINDIR)/vmgj-$(VERSION)-info

# Variables with special meaning in Autotools.
SUBDIRS = . native

# Variables that determines which files are part of the distribution
# and how they are installed.
jar_DATA = $(JARFILE)
dist_noinst_DATA = $(SRC) README.md LICENSE NEWS AUTHORS ChangeLog .version.m4 .macros.m4
dist_bin = $(BINDIR)/vmgj-$(VERSION)-info
dist_bin_SCRIPTS = $(BINDIR)/vmgj-$(VERSION)-info


########################## Static analysis ##################################

# Edit the wrappers in the subdirectories of $(STATANA) if needed.

# Do a Checkstyle analysis of the source and save the result in
# $(CSDIR)/checkstyle_report.txt.
checkstyle: $(CSDIR)/checkstyle_report.txt $(CSDIR)/checkstyle_filter.sh
$(CSDIR)/checkstyle_report.txt: headers.stamp
	rm -f $(CSDIR)/checkstyle_report_tmp.txt
	cd $(CSDIR); ./checkstyle_wrapper checkstyle_configure.xml checkstyle_raw_report.txt $(CWD)/$(PKGPATH); ./checkstyle_filter.sh checkstyle_raw_report.txt checkstyle_report.txt

# Do a Findbugs analysis of the jar and save the result in
# $(FBDIR)/findbugs_report.txt.
findbugs: $(FBDIR)/findbugs_report.txt
$(FBDIR)/findbugs_report.txt: headers.stamp
	./$(FBDIR)/findbugs_wrapper -textui -exclude $(FBDIR)/findbugs_configure.xml $(JARFILE) > $(FBDIR)/findbugs_report.txt

# Do a PMD analysis of the source and save the result in
# $(PMDDIR)/pmd_report.txt. PMD does not provide any proper way of
# installation. The $(PMDDIR)/pmd_wrapper tries to find the directory
# named pmd-bin-?-?-? for the most recent version. If no such
# directory exists, then it tries the same in the home directory. Edit
# the wrapper if needed.
pmd: $(PMDDIR)/pmd_report.txt
$(PMDDIR)/pmd_report.txt: headers.stamp
	./$(PMDDIR)/pmd_wrapper -rulesets $(PMDDIR)/pmd_ruleset.xml -d $(PKGPATH) | sed "s/.*verificatum\/\(com\/verificatum.*\)/\1/p" | uniq > $(PMDDIR)/pmd_raw_report.txt
	cd $(PMDDIR); ./pmd_filter.sh pmd_raw_report.txt pmd_report.txt

# Execute all static analyzers.
analysis: checkstyle findbugs pmd $(STATANA)/generate_analysis.sh
	cd $(STATANA); ./generate_analysis.sh
	cat $(STATANA)/analysis_report.txt


########################## Coverage analysis ################################

# Set JCOVJARDIR above to use this.
jcov: headers.stamp
	mkdir -p $(JCOVDIR)
	cd $(JCOVDIR); rm -f filtered.xml
	cp $(JARFILE) $(JCOVDIR)/
	cd $(JCOVDIR); java -jar $(JCOVJARDIR)/jcov.jar Instr -t template.xml -e com.verificatum.vmgj.TestVMG -e com.verificatum.vmgj.BenchVMG -field on -native off $(JARFILE)
	cd $(JCOVDIR); java -ea -Djava.library.path=$(NATLIBS):$(libdir) -classpath $(JCOVJARDIR)/jcov_file_saver.jar:$(JARFILE) com.verificatum.vmgj.TestVMG 1024
	cd $(JCOVDIR); java -jar $(JCOVJARDIR)/jcov.jar RepGen result.xml

# Set COBHOME above to use this. A recent version is required. After
# downloading Cobertura you need to convert the .sh files to Unix
# format with dos2unix and make them executable before running.
cobertura: headers.stamp
	rm -rf $(COBDIR)
	rm -f cobertura.ser
	mkdir -p $(COBDIR)/classes
	$(COBHOME)/cobertura-instrument.sh --ignore "java.lang.Error" --destination $(COBDIR)/classes $(CLASSESROOT)/com/verificatum/vmgj/VMG.class $(CLASSESROOT)/com/verificatum/vmgj/FpowmTab.class $(CLASSESROOT)/com/verificatum/vmgj/MillerRabin.class
	java -ea -Djava.library.path=$(NATLIBS):$(libdir) -classpath $(COBCLASSPATH):$(COBDIR)/classes:$(CLASSESROOT) com.verificatum.vmgj.TestVMG 1024
	$(COBHOME)/cobertura-report.sh --srcdir /home/dog/verificatum/vmgj/src/java/ --destination $(COBDIR)/report --format html $(COBDIR)/classes

coverage: jcov cobertura


########################### Usage targets ###################################

check: headers.stamp
	LD_LIBRARY_PATH=$(LD_LIBRARY_PATH):$(libdir) java -ea -Djava.library.path=$(NATLIBS):$(libdir) -classpath $(JARFILE):$(jardir)/$(JARFILE) com.verificatum.vmgj.TestVMG

bench: headers.stamp
	LD_LIBRARY_PATH=$(LD_LIBRARY_PATH):$(libdir) java -Djava.library.path=$(NATLIBS):$(libdir) -classpath $(JARFILE):$(jardir)/$(JARFILE) com.verificatum.vmgj.BenchVMG

api: headers.stamp
	rm -rf api
	javadoc -d api -link $(JDK_DOC) $(JAVA_FILES)


########################## Cleaning targets #################################

# Autoconf variable for files to be cleaned.
CLEANFILES = *.stamp $(JARFILE) $(distfile) $(JHDROUT)/com_verificatum_vmgj_VMG.h scriptmacros.m4 $(PKGPATH)/com/verificatum/vmgj/VMG.java

cleanapi:
	rm -rf api .libs

cleancheckstyle:
	rm -rf $(CSDIR)/checkstyle_raw_report.txt $(CSDIR)/checkstyle_report.txt

cleanfindbugs:
	rm -rf $(FBDIR)/findbugs_report.txt

cleanpmd:
	rm -rf $(PMDDIR)/pmd_report.txt $(PMDDIR)/pmd_raw_report.txt

cleananalysis: cleancheckstyle cleanfindbugs cleanpmd
	rm -rf $(STATANA)/analysis_report.txt

cleanjcov:
	rm -rf $(JCOVDIR)

cleancobertura:
	rm -rf $(COBDIR) cobertura.ser

cleancoverage: cleanjcov cleancobertura

clean-local: cleanapi cleananalysis cleancoverage
	find . -name "*~" -delete
	rm -rf report result.xml template.xml $(CLASSESROOT) compile $(BINDIR)/vmgj-$(VERSION)-info
