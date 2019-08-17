package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.visitor.Reifier;

public class ClassRepository extends GenericDeclRepository<ClassSignature> {
  public static final ClassRepository NONE = make("Ljava/lang/Object;", null);
  
  private ClassRepository(String paramString, GenericsFactory paramGenericsFactory) { super(paramString, paramGenericsFactory); }
  
  protected ClassSignature parse(String paramString) { return SignatureParser.make().parseClassSig(paramString); }
  
  public static ClassRepository make(String paramString, GenericsFactory paramGenericsFactory) { return new ClassRepository(paramString, paramGenericsFactory); }
  
  public Type getSuperclass() {
    Type type = this.superclass;
    if (type == null) {
      Reifier reifier = getReifier();
      ((ClassSignature)getTree()).getSuperclass().accept(reifier);
      type = reifier.getResult();
      this.superclass = type;
    } 
    return type;
  }
  
  public Type[] getSuperInterfaces() {
    Type[] arrayOfType = this.superInterfaces;
    if (arrayOfType == null) {
      ClassTypeSignature[] arrayOfClassTypeSignature = ((ClassSignature)getTree()).getSuperInterfaces();
      arrayOfType = new Type[arrayOfClassTypeSignature.length];
      for (byte b = 0; b < arrayOfClassTypeSignature.length; b++) {
        Reifier reifier = getReifier();
        arrayOfClassTypeSignature[b].accept(reifier);
        arrayOfType[b] = reifier.getResult();
      } 
      this.superInterfaces = arrayOfType;
    } 
    return (Type[])arrayOfType.clone();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\repository\ClassRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */