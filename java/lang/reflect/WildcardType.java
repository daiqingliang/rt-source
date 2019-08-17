package java.lang.reflect;

import java.lang.reflect.Type;

public interface WildcardType extends Type {
  Type[] getUpperBounds();
  
  Type[] getLowerBounds();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\WildcardType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */