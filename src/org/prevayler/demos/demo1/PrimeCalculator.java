// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld, Paul Hammant
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo1;

import org.prevayler.Prevayler;


/**
 * This is client code to the prevalent system. It does not need to be persisted.
 */
class PrimeCalculator {

	private final Prevayler _prevayler;
	private final NumberKeeper _numberKeeper;


	PrimeCalculator(Prevayler prevayler) {
		_prevayler = prevayler;
		_numberKeeper = (NumberKeeper)prevayler.prevalentSystem();
	}


	void start() throws Exception {
		int largestPrime = 0;
		int primesFound = 0;
		int primeCandidate = _numberKeeper.lastNumber() == 0
			? 2
			: _numberKeeper.lastNumber() + 1;

		while (primeCandidate <= Integer.MAX_VALUE) {
			if (isPrime(primeCandidate)) {

				_prevayler.execute(new NumberStorageTransaction(primeCandidate));

				largestPrime = primeCandidate;
				primesFound = _numberKeeper.numbers().size();
				System.out.println("Primes found: " + primesFound + ". Largest: " + largestPrime);
			}

			primeCandidate++;
		}
	}


    private boolean isPrime(int candidate) {
	    /*int factor = 2;
        candidate = candidate / 2;
        while (factor < candidate) {
        	if (candidate % factor == 0) return false;
        	factor++;
        }
        return true;*/
	    
	    if (candidate < 2) { return false; }
        if (candidate == 2) { return true; }
        if (candidate % 2 == 0) { return false; }

        int factor = 3;
        double square = Math.ceil(Math.sqrt(candidate));
        while (factor <= square) {
            if (candidate % factor == 0) return false;
            factor+=2;
        }
        return true;
    }

}