package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class NotFoundReason implements IDLEntity {
  private int __value;
  
  private static int __size = 3;
  
  private static NotFoundReason[] __array = new NotFoundReason[__size];
  
  public static final int _missing_node = 0;
  
  public static final NotFoundReason missing_node = new NotFoundReason(0);
  
  public static final int _not_context = 1;
  
  public static final NotFoundReason not_context = new NotFoundReason(1);
  
  public static final int _not_object = 2;
  
  public static final NotFoundReason not_object = new NotFoundReason(2);
  
  public int value() { return this.__value; }
  
  public static NotFoundReason from_int(int paramInt) {
    if (paramInt >= 0 && paramInt < __size)
      return __array[paramInt]; 
    throw new BAD_PARAM();
  }
  
  protected NotFoundReason(int paramInt) {
    this.__value = paramInt;
    __array[this.__value] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFoundReason.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */