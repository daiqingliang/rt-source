package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class UtilMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.util");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableUTIL_LOCATION(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("util.location", new Object[] { paramObject1, paramObject2 }); }
  
  public static String UTIL_LOCATION(Object paramObject1, Object paramObject2) { return localizer.localize(localizableUTIL_LOCATION(paramObject1, paramObject2)); }
  
  public static Localizable localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("util.failed.to.parse.handlerchain.file", new Object[] { paramObject1, paramObject2 }); }
  
  public static String UTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(Object paramObject1, Object paramObject2) { return localizer.localize(localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(paramObject1, paramObject2)); }
  
  public static Localizable localizableUTIL_PARSER_WRONG_ELEMENT(Object paramObject1, Object paramObject2, Object paramObject3) { return messageFactory.getMessage("util.parser.wrong.element", new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  public static String UTIL_PARSER_WRONG_ELEMENT(Object paramObject1, Object paramObject2, Object paramObject3) { return localizer.localize(localizableUTIL_PARSER_WRONG_ELEMENT(paramObject1, paramObject2, paramObject3)); }
  
  public static Localizable localizableUTIL_HANDLER_CLASS_NOT_FOUND(Object paramObject) { return messageFactory.getMessage("util.handler.class.not.found", new Object[] { paramObject }); }
  
  public static String UTIL_HANDLER_CLASS_NOT_FOUND(Object paramObject) { return localizer.localize(localizableUTIL_HANDLER_CLASS_NOT_FOUND(paramObject)); }
  
  public static Localizable localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object paramObject) { return messageFactory.getMessage("util.handler.endpoint.interface.no.webservice", new Object[] { paramObject }); }
  
  public static String UTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object paramObject) { return localizer.localize(localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(paramObject)); }
  
  public static Localizable localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(Object paramObject) { return messageFactory.getMessage("util.handler.no.webservice.annotation", new Object[] { paramObject }); }
  
  public static String UTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(Object paramObject) { return localizer.localize(localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(paramObject)); }
  
  public static Localizable localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("util.failed.to.find.handlerchain.file", new Object[] { paramObject1, paramObject2 }); }
  
  public static String UTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(Object paramObject1, Object paramObject2) { return localizer.localize(localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(paramObject1, paramObject2)); }
  
  public static Localizable localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() { return messageFactory.getMessage("util.handler.cannot.combine.soapmessagehandlers", new Object[0]); }
  
  public static String UTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() { return localizer.localize(localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\UtilMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */