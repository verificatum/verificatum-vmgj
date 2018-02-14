
/*
 * Copyright 2008-2018 Douglas Wikstrom
 *
 * This file is part of Verificatum Multiplicative Groups library for
 * Java (VMGJ).
 *
 * VMGJ is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * VMGJ is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with VMGJ. If not, see
 * <http://www.gnu.org/licenses/>.
 */

#include <jni.h>

#ifndef _convert
#define _convert
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Translates the representation of a positive integer given as a
 * jbyteArray in two's complement representation into its
 * representation as a GMP mpz_t element. It initializes gmpValue, so
 * it should point to an uninitialized variable before the call.
 */
void
jbyteArray_to_mpz_t(JNIEnv* env, mpz_t* gmpValue, jbyteArray javaBytes);

/*
 * Translates the representation of a positive integer given as a GMP
 * mpz_t element into its representation as a two's complement in a
 * jbyteArray. It allocates a jbyteArray in JVM memory space, so it
 * should be uninitialized before the call.
 */
void
mpz_t_to_jbyteArray(JNIEnv* env, jbyteArray* javaBytes, mpz_t gmpValue);

#ifdef __cplusplus
}
#endif
#endif
