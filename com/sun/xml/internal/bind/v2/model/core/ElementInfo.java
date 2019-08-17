package com.sun.xml.internal.bind.v2.model.core;

import java.util.Collection;

public interface ElementInfo<T, C> extends Element<T, C> {
  ElementPropertyInfo<T, C> getProperty();
  
  NonElement<T, C> getContentType();
  
  T getContentInMemoryType();
  
  T getType();
  
  ElementInfo<T, C> getSubstitutionHead();
  
  Collection<? extends ElementInfo<T, C>> getSubstitutionMembers();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\ElementInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */