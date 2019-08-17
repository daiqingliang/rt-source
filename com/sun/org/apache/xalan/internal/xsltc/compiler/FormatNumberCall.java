package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class FormatNumberCall extends FunctionCall {
  private Expression _value = argument(0);
  
  private Expression _format = argument(1);
  
  private Expression _name = (argumentCount() == 3) ? argument(2) : null;
  
  private QName _resolvedQName = null;
  
  public FormatNumberCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    getStylesheet().numberFormattingUsed();
    Type type1 = this._value.typeCheck(paramSymbolTable);
    if (!(type1 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType))
      this._value = new CastExpr(this._value, Type.Real); 
    Type type2 = this._format.typeCheck(paramSymbolTable);
    if (!(type2 instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType))
      this._format = new CastExpr(this._format, Type.String); 
    if (argumentCount() == 3) {
      Type type = this._name.typeCheck(paramSymbolTable);
      if (this._name instanceof LiteralExpr) {
        LiteralExpr literalExpr = (LiteralExpr)this._name;
        this._resolvedQName = getParser().getQNameIgnoreDefaultNs(literalExpr.getValue());
      } else if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType)) {
        this._name = new CastExpr(this._name, Type.String);
      } 
    } 
    return this._type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._value.translate(paramClassGenerator, paramMethodGenerator);
    this._format.translate(paramClassGenerator, paramMethodGenerator);
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "formatNumber", "(DLjava/lang/String;Ljava/text/DecimalFormat;)Ljava/lang/String;");
    int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getDecimalFormat", "(Ljava/lang/String;)Ljava/text/DecimalFormat;");
    instructionList.append(paramClassGenerator.loadTranslet());
    if (this._name == null) {
      instructionList.append(new PUSH(constantPoolGen, ""));
    } else if (this._resolvedQName != null) {
      instructionList.append(new PUSH(constantPoolGen, this._resolvedQName.toString()));
    } else {
      this._name.translate(paramClassGenerator, paramMethodGenerator);
    } 
    instructionList.append(new INVOKEVIRTUAL(j));
    instructionList.append(new INVOKESTATIC(i));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FormatNumberCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */