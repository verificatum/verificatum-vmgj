
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

/**
 * Provides a Java wrapper for a pointer to a native pre-computed
 * table used for fixed based modular exponentiation as implemented in
 * {@link VMG}.
 *
 * @author Douglas Wikstrom
 */
public class FpowmTab {

    /**
     * Stores native pointer to a precomputed fixed base
     * exponentiation table.
     */
    protected long tablePtr;

    /**
     * Creates a precomputed table for the given basis, modulus, and
     * exponent bit length.
     *
     * @param basis Basis element.
     * @param modulus Modulus used during modular exponentiations.
     * @param exponentBitlen Expected bit length of exponents used when
     * invoking the table.
     */
    public FpowmTab(final BigInteger basis,
                    final BigInteger modulus,
                    final int exponentBitlen) {
        this(basis, modulus, 16, exponentBitlen);
    }

    /**
     * Creates a precomputed table for the given basis, modulus, and
     * exponent bit length.
     *
     * @param basis Basis element.
     * @param modulus Modulus used during modular exponentiations.
     * @param blockWidth Number of basis elements used during
     * splitting.
     * @param exponentBitlen Expected bit length of exponents used when
     * invoking the table.
     */
    public FpowmTab(final BigInteger basis,
                    final BigInteger modulus,
                    final int blockWidth,
                    final int exponentBitlen) {
        tablePtr = VMG.fpowm_precomp(basis.toByteArray(),
                                     modulus.toByteArray(),
                                     blockWidth,
                                     exponentBitlen);
    }

    /**
     * Computes a modular exponentiation using the given exponent and
     * the basis and modulus previously used to construct this table.
     *
     * @param exponent Exponent used in modular exponentiation.
     * @return Power of basis for which pre-computation took place.
     */
    public BigInteger fpowm(final BigInteger exponent) {
        return new BigInteger(VMG.fpowm(tablePtr,
                                        exponent.toByteArray()));
    }

    /**
     * Release resources allocated by native code.
     */
    public void free() {
        if (tablePtr != 0) {
            VMG.fpowm_clear(tablePtr);
            tablePtr = 0;
        }
    }

    // It took them about 20 years to figure out that finalize() without
    // any guarantees is harmful.
    //
    // /**
    //  * This is optimistic, but we only allocate a fixed amount of
    //  * memory and do not rely on this.
    //  *
    //  * @throws Throwable If this instance can not be finalized.
    //  */
    // protected void finalize() throws Throwable {
    //     free();
    //     super.finalize();
    // }
}
