package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingMethodAccessorImpl extends MethodAccessorImpl {
  private MethodAccessorImpl delegate;
  
  DelegatingMethodAccessorImpl(MethodAccessorImpl paramMethodAccessorImpl) { setDelegate(paramMethodAccessorImpl); }
  
  public Object invoke(Object paramObject, Object[] paramArrayOfObject) throws IllegalArgumentException, InvocationTargetException { return this.delegate.invoke(paramObject, paramArrayOfObject); }
  
  void setDelegate(MethodAccessorImpl paramMethodAccessorImpl) { this.delegate = paramMethodAccessorImpl; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\DelegatingMethodAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */