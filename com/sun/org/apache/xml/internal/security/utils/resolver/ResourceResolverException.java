package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Attr;

public class ResourceResolverException extends XMLSecurityException {
  private static final long serialVersionUID = 1L;
  
  private Attr uri = null;
  
  private String baseURI = null;
  
  public ResourceResolverException(String paramString1, Attr paramAttr, String paramString2) {
    super(paramString1);
    this.uri = paramAttr;
    this.baseURI = paramString2;
  }
  
  public ResourceResolverException(String paramString1, Object[] paramArrayOfObject, Attr paramAttr, String paramString2) {
    super(paramString1, paramArrayOfObject);
    this.uri = paramAttr;
    this.baseURI = paramString2;
  }
  
  public ResourceResolverException(String paramString1, Exception paramException, Attr paramAttr, String paramString2) {
    super(paramString1, paramException);
    this.uri = paramAttr;
    this.baseURI = paramString2;
  }
  
  public ResourceResolverException(String paramString1, Object[] paramArrayOfObject, Exception paramException, Attr paramAttr, String paramString2) {
    super(paramString1, paramArrayOfObject, paramException);
    this.uri = paramAttr;
    this.baseURI = paramString2;
  }
  
  public void setURI(Attr paramAttr) { this.uri = paramAttr; }
  
  public Attr getURI() { return this.uri; }
  
  public void setbaseURI(String paramString) { this.baseURI = paramString; }
  
  public String getbaseURI() { return this.baseURI; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\ResourceResolverException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */