package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import java.io.Serializable;
import java.util.Objects;

public class LocalVariableGen implements InstructionTargeter, NamedAndTyped, Cloneable, Serializable {
  private final int index;
  
  private String name;
  
  private Type type;
  
  private InstructionHandle start;
  
  private InstructionHandle end;
  
  public LocalVariableGen(int paramInt, String paramString, Type paramType, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    if (paramInt < 0 || paramInt > 65535)
      throw new ClassGenException("Invalid index index: " + paramInt); 
    this.name = paramString;
    this.type = paramType;
    this.index = paramInt;
    setStart(paramInstructionHandle1);
    setEnd(paramInstructionHandle2);
  }
  
  public LocalVariable getLocalVariable(ConstantPoolGen paramConstantPoolGen) {
    int i = this.start.getPosition();
    int j = this.end.getPosition() - i;
    if (j > 0)
      j += this.end.getInstruction().getLength(); 
    int k = paramConstantPoolGen.addUtf8(this.name);
    int m = paramConstantPoolGen.addUtf8(this.type.getSignature());
    return new LocalVariable(i, j, k, m, this.index, paramConstantPoolGen.getConstantPool());
  }
  
  public int getIndex() { return this.index; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public String getName() { return this.name; }
  
  public void setType(Type paramType) { this.type = paramType; }
  
  public Type getType() { return this.type; }
  
  public InstructionHandle getStart() { return this.start; }
  
  public InstructionHandle getEnd() { return this.end; }
  
  void notifyTargetChanging() {
    BranchInstruction.notifyTargetChanging(this.start, this);
    if (this.end != this.start)
      BranchInstruction.notifyTargetChanging(this.end, this); 
  }
  
  void notifyTargetChanged() {
    BranchInstruction.notifyTargetChanged(this.start, this);
    if (this.end != this.start)
      BranchInstruction.notifyTargetChanged(this.end, this); 
  }
  
  public final void setStart(InstructionHandle paramInstructionHandle) {
    notifyTargetChanging();
    this.start = paramInstructionHandle;
    notifyTargetChanged();
  }
  
  public final void setEnd(InstructionHandle paramInstructionHandle) {
    notifyTargetChanging();
    this.end = paramInstructionHandle;
    notifyTargetChanged();
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    boolean bool = false;
    if (this.start == paramInstructionHandle1) {
      bool = true;
      setStart(paramInstructionHandle2);
    } 
    if (this.end == paramInstructionHandle1) {
      bool = true;
      setEnd(paramInstructionHandle2);
    } 
    if (!bool)
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but {" + this.start + ", " + this.end + "}"); 
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle) { return (this.start == paramInstructionHandle || this.end == paramInstructionHandle); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof LocalVariableGen))
      return false; 
    LocalVariableGen localVariableGen = (LocalVariableGen)paramObject;
    return (localVariableGen.index == this.index && localVariableGen.start == this.start && localVariableGen.end == this.end);
  }
  
  public int hashCode() {
    null = 7;
    null = 59 * null + this.index;
    null = 59 * null + Objects.hashCode(this.start);
    return 59 * null + Objects.hashCode(this.end);
  }
  
  public String toString() { return "LocalVariableGen(" + this.name + ", " + this.type + ", " + this.start + ", " + this.end + ")"; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      System.err.println(cloneNotSupportedException);
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LocalVariableGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */