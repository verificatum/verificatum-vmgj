
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

AC_DEFUN([ACE_CHECK_VMGJ],[
AC_REQUIRE([ACE_PROG_JAVA])
ace_res=$($JAVA $JAVAFLAGS -classpath $CLASSPATH TestLoadVMGJ)

echo -n "checking for vmgj.jar... "
if test "x$ace_res" = x
then
   echo "yes"
else
   echo "no"
   AC_MSG_ERROR([$ace_res Please make sure that VMGJ is installed (found at www.verificatum.org) and that your \$CLASSPATH points to the proper location. You can check your VMGJ installation using \"java vmgj.Test\". This should give you a usage description.])
fi
])
