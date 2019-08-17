package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class IdAssignmentPolicyValue implements IDLEntity {
  private int __value;
  
  private static int __size = 2;
  
  private static IdAssignmentPolicyValue[] __array = new IdAssignmentPolicyValue[__size];
  
  public static final int _USER_ID = 0;
  
  public static final IdAssignmentPolicyValue USER_ID = new IdAssignmentPolicyValue(0);
  
  public static final int _SYSTEM_ID = 1;
  
  public static final IdAssignmentPolicyValue SYSTEM_ID = new IdAssignmentPolicyValue(1);
  
  public int value() { return this.__value; }
  
  public static IdAssignmentPolicyValue from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected IdAssignmentPolicyValue(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\IdAssignmentPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */