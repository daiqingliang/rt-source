package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class CopyOf extends Instruction {
  private Expression _select;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("CopyOf");
    indent(paramInt + 4);
    Util.println("select " + this._select.toString());
  }
  
  public void parseContents(Parser paramParser) {
    this._select = paramParser.parseExpression(this, "select", null);
    if (this._select.isDummy()) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select");
      return;
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._select.typeCheck(paramSymbolTable);
    if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) && !(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType) && !(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) && !(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType))
      this._select = new CastExpr(this._select, Type.String); 
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    Type type = this._select.getType();
    String str1 = "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    String str2 = "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
    int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    String str3 = "()I";
    int k = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getDocument", "()I");
    if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType) {
      instructionList.append(paramMethodGenerator.loadDOM());
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      this._select.startIterator(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new INVOKEINTERFACE(i, 3));
    } else if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) {
      instructionList.append(paramMethodGenerator.loadDOM());
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new INVOKEINTERFACE(j, 3));
    } else if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(DUP);
      instructionList.append(new INVOKEINTERFACE(k, 1));
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new INVOKEINTERFACE(j, 3));
    } else if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) {
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      instructionList.append(paramMethodGenerator.loadDOM());
      int m = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "copy", "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
      instructionList.append(new INVOKESTATIC(m));
    } else {
      instructionList.append(paramClassGenerator.loadTranslet());
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V")));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CopyOf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */