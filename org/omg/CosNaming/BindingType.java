package org.omg.CosNaming;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class BindingType implements IDLEntity {
  private int __value;
  
  private static int __size = 2;
  
  private static BindingType[] __array = new BindingType[__size];
  
  public static final int _nobject = 0;
  
  public static final BindingType nobject = new BindingType(0);
  
  public static final int _ncontext = 1;
  
  public static final BindingType ncontext = new BindingType(1);
  
  public int value() { return this.__value; }
  
  public static BindingType from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected BindingType(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\BindingType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */