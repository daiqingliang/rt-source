package com.sun.org.omg.CORBA;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class OperationMode implements IDLEntity {
  private int __value;
  
  private static int __size = 2;
  
  private static OperationMode[] __array = new OperationMode[__size];
  
  public static final int _OP_NORMAL = 0;
  
  public static final OperationMode OP_NORMAL = new OperationMode(0);
  
  public static final int _OP_ONEWAY = 1;
  
  public static final OperationMode OP_ONEWAY = new OperationMode(1);
  
  public int value() { return this.__value; }
  
  public static OperationMode from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected OperationMode(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\OperationMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */