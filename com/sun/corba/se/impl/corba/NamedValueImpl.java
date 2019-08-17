package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;

public class NamedValueImpl extends NamedValue {
  private String _name;
  
  private Any _value;
  
  private int _flags;
  
  private ORB _orb;
  
  public NamedValueImpl(ORB paramORB) {
    this._orb = paramORB;
    this._value = new AnyImpl(this._orb);
  }
  
  public NamedValueImpl(ORB paramORB, String paramString, Any paramAny, int paramInt) {
    this._orb = paramORB;
    this._name = paramString;
    this._value = paramAny;
    this._flags = paramInt;
  }
  
  public String name() { return this._name; }
  
  public Any value() { return this._value; }
  
  public int flags() { return this._flags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\NamedValueImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */