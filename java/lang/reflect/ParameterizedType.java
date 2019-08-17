package java.lang.reflect;

import java.lang.reflect.Type;

public interface ParameterizedType extends Type {
  Type[] getActualTypeArguments();
  
  Type getRawType();
  
  Type getOwnerType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\ParameterizedType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */