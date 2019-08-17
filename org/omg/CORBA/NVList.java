package org.omg.CORBA;

public abstract class NVList {
  public abstract int count();
  
  public abstract NamedValue add(int paramInt);
  
  public abstract NamedValue add_item(String paramString, int paramInt);
  
  public abstract NamedValue add_value(String paramString, Any paramAny, int paramInt);
  
  public abstract NamedValue item(int paramInt);
  
  public abstract void remove(int paramInt) throws Bounds;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\NVList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */