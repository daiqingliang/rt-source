package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ByteSignature implements BaseType {
  private static final ByteSignature singleton = new ByteSignature();
  
  public static ByteSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitByteSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\ByteSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */