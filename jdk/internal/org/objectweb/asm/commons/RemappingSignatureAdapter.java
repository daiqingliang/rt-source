package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter extends SignatureVisitor {
  private final SignatureVisitor v;
  
  private final Remapper remapper;
  
  private String className;
  
  public RemappingSignatureAdapter(SignatureVisitor paramSignatureVisitor, Remapper paramRemapper) { this(327680, paramSignatureVisitor, paramRemapper); }
  
  protected RemappingSignatureAdapter(int paramInt, SignatureVisitor paramSignatureVisitor, Remapper paramRemapper) {
    super(paramInt);
    this.v = paramSignatureVisitor;
    this.remapper = paramRemapper;
  }
  
  public void visitClassType(String paramString) {
    this.className = paramString;
    this.v.visitClassType(this.remapper.mapType(paramString));
  }
  
  public void visitInnerClassType(String paramString) {
    String str1 = this.remapper.mapType(this.className) + '$';
    this.className += '$' + paramString;
    String str2 = this.remapper.mapType(this.className);
    int i = str2.startsWith(str1) ? str1.length() : (str2.lastIndexOf('$') + 1);
    this.v.visitInnerClassType(str2.substring(i));
  }
  
  public void visitFormalTypeParameter(String paramString) { this.v.visitFormalTypeParameter(paramString); }
  
  public void visitTypeVariable(String paramString) { this.v.visitTypeVariable(paramString); }
  
  public SignatureVisitor visitArrayType() {
    this.v.visitArrayType();
    return this;
  }
  
  public void visitBaseType(char paramChar) { this.v.visitBaseType(paramChar); }
  
  public SignatureVisitor visitClassBound() {
    this.v.visitClassBound();
    return this;
  }
  
  public SignatureVisitor visitExceptionType() {
    this.v.visitExceptionType();
    return this;
  }
  
  public SignatureVisitor visitInterface() {
    this.v.visitInterface();
    return this;
  }
  
  public SignatureVisitor visitInterfaceBound() {
    this.v.visitInterfaceBound();
    return this;
  }
  
  public SignatureVisitor visitParameterType() {
    this.v.visitParameterType();
    return this;
  }
  
  public SignatureVisitor visitReturnType() {
    this.v.visitReturnType();
    return this;
  }
  
  public SignatureVisitor visitSuperclass() {
    this.v.visitSuperclass();
    return this;
  }
  
  public void visitTypeArgument() { this.v.visitTypeArgument(); }
  
  public SignatureVisitor visitTypeArgument(char paramChar) {
    this.v.visitTypeArgument(paramChar);
    return this;
  }
  
  public void visitEnd() { this.v.visitEnd(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingSignatureAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */