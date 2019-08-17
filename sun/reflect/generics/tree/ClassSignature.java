package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class ClassSignature implements Signature {
  private final FormalTypeParameter[] formalTypeParams;
  
  private final ClassTypeSignature superclass;
  
  private final ClassTypeSignature[] superInterfaces;
  
  private ClassSignature(FormalTypeParameter[] paramArrayOfFormalTypeParameter, ClassTypeSignature paramClassTypeSignature, ClassTypeSignature[] paramArrayOfClassTypeSignature) {
    this.formalTypeParams = paramArrayOfFormalTypeParameter;
    this.superclass = paramClassTypeSignature;
    this.superInterfaces = paramArrayOfClassTypeSignature;
  }
  
  public static ClassSignature make(FormalTypeParameter[] paramArrayOfFormalTypeParameter, ClassTypeSignature paramClassTypeSignature, ClassTypeSignature[] paramArrayOfClassTypeSignature) { return new ClassSignature(paramArrayOfFormalTypeParameter, paramClassTypeSignature, paramArrayOfClassTypeSignature); }
  
  public FormalTypeParameter[] getFormalTypeParameters() { return this.formalTypeParams; }
  
  public ClassTypeSignature getSuperclass() { return this.superclass; }
  
  public ClassTypeSignature[] getSuperInterfaces() { return this.superInterfaces; }
  
  public void accept(Visitor<?> paramVisitor) { paramVisitor.visitClassSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\ClassSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */