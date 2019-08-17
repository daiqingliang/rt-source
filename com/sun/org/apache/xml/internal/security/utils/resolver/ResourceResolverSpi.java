package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public abstract class ResourceResolverSpi {
  private static Logger log = Logger.getLogger(ResourceResolverSpi.class.getName());
  
  protected Map<String, String> properties = null;
  
  @Deprecated
  protected final boolean secureValidation = true;
  
  @Deprecated
  public XMLSignatureInput engineResolve(Attr paramAttr, String paramString) throws ResourceResolverException { throw new UnsupportedOperationException(); }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext) throws ResourceResolverException { return engineResolve(paramResourceResolverContext.attr, paramResourceResolverContext.baseUri); }
  
  public void engineSetProperty(String paramString1, String paramString2) {
    if (this.properties == null)
      this.properties = new HashMap(); 
    this.properties.put(paramString1, paramString2);
  }
  
  public String engineGetProperty(String paramString) { return (this.properties == null) ? null : (String)this.properties.get(paramString); }
  
  public void engineAddProperies(Map<String, String> paramMap) {
    if (paramMap != null && !paramMap.isEmpty()) {
      if (this.properties == null)
        this.properties = new HashMap(); 
      this.properties.putAll(paramMap);
    } 
  }
  
  public boolean engineIsThreadSafe() { return false; }
  
  @Deprecated
  public boolean engineCanResolve(Attr paramAttr, String paramString) { throw new UnsupportedOperationException(); }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext) { return engineCanResolve(paramResourceResolverContext.attr, paramResourceResolverContext.baseUri); }
  
  public String[] engineGetPropertyKeys() { return new String[0]; }
  
  public boolean understandsProperty(String paramString) {
    String[] arrayOfString = engineGetPropertyKeys();
    if (arrayOfString != null)
      for (byte b = 0; b < arrayOfString.length; b++) {
        if (arrayOfString[b].equals(paramString))
          return true; 
      }  
    return false;
  }
  
  public static String fixURI(String paramString) {
    paramString = paramString.replace(File.separatorChar, '/');
    if (paramString.length() >= 4) {
      char c1 = Character.toUpperCase(paramString.charAt(0));
      char c2 = paramString.charAt(1);
      char c3 = paramString.charAt(2);
      char c4 = paramString.charAt(3);
      boolean bool = ('A' <= c1 && c1 <= 'Z' && c2 == ':' && c3 == '/' && c4 != '/') ? 1 : 0;
      if (bool && log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Found DOS filename: " + paramString); 
    } 
    if (paramString.length() >= 2) {
      char c = paramString.charAt(1);
      if (c == ':') {
        char c1 = Character.toUpperCase(paramString.charAt(0));
        if ('A' <= c1 && c1 <= 'Z')
          paramString = "/" + paramString; 
      } 
    } 
    return paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\ResourceResolverSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */