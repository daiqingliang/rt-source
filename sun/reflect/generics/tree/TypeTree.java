package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public interface TypeTree extends Tree {
  void accept(TypeTreeVisitor<?> paramTypeTreeVisitor);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\TypeTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */