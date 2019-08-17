package com.sun.xml.internal.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class AnyTypeAdapter extends XmlAdapter<Object, Object> {
  public Object unmarshal(Object paramObject) { return paramObject; }
  
  public Object marshal(Object paramObject) { return paramObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\AnyTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */