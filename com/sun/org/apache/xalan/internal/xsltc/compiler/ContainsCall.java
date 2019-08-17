package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ContainsCall extends FunctionCall {
  private Expression _base = null;
  
  private Expression _token = null;
  
  public ContainsCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public boolean isBoolean() { return true; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (argumentCount() != 2)
      throw new TypeCheckError("ILLEGAL_ARG_ERR", getName(), this); 
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
    translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    synthesize(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._base.translate(paramClassGenerator, paramMethodGenerator);
    this._token.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.lang.String", "indexOf", "(Ljava/lang/String;)I")));
    this._falseList.add(instructionList.append(new IFLT(null)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ContainsCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */