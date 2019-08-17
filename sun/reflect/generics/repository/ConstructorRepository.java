package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class ConstructorRepository extends GenericDeclRepository<MethodTypeSignature> {
  private Type[] paramTypes;
  
  private Type[] exceptionTypes;
  
  protected ConstructorRepository(String paramString, GenericsFactory paramGenericsFactory) { super(paramString, paramGenericsFactory); }
  
  protected MethodTypeSignature parse(String paramString) { return SignatureParser.make().parseMethodSig(paramString); }
  
  public static ConstructorRepository make(String paramString, GenericsFactory paramGenericsFactory) { return new ConstructorRepository(paramString, paramGenericsFactory); }
  
  public Type[] getParameterTypes() {
    if (this.paramTypes == null) {
      TypeSignature[] arrayOfTypeSignature = ((MethodTypeSignature)getTree()).getParameterTypes();
      Type[] arrayOfType = new Type[arrayOfTypeSignature.length];
      for (byte b = 0; b < arrayOfTypeSignature.length; b++) {
        Reifier reifier = getReifier();
        arrayOfTypeSignature[b].accept(reifier);
        arrayOfType[b] = reifier.getResult();
      } 
      this.paramTypes = arrayOfType;
    } 
    return (Type[])this.paramTypes.clone();
  }
  
  public Type[] getExceptionTypes() {
    if (this.exceptionTypes == null) {
      FieldTypeSignature[] arrayOfFieldTypeSignature = ((MethodTypeSignature)getTree()).getExceptionTypes();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (byte b = 0; b < arrayOfFieldTypeSignature.length; b++) {
        Reifier reifier = getReifier();
        arrayOfFieldTypeSignature[b].accept(reifier);
        arrayOfType[b] = reifier.getResult();
      } 
      this.exceptionTypes = arrayOfType;
    } 
    return (Type[])this.exceptionTypes.clone();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\repository\ConstructorRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */