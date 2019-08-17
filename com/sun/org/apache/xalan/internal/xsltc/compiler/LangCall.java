package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class LangCall extends FunctionCall {
  private Expression _lang = argument(0);
  
  private Type _langType;
  
  public LangCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._langType = this._lang.typeCheck(paramSymbolTable);
    if (!(this._langType instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType))
      this._lang = new CastExpr(this._lang, Type.String); 
    return Type.Boolean;
  }
  
  public Type getType() { return Type.Boolean; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "testLanguage", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)Z");
    this._lang.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(paramMethodGenerator.loadDOM());
    if (paramClassGenerator instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator) {
      instructionList.append(new ILOAD(1));
    } else {
      instructionList.append(paramMethodGenerator.loadContextNode());
    } 
    instructionList.append(new INVOKESTATIC(i));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LangCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */