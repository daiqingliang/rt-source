package sun.reflect.generics.tree;

import java.util.List;
import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ClassTypeSignature implements FieldTypeSignature {
  private final List<SimpleClassTypeSignature> path;
  
  private ClassTypeSignature(List<SimpleClassTypeSignature> paramList) { this.path = paramList; }
  
  public static ClassTypeSignature make(List<SimpleClassTypeSignature> paramList) { return new ClassTypeSignature(paramList); }
  
  public List<SimpleClassTypeSignature> getPath() { return this.path; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitClassTypeSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\ClassTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */