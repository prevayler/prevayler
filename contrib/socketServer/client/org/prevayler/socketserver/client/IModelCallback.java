package org.prevayler.socketserver.client;

/**
 * The interface for notifiaction callbacks (Model Callbacks)
 * @author DaveO
 */
public interface IModelCallback {
    /**
     * Called when the event in which interest was registered happened
     * @param The name of the event that happened
     */
    public abstract void happened(Long connectionID, String name, Object obj);
}

