package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class ReferenceType extends Type {
  public String toString() { return "reference"; }
  
  public boolean identicalTo(Type paramType) { return (this == paramType); }
  
  public String toSignature() { return "Ljava/lang/Object;"; }
  
  public Type toJCType() { return Type.OBJECT; }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.String) {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    } else if (paramType == Type.Real) {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    } else if (paramType == Type.Boolean) {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    } else if (paramType == Type.NodeSet) {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeSetType)paramType);
    } else if (paramType == Type.Node) {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeType)paramType);
    } else if (paramType == Type.ResultTree) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ResultTreeType)paramType);
    } else if (paramType == Type.Object) {
      translateTo(paramClassGenerator, paramMethodGenerator, (ObjectType)paramType);
    } else if (paramType != Type.Reference) {
      ErrorMsg errorMsg = new ErrorMsg("INTERNAL_ERR", paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    int i = paramMethodGenerator.getLocalIndex("current");
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (i < 0) {
      instructionList.append(new PUSH(constantPoolGen, 0));
    } else {
      instructionList.append(new ILOAD(i));
    } 
    instructionList.append(paramMethodGenerator.loadDOM());
    int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "stringF", "(Ljava/lang/Object;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
    instructionList.append(new INVOKESTATIC(j));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(paramMethodGenerator.loadDOM());
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "numberF", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)D");
    instructionList.append(new INVOKESTATIC(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "booleanF", "(Ljava/lang/Object;)Z");
    instructionList.append(new INVOKESTATIC(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeSetType paramNodeSetType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    instructionList.append(new INVOKESTATIC(i));
    i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "reset", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    instructionList.append(new INVOKEINTERFACE(i, 1));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeType paramNodeType) {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
    Type.NodeSet.translateTo(paramClassGenerator, paramMethodGenerator, paramNodeType);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ResultTreeType paramResultTreeType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToResultTree", "(Ljava/lang/Object;)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    instructionList.append(new INVOKESTATIC(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToLong", "(Ljava/lang/Object;)J");
    int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToDouble", "(Ljava/lang/Object;)D");
    int k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToBoolean", "(Ljava/lang/Object;)Z");
    if (paramClass.getName().equals("java.lang.Object")) {
      instructionList.append(NOP);
    } else if (paramClass == double.class) {
      instructionList.append(new INVOKESTATIC(j));
    } else if (paramClass.getName().equals("java.lang.Double")) {
      instructionList.append(new INVOKESTATIC(j));
      Type.Real.translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    } else if (paramClass == float.class) {
      instructionList.append(new INVOKESTATIC(j));
      instructionList.append(D2F);
    } else if (paramClass.getName().equals("java.lang.String")) {
      int m = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToString", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new INVOKESTATIC(m));
    } else if (paramClass.getName().equals("org.w3c.dom.Node")) {
      int m = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNode", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/Node;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new INVOKESTATIC(m));
    } else if (paramClass.getName().equals("org.w3c.dom.NodeList")) {
      int m = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeList", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/NodeList;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new INVOKESTATIC(m));
    } else if (paramClass.getName().equals("com.sun.org.apache.xalan.internal.xsltc.DOM")) {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.ResultTree);
    } else if (paramClass == long.class) {
      instructionList.append(new INVOKESTATIC(i));
    } else if (paramClass == int.class) {
      instructionList.append(new INVOKESTATIC(i));
      instructionList.append(L2I);
    } else if (paramClass == short.class) {
      instructionList.append(new INVOKESTATIC(i));
      instructionList.append(L2I);
      instructionList.append(I2S);
    } else if (paramClass == byte.class) {
      instructionList.append(new INVOKESTATIC(i));
      instructionList.append(L2I);
      instructionList.append(I2B);
    } else if (paramClass == char.class) {
      instructionList.append(new INVOKESTATIC(i));
      instructionList.append(L2I);
      instructionList.append(I2C);
    } else if (paramClass == boolean.class) {
      instructionList.append(new INVOKESTATIC(k));
    } else if (paramClass.getName().equals("java.lang.Boolean")) {
      instructionList.append(new INVOKESTATIC(k));
      Type.Boolean.translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    if (paramClass.getName().equals("java.lang.Object")) {
      paramMethodGenerator.getInstructionList().append(NOP);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    translateTo(paramClassGenerator, paramMethodGenerator, paramBooleanType);
    return new FlowList(instructionList.append(new IFEQ(null)));
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public Instruction LOAD(int paramInt) { return new ALOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ASTORE(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\ReferenceType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */