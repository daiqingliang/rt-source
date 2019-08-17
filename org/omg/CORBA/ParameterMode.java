package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class ParameterMode implements IDLEntity {
  private int __value;
  
  private static int __size = 3;
  
  private static ParameterMode[] __array = new ParameterMode[__size];
  
  public static final int _PARAM_IN = 0;
  
  public static final ParameterMode PARAM_IN = new ParameterMode(0);
  
  public static final int _PARAM_OUT = 1;
  
  public static final ParameterMode PARAM_OUT = new ParameterMode(1);
  
  public static final int _PARAM_INOUT = 2;
  
  public static final ParameterMode PARAM_INOUT = new ParameterMode(2);
  
  public int value() { return this.__value; }
  
  public static ParameterMode from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected ParameterMode(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ParameterMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */