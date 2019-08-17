package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class NotCall extends FunctionCall {
  public NotCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    argument().translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(ICONST_1);
    instructionList.append(IXOR);
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    Expression expression = argument();
    expression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    this._trueList = expression._falseList;
    this._falseList = expression._trueList;
    this._falseList.add(branchHandle);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NotCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */