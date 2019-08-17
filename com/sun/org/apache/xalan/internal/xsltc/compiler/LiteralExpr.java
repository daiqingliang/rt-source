package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class LiteralExpr extends Expression {
  private final String _value;
  
  private final String _namespace;
  
  public LiteralExpr(String paramString) {
    this._value = paramString;
    this._namespace = null;
  }
  
  public LiteralExpr(String paramString1, String paramString2) {
    this._value = paramString1;
    this._namespace = paramString2.equals("") ? null : paramString2;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return this._type = Type.String; }
  
  public String toString() { return "literal-expr(" + this._value + ')'; }
  
  protected boolean contextDependent() { return false; }
  
  protected String getValue() { return this._value; }
  
  protected String getNamespace() { return this._namespace; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new PUSH(constantPoolGen, this._value));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LiteralExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */