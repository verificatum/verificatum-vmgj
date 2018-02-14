
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

AC_DEFUN([ACE_PROG_JAR],[
AC_CHECK_PROG([JAR], [jar], [jar], [no])

if test $JAR = no
then
   AC_MSG_ERROR([No jar found in \$PATH. Please install JDK 6!])
fi
])
