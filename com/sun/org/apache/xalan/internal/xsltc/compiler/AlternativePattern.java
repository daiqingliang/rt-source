package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class AlternativePattern extends Pattern {
  private final Pattern _left;
  
  private final Pattern _right;
  
  public AlternativePattern(Pattern paramPattern1, Pattern paramPattern2) {
    this._left = paramPattern1;
    this._right = paramPattern2;
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
  }
  
  public Pattern getLeft() { return this._left; }
  
  public Pattern getRight() { return this._right; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._left.typeCheck(paramSymbolTable);
    this._right.typeCheck(paramSymbolTable);
    return null;
  }
  
  public double getPriority() {
    double d1 = this._left.getPriority();
    double d2 = this._right.getPriority();
    return (d1 < d2) ? d1 : d2;
  }
  
  public String toString() { return "alternative(" + this._left + ", " + this._right + ')'; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    instructionList.append(paramMethodGenerator.loadContextNode());
    this._right.translate(paramClassGenerator, paramMethodGenerator);
    this._left._trueList.backPatch(branchHandle);
    this._left._falseList.backPatch(branchHandle.getNext());
    this._trueList.append(this._right._trueList.add(branchHandle));
    this._falseList.append(this._right._falseList);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AlternativePattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */