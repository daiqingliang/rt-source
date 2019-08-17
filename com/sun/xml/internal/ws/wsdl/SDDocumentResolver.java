package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.SDDocument;

public interface SDDocumentResolver {
  @Nullable
  SDDocument resolve(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\SDDocumentResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */