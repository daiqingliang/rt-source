package sun.reflect;

import java.lang.reflect.InvocationTargetException;

abstract class ConstructorAccessorImpl extends MagicAccessorImpl implements ConstructorAccessor {
  public abstract Object newInstance(Object[] paramArrayOfObject) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\ConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */