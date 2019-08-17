package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class NodeSetType extends Type {
  public String toString() { return "node-set"; }
  
  public boolean identicalTo(Type paramType) { return (this == paramType); }
  
  public String toSignature() { return "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"; }
  
  public Type toJCType() { return new ObjectType("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator"); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.String) {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    } else if (paramType == Type.Boolean) {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    } else if (paramType == Type.Real) {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    } else if (paramType == Type.Node) {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeType)paramType);
    } else if (paramType == Type.Reference) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    } else if (paramType == Type.Object) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ObjectType)paramType);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    if (paramClass.getName().equals("org.w3c.dom.NodeList")) {
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(paramMethodGenerator.loadDOM());
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "nodeList2Iterator", "(Lorg/w3c/dom/NodeList;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(new INVOKESTATIC(i));
    } else if (paramClass.getName().equals("org.w3c.dom.Node")) {
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(paramMethodGenerator.loadDOM());
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "node2Iterator", "(Lorg/w3c/dom/Node;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(new INVOKESTATIC(i));
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
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
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    getFirstNode(paramClassGenerator, paramMethodGenerator);
    instructionList.append(DUP);
    BranchHandle branchHandle1 = instructionList.append(new IFLT(null));
    Type.Node.translateTo(paramClassGenerator, paramMethodGenerator, paramStringType);
    BranchHandle branchHandle2 = instructionList.append(new GOTO(null));
    branchHandle1.setTarget(instructionList.append(POP));
    instructionList.append(new PUSH(paramClassGenerator.getConstantPool(), ""));
    branchHandle2.setTarget(instructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType) {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    Type.String.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeType paramNodeType) { getFirstNode(paramClassGenerator, paramMethodGenerator); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    getFirstNode(paramClassGenerator, paramMethodGenerator);
    return new FlowList(instructionList.append(new IFLT(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    String str = paramClass.getName();
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(SWAP);
    if (str.equals("org.w3c.dom.Node")) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } else if (str.equals("org.w3c.dom.NodeList") || str.equals("java.lang.Object")) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } else if (str.equals("java.lang.String")) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I");
      int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
      instructionList.append(new INVOKEINTERFACE(i, 1));
      instructionList.append(new INVOKEINTERFACE(j, 2));
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), str);
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  private void getFirstNode(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new INVOKEINTERFACE(constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I"), 1));
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference); }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public String getClassName() { return "com.sun.org.apache.xml.internal.dtm.DTMAxisIterator"; }
  
  public Instruction LOAD(int paramInt) { return new ALOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ASTORE(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\NodeSetType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */