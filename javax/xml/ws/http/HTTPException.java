package javax.xml.ws.http;

import javax.xml.ws.ProtocolException;

public class HTTPException extends ProtocolException {
  private int statusCode;
  
  public HTTPException(int paramInt) { this.statusCode = paramInt; }
  
  public int getStatusCode() { return this.statusCode; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\http\HTTPException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */