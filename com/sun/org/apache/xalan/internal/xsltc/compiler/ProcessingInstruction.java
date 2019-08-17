package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class ProcessingInstruction extends Instruction {
  private AttributeValue _name;
  
  private boolean _isLiteral = false;
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("name");
    if (str.length() > 0) {
      this._isLiteral = Util.isLiteral(str);
      if (this._isLiteral && !XML11Char.isXML11ValidNCName(str)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_NCNAME_ERR", str, this);
        paramParser.reportError(3, errorMsg);
      } 
      this._name = AttributeValue.create(this, str, paramParser);
    } else {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    } 
    if (str.equals("xml"))
      reportError(this, paramParser, "ILLEGAL_PI_ERR", "xml"); 
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._name.typeCheck(paramSymbolTable);
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (!this._isLiteral) {
      LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
      this._name.translate(paramClassGenerator, paramMethodGenerator);
      localVariableGen.setStart(instructionList.append(new ASTORE(localVariableGen.getIndex())));
      instructionList.append(new ALOAD(localVariableGen.getIndex()));
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkNCName", "(Ljava/lang/String;)V");
      instructionList.append(new INVOKESTATIC(j));
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(DUP);
      localVariableGen.setEnd(instructionList.append(new ALOAD(localVariableGen.getIndex())));
    } else {
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(DUP);
      this._name.translate(paramClassGenerator, paramMethodGenerator);
    } 
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.storeHandler());
    translateContents(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValueOfPI", "()Ljava/lang/String;")));
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "processingInstruction", "(Ljava/lang/String;Ljava/lang/String;)V");
    instructionList.append(new INVOKEINTERFACE(i, 3));
    instructionList.append(paramMethodGenerator.storeHandler());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ProcessingInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */