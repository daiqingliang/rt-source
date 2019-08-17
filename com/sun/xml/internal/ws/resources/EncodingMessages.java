package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class EncodingMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.encoding");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableFAILED_TO_READ_RESPONSE(Object paramObject) { return messageFactory.getMessage("failed.to.read.response", new Object[] { paramObject }); }
  
  public static String FAILED_TO_READ_RESPONSE(Object paramObject) { return localizer.localize(localizableFAILED_TO_READ_RESPONSE(paramObject)); }
  
  public static Localizable localizableEXCEPTION_INCORRECT_TYPE(Object paramObject) { return messageFactory.getMessage("exception.incorrectType", new Object[] { paramObject }); }
  
  public static String EXCEPTION_INCORRECT_TYPE(Object paramObject) { return localizer.localize(localizableEXCEPTION_INCORRECT_TYPE(paramObject)); }
  
  public static Localizable localizableEXCEPTION_NOTFOUND(Object paramObject) { return messageFactory.getMessage("exception.notfound", new Object[] { paramObject }); }
  
  public static String EXCEPTION_NOTFOUND(Object paramObject) { return localizer.localize(localizableEXCEPTION_NOTFOUND(paramObject)); }
  
  public static Localizable localizableXSD_UNEXPECTED_ELEMENT_NAME(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("xsd.unexpectedElementName", new Object[] { paramObject1, paramObject2 }); }
  
  public static String XSD_UNEXPECTED_ELEMENT_NAME(Object paramObject1, Object paramObject2) { return localizer.localize(localizableXSD_UNEXPECTED_ELEMENT_NAME(paramObject1, paramObject2)); }
  
  public static Localizable localizableNESTED_DESERIALIZATION_ERROR(Object paramObject) { return messageFactory.getMessage("nestedDeserializationError", new Object[] { paramObject }); }
  
  public static String NESTED_DESERIALIZATION_ERROR(Object paramObject) { return localizer.localize(localizableNESTED_DESERIALIZATION_ERROR(paramObject)); }
  
  public static Localizable localizableNESTED_ENCODING_ERROR(Object paramObject) { return messageFactory.getMessage("nestedEncodingError", new Object[] { paramObject }); }
  
  public static String NESTED_ENCODING_ERROR(Object paramObject) { return localizer.localize(localizableNESTED_ENCODING_ERROR(paramObject)); }
  
  public static Localizable localizableXSD_UNKNOWN_PREFIX(Object paramObject) { return messageFactory.getMessage("xsd.unknownPrefix", new Object[] { paramObject }); }
  
  public static String XSD_UNKNOWN_PREFIX(Object paramObject) { return localizer.localize(localizableXSD_UNKNOWN_PREFIX(paramObject)); }
  
  public static Localizable localizableNESTED_SERIALIZATION_ERROR(Object paramObject) { return messageFactory.getMessage("nestedSerializationError", new Object[] { paramObject }); }
  
  public static String NESTED_SERIALIZATION_ERROR(Object paramObject) { return localizer.localize(localizableNESTED_SERIALIZATION_ERROR(paramObject)); }
  
  public static Localizable localizableNO_SUCH_CONTENT_ID(Object paramObject) { return messageFactory.getMessage("noSuchContentId", new Object[] { paramObject }); }
  
  public static String NO_SUCH_CONTENT_ID(Object paramObject) { return localizer.localize(localizableNO_SUCH_CONTENT_ID(paramObject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\EncodingMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */