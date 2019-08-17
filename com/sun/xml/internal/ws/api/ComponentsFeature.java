package com.sun.xml.internal.ws.api;

import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ComponentsFeature extends WebServiceFeature implements ServiceSharedFeatureMarker {
  private final List<ComponentFeature> componentFeatures;
  
  public ComponentsFeature(List<ComponentFeature> paramList) { this.componentFeatures = paramList; }
  
  public String getID() { return ComponentsFeature.class.getName(); }
  
  public List<ComponentFeature> getComponentFeatures() { return this.componentFeatures; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\ComponentsFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */