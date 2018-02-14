
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
 * Implements primality tests and safe-primality tests such that the
 * caller provides the randomness. This allows a theoretically sound
 * primality test in contrast to builtin routines. Interlacing and
 * trial division is used in the test of safe primes, which speeds up
 * searching drastically. Consult the native code for more
 * information.
 *
 * @author Douglas Wikstrom
 */
public class MillerRabin {

    /**
     * Stores native pointer to state.
     */
    protected long statePtr;

    /**
     * Decides if we are checking primality or safe primality.
     */
    protected boolean primality;

    /**
     * Initializes the Miller-Rabin test for the given
     * integers. Please use the method {@link #trial()} and read the
     * comment.
     *
     * @param n Integer to test.
     * @param primality Decides if we are checking primality or safe
     * primality.
     * @param search Decides if we are searching for an integer or testing.
     */
    public MillerRabin(final BigInteger n, final boolean primality,
                       final boolean search) {
        if (n.compareTo(BigInteger.ZERO) <= 0) {
            throw new ArithmeticException("Primality check of non-positive "
                                          + "integer!");
        }
        this.primality = primality;
        if (primality) {
            statePtr = VMG.millerrabin_init(n.toByteArray(), search);
        } else {
            statePtr = VMG.millerrabin_safe_init(n.toByteArray(), search);
        }
    }

    /**
     * Returns the result of the trial divisions. {@link
     * #once(BigInteger)} or {@link #done()} must not be called if this
     * function returns false. Note that if this instance is created
     * for searching, this will always return <code>true</code>, since
     * the constructor in that case moves to the first candidate
     * integer that passes trial divisions.
     *
     * @return Returns <code>true</code> or <code>false</code>
     * depending on if the integer is found not to be a candidate
     * after trial divisions.
     */
    public boolean trial() {
        return statePtr != 0;
    }

    /**
     * Increases the integer to the next candidate prime, or safe
     * prime, depending on how this instance was created a candidate
     * prime passes all trial divisions.
     */
    public void nextCandidate() {
        if (primality) {
            VMG.millerrabin_next_cand(statePtr);
        } else {
            VMG.millerrabin_safe_next_cand(statePtr);
        }
    }

    /**
     * Returns the current candidate.
     *
     * @return Current candidate.
     */
    public BigInteger getCurrentCandidate() {
        if (primality) {
            return new BigInteger(VMG.millerrabin_current(statePtr));
        } else {
            return new BigInteger(VMG.millerrabin_current_safe(statePtr));
        }
    }

    /**
     * Perform one Miller-Rabin test using the given base.
     *
     * @param base Base used in testing.
     * @return <code>false</code> if the integer is not prime and
     * <code>true</code> otherwise.
     */
    public boolean once(final BigInteger base) {
        return VMG.millerrabin_once(statePtr, base.toByteArray()) == 1;
    }

    /**
     * Perform one Miller-Rabin test using the given base.
     *
     * @param base Base used in testing.
     * @param index Determines if Miller-Rabin is executed on the
     * tested integer <i>n</i> or <i>(n-1)/2</i>.
     * @return <code>false</code> if the integer is not prime and
     * <code>true</code> otherwise.
     */
    public boolean once(final BigInteger base, final int index) {
        return VMG.millerrabin_safe_once(statePtr,
                                         base.toByteArray(),
                                         index) == 1;
    }

    /**
     * Releases resources allocated for testing. This must be called
     * after testing is completed, but it must not be called if {@link
     * #trial()} returns 0.
     */
    public void done() {
        if (statePtr != 0) {
            if (primality) {
                VMG.millerrabin_clear(statePtr);
            } else {
                VMG.millerrabin_safe_clear(statePtr);
            }
            statePtr = 0;
        }
    }
}
