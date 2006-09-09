// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package traverser;

public class ObjectPath {

	public final ObjectPath _parent;
	public final String _branchName;
	public final Object _object;

	ObjectPath(Object root) {
		this(null, "", root);
	}

	private ObjectPath(ObjectPath parent, String branchName, Object object) {
		_parent = parent;
		_branchName = branchName;
		_object = object;
	}

	ObjectPath createBranch(String branchName, Object object) {
		return new ObjectPath(this, branchName, object);
	}

	@Override
	public String toString() {
		String parent = _parent == null
			? ""
			: _parent.toString();
		return parent + "." + _branchName + " -> " + _object.getClass().getSimpleName();
	}

}