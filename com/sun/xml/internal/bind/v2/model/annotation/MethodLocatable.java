package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class MethodLocatable<M> extends Object implements Locatable {
  private final Locatable upstream;
  
  private final M method;
  
  private final Navigator<?, ?, ?, M> nav;
  
  public MethodLocatable(Locatable paramLocatable, M paramM, Navigator<?, ?, ?, M> paramNavigator) {
    this.upstream = paramLocatable;
    this.method = paramM;
    this.nav = paramNavigator;
  }
  
  public Locatable getUpstream() { return this.upstream; }
  
  public Location getLocation() { return this.nav.getMethodLocation(this.method); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\MethodLocatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */