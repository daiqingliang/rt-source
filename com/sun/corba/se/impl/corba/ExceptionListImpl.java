package com.sun.corba.se.impl.corba;

import java.util.Vector;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.TypeCode;

public class ExceptionListImpl extends ExceptionList {
  private final int INITIAL_CAPACITY = 2;
  
  private final int CAPACITY_INCREMENT = 2;
  
  private Vector _exceptions = new Vector(2, 2);
  
  public int count() { return this._exceptions.size(); }
  
  public void add(TypeCode paramTypeCode) { this._exceptions.addElement(paramTypeCode); }
  
  public TypeCode item(int paramInt) throws Bounds {
    try {
      return (TypeCode)this._exceptions.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new Bounds();
    } 
  }
  
  public void remove(int paramInt) throws Bounds {
    try {
      this._exceptions.removeElementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new Bounds();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\ExceptionListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */