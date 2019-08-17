package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class ClientMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.client");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableFAILED_TO_PARSE(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("failed.to.parse", new Object[] { paramObject1, paramObject2 }); }
  
  public static String FAILED_TO_PARSE(Object paramObject1, Object paramObject2) { return localizer.localize(localizableFAILED_TO_PARSE(paramObject1, paramObject2)); }
  
  public static Localizable localizableINVALID_BINDING_ID(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("invalid.binding.id", new Object[] { paramObject1, paramObject2 }); }
  
  public static String INVALID_BINDING_ID(Object paramObject1, Object paramObject2) { return localizer.localize(localizableINVALID_BINDING_ID(paramObject1, paramObject2)); }
  
  public static Localizable localizableEPR_WITHOUT_ADDRESSING_ON() { return messageFactory.getMessage("epr.without.addressing.on", new Object[0]); }
  
  public static String EPR_WITHOUT_ADDRESSING_ON() { return localizer.localize(localizableEPR_WITHOUT_ADDRESSING_ON()); }
  
  public static Localizable localizableINVALID_SERVICE_NO_WSDL(Object paramObject) { return messageFactory.getMessage("invalid.service.no.wsdl", new Object[] { paramObject }); }
  
  public static String INVALID_SERVICE_NO_WSDL(Object paramObject) { return localizer.localize(localizableINVALID_SERVICE_NO_WSDL(paramObject)); }
  
  public static Localizable localizableINVALID_SOAP_ROLE_NONE() { return messageFactory.getMessage("invalid.soap.role.none", new Object[0]); }
  
  public static String INVALID_SOAP_ROLE_NONE() { return localizer.localize(localizableINVALID_SOAP_ROLE_NONE()); }
  
  public static Localizable localizableUNDEFINED_BINDING(Object paramObject) { return messageFactory.getMessage("undefined.binding", new Object[] { paramObject }); }
  
  public static String UNDEFINED_BINDING(Object paramObject) { return localizer.localize(localizableUNDEFINED_BINDING(paramObject)); }
  
  public static Localizable localizableHTTP_NOT_FOUND(Object paramObject) { return messageFactory.getMessage("http.not.found", new Object[] { paramObject }); }
  
  public static String HTTP_NOT_FOUND(Object paramObject) { return localizer.localize(localizableHTTP_NOT_FOUND(paramObject)); }
  
  public static Localizable localizableINVALID_EPR_PORT_NAME(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("invalid.epr.port.name", new Object[] { paramObject1, paramObject2 }); }
  
  public static String INVALID_EPR_PORT_NAME(Object paramObject1, Object paramObject2) { return localizer.localize(localizableINVALID_EPR_PORT_NAME(paramObject1, paramObject2)); }
  
  public static Localizable localizableFAILED_TO_PARSE_WITH_MEX(Object paramObject1, Object paramObject2, Object paramObject3) { return messageFactory.getMessage("failed.to.parseWithMEX", new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  public static String FAILED_TO_PARSE_WITH_MEX(Object paramObject1, Object paramObject2, Object paramObject3) { return localizer.localize(localizableFAILED_TO_PARSE_WITH_MEX(paramObject1, paramObject2, paramObject3)); }
  
  public static Localizable localizableHTTP_STATUS_CODE(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("http.status.code", new Object[] { paramObject1, paramObject2 }); }
  
  public static String HTTP_STATUS_CODE(Object paramObject1, Object paramObject2) { return localizer.localize(localizableHTTP_STATUS_CODE(paramObject1, paramObject2)); }
  
  public static Localizable localizableINVALID_ADDRESS(Object paramObject) { return messageFactory.getMessage("invalid.address", new Object[] { paramObject }); }
  
  public static String INVALID_ADDRESS(Object paramObject) { return localizer.localize(localizableINVALID_ADDRESS(paramObject)); }
  
  public static Localizable localizableUNDEFINED_PORT_TYPE(Object paramObject) { return messageFactory.getMessage("undefined.portType", new Object[] { paramObject }); }
  
  public static String UNDEFINED_PORT_TYPE(Object paramObject) { return localizer.localize(localizableUNDEFINED_PORT_TYPE(paramObject)); }
  
  public static Localizable localizableWSDL_CONTAINS_NO_SERVICE(Object paramObject) { return messageFactory.getMessage("wsdl.contains.no.service", new Object[] { paramObject }); }
  
  public static String WSDL_CONTAINS_NO_SERVICE(Object paramObject) { return localizer.localize(localizableWSDL_CONTAINS_NO_SERVICE(paramObject)); }
  
  public static Localizable localizableINVALID_SOAP_ACTION() { return messageFactory.getMessage("invalid.soap.action", new Object[0]); }
  
  public static String INVALID_SOAP_ACTION() { return localizer.localize(localizableINVALID_SOAP_ACTION()); }
  
  public static Localizable localizableNON_LOGICAL_HANDLER_SET(Object paramObject) { return messageFactory.getMessage("non.logical.handler.set", new Object[] { paramObject }); }
  
  public static String NON_LOGICAL_HANDLER_SET(Object paramObject) { return localizer.localize(localizableNON_LOGICAL_HANDLER_SET(paramObject)); }
  
  public static Localizable localizableLOCAL_CLIENT_FAILED(Object paramObject) { return messageFactory.getMessage("local.client.failed", new Object[] { paramObject }); }
  
  public static String LOCAL_CLIENT_FAILED(Object paramObject) { return localizer.localize(localizableLOCAL_CLIENT_FAILED(paramObject)); }
  
  public static Localizable localizableRUNTIME_WSDLPARSER_INVALID_WSDL(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return messageFactory.getMessage("runtime.wsdlparser.invalidWSDL", new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 }); }
  
  public static String RUNTIME_WSDLPARSER_INVALID_WSDL(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return localizer.localize(localizableRUNTIME_WSDLPARSER_INVALID_WSDL(paramObject1, paramObject2, paramObject3, paramObject4)); }
  
  public static Localizable localizableWSDL_NOT_FOUND(Object paramObject) { return messageFactory.getMessage("wsdl.not.found", new Object[] { paramObject }); }
  
  public static String WSDL_NOT_FOUND(Object paramObject) { return localizer.localize(localizableWSDL_NOT_FOUND(paramObject)); }
  
  public static Localizable localizableHTTP_CLIENT_FAILED(Object paramObject) { return messageFactory.getMessage("http.client.failed", new Object[] { paramObject }); }
  
  public static String HTTP_CLIENT_FAILED(Object paramObject) { return localizer.localize(localizableHTTP_CLIENT_FAILED(paramObject)); }
  
  public static Localizable localizableINVALID_SERVICE_NAME_NULL(Object paramObject) { return messageFactory.getMessage("invalid.service.name.null", new Object[] { paramObject }); }
  
  public static String INVALID_SERVICE_NAME_NULL(Object paramObject) { return localizer.localize(localizableINVALID_SERVICE_NAME_NULL(paramObject)); }
  
  public static Localizable localizableINVALID_WSDL_URL(Object paramObject) { return messageFactory.getMessage("invalid.wsdl.url", new Object[] { paramObject }); }
  
  public static String INVALID_WSDL_URL(Object paramObject) { return localizer.localize(localizableINVALID_WSDL_URL(paramObject)); }
  
  public static Localizable localizableINVALID_PORT_NAME(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("invalid.port.name", new Object[] { paramObject1, paramObject2 }); }
  
  public static String INVALID_PORT_NAME(Object paramObject1, Object paramObject2) { return localizer.localize(localizableINVALID_PORT_NAME(paramObject1, paramObject2)); }
  
  public static Localizable localizableINVALID_SERVICE_NAME(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("invalid.service.name", new Object[] { paramObject1, paramObject2 }); }
  
  public static String INVALID_SERVICE_NAME(Object paramObject1, Object paramObject2) { return localizer.localize(localizableINVALID_SERVICE_NAME(paramObject1, paramObject2)); }
  
  public static Localizable localizableUNSUPPORTED_OPERATION(Object paramObject1, Object paramObject2, Object paramObject3) { return messageFactory.getMessage("unsupported.operation", new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  public static String UNSUPPORTED_OPERATION(Object paramObject1, Object paramObject2, Object paramObject3) { return localizer.localize(localizableUNSUPPORTED_OPERATION(paramObject1, paramObject2, paramObject3)); }
  
  public static Localizable localizableFAILED_TO_PARSE_EPR(Object paramObject) { return messageFactory.getMessage("failed.to.parse.epr", new Object[] { paramObject }); }
  
  public static String FAILED_TO_PARSE_EPR(Object paramObject) { return localizer.localize(localizableFAILED_TO_PARSE_EPR(paramObject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\ClientMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */