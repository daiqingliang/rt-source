package javax.xml.ws.spi.http;

import java.util.Set;

public abstract class HttpContext {
  protected HttpHandler handler;
  
  public void setHandler(HttpHandler paramHttpHandler) { this.handler = paramHttpHandler; }
  
  public abstract String getPath();
  
  public abstract Object getAttribute(String paramString);
  
  public abstract Set<String> getAttributeNames();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\spi\http\HttpContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */