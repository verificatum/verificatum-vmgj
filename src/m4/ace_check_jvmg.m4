
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
