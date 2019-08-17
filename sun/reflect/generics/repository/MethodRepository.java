package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class MethodRepository extends ConstructorRepository {
  private Type returnType;
  
  private MethodRepository(String paramString, GenericsFactory paramGenericsFactory) { super(paramString, paramGenericsFactory); }
  
  public static MethodRepository make(String paramString, GenericsFactory paramGenericsFactory) { return new MethodRepository(paramString, paramGenericsFactory); }
  
  public Type getReturnType() {
    if (this.returnType == null) {
      Reifier reifier = getReifier();
      ((MethodTypeSignature)getTree()).getReturnType().accept(reifier);
      this.returnType = reifier.getResult();
    } 
    return this.returnType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\repository\MethodRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */