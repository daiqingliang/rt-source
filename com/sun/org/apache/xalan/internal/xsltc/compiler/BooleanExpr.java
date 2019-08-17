package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class BooleanExpr extends Expression {
  private boolean _value;
  
  public BooleanExpr(boolean paramBoolean) { this._value = paramBoolean; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._type = Type.Boolean;
    return this._type;
  }
  
  public String toString() { return this._value ? "true()" : "false()"; }
  
  public boolean getValue() { return this._value; }
  
  public boolean contextDependent() { return false; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new PUSH(constantPoolGen, this._value));
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._value) {
      instructionList.append(NOP);
    } else {
      this._falseList.add(instructionList.append(new GOTO(null)));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\BooleanExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */