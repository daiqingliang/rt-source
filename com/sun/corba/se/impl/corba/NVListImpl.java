package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;
import java.util.Vector;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;

public class NVListImpl extends NVList {
  private final int INITIAL_CAPACITY = 4;
  
  private final int CAPACITY_INCREMENT = 2;
  
  private Vector _namedValues;
  
  private ORB orb;
  
  public NVListImpl(ORB paramORB) {
    this.orb = paramORB;
    this._namedValues = new Vector(4, 2);
  }
  
  public NVListImpl(ORB paramORB, int paramInt) {
    this.orb = paramORB;
    this._namedValues = new Vector(paramInt);
  }
  
  public int count() { return this._namedValues.size(); }
  
  public NamedValue add(int paramInt) {
    NamedValueImpl namedValueImpl = new NamedValueImpl(this.orb, "", new AnyImpl(this.orb), paramInt);
    this._namedValues.addElement(namedValueImpl);
    return namedValueImpl;
  }
  
  public NamedValue add_item(String paramString, int paramInt) {
    NamedValueImpl namedValueImpl = new NamedValueImpl(this.orb, paramString, new AnyImpl(this.orb), paramInt);
    this._namedValues.addElement(namedValueImpl);
    return namedValueImpl;
  }
  
  public NamedValue add_value(String paramString, Any paramAny, int paramInt) {
    NamedValueImpl namedValueImpl = new NamedValueImpl(this.orb, paramString, paramAny, paramInt);
    this._namedValues.addElement(namedValueImpl);
    return namedValueImpl;
  }
  
  public NamedValue item(int paramInt) {
    try {
      return (NamedValue)this._namedValues.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new Bounds();
    } 
  }
  
  public void remove(int paramInt) throws Bounds {
    try {
      this._namedValues.removeElementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new Bounds();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\NVListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */