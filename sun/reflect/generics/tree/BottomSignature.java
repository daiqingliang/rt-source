package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class BottomSignature implements FieldTypeSignature {
  private static final BottomSignature singleton = new BottomSignature();
  
  public static BottomSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitBottomSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\BottomSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */