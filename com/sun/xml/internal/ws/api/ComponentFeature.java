package com.sun.xml.internal.ws.api;

import javax.xml.ws.WebServiceFeature;

public class ComponentFeature extends WebServiceFeature implements ServiceSharedFeatureMarker {
  private final Component component;
  
  private final Target target;
  
  public ComponentFeature(Component paramComponent) { this(paramComponent, Target.CONTAINER); }
  
  public ComponentFeature(Component paramComponent, Target paramTarget) {
    this.component = paramComponent;
    this.target = paramTarget;
  }
  
  public String getID() { return ComponentFeature.class.getName(); }
  
  public Component getComponent() { return this.component; }
  
  public Target getTarget() { return this.target; }
  
  public enum Target {
    CONTAINER, ENDPOINT, SERVICE, STUB;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\ComponentFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */