package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public abstract class UserException extends Exception implements IDLEntity {
  protected UserException() {}
  
  protected UserException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UserException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */