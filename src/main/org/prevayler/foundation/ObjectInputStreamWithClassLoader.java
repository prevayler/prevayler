package org.prevayler.foundation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ObjectInputStreamWithClassLoader extends ObjectInputStream
{
	ClassLoader _loader;
	
	public ObjectInputStreamWithClassLoader(InputStream stream, ClassLoader loader) throws IOException
	{
		super(stream);
		
		_loader = loader;
	}
	
	protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException
	{
		return(_loader != null ? _loader.loadClass(v.getName()) : super.resolveClass(v));
	}	
}
