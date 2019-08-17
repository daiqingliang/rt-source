package com.sun.org.apache.xml.internal.security.encryption;

import org.w3c.dom.Attr;

public interface CipherReference {
  String getURI();
  
  Attr getURIAsAttr();
  
  Transforms getTransforms();
  
  void setTransforms(Transforms paramTransforms);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\CipherReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */