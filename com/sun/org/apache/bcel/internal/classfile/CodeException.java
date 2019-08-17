package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public final class CodeException implements Cloneable, Constants, Node, Serializable {
  private int start_pc;
  
  private int end_pc;
  
  private int handler_pc;
  
  private int catch_type;
  
  public CodeException(CodeException paramCodeException) { this(paramCodeException.getStartPC(), paramCodeException.getEndPC(), paramCodeException.getHandlerPC(), paramCodeException.getCatchType()); }
  
  CodeException(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort()); }
  
  public CodeException(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.start_pc = paramInt1;
    this.end_pc = paramInt2;
    this.handler_pc = paramInt3;
    this.catch_type = paramInt4;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitCodeException(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.start_pc);
    paramDataOutputStream.writeShort(this.end_pc);
    paramDataOutputStream.writeShort(this.handler_pc);
    paramDataOutputStream.writeShort(this.catch_type);
  }
  
  public final int getCatchType() { return this.catch_type; }
  
  public final int getEndPC() { return this.end_pc; }
  
  public final int getHandlerPC() { return this.handler_pc; }
  
  public final int getStartPC() { return this.start_pc; }
  
  public final void setCatchType(int paramInt) { this.catch_type = paramInt; }
  
  public final void setEndPC(int paramInt) { this.end_pc = paramInt; }
  
  public final void setHandlerPC(int paramInt) { this.handler_pc = paramInt; }
  
  public final void setStartPC(int paramInt) { this.start_pc = paramInt; }
  
  public final String toString() { return "CodeException(start_pc = " + this.start_pc + ", end_pc = " + this.end_pc + ", handler_pc = " + this.handler_pc + ", catch_type = " + this.catch_type + ")"; }
  
  public final String toString(ConstantPool paramConstantPool, boolean paramBoolean) {
    String str;
    if (this.catch_type == 0) {
      str = "<Any exception>(0)";
    } else {
      str = Utility.compactClassName(paramConstantPool.getConstantString(this.catch_type, (byte)7), false) + (paramBoolean ? ("(" + this.catch_type + ")") : "");
    } 
    return this.start_pc + "\t" + this.end_pc + "\t" + this.handler_pc + "\t" + str;
  }
  
  public final String toString(ConstantPool paramConstantPool) { return toString(paramConstantPool, true); }
  
  public CodeException copy() {
    try {
      return (CodeException)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\CodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */