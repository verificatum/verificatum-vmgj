
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

    /**
     * This is optimistic, but we only allocate a fixed amount of
     * memory and do not rely on this.
     *
     * @throws Throwable If this instance can not be finalized.
     */
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }
}
