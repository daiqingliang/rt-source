package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;
import java.io.File;
import java.util.Map;

public class DatabindingProviderImpl implements DatabindingProvider {
  private static final String CachedDatabinding = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
  
  Map<String, Object> properties;
  
  public void init(Map<String, Object> paramMap) { this.properties = paramMap; }
  
  DatabindingImpl getCachedDatabindingImpl(DatabindingConfig paramDatabindingConfig) {
    Object object = paramDatabindingConfig.properties().get("com.sun.xml.internal.ws.db.DatabindingProviderImpl");
    return (object != null && object instanceof DatabindingImpl) ? (DatabindingImpl)object : null;
  }
  
  public Databinding create(DatabindingConfig paramDatabindingConfig) {
    DatabindingImpl databindingImpl = getCachedDatabindingImpl(paramDatabindingConfig);
    if (databindingImpl == null) {
      databindingImpl = new DatabindingImpl(this, paramDatabindingConfig);
      paramDatabindingConfig.properties().put("com.sun.xml.internal.ws.db.DatabindingProviderImpl", databindingImpl);
    } 
    return databindingImpl;
  }
  
  public WSDLGenerator wsdlGen(DatabindingConfig paramDatabindingConfig) {
    DatabindingImpl databindingImpl = (DatabindingImpl)create(paramDatabindingConfig);
    return new JaxwsWsdlGen(databindingImpl);
  }
  
  public boolean isFor(String paramString) { return true; }
  
  public static class JaxwsWsdlGen implements WSDLGenerator {
    DatabindingImpl databinding;
    
    WSDLGenInfo wsdlGenInfo;
    
    JaxwsWsdlGen(DatabindingImpl param1DatabindingImpl) {
      this.databinding = param1DatabindingImpl;
      this.wsdlGenInfo = new WSDLGenInfo();
    }
    
    public WSDLGenerator inlineSchema(boolean param1Boolean) {
      this.wsdlGenInfo.setInlineSchemas(param1Boolean);
      return this;
    }
    
    public WSDLGenerator property(String param1String, Object param1Object) { return this; }
    
    public void generate(WSDLResolver param1WSDLResolver) {
      this.wsdlGenInfo.setWsdlResolver(param1WSDLResolver);
      this.databinding.generateWSDL(this.wsdlGenInfo);
    }
    
    public void generate(File param1File, String param1String) { this.databinding.generateWSDL(this.wsdlGenInfo); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\db\DatabindingProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */