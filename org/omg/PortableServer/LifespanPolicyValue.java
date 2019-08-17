package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class LifespanPolicyValue implements IDLEntity {
  private int __value;
  
  private static int __size = 2;
  
  private static LifespanPolicyValue[] __array = new LifespanPolicyValue[__size];
  
  public static final int _TRANSIENT = 0;
  
  public static final LifespanPolicyValue TRANSIENT = new LifespanPolicyValue(0);
  
  public static final int _PERSISTENT = 1;
  
  public static final LifespanPolicyValue PERSISTENT = new LifespanPolicyValue(1);
  
  public int value() { return this.__value; }
  
  public static LifespanPolicyValue from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected LifespanPolicyValue(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\LifespanPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */