package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class IntType extends NumberType {
  public String toString() { return "int"; }
  
  public boolean identicalTo(Type paramType) { return (this == paramType); }
  
  public String toSignature() { return "I"; }
  
  public Type toJCType() { return Type.INT; }
  
  public int distanceTo(Type paramType) { return (paramType == this) ? 0 : ((paramType == Type.Real) ? 1 : Integer.MAX_VALUE); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.Real) {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    } else if (paramType == Type.String) {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    } else if (paramType == Type.Boolean) {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    } else if (paramType == Type.Reference) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType) { paramMethodGenerator.getInstructionList().append(I2D); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new INVOKESTATIC(constantPoolGen.addMethodref("java.lang.Integer", "toString", "(I)Ljava/lang/String;")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    BranchHandle branchHandle1 = instructionList.append(new IFEQ(null));
    instructionList.append(ICONST_1);
    BranchHandle branchHandle2 = instructionList.append(new GOTO(null));
    branchHandle1.setTarget(instructionList.append(ICONST_0));
    branchHandle2.setTarget(instructionList.append(NOP));
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    return new FlowList(instructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new NEW(constantPoolGen.addClass("java.lang.Integer")));
    instructionList.append(DUP_X1);
    instructionList.append(SWAP);
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("java.lang.Integer", "<init>", "(I)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (paramClass == char.class) {
      instructionList.append(I2C);
    } else if (paramClass == byte.class) {
      instructionList.append(I2B);
    } else if (paramClass == short.class) {
      instructionList.append(I2S);
    } else if (paramClass == int.class) {
      instructionList.append(NOP);
    } else if (paramClass == long.class) {
      instructionList.append(I2L);
    } else if (paramClass == float.class) {
      instructionList.append(I2F);
    } else if (paramClass == double.class) {
      instructionList.append(I2D);
    } else if (paramClass.isAssignableFrom(Double.class)) {
      instructionList.append(I2D);
      Type.Real.translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference); }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new CHECKCAST(constantPoolGen.addClass("java.lang.Integer")));
    int i = constantPoolGen.addMethodref("java.lang.Integer", "intValue", "()I");
    instructionList.append(new INVOKEVIRTUAL(i));
  }
  
  public Instruction ADD() { return InstructionConstants.IADD; }
  
  public Instruction SUB() { return InstructionConstants.ISUB; }
  
  public Instruction MUL() { return InstructionConstants.IMUL; }
  
  public Instruction DIV() { return InstructionConstants.IDIV; }
  
  public Instruction REM() { return InstructionConstants.IREM; }
  
  public Instruction NEG() { return InstructionConstants.INEG; }
  
  public Instruction LOAD(int paramInt) { return new ILOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ISTORE(paramInt); }
  
  public BranchInstruction GT(boolean paramBoolean) { return paramBoolean ? new IFGT(null) : new IF_ICMPGT(null); }
  
  public BranchInstruction GE(boolean paramBoolean) { return paramBoolean ? new IFGE(null) : new IF_ICMPGE(null); }
  
  public BranchInstruction LT(boolean paramBoolean) { return paramBoolean ? new IFLT(null) : new IF_ICMPLT(null); }
  
  public BranchInstruction LE(boolean paramBoolean) { return paramBoolean ? new IFLE(null) : new IF_ICMPLE(null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\IntType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */