package sun.reflect.generics.repository;

import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.visitor.Reifier;

public abstract class AbstractRepository<T extends Tree> extends Object {
  private final GenericsFactory factory;
  
  private final T tree;
  
  private GenericsFactory getFactory() { return this.factory; }
  
  protected T getTree() { return (T)this.tree; }
  
  protected Reifier getReifier() { return Reifier.make(getFactory()); }
  
  protected AbstractRepository(String paramString, GenericsFactory paramGenericsFactory) {
    this.tree = parse(paramString);
    this.factory = paramGenericsFactory;
  }
  
  protected abstract T parse(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\repository\AbstractRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */