package sun.reflect.generics.repository;

import java.lang.reflect.TypeVariable;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.Signature;
import sun.reflect.generics.visitor.Reifier;

public abstract class GenericDeclRepository<S extends Signature> extends AbstractRepository<S> {
  protected GenericDeclRepository(String paramString, GenericsFactory paramGenericsFactory) { super(paramString, paramGenericsFactory); }
  
  public TypeVariable<?>[] getTypeParameters() {
    TypeVariable[] arrayOfTypeVariable = this.typeParams;
    if (arrayOfTypeVariable == null) {
      FormalTypeParameter[] arrayOfFormalTypeParameter = ((Signature)getTree()).getFormalTypeParameters();
      arrayOfTypeVariable = new TypeVariable[arrayOfFormalTypeParameter.length];
      for (byte b = 0; b < arrayOfFormalTypeParameter.length; b++) {
        Reifier reifier = getReifier();
        arrayOfFormalTypeParameter[b].accept(reifier);
        arrayOfTypeVariable[b] = (TypeVariable)reifier.getResult();
      } 
      this.typeParams = arrayOfTypeVariable;
    } 
    return (TypeVariable[])arrayOfTypeVariable.clone();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\repository\GenericDeclRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */