package com.oracle.webservices.internal.api.databinding;

import java.lang.reflect.Method;

public interface JavaCallInfo {
  Method getMethod();
  
  Object[] getParameters();
  
  Object getReturnValue();
  
  void setReturnValue(Object paramObject);
  
  Throwable getException();
  
  void setException(Throwable paramThrowable);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\databinding\JavaCallInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */