
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

#include <gmp.h>
#include "convert.h"

void
jbyteArray_to_mpz_t(JNIEnv* env, mpz_t* gmpValue, jbyteArray javaBytes)
{

  jsize byte_len;
  jbyte *cBytes;
  mpz_t tmp;

  /* Find length in bytes of the jbyteArray. */
  byte_len = (*env)->GetArrayLength(env, javaBytes);

  /* Fetch a pointer to the jbyteArray, viewed as a jbyte[]. The NULL
     parameter indicates that we do not need to know if the JVM copies
     the bytes for us to a new array or not. */
  cBytes = (*env)->GetByteArrayElements(env, javaBytes, NULL);

  /* Allocate space for result. */
  mpz_init(*gmpValue);

  /* Execute unsigned conversion. */
  mpz_import(*gmpValue,            /* Resulting mpz_t */
             byte_len,             /* Number of words */
             1,                    /* Most significant word first */
             1,                    /* Number of bytes in each word (is one) */
             1,                    /* Most signif. byte of each word first */
             0,                    /* No. ignored leading bits of words */
             (void*)cBytes);       /* Source of the bytes */

  /* If the original integer is negative, then the jbyteArray
     corresponds to 2^n-gmpValue, where n/8 is the number of bytes in
     jbyteArray integer. Thus, we must subtract 2^n. */
  if (cBytes[0] & 0x80)
    {
      mpz_init(tmp);
      mpz_setbit(tmp, 8 * byte_len);
      mpz_sub(*gmpValue, *gmpValue, tmp);
      mpz_clear(tmp);
    }

  /* Release our handle to the jbyteArray. JNI_ABORT indicates that we
     do not require that the jbyteArray is copied back into JVM
     memory, even if the JVM has a separate native memory space. */
  (*env)->ReleaseByteArrayElements(env, javaBytes, cBytes, JNI_ABORT);
}

void mpz_t_to_jbyteArray(JNIEnv* env, jbyteArray* javaBytes, mpz_t gmpValue)
{

  size_t byte_len;
  jbyte* cBytes;
  mpz_t tmp;

  /* Find length in bytes of the GMP mpz_t element. We add an
     additional leading byte where the sign of the integer is
     encoded. */
  byte_len = (mpz_sizeinbase(gmpValue, 2) + 7) / 8 + 1;

  /* Allocate a new java byte array in JVM space. */
  *javaBytes = (*env)->NewByteArray(env, byte_len);

  /* Fetch a pointer to the java byte array, viewed as a jbyte
     array. */
  cBytes = (*env)->GetByteArrayElements(env, *javaBytes, NULL);

  /* If the integer gmpValue is negative we add the smallest integer
     of the form 2^n such that 2^n > |gmpValue| and n is a multiple of
     8. */
  if (mpz_sgn(gmpValue) < 0)
    {
      mpz_init(tmp);
      mpz_setbit(tmp, 8 * byte_len);
      mpz_add(tmp, tmp, gmpValue);
      cBytes[0] = (jbyte)0xff;

      /* Execute unsigned conversion. */
      mpz_export((void*)&(cBytes[1]),
                 &byte_len,
                 1,                 /* Most significant word first */
                 1,                 /* Number of bytes in each word (is one) */
                 1,                 /* Most signif. byte of each word first */
                 0,                 /* No. ignored leading bits of words */
                 tmp);              /* Source of the bytes */
      mpz_clear(tmp);
    }
  else
    {
      cBytes[0] = (jbyte)0x00;

      /* Execute unsigned conversion. */
      mpz_export((void*)&(cBytes[1]),
                 &byte_len,
                 1,                 /* Most significant word first */
                 1,                 /* Number of bytes in each word (is one) */
                 1,                 /* Most signif. byte of each word first */
                 0,                 /* No. ignored leading bits of words */
                 gmpValue);         /* Source of the bytes */
    }


  /* Release our handle to the java bytes. Force "copy" of the bytes
     into JVM memory space only if needed. */
  (*env)->ReleaseByteArrayElements(env, *javaBytes, cBytes, 0);
}
