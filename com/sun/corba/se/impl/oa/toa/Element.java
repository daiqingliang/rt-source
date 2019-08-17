package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;

final class Element {
  Object servant = null;
  
  Object servantData = null;
  
  int index = -1;
  
  int counter = 0;
  
  boolean valid = false;
  
  Element(int paramInt, Object paramObject) {
    this.servant = paramObject;
    this.index = paramInt;
  }
  
  byte[] getKey(Object paramObject1, Object paramObject2) {
    this.servant = paramObject1;
    this.servantData = paramObject2;
    this.valid = true;
    return toBytes();
  }
  
  byte[] toBytes() {
    byte[] arrayOfByte = new byte[8];
    ORBUtility.intToBytes(this.index, arrayOfByte, 0);
    ORBUtility.intToBytes(this.counter, arrayOfByte, 4);
    return arrayOfByte;
  }
  
  void delete(Element paramElement) {
    if (!this.valid)
      return; 
    this.counter++;
    this.servantData = null;
    this.valid = false;
    this.servant = paramElement;
  }
  
  public String toString() { return "Element[" + this.index + ", " + this.counter + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\toa\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */