package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class HttpserverMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.httpserver");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableUNEXPECTED_HTTP_METHOD(Object paramObject) { return messageFactory.getMessage("unexpected.http.method", new Object[] { paramObject }); }
  
  public static String UNEXPECTED_HTTP_METHOD(Object paramObject) { return localizer.localize(localizableUNEXPECTED_HTTP_METHOD(paramObject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\HttpserverMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */