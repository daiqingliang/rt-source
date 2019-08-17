package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class BooleanSignature implements BaseType {
  private static final BooleanSignature singleton = new BooleanSignature();
  
  public static BooleanSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitBooleanSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\BooleanSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */