package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class HandlerMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.handler");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("handler.messageContext.invalid.class", new Object[] { paramObject1, paramObject2 }); }
  
  public static String HANDLER_MESSAGE_CONTEXT_INVALID_CLASS(Object paramObject1, Object paramObject2) { return localizer.localize(localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(paramObject1, paramObject2)); }
  
  public static Localizable localizableCANNOT_EXTEND_HANDLER_DIRECTLY(Object paramObject) { return messageFactory.getMessage("cannot.extend.handler.directly", new Object[] { paramObject }); }
  
  public static String CANNOT_EXTEND_HANDLER_DIRECTLY(Object paramObject) { return localizer.localize(localizableCANNOT_EXTEND_HANDLER_DIRECTLY(paramObject)); }
  
  public static Localizable localizableHANDLER_NOT_VALID_TYPE(Object paramObject) { return messageFactory.getMessage("handler.not.valid.type", new Object[] { paramObject }); }
  
  public static String HANDLER_NOT_VALID_TYPE(Object paramObject) { return localizer.localize(localizableHANDLER_NOT_VALID_TYPE(paramObject)); }
  
  public static Localizable localizableCANNOT_INSTANTIATE_HANDLER(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("cannot.instantiate.handler", new Object[] { paramObject1, paramObject2 }); }
  
  public static String CANNOT_INSTANTIATE_HANDLER(Object paramObject1, Object paramObject2) { return localizer.localize(localizableCANNOT_INSTANTIATE_HANDLER(paramObject1, paramObject2)); }
  
  public static Localizable localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(Object paramObject) { return messageFactory.getMessage("handler.chain.contains.handler.only", new Object[] { paramObject }); }
  
  public static String HANDLER_CHAIN_CONTAINS_HANDLER_ONLY(Object paramObject) { return localizer.localize(localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(paramObject)); }
  
  public static Localizable localizableHANDLER_NESTED_ERROR(Object paramObject) { return messageFactory.getMessage("handler.nestedError", new Object[] { paramObject }); }
  
  public static String HANDLER_NESTED_ERROR(Object paramObject) { return localizer.localize(localizableHANDLER_NESTED_ERROR(paramObject)); }
  
  public static Localizable localizableHANDLER_PREDESTROY_IGNORE(Object paramObject) { return messageFactory.getMessage("handler.predestroy.ignore", new Object[] { paramObject }); }
  
  public static String HANDLER_PREDESTROY_IGNORE(Object paramObject) { return localizer.localize(localizableHANDLER_PREDESTROY_IGNORE(paramObject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\HandlerMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */