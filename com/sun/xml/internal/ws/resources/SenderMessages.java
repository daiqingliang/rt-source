package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class SenderMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.sender");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object paramObject) { return messageFactory.getMessage("sender.request.illegalValueForContentNegotiation", new Object[] { paramObject }); }
  
  public static String SENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object paramObject) { return localizer.localize(localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(paramObject)); }
  
  public static Localizable localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() { return messageFactory.getMessage("sender.response.cannotDecodeFaultDetail", new Object[0]); }
  
  public static String SENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() { return localizer.localize(localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL()); }
  
  public static Localizable localizableSENDER_NESTED_ERROR(Object paramObject) { return messageFactory.getMessage("sender.nestedError", new Object[] { paramObject }); }
  
  public static String SENDER_NESTED_ERROR(Object paramObject) { return localizer.localize(localizableSENDER_NESTED_ERROR(paramObject)); }
  
  public static Localizable localizableSENDER_REQUEST_MESSAGE_NOT_READY() { return messageFactory.getMessage("sender.request.messageNotReady", new Object[0]); }
  
  public static String SENDER_REQUEST_MESSAGE_NOT_READY() { return localizer.localize(localizableSENDER_REQUEST_MESSAGE_NOT_READY()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\SenderMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */