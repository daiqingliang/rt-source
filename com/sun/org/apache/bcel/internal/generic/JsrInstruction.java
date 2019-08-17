package com.sun.org.apache.bcel.internal.generic;

public abstract class JsrInstruction extends BranchInstruction implements UnconditionalBranch, TypedInstruction, StackProducer {
  JsrInstruction(short paramShort, InstructionHandle paramInstructionHandle) { super(paramShort, paramInstructionHandle); }
  
  JsrInstruction() {}
  
  public Type getType(ConstantPoolGen paramConstantPoolGen) { return new ReturnaddressType(physicalSuccessor()); }
  
  public InstructionHandle physicalSuccessor() {
    InstructionHandle instructionHandle1;
    for (instructionHandle1 = this.target; instructionHandle1.getPrev() != null; instructionHandle1 = instructionHandle1.getPrev());
    while (instructionHandle1.getInstruction() != this)
      instructionHandle1 = instructionHandle1.getNext(); 
    InstructionHandle instructionHandle2 = instructionHandle1;
    while (instructionHandle1 != null) {
      instructionHandle1 = instructionHandle1.getNext();
      if (instructionHandle1 != null && instructionHandle1.getInstruction() == this)
        throw new RuntimeException("physicalSuccessor() called on a shared JsrInstruction."); 
    } 
    return instructionHandle2.getNext();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\JsrInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */