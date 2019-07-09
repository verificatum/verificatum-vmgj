
/* Copyright 2008-2019 Douglas Wikstrom
 *
 * This file is part of Verificatum Multiplicative Groups library for
 * Java (VMGJ).
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

#include <stdio.h>
#include <stdlib.h>

#include <gmp.h>
#include "gmpmee.h"
#include "convert.h"
#include <stdio.h>
/*
 * We use compiler flags that enforce that unused variables are
 * flagged as errors. Here we are forced to use a given API, so we
 * need to explicitly trick the compiler to not issue an error for
 * those parameters that we do not use.
 */
#define VMGJ_UNUSED(x) ((void)(x))

#ifdef __cplusplus
extern "C" {
#endif

  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    powm
   * Signature: ([B[B[B)[B
   */
  JNIEXPORT jbyteArray JNICALL Java_com_verificatum_vmgj_VMG_powm
  (JNIEnv *env, jclass clazz, jbyteArray javaBasis, jbyteArray javaExponent,
   jbyteArray javaModulus)
  {

    mpz_t basis;
    mpz_t exponent;
    mpz_t modulus;
    mpz_t result;

    jbyteArray javaResult;

    VMGJ_UNUSED(clazz);

    /* Translate jbyteArray-parameters to their corresponding GMP
       mpz_t-elements. */
    jbyteArray_to_mpz_t(env, &basis, javaBasis);
    jbyteArray_to_mpz_t(env, &exponent, javaExponent);
    jbyteArray_to_mpz_t(env, &modulus, javaModulus);

    /* Compute modular exponentiation. */
    mpz_init(result);

    mpz_powm(result, basis, exponent, modulus);

    /* Translate result back to jbyteArray (this also allocates the
       result array on the JVM heap). */
    mpz_t_to_jbyteArray(env, &javaResult, result);

    /* Deallocate resources. */
    mpz_clear(result);
    mpz_clear(modulus);
    mpz_clear(exponent);
    mpz_clear(basis);

    return javaResult;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    spowm
   * Signature: ([[B[[B[B)[B
   */
  JNIEXPORT jbyteArray JNICALL Java_com_verificatum_vmgj_VMG_spowm
  (JNIEnv *env, jclass clazz, jobjectArray javaBases,
   jobjectArray javaExponents, jbyteArray javaModulus)
  {

    int i;
    mpz_t *bases;
    mpz_t *exponents;
    mpz_t modulus;
    mpz_t result;

    jbyteArray javaResult;
    jbyteArray javaBase;
    jbyteArray javaExponent;

    /* Extract number of bases/exponents. */
    jsize numberOfBases = (*env)->GetArrayLength(env, javaBases);

    VMGJ_UNUSED(clazz);

    /* Convert exponents represented as array of byte[] to array of
       mpz_t. */
    bases = gmpmee_array_alloc(numberOfBases);
    for (i = 0; i < numberOfBases; i++)
      {
        javaBase = (jbyteArray)(*env)->GetObjectArrayElement(env, javaBases, i);
        jbyteArray_to_mpz_t(env, &(bases[i]), javaBase);
      }

    /* Convert exponents represented as array of byte[] to an array of
       mpz_t. */
    exponents = gmpmee_array_alloc(numberOfBases);
    for (i = 0; i < numberOfBases; i++)
      {
        javaExponent =
          (jbyteArray)(*env)->GetObjectArrayElement(env, javaExponents, i);
        jbyteArray_to_mpz_t(env, &(exponents[i]), javaExponent);
      }

    /* Convert modulus represented as a byte[] to a mpz_t. */
    jbyteArray_to_mpz_t(env, &modulus, javaModulus);

    /* Call GMP's exponentiated product function. */
    mpz_init(result);
    gmpmee_spowm(result, bases, exponents, numberOfBases, modulus);

    /* Convert result to a jbyteArray. */
    mpz_t_to_jbyteArray(env, &javaResult, result);

    /* Deallocate resources. */
    mpz_clear(result);
    mpz_clear(modulus);
    gmpmee_array_clear_dealloc(exponents, numberOfBases);
    gmpmee_array_clear_dealloc(bases, numberOfBases);

    return javaResult;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    fpowm_precomp
   * Signature: ([B[BII)J
   */
  JNIEXPORT jlong JNICALL Java_com_verificatum_vmgj_VMG_fpowm_1precomp
  (JNIEnv *env, jclass clazz, jbyteArray javaBasis, jbyteArray javaModulus,
   jint javaBlockWidth, jint javaExponentBitlen)
  {
    mpz_t basis;
    mpz_t modulus;
    gmpmee_fpowm_tab *tablePtr =
      (gmpmee_fpowm_tab *)malloc(sizeof(gmpmee_fpowm_tab));

    VMGJ_UNUSED(clazz);

    jbyteArray_to_mpz_t(env, &basis, javaBasis);
    jbyteArray_to_mpz_t(env, &modulus, javaModulus);

    gmpmee_fpowm_init_precomp(*tablePtr, basis, modulus,
                              (int)javaBlockWidth, (int)javaExponentBitlen);
    mpz_clear(modulus);
    mpz_clear(basis);

    return (jlong)(long)tablePtr;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    fpowm
   * Signature: (J[B)[B
   */
  JNIEXPORT jbyteArray JNICALL Java_com_verificatum_vmgj_VMG_fpowm
  (JNIEnv *env, jclass clazz, jlong javaTablePtr, jbyteArray javaExponent)
  {
    mpz_t exponent;
    mpz_t result;

    jbyteArray javaResult;

    VMGJ_UNUSED(clazz);

    jbyteArray_to_mpz_t(env, &exponent, javaExponent);
    mpz_init(result);

    gmpmee_fpowm(result, *(gmpmee_fpowm_tab *)(long)javaTablePtr, exponent);

    /* Translate result back to jbyteArray (this also allocates the
       result array on the JVM heap). */
    mpz_t_to_jbyteArray(env, &javaResult, result);

    mpz_clear(result);
    mpz_clear(exponent);

    return javaResult;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    fpowm_clear
   * Signature: (J)V
   */
  JNIEXPORT void JNICALL Java_com_verificatum_vmgj_VMG_fpowm_1clear
  (JNIEnv *env, jclass clazz, jlong javaTablePtr)
  {
    VMGJ_UNUSED(env);
    VMGJ_UNUSED(clazz);
    gmpmee_fpowm_clear(*(gmpmee_fpowm_tab *)(long)javaTablePtr);
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    legendre
   * Signature: ([B[B)I
   */
  JNIEXPORT jint JNICALL Java_com_verificatum_vmgj_VMG_legendre
  (JNIEnv *env, jclass clazz, jbyteArray javaOp, jbyteArray javaOddPrime)
  {
    mpz_t op;
    mpz_t oddPrime;
    int symbol;

    VMGJ_UNUSED(clazz);

    jbyteArray_to_mpz_t(env, &op, javaOp);
    jbyteArray_to_mpz_t(env, &oddPrime, javaOddPrime);

    symbol = mpz_legendre(op, oddPrime);

    mpz_clear(op);
    mpz_clear(oddPrime);

    return (jint)symbol;
  }

  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_init
   * Signature: ([BZ)J
   */
  JNIEXPORT jlong JNICALL Java_com_verificatum_vmgj_VMG_millerrabin_1init
  (JNIEnv *env, jclass clazz, jbyteArray javaN, jboolean search)
  {
    mpz_t n;
    gmpmee_millerrabin_state *statePtr = (void*)0;

    VMGJ_UNUSED(clazz);

    jbyteArray_to_mpz_t(env, &n, javaN);

    if (search || gmpmee_millerrabin_trial(n)) {
      statePtr =
        (gmpmee_millerrabin_state *)malloc(sizeof(gmpmee_millerrabin_state));
      gmpmee_millerrabin_init(*statePtr, n);
    }
    if (search) {
      gmpmee_millerrabin_next_cand(*statePtr);
    }

    mpz_clear(n);

    return (jlong)(long)statePtr;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_next_cand
   * Signature: (J)V
   */
  JNIEXPORT void JNICALL Java_com_verificatum_vmgj_VMG_millerrabin_1next_1cand
  (JNIEnv *env, jclass clazz, jlong javaStatePtr)
  {
    VMGJ_UNUSED(env);
    VMGJ_UNUSED(clazz);
    gmpmee_millerrabin_next_cand(*(gmpmee_millerrabin_state *)(long)
                                 javaStatePtr);
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_once
   * Signature: (J[B)I
   */
  JNIEXPORT jint JNICALL Java_com_verificatum_vmgj_VMG_millerrabin_1once
  (JNIEnv *env, jclass clazz, jlong javaStatePtr, jbyteArray javaBase)
  {
    mpz_t base;
    int res;

    VMGJ_UNUSED(clazz);

    jbyteArray_to_mpz_t(env, &base, javaBase);
    res = gmpmee_millerrabin_once(*(gmpmee_millerrabin_state *)(long)
                                  javaStatePtr, base);

    mpz_clear(base);

    return res;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_clear
   * Signature: (J)V
   */
  JNIEXPORT void JNICALL Java_com_verificatum_vmgj_VMG_millerrabin_1clear
  (JNIEnv *env, jclass clazz, jlong javaStatePtr)
  {
    VMGJ_UNUSED(env);
    VMGJ_UNUSED(clazz);
    gmpmee_millerrabin_clear(*(gmpmee_millerrabin_state *)(long)javaStatePtr);
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_current
   * Signature: (J)[B
   */
  JNIEXPORT jbyteArray JNICALL
  Java_com_verificatum_vmgj_VMG_millerrabin_1current
  (JNIEnv *env, jclass clazz, jlong javaStatePtr)
  {
    jbyteArray javaResult;

    VMGJ_UNUSED(env);
    VMGJ_UNUSED(clazz);

    mpz_t_to_jbyteArray(env, &javaResult,
                        (*(gmpmee_millerrabin_state *)(long)
                         javaStatePtr)->n);
    return javaResult;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_safe_init
   * Signature: ([BZ)J
   */
  JNIEXPORT jlong JNICALL Java_com_verificatum_vmgj_VMG_millerrabin_1safe_1init
  (JNIEnv *env, jclass clazz, jbyteArray javaN, jboolean search)
  {
    mpz_t n;
    gmpmee_millerrabin_safe_state *statePtr = (void*)0;

    VMGJ_UNUSED(clazz);

    jbyteArray_to_mpz_t(env, &n, javaN);

    if (search || gmpmee_millerrabin_safe_trial(n)) {
      statePtr = (gmpmee_millerrabin_safe_state *)
        malloc(sizeof(gmpmee_millerrabin_safe_state));
      gmpmee_millerrabin_safe_init(*statePtr, n);
    }
    if (search) {
      gmpmee_millerrabin_safe_next_cand(*statePtr);
    }

    mpz_clear(n);

    return (jlong)(long)statePtr;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_safe_next_cand
   * Signature: (J)V
   */
  JNIEXPORT void JNICALL
  Java_com_verificatum_vmgj_VMG_millerrabin_1safe_1next_1cand
  (JNIEnv *env, jclass clazz, jlong javaStatePtr)
  {
    VMGJ_UNUSED(env);
    VMGJ_UNUSED(clazz);
    gmpmee_millerrabin_safe_next_cand(*(gmpmee_millerrabin_safe_state *)(long)
                                      javaStatePtr);
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_safe_once
   * Signature: (J[BI)I
   */
  JNIEXPORT jint JNICALL Java_com_verificatum_vmgj_VMG_millerrabin_1safe_1once
  (JNIEnv *env, jclass clazz, jlong javaStatePtr, jbyteArray javaBase,
   jint javaIndex) {

    mpz_t base;
    int res;

    VMGJ_UNUSED(clazz);

    jbyteArray_to_mpz_t(env, &base, javaBase);

    if (((int)javaIndex) % 2 == 0)
      {
        res = gmpmee_millerrabin_once((*(gmpmee_millerrabin_safe_state *)(long)
                                       javaStatePtr)->nstate,
                                      base);
      }
    else
      {
        res = gmpmee_millerrabin_once((*(gmpmee_millerrabin_safe_state *)(long)
                                       javaStatePtr)->mstate,
                                      base);
      }

    mpz_clear(base);

    return res;
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_safe_clear
   * Signature: (J)V
   */
  JNIEXPORT void JNICALL Java_com_verificatum_vmgj_VMG_millerrabin_1safe_1clear
  (JNIEnv *env, jclass clazz, jlong javaStatePtr)
  {
    VMGJ_UNUSED(env);
    VMGJ_UNUSED(clazz);
    gmpmee_millerrabin_safe_clear(*(gmpmee_millerrabin_safe_state *)(long)
                                  javaStatePtr);
  }


  /*
   * Class:     com_verificatum_vmgj_VMG
   * Method:    millerrabin_current_safe
   * Signature: (J)[B
   */
  JNIEXPORT jbyteArray JNICALL
  Java_com_verificatum_vmgj_VMG_millerrabin_1current_1safe
  (JNIEnv *env, jclass clazz, jlong javaStatePtr)
  {
    jbyteArray javaResult;

    VMGJ_UNUSED(clazz);
    mpz_t_to_jbyteArray(env, &javaResult,
                        (*(gmpmee_millerrabin_safe_state *)(long)
                         javaStatePtr)->nstate->n);
    return javaResult;
  }

#ifdef __cplusplus
}
#endif
