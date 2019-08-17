package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public class DummyScope implements Scope {
  private static final DummyScope singleton = new DummyScope();
  
  public static DummyScope make() { return singleton; }
  
  public TypeVariable<?> lookup(String paramString) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\scope\DummyScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */