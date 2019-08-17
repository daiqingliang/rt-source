package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class NodeType extends Type {
  private final int _type;
  
  protected NodeType() { this(-1); }
  
  protected NodeType(int paramInt) { this._type = paramInt; }
  
  public int getType() { return this._type; }
  
  public String toString() { return "node-type"; }
  
  public boolean identicalTo(Type paramType) { return paramType instanceof NodeType; }
  
  public int hashCode() { return this._type; }
  
  public String toSignature() { return "I"; }
  
  public Type toJCType() { return Type.INT; }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.String) {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    } else if (paramType == Type.Boolean) {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    } else if (paramType == Type.Real) {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    } else if (paramType == Type.NodeSet) {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeSetType)paramType);
    } else if (paramType == Type.Reference) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    } else if (paramType == Type.Object) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ObjectType)paramType);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    int i;
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    switch (this._type) {
      case 1:
      case 9:
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(SWAP);
        i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getElementValue", "(I)Ljava/lang/String;");
        instructionList.append(new INVOKEINTERFACE(i, 2));
        return;
      case -1:
      case 2:
      case 7:
      case 8:
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(SWAP);
        i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
        instructionList.append(new INVOKEINTERFACE(i, 2));
        return;
    } 
    ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramStringType.toString());
    paramClassGenerator.getParser().reportError(2, errorMsg);
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
    translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    Type.String.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeSetType paramNodeSetType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator")));
    instructionList.append(DUP_X1);
    instructionList.append(SWAP);
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator", "<init>", "(I)V");
    instructionList.append(new INVOKESPECIAL(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    return new FlowList(instructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.Node")));
    instructionList.append(DUP_X1);
    instructionList.append(SWAP);
    instructionList.append(new PUSH(constantPoolGen, this._type));
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.Node", "<init>", "(II)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    String str = paramClass.getName();
    if (str.equals("java.lang.String")) {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      return;
    } 
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(SWAP);
    if (str.equals("org.w3c.dom.Node") || str.equals("java.lang.Object")) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(I)Lorg/w3c/dom/Node;");
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } else if (str.equals("org.w3c.dom.NodeList")) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(I)Lorg/w3c/dom/NodeList;");
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), str);
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference); }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new CHECKCAST(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.Node")));
    instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.Node", "node", "I")));
  }
  
  public String getClassName() { return "com.sun.org.apache.xalan.internal.xsltc.runtime.Node"; }
  
  public Instruction LOAD(int paramInt) { return new ILOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ISTORE(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\NodeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */