package java.lang.reflect;

import java.lang.reflect.AnnotatedType;

public interface AnnotatedWildcardType extends AnnotatedType {
  AnnotatedType[] getAnnotatedLowerBounds();
  
  AnnotatedType[] getAnnotatedUpperBounds();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\AnnotatedWildcardType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */