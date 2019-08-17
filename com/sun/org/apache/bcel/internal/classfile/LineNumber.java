package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public final class LineNumber implements Cloneable, Node, Serializable {
  private int start_pc;
  
  private int line_number;
  
  public LineNumber(LineNumber paramLineNumber) { this(paramLineNumber.getStartPC(), paramLineNumber.getLineNumber()); }
  
  LineNumber(DataInputStream paramDataInputStream) throws IOException { this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort()); }
  
  public LineNumber(int paramInt1, int paramInt2) {
    this.start_pc = paramInt1;
    this.line_number = paramInt2;
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitLineNumber(this); }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.start_pc);
    paramDataOutputStream.writeShort(this.line_number);
  }
  
  public final int getLineNumber() { return this.line_number; }
  
  public final int getStartPC() { return this.start_pc; }
  
  public final void setLineNumber(int paramInt) { this.line_number = paramInt; }
  
  public final void setStartPC(int paramInt) { this.start_pc = paramInt; }
  
  public final String toString() { return "LineNumber(" + this.start_pc + ", " + this.line_number + ")"; }
  
  public LineNumber copy() {
    try {
      return (LineNumber)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LineNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */