// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability.prevayler;

import java.io.File;

import org.prevayler.PrevaylerFactory;
//import org.prevayler.implementation.PrevalenceTest;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject {

	static final String PREVALENCE_BASE = "QueryTest";

	public PrevaylerQuerySubject() throws java.io.IOException, ClassNotFoundException {
		if (new File(PREVALENCE_BASE).exists()) PrevalenceTest.delete(PREVALENCE_BASE);
		prevayler = PrevaylerFactory.createPrevayler(new QuerySystem(), PREVALENCE_BASE);
	}


	public Object createTestConnection() {
		return new PrevaylerQueryConnection((QuerySystem)prevayler.prevalentSystem());
	}
}
