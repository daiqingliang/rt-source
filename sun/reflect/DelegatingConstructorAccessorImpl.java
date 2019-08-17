package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingConstructorAccessorImpl extends ConstructorAccessorImpl {
  private ConstructorAccessorImpl delegate;
  
  DelegatingConstructorAccessorImpl(ConstructorAccessorImpl paramConstructorAccessorImpl) { setDelegate(paramConstructorAccessorImpl); }
  
  public Object newInstance(Object[] paramArrayOfObject) throws InstantiationException, IllegalArgumentException, InvocationTargetException { return this.delegate.newInstance(paramArrayOfObject); }
  
  void setDelegate(ConstructorAccessorImpl paramConstructorAccessorImpl) { this.delegate = paramConstructorAccessorImpl; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\DelegatingConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */