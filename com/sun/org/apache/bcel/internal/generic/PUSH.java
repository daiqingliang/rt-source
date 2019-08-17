package com.sun.org.apache.bcel.internal.generic;

public final class PUSH implements CompoundInstruction, VariableLengthInstruction, InstructionConstants {
  private Instruction instruction;
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, int paramInt) {
    if (paramInt >= -1 && paramInt <= 5) {
      this.instruction = INSTRUCTIONS[3 + paramInt];
    } else if (paramInt >= -128 && paramInt <= 127) {
      this.instruction = new BIPUSH((byte)paramInt);
    } else if (paramInt >= -32768 && paramInt <= 32767) {
      this.instruction = new SIPUSH((short)paramInt);
    } else {
      this.instruction = new LDC(paramConstantPoolGen.addInteger(paramInt));
    } 
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, boolean paramBoolean) { this.instruction = INSTRUCTIONS[3 + (paramBoolean ? 1 : 0)]; }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, float paramFloat) {
    if (paramFloat == 0.0D) {
      this.instruction = FCONST_0;
    } else if (paramFloat == 1.0D) {
      this.instruction = FCONST_1;
    } else if (paramFloat == 2.0D) {
      this.instruction = FCONST_2;
    } else {
      this.instruction = new LDC(paramConstantPoolGen.addFloat(paramFloat));
    } 
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, long paramLong) {
    if (paramLong == 0L) {
      this.instruction = LCONST_0;
    } else if (paramLong == 1L) {
      this.instruction = LCONST_1;
    } else {
      this.instruction = new LDC2_W(paramConstantPoolGen.addLong(paramLong));
    } 
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, double paramDouble) {
    if (paramDouble == 0.0D) {
      this.instruction = DCONST_0;
    } else if (paramDouble == 1.0D) {
      this.instruction = DCONST_1;
    } else {
      this.instruction = new LDC2_W(paramConstantPoolGen.addDouble(paramDouble));
    } 
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, String paramString) {
    if (paramString == null) {
      this.instruction = ACONST_NULL;
    } else {
      this.instruction = new LDC(paramConstantPoolGen.addString(paramString));
    } 
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, Number paramNumber) {
    if (paramNumber instanceof Integer || paramNumber instanceof Short || paramNumber instanceof Byte) {
      this.instruction = (new PUSH(paramConstantPoolGen, paramNumber.intValue())).instruction;
    } else if (paramNumber instanceof Double) {
      this.instruction = (new PUSH(paramConstantPoolGen, paramNumber.doubleValue())).instruction;
    } else if (paramNumber instanceof Float) {
      this.instruction = (new PUSH(paramConstantPoolGen, paramNumber.floatValue())).instruction;
    } else if (paramNumber instanceof Long) {
      this.instruction = (new PUSH(paramConstantPoolGen, paramNumber.longValue())).instruction;
    } else {
      throw new ClassGenException("What's this: " + paramNumber);
    } 
  }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, Character paramCharacter) { this(paramConstantPoolGen, paramCharacter.charValue()); }
  
  public PUSH(ConstantPoolGen paramConstantPoolGen, Boolean paramBoolean) { this(paramConstantPoolGen, paramBoolean.booleanValue()); }
  
  public final InstructionList getInstructionList() { return new InstructionList(this.instruction); }
  
  public final Instruction getInstruction() { return this.instruction; }
  
  public String toString() { return this.instruction.toString() + " (PUSH)"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\PUSH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */