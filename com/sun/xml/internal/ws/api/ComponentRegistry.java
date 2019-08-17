package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import java.util.Set;

public interface ComponentRegistry extends Component {
  @NotNull
  Set<Component> getComponents();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\ComponentRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */