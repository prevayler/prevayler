package org.prevayler.socketserver.example.transactions;

import java.beans.PropertyDescriptor;

import org.prevayler.socketserver.transactions.RemoteTransaction;

/**
 * A generic parent Transaction for setting properties of beans.
 * 
 * @author djo
 */
public abstract class BeanSetter extends RemoteTransaction {

    // Declare instance fields.  All subclasses should treat these fields
    // as read-only.
    protected int id;
    protected String field;
    protected Object value;
    
	/**
	 * Method BeanSetter.  Construct a BeanSetter transasction.
	 * @param id
	 * @param field
	 * @param value
	 */
    public BeanSetter(int id, String field, Object value) {
        this.id = id;
        this.field = field;
        this.value = value;
    }

    // Declare the static cache for the PropertyDescriptor info
    // This object must be initialized in the static initializer of child classes
    protected static PropertyDescriptor[] propertyDescriptors;

	/**
	 * Method callSetter.
	 * @param object
	 * @param field
	 * @param value
	 * @throws Exception
	 */
    protected void callSetter(Object object) throws Exception {
        for (int i = 0; i < propertyDescriptors.length; i++) {
            if (propertyDescriptors[i].getName().equals(field)) {
                propertyDescriptors[i].getWriteMethod().invoke(object, new Object[] {value});
                break;
            }
        }
    }
    
    /**
     * Look-up the object on which the property value will be set
     * @param prevalentSystem the system on which to look up the object
     */
    protected abstract Object lookup(Object prevalentSystem) throws Exception;
    
    /**
	 * @see org.prevayler.util.TransactionWithQuery#executeAndQuery(Object)
	 */
	protected Object executeAndQuery(Object prevalentSystem) throws Exception {
        Object subject = lookup(prevalentSystem);
        callSetter(subject);
		return null;
	}
}

