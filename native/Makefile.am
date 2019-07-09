
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

ACLOCAL_AMFLAGS=-I m4

# Extracts the flags used by GMP.
GMP_CFLAGS := $(shell ./extract_GMP_CFLAGS)

# We use pedantic flags, strip the optimization flag of GMP, and
# insert our own level of optimization.
AM_CFLAGS := -O3 -Wall -W -Werror $(shell echo ${GMP_CFLAGS} | sed -e "s/-O[O12345]//")

libvmgj_la_LIBADD = -lgmp -lgmpmee

# We use -release to glue the native code and Java code together. We
# are aware that this violate common practice for library versioning.
libvmgj_la_LDFLAGS = -release $(VERSION)

# This is generated from a Java file and copied to this directory by
# the parent directory.
# include_HEADERS = com_verificatum_vmgj_VMG.h

lib_LTLIBRARIES = libvmgj.la
libvmgj_la_SOURCES = com_verificatum_vmgj_VMG.c convert.c convert.h 

dist_noinst_DATA = extract_GMP_CFLAGS.c

clean-local:
	find . -name "*~" -delete
	rm -rf compile
