package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class FieldLocatable<F> extends Object implements Locatable {
  private final Locatable upstream;
  
  private final F field;
  
  private final Navigator<?, ?, F, ?> nav;
  
  public FieldLocatable(Locatable paramLocatable, F paramF, Navigator<?, ?, F, ?> paramNavigator) {
    this.upstream = paramLocatable;
    this.field = paramF;
    this.nav = paramNavigator;
  }
  
  public Locatable getUpstream() { return this.upstream; }
  
  public Location getLocation() { return this.nav.getFieldLocation(this.field); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\FieldLocatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */