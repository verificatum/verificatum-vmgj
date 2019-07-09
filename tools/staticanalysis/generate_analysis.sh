#!/bin/sh

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

add_result() {

    TOOLNAME=$1
    CONTENTFILE=$2
    OUTPUTFILE=$3

    printf "\n#########################################################\n" \
>> $OUTPUTFILE
    printf "$TOOLNAME\n\n" >> $OUTPUTFILE

    CONTENT=`cat $CONTENTFILE`

    if test "x$CONTENT" = x;
    then
        printf "NO COMPLAINTS!\n" >> $OUTPUTFILE
    else
        printf "%s" "$CONTENT" >> $OUTPUTFILE
    fi
}

printf "\nCODE ANALYSIS REPORTS\n" > analysis_report.txt
add_result "Checkstyle (configured using checkstyle_ruleset.xml and checkstyle_suppressions.xml)" checkstyle/checkstyle_report.txt analysis_report.txt
add_result "Findbugs (configured using findbugs_configure.xml)" findbugs/findbugs_report.txt analysis_report.txt
add_result "PMD (configured using pmd_ruleset.xml and pmd_filter.sh)" pmd/pmd_report.txt analysis_report.txt

printf "\n"
