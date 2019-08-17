package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ParentPattern extends RelativePathPattern {
  private final Pattern _left;
  
  private final RelativePathPattern _right;
  
  public ParentPattern(Pattern paramPattern, RelativePathPattern paramRelativePathPattern) {
    (this._left = paramPattern).setParent(this);
    (this._right = paramRelativePathPattern).setParent(this);
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
  }
  
  public boolean isWildcard() { return false; }
  
  public StepPattern getKernelPattern() { return this._right.getKernelPattern(); }
  
  public void reduceKernelPattern() { this._right.reduceKernelPattern(); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._left.typeCheck(paramSymbolTable);
    return this._right.typeCheck(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable2("ppt", Util.getJCRefType("I"), null);
    ILOAD iLOAD = new ILOAD(localVariableGen.getIndex());
    ISTORE iSTORE = new ISTORE(localVariableGen.getIndex());
    if (this._right.isWildcard()) {
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
    } else if (this._right instanceof StepPattern) {
      instructionList.append(DUP);
      localVariableGen.setStart(instructionList.append(iSTORE));
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadDOM());
      localVariableGen.setEnd(instructionList.append(iLOAD));
    } else {
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      if (this._right instanceof AncestorPattern) {
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(SWAP);
      } 
    } 
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    instructionList.append(new INVOKEINTERFACE(i, 2));
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (syntaxTreeNode == null || syntaxTreeNode instanceof Instruction || syntaxTreeNode instanceof TopLevelElement) {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
    } else {
      instructionList.append(DUP);
      InstructionHandle instructionHandle = instructionList.append(iSTORE);
      if (localVariableGen.getStart() == null)
        localVariableGen.setStart(instructionHandle); 
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadDOM());
      localVariableGen.setEnd(instructionList.append(iLOAD));
    } 
    paramMethodGenerator.removeLocalVariable(localVariableGen);
    if (this._right instanceof AncestorPattern) {
      AncestorPattern ancestorPattern = (AncestorPattern)this._right;
      this._left.backPatchFalseList(ancestorPattern.getLoopHandle());
    } 
    this._trueList.append(this._right._trueList.append(this._left._trueList));
    this._falseList.append(this._right._falseList.append(this._left._falseList));
  }
  
  public String toString() { return "Parent(" + this._left + ", " + this._right + ')'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ParentPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */