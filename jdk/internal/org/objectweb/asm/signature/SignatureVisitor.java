package jdk.internal.org.objectweb.asm.signature;

public abstract class SignatureVisitor {
  public static final char EXTENDS = '+';
  
  public static final char SUPER = '-';
  
  public static final char INSTANCEOF = '=';
  
  protected final int api;
  
  public SignatureVisitor(int paramInt) {
    if (paramInt != 262144 && paramInt != 327680)
      throw new IllegalArgumentException(); 
    this.api = paramInt;
  }
  
  public void visitFormalTypeParameter(String paramString) {}
  
  public SignatureVisitor visitClassBound() { return this; }
  
  public SignatureVisitor visitInterfaceBound() { return this; }
  
  public SignatureVisitor visitSuperclass() { return this; }
  
  public SignatureVisitor visitInterface() { return this; }
  
  public SignatureVisitor visitParameterType() { return this; }
  
  public SignatureVisitor visitReturnType() { return this; }
  
  public SignatureVisitor visitExceptionType() { return this; }
  
  public void visitBaseType(char paramChar) {}
  
  public void visitTypeVariable(String paramString) {}
  
  public SignatureVisitor visitArrayType() { return this; }
  
  public void visitClassType(String paramString) {}
  
  public void visitInnerClassType(String paramString) {}
  
  public void visitTypeArgument() {}
  
  public SignatureVisitor visitTypeArgument(char paramChar) { return this; }
  
  public void visitEnd() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\signature\SignatureVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */