package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class ProviderApiMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.providerApi");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableNULL_ADDRESS_SERVICE_ENDPOINT() { return messageFactory.getMessage("null.address.service.endpoint", new Object[0]); }
  
  public static String NULL_ADDRESS_SERVICE_ENDPOINT() { return localizer.localize(localizableNULL_ADDRESS_SERVICE_ENDPOINT()); }
  
  public static Localizable localizableNO_WSDL_NO_PORT(Object paramObject) { return messageFactory.getMessage("no.wsdl.no.port", new Object[] { paramObject }); }
  
  public static String NO_WSDL_NO_PORT(Object paramObject) { return localizer.localize(localizableNO_WSDL_NO_PORT(paramObject)); }
  
  public static Localizable localizableNULL_SERVICE() { return messageFactory.getMessage("null.service", new Object[0]); }
  
  public static String NULL_SERVICE() { return localizer.localize(localizableNULL_SERVICE()); }
  
  public static Localizable localizableNULL_ADDRESS() { return messageFactory.getMessage("null.address", new Object[0]); }
  
  public static String NULL_ADDRESS() { return localizer.localize(localizableNULL_ADDRESS()); }
  
  public static Localizable localizableNULL_PORTNAME() { return messageFactory.getMessage("null.portname", new Object[0]); }
  
  public static String NULL_PORTNAME() { return localizer.localize(localizableNULL_PORTNAME()); }
  
  public static Localizable localizableNOTFOUND_SERVICE_IN_WSDL(Object paramObject1, Object paramObject2) { return messageFactory.getMessage("notfound.service.in.wsdl", new Object[] { paramObject1, paramObject2 }); }
  
  public static String NOTFOUND_SERVICE_IN_WSDL(Object paramObject1, Object paramObject2) { return localizer.localize(localizableNOTFOUND_SERVICE_IN_WSDL(paramObject1, paramObject2)); }
  
  public static Localizable localizableNULL_EPR() { return messageFactory.getMessage("null.epr", new Object[0]); }
  
  public static String NULL_EPR() { return localizer.localize(localizableNULL_EPR()); }
  
  public static Localizable localizableNULL_WSDL() { return messageFactory.getMessage("null.wsdl", new Object[0]); }
  
  public static String NULL_WSDL() { return localizer.localize(localizableNULL_WSDL()); }
  
  public static Localizable localizableNOTFOUND_PORT_IN_WSDL(Object paramObject1, Object paramObject2, Object paramObject3) { return messageFactory.getMessage("notfound.port.in.wsdl", new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  public static String NOTFOUND_PORT_IN_WSDL(Object paramObject1, Object paramObject2, Object paramObject3) { return localizer.localize(localizableNOTFOUND_PORT_IN_WSDL(paramObject1, paramObject2, paramObject3)); }
  
  public static Localizable localizableERROR_WSDL(Object paramObject) { return messageFactory.getMessage("error.wsdl", new Object[] { paramObject }); }
  
  public static String ERROR_WSDL(Object paramObject) { return localizer.localize(localizableERROR_WSDL(paramObject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\ProviderApiMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */