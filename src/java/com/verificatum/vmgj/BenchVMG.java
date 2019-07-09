
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
 * Allows invoking the modular exponentiation, simultaneous modular
 * exponentiation routines, primality tests, and related routines of
 * the <a href="http://gmplib.org">Gnu Multiprecision Library
 * (GMP)</a>and GMPMEE (a minor extension of GMP).
 *
 * <p>
 *
 * @author Douglas Wikstrom
 */
@SuppressWarnings("PMD.MethodNamingConventions")
public final class BenchVMG {

    /**
     * Avoid accidental instantiation.
     */
    private BenchVMG() {
    }

    /**
     * Default number of seconds used for a timing.
     */
    static final int DEFAULT_SPEED_TIME = 2000;

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
     * Times exponentiation.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     * @return Number of exponentiations performed.
     */
    protected static long time_powm(final int bitLength, final long milliSecs) {

        final SecureRandom random = new SecureRandom();

        final int len = 100;

        // Generate random modulus.
        BigInteger modulus = new BigInteger(bitLength, random);
        modulus = modulus.setBit(bitLength - 1);

        BigInteger basis = new BigInteger(bitLength, random);
        basis = basis.setBit(bitLength - 1);

        BigInteger[] exponents = new BigInteger[len];

        for (int l = 0; l < len; l++) {
            exponents[l] = new BigInteger(bitLength, random);
        }

        // Time optimized code.
        final long t = System.currentTimeMillis();
        long i = 0;
        int l = 0;
        while (!done(t, milliSecs)) {

            VMG.powm(basis, exponents[l], modulus);

            l = (l + 1) % len;

            i++;
        }
        return i;
    }

    /**
     * Times simultaneous exponentiation.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     * @return Number of simultaneous exponentiations performed.
     */
    protected static long time_spowm(final int bitLength,
                                     final long milliSecs) {

        final SecureRandom random = new SecureRandom();

        final int len = 100;

        // Generate random modulus.
        BigInteger modulus = new BigInteger(bitLength, random);
        modulus = modulus.setBit(bitLength - 1);

        final BigInteger[] bases = new BigInteger[len];
        final BigInteger[] exponents = new BigInteger[len];

        for (int l = 0; l < len; l++) {
            bases[l] = new BigInteger(bitLength, random);
            bases[l] = bases[l].setBit(bitLength - 1);

            exponents[l] = new BigInteger(bitLength, random);
        }

        final long t = System.currentTimeMillis();

        long i = 0;
        while (!done(t, milliSecs)) {

            VMG.spowm(bases, exponents, modulus);

            i++;
        }
        return i * len;
    }

    /**
     * Times fixed-basis exponentiation.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     * @return Number of fixed-basis exponentiations performed.
     */
    protected static long time_fpowm(final int bitLength,
                                     final long milliSecs) {

        final SecureRandom random = new SecureRandom();

        final int len = 100;

        // Generate random modulus.
        BigInteger modulus = new BigInteger(bitLength, random);
        modulus = modulus.setBit(bitLength - 1);

        BigInteger basis = new BigInteger(bitLength, random);
        basis = basis.setBit(bitLength - 1);

        final BigInteger[] exponents = new BigInteger[len];

        for (int l = 0; l < len; l++) {
            exponents[l] = new BigInteger(bitLength, random);
        }

        final FpowmTab tab = new FpowmTab(basis, modulus, bitLength);

        // Time optimized code.
        final long t = System.currentTimeMillis();
        long i = 0;
        int l = 0;
        while (!done(t, milliSecs)) {

            tab.fpowm(exponents[l]);

            l = (l + 1) % len;

            i++;
        }
        return i;
    }

    /**
     * Times modular arithmetic and prints the results.
     *
     * @param bitLength Number of bits of integers in the operation
     * timed.
     * @param milliSecs Duration of the timing.
     */
    public static void time_modulus(final int bitLength, final int milliSecs) {

        final String f =
            "%nTiming modular arithmetic: %d bitlength (%d ms/function)";

        System.out.println(String.format(f, bitLength, milliSecs));
        System.out.println(
            "----------------------------------------------------------------");

        System.out.println(String.format("%12d exponentiations",
                                         time_powm(bitLength, milliSecs)));
        System.out.println(String.format("%12d simultaneous exponentiations",
                                         time_spowm(bitLength, milliSecs)));
        System.out.println(String.format("%12d fixed-basis exponentiations",
                                         time_fpowm(bitLength, milliSecs)));
    }

    /**
     * Prints usage information.
     */
    protected static void usage() {
        System.out.println("Usage: vmgj.VMG [bitLength]... ");
    }

    /**
     * Executes the timing routines.
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
                    usage();
                    System.exit(1);
                }
            }
        }

        final String s =
"\n================================================================\n"
+ "\n        BENCHMARKS FOR com.verificatum.vmgj.VMG          \n\n"
+ "You need to consult the code understand exactly what is      \n"
+ "measured before drawing any conclusions, but the benchmarks  \n"
+ "are fairly self explanatory.\n"
+ "\n"
+ "The code makes calls to the GNU Multiple Precision Arithmetic\n"
+ "library (GMP) and GMP Modular Exponentiation Extension (VMG).\n"
+ "================================================================";

        System.out.println(s);

        for (int i = 0; i < bitLengths.length; i++) {

            time_modulus(bitLengths[i], DEFAULT_SPEED_TIME);
        }
        System.out.println("");
    }
}
//CHECKSTYLE.ON: LocalVariableName
//CHECKSTYLE.ON: LocalFinalVariableName
//CHECKSTYLE.ON: MethodName
//CHECKSTYLE.ON: ParameterName
