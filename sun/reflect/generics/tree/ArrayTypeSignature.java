package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ArrayTypeSignature implements FieldTypeSignature {
  private final TypeSignature componentType;
  
  private ArrayTypeSignature(TypeSignature paramTypeSignature) { this.componentType = paramTypeSignature; }
  
  public static ArrayTypeSignature make(TypeSignature paramTypeSignature) { return new ArrayTypeSignature(paramTypeSignature); }
  
  public TypeSignature getComponentType() { return this.componentType; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitArrayTypeSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\ArrayTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */