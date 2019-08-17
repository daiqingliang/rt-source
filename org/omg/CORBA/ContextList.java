package org.omg.CORBA;

public abstract class ContextList {
  public abstract int count();
  
  public abstract void add(String paramString);
  
  public abstract String item(int paramInt) throws Bounds;
  
  public abstract void remove(int paramInt) throws Bounds;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ContextList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */