
# Copyright 2008-2018 Douglas Wikstrom
#
# This file is part of Verificatum Multiplicative Groups library for
# Java (VMGJ).
#
# VMGJ is free software: you can redistribute it and/or modify it
# under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# VMGJ is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General
# Public License for more details.
#
# You should have received a copy of the GNU Affero General Public
# License along with VMGJ. If not, see <http://www.gnu.org/licenses/>.

#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.63])
AC_INIT([verificatum-vmgj],
        [m4_esyscmd([m4 .version.m4])],
        [info@verificatum.com])
AC_CANONICAL_SYSTEM # Must appear before AM_INIT_AUTOMAKE
AM_INIT_AUTOMAKE([tar-ustar -Wall -Werror -Wno-portability])
AC_CONFIG_MACRO_DIR([m4])

# Checks for programs.
ACE_PROG_JAVA
ACE_PROG_JAVAC
ACE_PROG_JAVAH
ACE_PROG_JAR
ACE_PROG_JAVADOC

AC_CONFIG_SUBDIRS([native])

AM_CONDITIONAL([BITS64], [test x`getconf LONG_BIT` = x64])

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
