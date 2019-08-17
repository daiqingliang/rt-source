package com.sun.corba.se.impl.corba;

import java.util.Vector;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ORB;

public class ContextListImpl extends ContextList {
  private final int INITIAL_CAPACITY = 2;
  
  private final int CAPACITY_INCREMENT = 2;
  
  private ORB _orb;
  
  private Vector _contexts;
  
  public ContextListImpl(ORB paramORB) {
    this._orb = paramORB;
    this._contexts = new Vector(2, 2);
  }
  
  public int count() { return this._contexts.size(); }
  
  public void add(String paramString) { this._contexts.addElement(paramString); }
  
  public String item(int paramInt) throws Bounds {
    try {
      return (String)this._contexts.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new Bounds();
    } 
  }
  
  public void remove(int paramInt) throws Bounds {
    try {
      this._contexts.removeElementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new Bounds();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\ContextListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */