package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class UnparsedEntityUriCall extends FunctionCall {
  private Expression _entity = argument();
  
  public UnparsedEntityUriCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._entity.typeCheck(paramSymbolTable);
    if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType))
      this._entity = new CastExpr(this._entity, Type.String); 
    return this._type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(paramMethodGenerator.loadDOM());
    this._entity.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new INVOKEINTERFACE(constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getUnparsedEntityURI", "(Ljava/lang/String;)Ljava/lang/String;"), 2));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnparsedEntityUriCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */