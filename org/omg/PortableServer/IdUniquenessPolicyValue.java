package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class IdUniquenessPolicyValue implements IDLEntity {
  private int __value;
  
  private static int __size = 2;
  
  private static IdUniquenessPolicyValue[] __array = new IdUniquenessPolicyValue[__size];
  
  public static final int _UNIQUE_ID = 0;
  
  public static final IdUniquenessPolicyValue UNIQUE_ID = new IdUniquenessPolicyValue(0);
  
  public static final int _MULTIPLE_ID = 1;
  
  public static final IdUniquenessPolicyValue MULTIPLE_ID = new IdUniquenessPolicyValue(1);
  
  public int value() { return this.__value; }
  
  public static IdUniquenessPolicyValue from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected IdUniquenessPolicyValue(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\IdUniquenessPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */