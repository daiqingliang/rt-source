package com.oracle.webservices.internal.api.databinding;

import com.sun.xml.internal.ws.api.ServiceSharedFeatureMarker;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.WebServiceFeature;

public class DatabindingModeFeature extends WebServiceFeature implements ServiceSharedFeatureMarker {
  public static final String ID = "http://jax-ws.java.net/features/databinding";
  
  public static final String GLASSFISH_JAXB = "glassfish.jaxb";
  
  private String mode;
  
  private Map<String, Object> properties;
  
  public DatabindingModeFeature(String paramString) {
    this.mode = paramString;
    this.properties = new HashMap();
  }
  
  public String getMode() { return this.mode; }
  
  public String getID() { return "http://jax-ws.java.net/features/databinding"; }
  
  public Map<String, Object> getProperties() { return this.properties; }
  
  public static Builder builder() { return new Builder(new DatabindingModeFeature(null)); }
  
  public static final class Builder {
    private final DatabindingModeFeature o;
    
    Builder(DatabindingModeFeature param1DatabindingModeFeature) { this.o = param1DatabindingModeFeature; }
    
    public DatabindingModeFeature build() { return this.o; }
    
    public Builder value(String param1String) {
      this.o.mode = param1String;
      return this;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\databinding\DatabindingModeFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */