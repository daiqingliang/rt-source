package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class ResultTreeType extends Type {
  private final String _methodName = null;
  
  protected ResultTreeType() {}
  
  public ResultTreeType(String paramString) {}
  
  public String toString() { return "result-tree"; }
  
  public boolean identicalTo(Type paramType) { return paramType instanceof ResultTreeType; }
  
  public String toSignature() { return "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"; }
  
  public Type toJCType() { return Util.getJCRefType(toSignature()); }
  
  public String getMethodName() { return this._methodName; }
  
  public boolean implementedAsMethod() { return (this._methodName != null); }
  
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
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(POP);
    instructionList.append(ICONST_1);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._methodName == null) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValue", "()Ljava/lang/String;");
      instructionList.append(new INVOKEINTERFACE(i, 1));
    } else {
      String str = paramClassGenerator.getClassName();
      int i = paramMethodGenerator.getLocalIndex("current");
      instructionList.append(paramClassGenerator.loadTranslet());
      if (paramClassGenerator.isExternal())
        instructionList.append(new CHECKCAST(constantPoolGen.addClass(str))); 
      instructionList.append(DUP);
      instructionList.append(new GETFIELD(constantPoolGen.addFieldref(str, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "<init>", "()V");
      instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler")));
      instructionList.append(DUP);
      instructionList.append(DUP);
      instructionList.append(new INVOKESPECIAL(j));
      LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable("rt_to_string_handler", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;"), null, null);
      localVariableGen.setStart(instructionList.append(new ASTORE(localVariableGen.getIndex())));
      j = constantPoolGen.addMethodref(str, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      instructionList.append(new INVOKEVIRTUAL(j));
      localVariableGen.setEnd(instructionList.append(new ALOAD(localVariableGen.getIndex())));
      j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;");
      instructionList.append(new INVOKEVIRTUAL(j));
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType) {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    Type.String.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._methodName == null) {
      instructionList.append(NOP);
    } else {
      String str = paramClassGenerator.getClassName();
      int i = paramMethodGenerator.getLocalIndex("current");
      instructionList.append(paramClassGenerator.loadTranslet());
      if (paramClassGenerator.isExternal())
        instructionList.append(new CHECKCAST(constantPoolGen.addClass(str))); 
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(paramMethodGenerator.loadDOM());
      int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
      instructionList.append(new PUSH(constantPoolGen, 32));
      instructionList.append(new PUSH(constantPoolGen, false));
      instructionList.append(new INVOKEINTERFACE(j, 3));
      instructionList.append(DUP);
      LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("rt_to_reference_dom", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
      instructionList.append(new CHECKCAST(constantPoolGen.addClass("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
      localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
      j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
      instructionList.append(new INVOKEINTERFACE(j, 1));
      instructionList.append(DUP);
      instructionList.append(DUP);
      LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("rt_to_reference_handler", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;"), null, null);
      localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
      j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V");
      instructionList.append(new INVOKEINTERFACE(j, 1));
      j = constantPoolGen.addMethodref(str, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      instructionList.append(new INVOKEVIRTUAL(j));
      localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
      j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V");
      instructionList.append(new INVOKEINTERFACE(j, 1));
      localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeSetType paramNodeSetType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(DUP);
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "setupMapping", "([Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
    instructionList.append(new INVOKEINTERFACE(i, 5));
    instructionList.append(DUP);
    int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    instructionList.append(new INVOKEINTERFACE(j, 1));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
    return new FlowList(instructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    String str = paramClass.getName();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (str.equals("org.w3c.dom.Node")) {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } else if (str.equals("org.w3c.dom.NodeList")) {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } else if (str.equals("java.lang.Object")) {
      instructionList.append(NOP);
    } else if (str.equals("java.lang.String")) {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), str);
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference); }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public String getClassName() { return "com.sun.org.apache.xalan.internal.xsltc.DOM"; }
  
  public Instruction LOAD(int paramInt) { return new ALOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ASTORE(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\ResultTreeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */