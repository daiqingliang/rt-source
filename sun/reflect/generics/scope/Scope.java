package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public interface Scope {
  TypeVariable<?> lookup(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\scope\Scope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */