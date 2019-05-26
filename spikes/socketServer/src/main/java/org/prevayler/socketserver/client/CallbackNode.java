package org.prevayler.socketserver.client;

class CallbackNode {
  public CallbackNode(String m, IModelCallback c) {
    message = m;
    callback = c;
    freed = false;
  }

  public String message;
  public IModelCallback callback;
  public boolean freed;
}