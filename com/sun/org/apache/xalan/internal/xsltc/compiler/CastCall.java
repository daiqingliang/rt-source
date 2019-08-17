package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class CastCall extends FunctionCall {
  private String _className;
  
  private Expression _right;
  
  public CastCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (argumentCount() != 2)
      throw new TypeCheckError(new ErrorMsg("ILLEGAL_ARG_ERR", getName(), this)); 
    Expression expression = argument(0);
    if (expression instanceof LiteralExpr) {
      this._className = ((LiteralExpr)expression).getValue();
      this._type = Type.newObjectType(this._className);
    } else {
      throw new TypeCheckError(new ErrorMsg("NEED_LITERAL_ERR", getName(), this));
    } 
    this._right = argument(1);
    Type type = this._right.typeCheck(paramSymbolTable);
    if (type != Type.Reference && !(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType))
      throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", type, this._type, this)); 
    return this._type;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._right.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new CHECKCAST(constantPoolGen.addClass(this._className)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CastCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */