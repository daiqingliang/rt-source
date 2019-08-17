package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Container implements ComponentRegistry, ComponentEx {
  private final Set<Component> components = new CopyOnWriteArraySet();
  
  public static final Container NONE = new NoneContainer(null);
  
  public <S> S getSPI(Class<S> paramClass) {
    if (this.components == null)
      return null; 
    for (Component component : this.components) {
      Object object = component.getSPI(paramClass);
      if (object != null)
        return (S)object; 
    } 
    return null;
  }
  
  public Set<Component> getComponents() { return this.components; }
  
  @NotNull
  public <E> Iterable<E> getIterableSPI(Class<E> paramClass) {
    Object object = getSPI(paramClass);
    return (object != null) ? Collections.singletonList(object) : Collections.emptySet();
  }
  
  private static final class NoneContainer extends Container {
    private NoneContainer() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\Container.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */