package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ServantRetentionPolicyValue implements IDLEntity {
  private int __value;
  
  private static int __size = 2;
  
  private static ServantRetentionPolicyValue[] __array = new ServantRetentionPolicyValue[__size];
  
  public static final int _RETAIN = 0;
  
  public static final ServantRetentionPolicyValue RETAIN = new ServantRetentionPolicyValue(0);
  
  public static final int _NON_RETAIN = 1;
  
  public static final ServantRetentionPolicyValue NON_RETAIN = new ServantRetentionPolicyValue(1);
  
  public int value() { return this.__value; }
  
  public static ServantRetentionPolicyValue from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected ServantRetentionPolicyValue(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\ServantRetentionPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */