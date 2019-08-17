package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FormalTypeParameter implements TypeTree {
  private final String name;
  
  private final FieldTypeSignature[] bounds;
  
  private FormalTypeParameter(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature) {
    this.name = paramString;
    this.bounds = paramArrayOfFieldTypeSignature;
  }
  
  public static FormalTypeParameter make(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature) { return new FormalTypeParameter(paramString, paramArrayOfFieldTypeSignature); }
  
  public FieldTypeSignature[] getBounds() { return this.bounds; }
  
  public String getName() { return this.name; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitFormalTypeParameter(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\FormalTypeParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */