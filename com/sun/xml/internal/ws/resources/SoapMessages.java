package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class SoapMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.soap");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableSOAP_FAULT_CREATE_ERR(Object paramObject) { return messageFactory.getMessage("soap.fault.create.err", new Object[] { paramObject }); }
  
  public static String SOAP_FAULT_CREATE_ERR(Object paramObject) { return localizer.localize(localizableSOAP_FAULT_CREATE_ERR(paramObject)); }
  
  public static Localizable localizableSOAP_MSG_FACTORY_CREATE_ERR(Object paramObject) { return messageFactory.getMessage("soap.msg.factory.create.err", new Object[] { paramObject }); }
  
  public static String SOAP_MSG_FACTORY_CREATE_ERR(Object paramObject) { return localizer.localize(localizableSOAP_MSG_FACTORY_CREATE_ERR(paramObject)); }
  
  public static Localizable localizableSOAP_MSG_CREATE_ERR(Object paramObject) { return messageFactory.getMessage("soap.msg.create.err", new Object[] { paramObject }); }
  
  public static String SOAP_MSG_CREATE_ERR(Object paramObject) { return localizer.localize(localizableSOAP_MSG_CREATE_ERR(paramObject)); }
  
  public static Localizable localizableSOAP_FACTORY_CREATE_ERR(Object paramObject) { return messageFactory.getMessage("soap.factory.create.err", new Object[] { paramObject }); }
  
  public static String SOAP_FACTORY_CREATE_ERR(Object paramObject) { return localizer.localize(localizableSOAP_FACTORY_CREATE_ERR(paramObject)); }
  
  public static Localizable localizableSOAP_PROTOCOL_INVALID_FAULT_CODE(Object paramObject) { return messageFactory.getMessage("soap.protocol.invalidFaultCode", new Object[] { paramObject }); }
  
  public static String SOAP_PROTOCOL_INVALID_FAULT_CODE(Object paramObject) { return localizer.localize(localizableSOAP_PROTOCOL_INVALID_FAULT_CODE(paramObject)); }
  
  public static Localizable localizableSOAP_VERSION_MISMATCH_ERR(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("soap.version.mismatch.err", new Object[] { paramObject1, paramObject2 }); }
  
  public static String SOAP_VERSION_MISMATCH_ERR(Object paramObject1, Object paramObject2) { return localizer.localize(localizableSOAP_VERSION_MISMATCH_ERR(paramObject1, paramObject2)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\SoapMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */