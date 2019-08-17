package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class WsdlmodelMessages {
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.wsdlmodel");
  
  private static final Localizer localizer = new Localizer();
  
  public static Localizable localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object paramObject1, Object paramObject2, Object paramObject3) { return messageFactory.getMessage("wsdl.portaddress.epraddress.not.match", new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  public static String WSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object paramObject1, Object paramObject2, Object paramObject3) { return localizer.localize(localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(paramObject1, paramObject2, paramObject3)); }
  
  public static Localizable localizableWSDL_IMPORT_SHOULD_BE_WSDL(Object paramObject) { return messageFactory.getMessage("wsdl.import.should.be.wsdl", new Object[] { paramObject }); }
  
  public static String WSDL_IMPORT_SHOULD_BE_WSDL(Object paramObject) { return localizer.localize(localizableWSDL_IMPORT_SHOULD_BE_WSDL(paramObject)); }
  
  public static Localizable localizableMEX_METADATA_SYSTEMID_NULL() { return messageFactory.getMessage("Mex.metadata.systemid.null", new Object[0]); }
  
  public static String MEX_METADATA_SYSTEMID_NULL() { return localizer.localize(localizableMEX_METADATA_SYSTEMID_NULL()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\resources\WsdlmodelMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */