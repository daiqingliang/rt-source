package sun.reflect.generics.reflectiveObjects;

import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.visitor.Reifier;

public abstract class LazyReflectiveObjectGenerator {
  private final GenericsFactory factory;
  
  protected LazyReflectiveObjectGenerator(GenericsFactory paramGenericsFactory) { this.factory = paramGenericsFactory; }
  
  private GenericsFactory getFactory() { return this.factory; }
  
  protected Reifier getReifier() { return Reifier.make(getFactory()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\reflectiveObjects\LazyReflectiveObjectGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */