package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class CharSignature implements BaseType {
  private static final CharSignature singleton = new CharSignature();
  
  public static CharSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitCharSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\CharSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */