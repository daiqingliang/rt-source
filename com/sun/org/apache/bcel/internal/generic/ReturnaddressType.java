package com.sun.org.apache.bcel.internal.generic;

import java.util.Objects;

public class ReturnaddressType extends Type {
  public static final ReturnaddressType NO_TARGET = new ReturnaddressType();
  
  private InstructionHandle returnTarget;
  
  private ReturnaddressType() { super((byte)16, "<return address>"); }
  
  public ReturnaddressType(InstructionHandle paramInstructionHandle) {
    super((byte)16, "<return address targeting " + paramInstructionHandle + ">");
    this.returnTarget = paramInstructionHandle;
  }
  
  public int hashCode() { return Objects.hashCode(this.returnTarget); }
  
  public boolean equals(Object paramObject) { return !(paramObject instanceof ReturnaddressType) ? false : ((ReturnaddressType)paramObject).returnTarget.equals(this.returnTarget); }
  
  public InstructionHandle getTarget() { return this.returnTarget; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ReturnaddressType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */