package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class RealType extends NumberType {
  public String toString() { return "real"; }
  
  public boolean identicalTo(Type paramType) { return (this == paramType); }
  
  public String toSignature() { return "D"; }
  
  public Type toJCType() { return Type.DOUBLE; }
  
  public int distanceTo(Type paramType) { return (paramType == this) ? 0 : ((paramType == Type.Int) ? 1 : Integer.MAX_VALUE); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.String) {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    } else if (paramType == Type.Boolean) {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    } else if (paramType == Type.Reference) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    } else if (paramType == Type.Int) {
      translateTo(paramClassGenerator, paramMethodGenerator, (IntType)paramType);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new INVOKESTATIC(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "realToString", "(D)Ljava/lang/String;")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    FlowList flowList = translateToDesynthesized(paramClassGenerator, paramMethodGenerator, paramBooleanType);
    instructionList.append(ICONST_1);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    flowList.backPatch(instructionList.append(ICONST_0));
    branchHandle.setTarget(instructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, IntType paramIntType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new INVOKESTATIC(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "realToInt", "(D)I")));
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    FlowList flowList = new FlowList();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(DUP2);
    LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable("real_to_boolean_tmp", Type.DOUBLE, null, null);
    localVariableGen.setStart(instructionList.append(new DSTORE(localVariableGen.getIndex())));
    instructionList.append(DCONST_0);
    instructionList.append(DCMPG);
    flowList.add(instructionList.append(new IFEQ(null)));
    instructionList.append(new DLOAD(localVariableGen.getIndex()));
    localVariableGen.setEnd(instructionList.append(new DLOAD(localVariableGen.getIndex())));
    instructionList.append(DCMPG);
    flowList.add(instructionList.append(new IFNE(null)));
    return flowList;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new NEW(constantPoolGen.addClass("java.lang.Double")));
    instructionList.append(DUP_X2);
    instructionList.append(DUP_X2);
    instructionList.append(POP);
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("java.lang.Double", "<init>", "(D)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (paramClass == char.class) {
      instructionList.append(D2I);
      instructionList.append(I2C);
    } else if (paramClass == byte.class) {
      instructionList.append(D2I);
      instructionList.append(I2B);
    } else if (paramClass == short.class) {
      instructionList.append(D2I);
      instructionList.append(I2S);
    } else if (paramClass == int.class) {
      instructionList.append(D2I);
    } else if (paramClass == long.class) {
      instructionList.append(D2L);
    } else if (paramClass == float.class) {
      instructionList.append(D2F);
    } else if (paramClass == double.class) {
      instructionList.append(NOP);
    } else if (paramClass.isAssignableFrom(Double.class)) {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (paramClass == char.class || paramClass == byte.class || paramClass == short.class || paramClass == int.class) {
      instructionList.append(I2D);
    } else if (paramClass == long.class) {
      instructionList.append(L2D);
    } else if (paramClass == float.class) {
      instructionList.append(F2D);
    } else if (paramClass == double.class) {
      instructionList.append(NOP);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference); }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new CHECKCAST(constantPoolGen.addClass("java.lang.Double")));
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.lang.Double", "doubleValue", "()D")));
  }
  
  public Instruction ADD() { return InstructionConstants.DADD; }
  
  public Instruction SUB() { return InstructionConstants.DSUB; }
  
  public Instruction MUL() { return InstructionConstants.DMUL; }
  
  public Instruction DIV() { return InstructionConstants.DDIV; }
  
  public Instruction REM() { return InstructionConstants.DREM; }
  
  public Instruction NEG() { return InstructionConstants.DNEG; }
  
  public Instruction LOAD(int paramInt) { return new DLOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new DSTORE(paramInt); }
  
  public Instruction POP() { return POP2; }
  
  public Instruction CMP(boolean paramBoolean) { return paramBoolean ? InstructionConstants.DCMPG : InstructionConstants.DCMPL; }
  
  public Instruction DUP() { return DUP2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\RealType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */