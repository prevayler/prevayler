package org.prevayler.util;

import org.prevayler.Prevayler;
import org.prevayler.implementation.TransientPrevayler;

public class PrevaylerFactory {

	public static Prevayler createTransient(Object prevalentSystem) {
		return new TransientPrevayler(prevalentSystem);
	}

}
