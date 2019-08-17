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
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class BooleanType extends Type {
  public String toString() { return "boolean"; }
  
  public boolean identicalTo(Type paramType) { return (this == paramType); }
  
  public String toSignature() { return "Z"; }
  
  public boolean isSimple() { return true; }
  
  public Type toJCType() { return Type.BOOLEAN; }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.String) {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    } else if (paramType == Type.Real) {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    } else if (paramType == Type.Reference) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    BranchHandle branchHandle1 = instructionList.append(new IFEQ(null));
    instructionList.append(new PUSH(constantPoolGen, "true"));
    BranchHandle branchHandle2 = instructionList.append(new GOTO(null));
    branchHandle1.setTarget(instructionList.append(new PUSH(constantPoolGen, "false")));
    branchHandle2.setTarget(instructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType) { paramMethodGenerator.getInstructionList().append(I2D); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new NEW(constantPoolGen.addClass("java.lang.Boolean")));
    instructionList.append(DUP_X1);
    instructionList.append(SWAP);
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("java.lang.Boolean", "<init>", "(Z)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    if (paramClass == boolean.class) {
      paramMethodGenerator.getInstructionList().append(NOP);
    } else if (paramClass.isAssignableFrom(Boolean.class)) {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) { translateTo(paramClassGenerator, paramMethodGenerator, paramClass); }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference); }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new CHECKCAST(constantPoolGen.addClass("java.lang.Boolean")));
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.lang.Boolean", "booleanValue", "()Z")));
  }
  
  public Instruction LOAD(int paramInt) { return new ILOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ISTORE(paramInt); }
  
  public BranchInstruction GT(boolean paramBoolean) { return paramBoolean ? new IFGT(null) : new IF_ICMPGT(null); }
  
  public BranchInstruction GE(boolean paramBoolean) { return paramBoolean ? new IFGE(null) : new IF_ICMPGE(null); }
  
  public BranchInstruction LT(boolean paramBoolean) { return paramBoolean ? new IFLT(null) : new IF_ICMPLT(null); }
  
  public BranchInstruction LE(boolean paramBoolean) { return paramBoolean ? new IFLE(null) : new IF_ICMPLE(null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\BooleanType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */