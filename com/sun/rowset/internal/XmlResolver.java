package com.sun.rowset.internal;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class XmlResolver implements EntityResolver {
  public InputSource resolveEntity(String paramString1, String paramString2) {
    String str = paramString2.substring(paramString2.lastIndexOf("/"));
    return paramString2.startsWith("http://java.sun.com/xml/ns/jdbc") ? new InputSource(getClass().getResourceAsStream(str)) : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\XmlResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */