package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class DoubleSignature implements BaseType {
  private static final DoubleSignature singleton = new DoubleSignature();
  
  public static DoubleSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitDoubleSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\DoubleSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */