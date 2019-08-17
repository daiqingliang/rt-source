package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FloatSignature implements BaseType {
  private static final FloatSignature singleton = new FloatSignature();
  
  public static FloatSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitFloatSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\FloatSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */