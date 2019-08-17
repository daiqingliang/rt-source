package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class InstantiationExceptionConstructorAccessorImpl extends ConstructorAccessorImpl {
  private final String message;
  
  InstantiationExceptionConstructorAccessorImpl(String paramString) { this.message = paramString; }
  
  public Object newInstance(Object[] paramArrayOfObject) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
    if (this.message == null)
      throw new InstantiationException(); 
    throw new InstantiationException(this.message);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\InstantiationExceptionConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */