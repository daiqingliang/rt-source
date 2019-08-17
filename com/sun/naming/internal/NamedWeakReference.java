package com.sun.naming.internal;

import java.lang.ref.WeakReference;

class NamedWeakReference<T> extends WeakReference<T> {
  private final String name;
  
  NamedWeakReference(T paramT, String paramString) {
    super(paramT);
    this.name = paramString;
  }
  
  String getName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\naming\internal\NamedWeakReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */