package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class StartsWithCall extends FunctionCall {
  private Expression _base = null;
  
  private Expression _token = null;
  
  public StartsWithCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (argumentCount() != 2) {
      ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_ARG_ERR", getName(), this);
      throw new TypeCheckError(errorMsg);
    } 
    this._base = argument(0);
    Type type1 = this._base.typeCheck(paramSymbolTable);
    if (type1 != Type.String)
      this._base = new CastExpr(this._base, Type.String); 
    this._token = argument(1);
    Type type2 = this._token.typeCheck(paramSymbolTable);
    if (type2 != Type.String)
      this._token = new CastExpr(this._token, Type.String); 
    return this._type = Type.Boolean;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._base.translate(paramClassGenerator, paramMethodGenerator);
    this._token.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.lang.String", "startsWith", "(Ljava/lang/String;)Z")));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\StartsWithCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */