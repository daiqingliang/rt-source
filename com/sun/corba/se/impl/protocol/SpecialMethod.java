package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

public abstract class SpecialMethod {
  static SpecialMethod[] methods = { new IsA(), new GetInterface(), new NonExistent(), new NotExistent() };
  
  public abstract boolean isNonExistentMethod();
  
  public abstract String getName();
  
  public abstract CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter);
  
  public static final SpecialMethod getSpecialMethod(String paramString) {
    for (byte b = 0; b < methods.length; b++) {
      if (methods[b].getName().equals(paramString))
        return methods[b]; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\SpecialMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */