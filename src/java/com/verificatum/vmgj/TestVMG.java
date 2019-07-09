
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

package com.verificatum.vmgj;

import java.math.BigInteger;
import java.security.SecureRandom;

// We use C style to name things in this file, since it should
// correspond to the native code.

//CHECKSTYLE.OFF: LocalVariableName
//CHECKSTYLE.OFF: LocalFinalVariableName
//CHECKSTYLE.OFF: MethodName
//CHECKSTYLE.OFF: ParameterName

/**
 * Testing routines for VMG, FpowmTab, and MillerRabin. These tests
 * are not meant to be a complete test of the arithmetic. This is
 * handled by the more comprehensive tests in the underlying native
 * code. Here we merely run basic sanity checks to make sure that the
 * native code is called correctly from Java.
 *
 * @author Douglas Wikstrom
 */
@SuppressWarnings("PMD.MethodNamingConventions")
public final class TestVMG {

    /**
     * Avoid accidental instantiation.
     */
    private TestVMG() {
    }

    /**
     * Default number of milliseconds used for a single test during
     * testing of a curve.
     */
    static final int DEFAULT_TEST_TIME = 2000;

    /**
     * Convenience method for bounding the execution time of a test.
     *
     * @param t Time when the test started.
     * @param milliSecs Milliseconds the should proceed.
     * @return True if the deadline has passed and false otherwise.
     */
    private static boolean done(final long t, final long milliSecs) {
        return System.currentTimeMillis() > t + milliSecs;
    }

    /**
     * Tests exponentiation.
     *
     * @param bitLength Number of bits of integers.
     * @param milliSecs Duration of the timing.
     */
    protected static void test_powm(final int bitLength, final long milliSecs) {

        final SecureRandom random = new SecureRandom();

        // Test optimized code.
        final long t = System.currentTimeMillis();
        while (!done(t, milliSecs)) {

            final BigInteger modulus = new BigInteger(bitLength, random);
            final BigInteger basis = new BigInteger(bitLength, random);
            final BigInteger exponent = new BigInteger(bitLength, random);

            final BigInteger vmg = VMG.powm(basis, exponent, modulus);
            final BigInteger java = basis.modPow(exponent, modulus);

            assert vmg.equals(java) : "Fixed basis exponentiation failed!";
        }
    }

    /**
     * Test simultaneous exponentiation.
     *
     * @param bitLength Number of bits of integers.
     * @param milliSecs Duration of the timing.
     */
    protected static void test_spowm(final int bitLength,
                                     final long milliSecs) {

        final SecureRandom random = new SecureRandom();

        final int len = 50;

        // Generate random modulus.
        final BigInteger modulus = new BigInteger(bitLength, random);

        final BigInteger[] bases = new BigInteger[len];
        final BigInteger[] exponents = new BigInteger[len];

        final long t = System.currentTimeMillis();
        while (!done(t, milliSecs)) {
            for (int l = 0; l < len; l++) {
                bases[l] = new BigInteger(bitLength, random).mod(modulus);
                exponents[l] = new BigInteger(bitLength, random);
            }

            final BigInteger vmg = VMG.spowm(bases, exponents, modulus);

            BigInteger res = BigInteger.ONE;
            for (int l = 0; l < len; l++) {
                res = res.multiply(bases[l].modPow(exponents[l], modulus));
                res = res.mod(modulus);
            }

            assert vmg.equals(res) : "Failed to simultanously exponentiate!";
        }
    }

    /**
     * Tests fixed-basis exponentiation.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     */
    protected static void test_fpowm(final int bitLength,
                                     final long milliSecs) {

        final SecureRandom random = new SecureRandom();

        // Generate random modulus.
        final BigInteger modulus = new BigInteger(bitLength, random);
        final BigInteger basis = new BigInteger(bitLength, random);
        final FpowmTab tab = new FpowmTab(basis, modulus, bitLength);

        // Test optimized code.
        final long t = System.currentTimeMillis();
        while (!done(t, milliSecs)) {

            final BigInteger exponent = new BigInteger(bitLength, random);
            final BigInteger vmg = tab.fpowm(exponent);
            final BigInteger res = basis.modPow(exponent, modulus);

            assert vmg.equals(res) : "Failed to fixed-basis exponentiate!";
        }
    }

    /**
     * Tests computation of Legendre symbols.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     */
    protected static void test_legendre(final int bitLength,
                                        final long milliSecs) {

        final SecureRandom random = new SecureRandom();

        final BigInteger two = BigInteger.ONE.add(BigInteger.ONE);

        // Test optimized code.
        final long t = System.currentTimeMillis();
        while (!done(t, milliSecs)) {

            // Make sure we have an odd prime modulus.
            final BigInteger prime =
                BigInteger.probablePrime(bitLength, random);
            final BigInteger exponent =
                prime.subtract(BigInteger.ONE).divide(two);

            final BigInteger bi = new BigInteger(bitLength, random);

            final int ivmg = VMG.legendre(bi, prime);
            final BigInteger vmg =
                new BigInteger(Integer.toString(ivmg)).mod(prime);
            final BigInteger res = bi.modPow(exponent, prime);

            assert vmg.equals(res) : "Failed to compute Legendre symbol!";
        }
    }

    /**
     * Generates random integer between 2 and modulus - 1.
     *
     * @param modulus Modulus.
     * @param random Random source.
     * @return Random integer between 2 and modulus - 1.
     */
    private static BigInteger getBasis(final BigInteger modulus,
                                       final SecureRandom random) {
        BigInteger basis;

        // Pick a random base in [2,modulus - 1].
        do {
            basis = new BigInteger(modulus.bitLength(), random).mod(modulus);
        } while (basis.equals(BigInteger.ZERO) || basis.equals(BigInteger.ONE));
        return basis;
    }

    /**
     * Tests primality testing.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     */
    public static void test_millerrabin_prime(final int bitLength,
                                              final int milliSecs) {

        final SecureRandom random = new SecureRandom();

        // Test optimized code.
        final long t = System.currentTimeMillis();
        while (!done(t, milliSecs)) {

            // Make sure we have an odd prime modulus.
            final BigInteger bi = new BigInteger(bitLength, random);

            final MillerRabin mr = new MillerRabin(bi, true, false);
            boolean vmg = mr.trial();
            for (int i = 0; vmg && i < 30; i++) {
                final BigInteger b = getBasis(bi, random);
                vmg = mr.once(b);
            }
            mr.done();

            final boolean res = bi.isProbablePrime(50);

            assert vmg == res : "Failed to test for primality!";
        }
    }

    /**
     * Tests next prime.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     */
    public static void test_millerrabin_nextprime(final int bitLength,
                                                  final int milliSecs) {

        final SecureRandom random = new SecureRandom();

        // Test optimized code.
        final long t = System.currentTimeMillis();
        while (!done(t, milliSecs)) {

            // Make sure we have an odd prime modulus.
            final BigInteger bi = new BigInteger(bitLength, random);
            final BigInteger primeA = bi.nextProbablePrime();

            final MillerRabin mr = new MillerRabin(bi, true, true);
            boolean vmg = false;
            BigInteger primeB = null;
            while (!vmg) {
                primeB = mr.getCurrentCandidate();
                vmg = true;
                for (int i = 0; vmg && i < 30; i++) {
                    final BigInteger b = getBasis(primeB, random);
                    vmg = mr.once(b);
                }
                mr.nextCandidate();
            }
            mr.done();

            assert primeA.equals(primeB) : "Failed get next prime!";
        }
    }

    /**
     * Tests next prime.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     */
    public static void test_millerrabin_nextsafeprime(final int bitLength,
                                                      final int milliSecs) {

        final BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
        final SecureRandom random = new SecureRandom();

        // Test optimized code.
        final long t = System.currentTimeMillis();
        while (!done(t, milliSecs)) {

            // Search for the next safe prime naively.
            BigInteger bi;
            BigInteger primeA;
            BigInteger subA;
            do {
                bi = new BigInteger(bitLength, random);
                primeA = bi.nextProbablePrime();
                subA = primeA.subtract(BigInteger.ONE).divide(two);
            } while (!subA.isProbablePrime(20));

            // Search using our routines.
            MillerRabin mr = new MillerRabin(bi, false, true);
            boolean vmg = false;
            BigInteger primeB = null;
            BigInteger subB = null;
            BigInteger b = null;
            while (!vmg) {
                primeB = mr.getCurrentCandidate();
                subB = primeB.subtract(BigInteger.ONE).divide(two);
                vmg = true;
                for (int i = 0; vmg && i < 30; i++) {
                    if (i % 2 == 0) {
                        b = getBasis(primeB, random);
                    } else {
                        b = getBasis(subB, random);
                    }
                    vmg = mr.once(b, i % 2);
                }
                mr.nextCandidate();
            }
            mr.done();

            assert primeA.equals(primeB) : "Failed get next prime!";

            mr = new MillerRabin(primeA, false, false);
            vmg = mr.trial();
            for (int i = 0; vmg && i < 40; i++) {
                if (i % 2 == 0) {
                    b = getBasis(primeA, random);
                } else {
                    b = getBasis(subA, random);
                }
                vmg = mr.once(b, i % 2);
            }
            mr.done();

            assert vmg && primeA.equals(primeB) : "Failed verify safe prime!";
        }
    }

    /**
     * Tests modular arithmetic and prints the results.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     */
    public static void test_vmg(final int bitLength, final int milliSecs) {

        final String f =
            "%nTesting modular arithmetic: %d bitlength (%d ms/function)";

        System.out.println(String.format(f, bitLength, milliSecs));
        System.out.println(
            "----------------------------------------------------------------");

        System.out.println("powm (plain modular exponentiation)");
        test_powm(bitLength, milliSecs);
        System.out.println("spowm (simultaneous modular exponentiation)");
        test_spowm(bitLength, milliSecs);
        System.out.println("fpowm (fixed-basis modular exponentiation)");
        test_fpowm(bitLength, milliSecs);
        System.out.println("legendre (Legendre symbol)");
        test_legendre(bitLength, milliSecs);
        System.out.println("prime (test random integers for primality)");
        test_millerrabin_prime(bitLength, milliSecs);
        System.out.println("nextprime (find next prime)");
        test_millerrabin_nextprime(100, milliSecs);
        System.out.println("nextsafeprime (find next and check safe prime "
                           + "100 bits)");
        test_millerrabin_nextsafeprime(70, milliSecs);
    }

    /**
     * Executes the testing routines.
     *
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {

        int[] bitLengths = new int[4];
        bitLengths[0] = 1024;
        bitLengths[1] = 2048;
        bitLengths[2] = 3072;
        bitLengths[3] = 4096;

        if (args.length > 0) {

            bitLengths = new int[args.length];
            for (int i = 0; i < args.length; i++) {

                try {
                    bitLengths[i] = Integer.parseInt(args[i]);
                } catch (NumberFormatException nfe) {
                    System.err.println("Failed to parse a bit length! ("
                                       + args[i] + ")");
                    System.exit(1);
                }
            }
        }

        final String s =
"\n================================================================\n"
+ "\n           TEST com.verificatum.vmgj.VMG \n\n"
+ " Although these tests technically give full coverage, they are\n"
+ " not sufficient to verify the correctness of the arithmetic.\n"
+ " The correctness of the arithmetic is guaranteed by the tests\n"
+ " of the native code. The tests run here merely verify that\n"
+ " native calls are handled correctly.\n\n"
+ "================================================================";

        System.out.println(s);

        for (int i = 0; i < bitLengths.length; i++) {

            test_vmg(bitLengths[i], DEFAULT_TEST_TIME);
        }
        System.out.println("");
    }
}
//CHECKSTYLE.ON: LocalVariableName
//CHECKSTYLE.ON: LocalFinalVariableName
//CHECKSTYLE.ON: MethodName
//CHECKSTYLE.ON: ParameterName
