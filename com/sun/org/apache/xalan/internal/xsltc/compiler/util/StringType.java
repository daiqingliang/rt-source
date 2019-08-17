package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public class StringType extends Type {
  public String toString() { return "string"; }
  
  public boolean identicalTo(Type paramType) { return (this == paramType); }
  
  public String toSignature() { return "Ljava/lang/String;"; }
  
  public boolean isSimple() { return true; }
  
  public Type toJCType() { return Type.STRING; }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.Boolean) {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    } else if (paramType == Type.Real) {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    } else if (paramType == Type.Reference) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    } else if (paramType != Type.ObjectString) {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    FlowList flowList = translateToDesynthesized(paramClassGenerator, paramMethodGenerator, paramBooleanType);
    instructionList.append(ICONST_1);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    flowList.backPatch(instructionList.append(ICONST_0));
    branchHandle.setTarget(instructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new INVOKESTATIC(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "stringToReal", "(Ljava/lang/String;)D")));
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.lang.String", "length", "()I")));
    return new FlowList(instructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    if (paramClass.isAssignableFrom(String.class)) {
      paramMethodGenerator.getInstructionList().append(NOP);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (paramClass.getName().equals("java.lang.String")) {
      instructionList.append(DUP);
      BranchHandle branchHandle = instructionList.append(new IFNONNULL(null));
      instructionList.append(POP);
      instructionList.append(new PUSH(constantPoolGen, ""));
      branchHandle.setTarget(instructionList.append(NOP));
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference); }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public String getClassName() { return "java.lang.String"; }
  
  public Instruction LOAD(int paramInt) { return new ALOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ASTORE(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\StringType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */