package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;
import java.util.List;

public final class UnsupportedMediaException extends JAXWSExceptionBase {
  public UnsupportedMediaException(@NotNull String paramString, List<String> paramList) { super(ServerMessages.localizableUNSUPPORTED_CONTENT_TYPE(paramString, paramList)); }
  
  public UnsupportedMediaException() { super(ServerMessages.localizableNO_CONTENT_TYPE()); }
  
  public UnsupportedMediaException(String paramString) { super(ServerMessages.localizableUNSUPPORTED_CHARSET(paramString)); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.server"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\UnsupportedMediaException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */