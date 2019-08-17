package com.sun.xml.internal.bind.v2.model.core;

public interface ValuePropertyInfo<T, C> extends PropertyInfo<T, C>, NonElementRef<T, C> {
  Adapter<T, C> getAdapter();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\ValuePropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */