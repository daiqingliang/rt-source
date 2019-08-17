package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class Wildcard implements TypeArgument {
  private FieldTypeSignature[] upperBounds;
  
  private FieldTypeSignature[] lowerBounds;
  
  private static final FieldTypeSignature[] emptyBounds = new FieldTypeSignature[0];
  
  private Wildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2) {
    this.upperBounds = paramArrayOfFieldTypeSignature1;
    this.lowerBounds = paramArrayOfFieldTypeSignature2;
  }
  
  public static Wildcard make(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2) { return new Wildcard(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2); }
  
  public FieldTypeSignature[] getUpperBounds() { return this.upperBounds; }
  
  public FieldTypeSignature[] getLowerBounds() { return (this.lowerBounds.length == 1 && this.lowerBounds[false] == BottomSignature.make()) ? emptyBounds : this.lowerBounds; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitWildcard(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\Wildcard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */