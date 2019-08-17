package java.lang.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.TypeVariable;

public interface GenericDeclaration extends AnnotatedElement {
  TypeVariable<?>[] getTypeParameters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\GenericDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */