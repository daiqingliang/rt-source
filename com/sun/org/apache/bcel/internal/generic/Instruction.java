package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class Instruction implements Cloneable, Serializable {
  protected short length = 1;
  
  protected short opcode = -1;
  
  private static InstructionComparator cmp = InstructionComparator.DEFAULT;
  
  Instruction() {}
  
  public Instruction(short paramShort1, short paramShort2) {
    this.length = paramShort2;
    this.opcode = paramShort1;
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException { paramDataOutputStream.writeByte(this.opcode); }
  
  public String getName() { return Constants.OPCODE_NAMES[this.opcode]; }
  
  public String toString(boolean paramBoolean) { return paramBoolean ? (getName() + "[" + this.opcode + "](" + this.length + ")") : getName(); }
  
  public String toString() { return toString(true); }
  
  public String toString(ConstantPool paramConstantPool) { return toString(false); }
  
  public Instruction copy() {
    Instruction instruction = null;
    if (InstructionConstants.INSTRUCTIONS[getOpcode()] != null) {
      instruction = this;
    } else {
      try {
        instruction = (Instruction)clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        System.err.println(cloneNotSupportedException);
      } 
    } 
    return instruction;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {}
  
  public static final Instruction readInstruction(ByteSequence paramByteSequence) throws IOException {
    Class clazz;
    boolean bool = false;
    short s = (short)paramByteSequence.readUnsignedByte();
    Instruction instruction = null;
    if (s == 196) {
      bool = true;
      s = (short)paramByteSequence.readUnsignedByte();
    } 
    if (InstructionConstants.INSTRUCTIONS[s] != null)
      return InstructionConstants.INSTRUCTIONS[s]; 
    try {
      clazz = Class.forName(className(s));
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ClassGenException("Illegal opcode detected.");
    } 
    try {
      instruction = (Instruction)clazz.newInstance();
      if (bool && !(instruction instanceof LocalVariableInstruction) && !(instruction instanceof IINC) && !(instruction instanceof RET))
        throw new Exception("Illegal opcode after wide: " + s); 
      instruction.setOpcode(s);
      instruction.initFromFile(paramByteSequence, bool);
    } catch (Exception exception) {
      throw new ClassGenException(exception.toString());
    } 
    return instruction;
  }
  
  private static final String className(short paramShort) {
    String str = Constants.OPCODE_NAMES[paramShort].toUpperCase();
    try {
      int i = str.length();
      char c1 = str.charAt(i - 2);
      char c2 = str.charAt(i - 1);
      if (c1 == '_' && c2 >= '0' && c2 <= '5')
        str = str.substring(0, i - 2); 
      if (str.equals("ICONST_M1"))
        str = "ICONST"; 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      System.err.println(stringIndexOutOfBoundsException);
    } 
    return "com.sun.org.apache.bcel.internal.generic." + str;
  }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen) { return Constants.CONSUME_STACK[this.opcode]; }
  
  public int produceStack(ConstantPoolGen paramConstantPoolGen) { return Constants.PRODUCE_STACK[this.opcode]; }
  
  public short getOpcode() { return this.opcode; }
  
  public int getLength() { return this.length; }
  
  private void setOpcode(short paramShort) { this.opcode = paramShort; }
  
  void dispose() {}
  
  public abstract void accept(Visitor paramVisitor);
  
  public static InstructionComparator getComparator() { return cmp; }
  
  public static void setComparator(InstructionComparator paramInstructionComparator) { cmp = paramInstructionComparator; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Instruction) ? cmp.equals(this, (Instruction)paramObject) : 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\Instruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */