package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ValueOf extends Instruction {
  private Expression _select;
  
  private boolean _escaping = true;
  
  private boolean _isString = false;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("ValueOf");
    indent(paramInt + 4);
    Util.println("select " + this._select.toString());
  }
  
  public void parseContents(Parser paramParser) {
    this._select = paramParser.parseExpression(this, "select", null);
    if (this._select.isDummy()) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select");
      return;
    } 
    String str = getAttribute("disable-output-escaping");
    if (str != null && str.equals("yes"))
      this._escaping = false; 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._select.typeCheck(paramSymbolTable);
    if (type != null && !type.identicalTo(Type.Node))
      if (type.identicalTo(Type.NodeSet)) {
        this._select = new CastExpr(this._select, Type.Node);
      } else {
        this._isString = true;
        if (!type.identicalTo(Type.String))
          this._select = new CastExpr(this._select, Type.String); 
        this._isString = true;
      }  
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "setEscaping", "(Z)Z");
    if (!this._escaping) {
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new PUSH(constantPoolGen, false));
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } 
    if (this._isString) {
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      instructionList.append(paramClassGenerator.loadTranslet());
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new INVOKEVIRTUAL(j));
    } else {
      int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "characters", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      instructionList.append(paramMethodGenerator.loadDOM());
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new INVOKEINTERFACE(j, 3));
    } 
    if (!this._escaping) {
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(SWAP);
      instructionList.append(new INVOKEINTERFACE(i, 2));
      instructionList.append(POP);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ValueOf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */