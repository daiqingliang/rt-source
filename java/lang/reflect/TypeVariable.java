package java.lang.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;

public interface TypeVariable<D extends GenericDeclaration> extends Type, AnnotatedElement {
  Type[] getBounds();
  
  D getGenericDeclaration();
  
  String getName();
  
  AnnotatedType[] getAnnotatedBounds();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\TypeVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */