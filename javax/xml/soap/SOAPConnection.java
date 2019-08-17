package javax.xml.soap;

public abstract class SOAPConnection {
  public abstract SOAPMessage call(SOAPMessage paramSOAPMessage, Object paramObject) throws SOAPException;
  
  public SOAPMessage get(Object paramObject) throws SOAPException { throw new UnsupportedOperationException("All subclasses of SOAPConnection must override get()"); }
  
  public abstract void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */