package sun.reflect.generics.scope;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public abstract class AbstractScope<D extends GenericDeclaration> extends Object implements Scope {
  private final D recvr;
  
  protected AbstractScope(D paramD) { this.recvr = paramD; }
  
  protected D getRecvr() { return (D)this.recvr; }
  
  protected abstract Scope computeEnclosingScope();
  
  protected Scope getEnclosingScope() {
    Scope scope = this.enclosingScope;
    if (scope == null) {
      scope = computeEnclosingScope();
      this.enclosingScope = scope;
    } 
    return scope;
  }
  
  public TypeVariable<?> lookup(String paramString) {
    TypeVariable[] arrayOfTypeVariable = getRecvr().getTypeParameters();
    for (TypeVariable typeVariable : arrayOfTypeVariable) {
      if (typeVariable.getName().equals(paramString))
        return typeVariable; 
    } 
    return getEnclosingScope().lookup(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\scope\AbstractScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */