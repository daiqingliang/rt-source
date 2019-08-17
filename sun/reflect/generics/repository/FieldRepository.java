package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class FieldRepository extends AbstractRepository<TypeSignature> {
  private Type genericType;
  
  protected FieldRepository(String paramString, GenericsFactory paramGenericsFactory) { super(paramString, paramGenericsFactory); }
  
  protected TypeSignature parse(String paramString) { return SignatureParser.make().parseTypeSig(paramString); }
  
  public static FieldRepository make(String paramString, GenericsFactory paramGenericsFactory) { return new FieldRepository(paramString, paramGenericsFactory); }
  
  public Type getGenericType() {
    if (this.genericType == null) {
      Reifier reifier = getReifier();
      ((TypeSignature)getTree()).accept(reifier);
      this.genericType = reifier.getResult();
    } 
    return this.genericType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\repository\FieldRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */