package javax.xml.ws.handler;

public interface Handler<C extends MessageContext> {
  boolean handleMessage(C paramC);
  
  boolean handleFault(C paramC);
  
  void close(MessageContext paramMessageContext);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\handler\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */