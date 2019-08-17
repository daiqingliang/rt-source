package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.DataInputStream;
import java.io.IOException;

public final class Method extends FieldOrMethod {
  public Method() {}
  
  public Method(Method paramMethod) { super(paramMethod); }
  
  Method(DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException, ClassFormatException { super(paramDataInputStream, paramConstantPool); }
  
  public Method(int paramInt1, int paramInt2, int paramInt3, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool) { super(paramInt1, paramInt2, paramInt3, paramArrayOfAttribute, paramConstantPool); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitMethod(this); }
  
  public final Code getCode() {
    for (byte b = 0; b < this.attributes_count; b++) {
      if (this.attributes[b] instanceof Code)
        return (Code)this.attributes[b]; 
    } 
    return null;
  }
  
  public final ExceptionTable getExceptionTable() {
    for (byte b = 0; b < this.attributes_count; b++) {
      if (this.attributes[b] instanceof ExceptionTable)
        return (ExceptionTable)this.attributes[b]; 
    } 
    return null;
  }
  
  public final LocalVariableTable getLocalVariableTable() {
    Code code = getCode();
    return (code != null) ? code.getLocalVariableTable() : null;
  }
  
  public final LineNumberTable getLineNumberTable() {
    Code code = getCode();
    return (code != null) ? code.getLineNumberTable() : null;
  }
  
  public final String toString() {
    String str3 = Utility.accessToString(this.access_flags);
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
    String str2 = constantUtf8.getBytes();
    constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.name_index, (byte)1);
    String str1 = constantUtf8.getBytes();
    str2 = Utility.methodSignatureToString(str2, str1, str3, true, getLocalVariableTable());
    StringBuffer stringBuffer = new StringBuffer(str2);
    for (byte b = 0; b < this.attributes_count; b++) {
      Attribute attribute = this.attributes[b];
      if (!(attribute instanceof Code) && !(attribute instanceof ExceptionTable))
        stringBuffer.append(" [" + attribute.toString() + "]"); 
    } 
    ExceptionTable exceptionTable = getExceptionTable();
    if (exceptionTable != null) {
      String str = exceptionTable.toString();
      if (!str.equals(""))
        stringBuffer.append("\n\t\tthrows " + str); 
    } 
    return stringBuffer.toString();
  }
  
  public final Method copy(ConstantPool paramConstantPool) { return (Method)copy_(paramConstantPool); }
  
  public Type getReturnType() { return Type.getReturnType(getSignature()); }
  
  public Type[] getArgumentTypes() { return Type.getArgumentTypes(getSignature()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Method.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */