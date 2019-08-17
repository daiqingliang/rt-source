package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class LongSignature implements BaseType {
  private static final LongSignature singleton = new LongSignature();
  
  public static LongSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitLongSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\LongSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */