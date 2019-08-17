package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public interface MethodAccessor {
  Object invoke(Object paramObject, Object[] paramArrayOfObject) throws IllegalArgumentException, InvocationTargetException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\MethodAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */