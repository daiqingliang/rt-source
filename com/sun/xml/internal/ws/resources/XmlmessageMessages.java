package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class XmlmessageMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.xmlmessage");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableXML_NULL_HEADERS() { return messageFactory.getMessage("xml.null.headers", new Object[0]); }
  
  public static String XML_NULL_HEADERS() { return localizer.localize(localizableXML_NULL_HEADERS()); }
  
  public static Localizable localizableXML_SET_PAYLOAD_ERR() { return messageFactory.getMessage("xml.set.payload.err", new Object[0]); }
  
  public static String XML_SET_PAYLOAD_ERR() { return localizer.localize(localizableXML_SET_PAYLOAD_ERR()); }
  
  public static Localizable localizableXML_CONTENT_TYPE_MUSTBE_MULTIPART() { return messageFactory.getMessage("xml.content-type.mustbe.multipart", new Object[0]); }
  
  public static String XML_CONTENT_TYPE_MUSTBE_MULTIPART() { return localizer.localize(localizableXML_CONTENT_TYPE_MUSTBE_MULTIPART()); }
  
  public static Localizable localizableXML_UNKNOWN_CONTENT_TYPE() { return messageFactory.getMessage("xml.unknown.Content-Type", new Object[0]); }
  
  public static String XML_UNKNOWN_CONTENT_TYPE() { return localizer.localize(localizableXML_UNKNOWN_CONTENT_TYPE()); }
  
  public static Localizable localizableXML_GET_DS_ERR() { return messageFactory.getMessage("xml.get.ds.err", new Object[0]); }
  
  public static String XML_GET_DS_ERR() { return localizer.localize(localizableXML_GET_DS_ERR()); }
  
  public static Localizable localizableXML_CONTENT_TYPE_PARSE_ERR() { return messageFactory.getMessage("xml.Content-Type.parse.err", new Object[0]); }
  
  public static String XML_CONTENT_TYPE_PARSE_ERR() { return localizer.localize(localizableXML_CONTENT_TYPE_PARSE_ERR()); }
  
  public static Localizable localizableXML_GET_SOURCE_ERR() { return messageFactory.getMessage("xml.get.source.err", new Object[0]); }
  
  public static String XML_GET_SOURCE_ERR() { return localizer.localize(localizableXML_GET_SOURCE_ERR()); }
  
  public static Localizable localizableXML_CANNOT_INTERNALIZE_MESSAGE() { return messageFactory.getMessage("xml.cannot.internalize.message", new Object[0]); }
  
  public static String XML_CANNOT_INTERNALIZE_MESSAGE() { return localizer.localize(localizableXML_CANNOT_INTERNALIZE_MESSAGE()); }
  
  public static Localizable localizableXML_NO_CONTENT_TYPE() { return messageFactory.getMessage("xml.no.Content-Type", new Object[0]); }
  
  public static String XML_NO_CONTENT_TYPE() { return localizer.localize(localizableXML_NO_CONTENT_TYPE()); }
  
  public static Localizable localizableXML_ROOT_PART_INVALID_CONTENT_TYPE(Object paramObject) { return messageFactory.getMessage("xml.root.part.invalid.Content-Type", new Object[] { paramObject }); }
  
  public static String XML_ROOT_PART_INVALID_CONTENT_TYPE(Object paramObject) { return localizer.localize(localizableXML_ROOT_PART_INVALID_CONTENT_TYPE(paramObject)); }
  
  public static Localizable localizableXML_INVALID_CONTENT_TYPE(Object paramObject) { return messageFactory.getMessage("xml.invalid.content-type", new Object[] { paramObject }); }
  
  public static String XML_INVALID_CONTENT_TYPE(Object paramObject) { return localizer.localize(localizableXML_INVALID_CONTENT_TYPE(paramObject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\XmlmessageMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */