package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.ArrayList;
import java.util.Collection;

final class FinalArrayList<T> extends ArrayList<T> {
  public FinalArrayList(int paramInt) { super(paramInt); }
  
  public FinalArrayList() {}
  
  public FinalArrayList(Collection<? extends T> paramCollection) { super(paramCollection); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\FinalArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */