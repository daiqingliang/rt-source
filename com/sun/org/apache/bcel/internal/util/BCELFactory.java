package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.generic.AllocationInstruction;
import com.sun.org.apache.bcel.internal.generic.ArrayInstruction;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CPInstruction;
import com.sun.org.apache.bcel.internal.generic.CodeExceptionGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPushInstruction;
import com.sun.org.apache.bcel.internal.generic.EmptyVisitor;
import com.sun.org.apache.bcel.internal.generic.FieldInstruction;
import com.sun.org.apache.bcel.internal.generic.IINC;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InvokeInstruction;
import com.sun.org.apache.bcel.internal.generic.LDC;
import com.sun.org.apache.bcel.internal.generic.LDC2_W;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.ReturnInstruction;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

class BCELFactory extends EmptyVisitor {
  private MethodGen _mg;
  
  private PrintWriter _out;
  
  private ConstantPoolGen _cp;
  
  private HashMap branch_map = new HashMap();
  
  private ArrayList branches = new ArrayList();
  
  BCELFactory(MethodGen paramMethodGen, PrintWriter paramPrintWriter) {
    this._mg = paramMethodGen;
    this._cp = paramMethodGen.getConstantPool();
    this._out = paramPrintWriter;
  }
  
  public void start() {
    if (!this._mg.isAbstract() && !this._mg.isNative()) {
      for (InstructionHandle instructionHandle = this._mg.getInstructionList().getStart(); instructionHandle != null; instructionHandle = instructionHandle.getNext()) {
        Instruction instruction = instructionHandle.getInstruction();
        if (instruction instanceof BranchInstruction)
          this.branch_map.put(instruction, instructionHandle); 
        if (instructionHandle.hasTargeters()) {
          if (instruction instanceof BranchInstruction) {
            this._out.println("    InstructionHandle ih_" + instructionHandle.getPosition() + ";");
          } else {
            this._out.print("    InstructionHandle ih_" + instructionHandle.getPosition() + " = ");
          } 
        } else {
          this._out.print("    ");
        } 
        if (!visitInstruction(instruction))
          instruction.accept(this); 
      } 
      updateBranchTargets();
      updateExceptionHandlers();
    } 
  }
  
  private boolean visitInstruction(Instruction paramInstruction) {
    short s = paramInstruction.getOpcode();
    if (InstructionConstants.INSTRUCTIONS[s] != null && !(paramInstruction instanceof ConstantPushInstruction) && !(paramInstruction instanceof ReturnInstruction)) {
      this._out.println("il.append(InstructionConstants." + paramInstruction.getName().toUpperCase() + ");");
      return true;
    } 
    return false;
  }
  
  public void visitLocalVariableInstruction(LocalVariableInstruction paramLocalVariableInstruction) {
    short s = paramLocalVariableInstruction.getOpcode();
    Type type = paramLocalVariableInstruction.getType(this._cp);
    if (s == 132) {
      this._out.println("il.append(new IINC(" + paramLocalVariableInstruction.getIndex() + ", " + ((IINC)paramLocalVariableInstruction).getIncrement() + "));");
    } else {
      String str = (s < 54) ? "Load" : "Store";
      this._out.println("il.append(_factory.create" + str + "(" + BCELifier.printType(type) + ", " + paramLocalVariableInstruction.getIndex() + "));");
    } 
  }
  
  public void visitArrayInstruction(ArrayInstruction paramArrayInstruction) {
    short s = paramArrayInstruction.getOpcode();
    Type type = paramArrayInstruction.getType(this._cp);
    String str = (s < 79) ? "Load" : "Store";
    this._out.println("il.append(_factory.createArray" + str + "(" + BCELifier.printType(type) + "));");
  }
  
  public void visitFieldInstruction(FieldInstruction paramFieldInstruction) {
    short s = paramFieldInstruction.getOpcode();
    String str1 = paramFieldInstruction.getClassName(this._cp);
    String str2 = paramFieldInstruction.getFieldName(this._cp);
    Type type = paramFieldInstruction.getFieldType(this._cp);
    this._out.println("il.append(_factory.createFieldAccess(\"" + str1 + "\", \"" + str2 + "\", " + BCELifier.printType(type) + ", Constants." + Constants.OPCODE_NAMES[s].toUpperCase() + "));");
  }
  
  public void visitInvokeInstruction(InvokeInstruction paramInvokeInstruction) {
    short s = paramInvokeInstruction.getOpcode();
    String str1 = paramInvokeInstruction.getClassName(this._cp);
    String str2 = paramInvokeInstruction.getMethodName(this._cp);
    Type type = paramInvokeInstruction.getReturnType(this._cp);
    Type[] arrayOfType = paramInvokeInstruction.getArgumentTypes(this._cp);
    this._out.println("il.append(_factory.createInvoke(\"" + str1 + "\", \"" + str2 + "\", " + BCELifier.printType(type) + ", " + BCELifier.printArgumentTypes(arrayOfType) + ", Constants." + Constants.OPCODE_NAMES[s].toUpperCase() + "));");
  }
  
  public void visitAllocationInstruction(AllocationInstruction paramAllocationInstruction) {
    Type type;
    if (paramAllocationInstruction instanceof CPInstruction) {
      type = ((CPInstruction)paramAllocationInstruction).getType(this._cp);
    } else {
      type = ((NEWARRAY)paramAllocationInstruction).getType();
    } 
    short s = ((Instruction)paramAllocationInstruction).getOpcode();
    short s1 = 1;
    switch (s) {
      case 187:
        this._out.println("il.append(_factory.createNew(\"" + ((ObjectType)type).getClassName() + "\"));");
        return;
      case 197:
        s1 = ((MULTIANEWARRAY)paramAllocationInstruction).getDimensions();
      case 188:
      case 189:
        this._out.println("il.append(_factory.createNewArray(" + BCELifier.printType(type) + ", (short) " + s1 + "));");
        return;
    } 
    throw new RuntimeException("Oops: " + s);
  }
  
  private void createConstant(Object paramObject) {
    String str = paramObject.toString();
    if (paramObject instanceof String) {
      str = '"' + Utility.convertString(paramObject.toString()) + '"';
    } else if (paramObject instanceof Character) {
      str = "(char)0x" + Integer.toHexString(((Character)paramObject).charValue());
    } 
    this._out.println("il.append(new PUSH(_cp, " + str + "));");
  }
  
  public void visitLDC(LDC paramLDC) { createConstant(paramLDC.getValue(this._cp)); }
  
  public void visitLDC2_W(LDC2_W paramLDC2_W) { createConstant(paramLDC2_W.getValue(this._cp)); }
  
  public void visitConstantPushInstruction(ConstantPushInstruction paramConstantPushInstruction) { createConstant(paramConstantPushInstruction.getValue()); }
  
  public void visitINSTANCEOF(INSTANCEOF paramINSTANCEOF) {
    Type type = paramINSTANCEOF.getType(this._cp);
    this._out.println("il.append(new INSTANCEOF(_cp.addClass(" + BCELifier.printType(type) + ")));");
  }
  
  public void visitCHECKCAST(CHECKCAST paramCHECKCAST) {
    Type type = paramCHECKCAST.getType(this._cp);
    this._out.println("il.append(_factory.createCheckCast(" + BCELifier.printType(type) + "));");
  }
  
  public void visitReturnInstruction(ReturnInstruction paramReturnInstruction) {
    Type type = paramReturnInstruction.getType(this._cp);
    this._out.println("il.append(_factory.createReturn(" + BCELifier.printType(type) + "));");
  }
  
  public void visitBranchInstruction(BranchInstruction paramBranchInstruction) {
    BranchHandle branchHandle = (BranchHandle)this.branch_map.get(paramBranchInstruction);
    int i = branchHandle.getPosition();
    String str = paramBranchInstruction.getName() + "_" + i;
    if (paramBranchInstruction instanceof Select) {
      Select select = (Select)paramBranchInstruction;
      this.branches.add(paramBranchInstruction);
      StringBuffer stringBuffer = new StringBuffer("new int[] { ");
      int[] arrayOfInt = select.getMatchs();
      byte b;
      for (b = 0; b < arrayOfInt.length; b++) {
        stringBuffer.append(arrayOfInt[b]);
        if (b < arrayOfInt.length - 1)
          stringBuffer.append(", "); 
      } 
      stringBuffer.append(" }");
      this._out.print("    Select " + str + " = new " + paramBranchInstruction.getName().toUpperCase() + "(" + stringBuffer + ", new InstructionHandle[] { ");
      for (b = 0; b < arrayOfInt.length; b++) {
        this._out.print("null");
        if (b < arrayOfInt.length - 1)
          this._out.print(", "); 
      } 
      this._out.println(");");
    } else {
      String str1;
      int j = branchHandle.getTarget().getPosition();
      if (i > j) {
        str1 = "ih_" + j;
      } else {
        this.branches.add(paramBranchInstruction);
        str1 = "null";
      } 
      this._out.println("    BranchInstruction " + str + " = _factory.createBranchInstruction(Constants." + paramBranchInstruction.getName().toUpperCase() + ", " + str1 + ");");
    } 
    if (branchHandle.hasTargeters()) {
      this._out.println("    ih_" + i + " = il.append(" + str + ");");
    } else {
      this._out.println("    il.append(" + str + ");");
    } 
  }
  
  public void visitRET(RET paramRET) { this._out.println("il.append(new RET(" + paramRET.getIndex() + ")));"); }
  
  private void updateBranchTargets() {
    for (BranchInstruction branchInstruction : this.branches) {
      BranchHandle branchHandle = (BranchHandle)this.branch_map.get(branchInstruction);
      int i = branchHandle.getPosition();
      String str = branchInstruction.getName() + "_" + i;
      int j = branchHandle.getTarget().getPosition();
      this._out.println("    " + str + ".setTarget(ih_" + j + ");");
      if (branchInstruction instanceof Select) {
        InstructionHandle[] arrayOfInstructionHandle = ((Select)branchInstruction).getTargets();
        for (byte b = 0; b < arrayOfInstructionHandle.length; b++) {
          j = arrayOfInstructionHandle[b].getPosition();
          this._out.println("    " + str + ".setTarget(" + b + ", ih_" + j + ");");
        } 
      } 
    } 
  }
  
  private void updateExceptionHandlers() {
    CodeExceptionGen[] arrayOfCodeExceptionGen = this._mg.getExceptionHandlers();
    for (byte b = 0; b < arrayOfCodeExceptionGen.length; b++) {
      CodeExceptionGen codeExceptionGen = arrayOfCodeExceptionGen[b];
      String str = (codeExceptionGen.getCatchType() == null) ? "null" : BCELifier.printType(codeExceptionGen.getCatchType());
      this._out.println("    method.addExceptionHandler(ih_" + codeExceptionGen.getStartPC().getPosition() + ", ih_" + codeExceptionGen.getEndPC().getPosition() + ", ih_" + codeExceptionGen.getHandlerPC().getPosition() + ", " + str + ");");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\BCELFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */