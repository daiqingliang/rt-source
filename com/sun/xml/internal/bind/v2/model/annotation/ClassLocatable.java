package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class ClassLocatable<C> extends Object implements Locatable {
  private final Locatable upstream;
  
  private final C clazz;
  
  private final Navigator<?, C, ?, ?> nav;
  
  public ClassLocatable(Locatable paramLocatable, C paramC, Navigator<?, C, ?, ?> paramNavigator) {
    this.upstream = paramLocatable;
    this.clazz = paramC;
    this.nav = paramNavigator;
  }
  
  public Locatable getUpstream() { return this.upstream; }
  
  public Location getLocation() { return this.nav.getClassLocation(this.clazz); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\ClassLocatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */