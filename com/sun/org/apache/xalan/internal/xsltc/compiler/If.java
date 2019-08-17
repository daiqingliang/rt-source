package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class If extends Instruction {
  private Expression _test;
  
  private boolean _ignore = false;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("If");
    indent(paramInt + 4);
    System.out.print("test ");
    Util.println(this._test.toString());
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser) {
    this._test = paramParser.parseExpression(this, "test", null);
    if (this._test.isDummy()) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "test");
      return;
    } 
    Object object = this._test.evaluateAtCompileTime();
    if (object != null && object instanceof Boolean)
      this._ignore = !((Boolean)object).booleanValue(); 
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (!(this._test.typeCheck(paramSymbolTable) instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType))
      this._test = new CastExpr(this._test, Type.Boolean); 
    if (!this._ignore)
      typeCheckContents(paramSymbolTable); 
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._test.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    InstructionHandle instructionHandle = instructionList.getEnd();
    if (!this._ignore)
      translateContents(paramClassGenerator, paramMethodGenerator); 
    this._test.backPatchFalseList(instructionList.append(NOP));
    this._test.backPatchTrueList(instructionHandle.getNext());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\If.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */