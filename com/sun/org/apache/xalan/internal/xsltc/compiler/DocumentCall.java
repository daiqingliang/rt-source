package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class DocumentCall extends FunctionCall {
  private Expression _arg1 = null;
  
  private Expression _arg2 = null;
  
  private Type _arg1Type;
  
  public DocumentCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    int i = argumentCount();
    if (i < 1 || i > 2) {
      ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
      throw new TypeCheckError(errorMsg);
    } 
    if (getStylesheet() == null) {
      ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
      throw new TypeCheckError(errorMsg);
    } 
    this._arg1 = argument(0);
    if (this._arg1 == null) {
      ErrorMsg errorMsg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
      throw new TypeCheckError(errorMsg);
    } 
    this._arg1Type = this._arg1.typeCheck(paramSymbolTable);
    if (this._arg1Type != Type.NodeSet && this._arg1Type != Type.String)
      this._arg1 = new CastExpr(this._arg1, Type.String); 
    if (i == 2) {
      this._arg2 = argument(1);
      if (this._arg2 == null) {
        ErrorMsg errorMsg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
        throw new TypeCheckError(errorMsg);
      } 
      Type type = this._arg2.typeCheck(paramSymbolTable);
      if (type.identicalTo(Type.Node)) {
        this._arg2 = new CastExpr(this._arg2, Type.NodeSet);
      } else if (!type.identicalTo(Type.NodeSet)) {
        ErrorMsg errorMsg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
        throw new TypeCheckError(errorMsg);
      } 
    } 
    return this._type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = argumentCount();
    int j = constantPoolGen.addFieldref(paramClassGenerator.getClassName(), "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    String str = null;
    if (i == 1) {
      str = "(Ljava/lang/Object;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
    } else {
      str = "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
    } 
    int k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.LoadDocument", "documentF", str);
    this._arg1.translate(paramClassGenerator, paramMethodGenerator);
    if (this._arg1Type == Type.NodeSet)
      this._arg1.startIterator(paramClassGenerator, paramMethodGenerator); 
    if (i == 2) {
      this._arg2.translate(paramClassGenerator, paramMethodGenerator);
      this._arg2.startIterator(paramClassGenerator, paramMethodGenerator);
    } 
    instructionList.append(new PUSH(constantPoolGen, getStylesheet().getSystemId()));
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(DUP);
    instructionList.append(new GETFIELD(j));
    instructionList.append(new INVOKESTATIC(k));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\DocumentCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */