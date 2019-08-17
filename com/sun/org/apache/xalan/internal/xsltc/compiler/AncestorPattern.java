package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLT;
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

final class AncestorPattern extends RelativePathPattern {
  private final Pattern _left;
  
  private final RelativePathPattern _right;
  
  private InstructionHandle _loop;
  
  public AncestorPattern(RelativePathPattern paramRelativePathPattern) { this(null, paramRelativePathPattern); }
  
  public AncestorPattern(Pattern paramPattern, RelativePathPattern paramRelativePathPattern) {
    this._left = paramPattern;
    (this._right = paramRelativePathPattern).setParent(this);
    if (paramPattern != null)
      paramPattern.setParent(this); 
  }
  
  public InstructionHandle getLoopHandle() { return this._loop; }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    if (this._left != null)
      this._left.setParser(paramParser); 
    this._right.setParser(paramParser);
  }
  
  public boolean isWildcard() { return false; }
  
  public StepPattern getKernelPattern() { return this._right.getKernelPattern(); }
  
  public void reduceKernelPattern() { this._right.reduceKernelPattern(); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._left != null)
      this._left.typeCheck(paramSymbolTable); 
    return this._right.typeCheck(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable2("app", Util.getJCRefType("I"), instructionList.getEnd());
    ILOAD iLOAD = new ILOAD(localVariableGen.getIndex());
    ISTORE iSTORE = new ISTORE(localVariableGen.getIndex());
    if (this._right instanceof StepPattern) {
      instructionList.append(DUP);
      instructionList.append(iSTORE);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(iLOAD);
    } else {
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      if (this._right instanceof AncestorPattern) {
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(SWAP);
      } 
    } 
    if (this._left != null) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
      InstructionHandle instructionHandle = instructionList.append(new INVOKEINTERFACE(i, 2));
      instructionList.append(DUP);
      instructionList.append(iSTORE);
      this._falseList.add(instructionList.append(new IFLT(null)));
      instructionList.append(iLOAD);
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      SyntaxTreeNode syntaxTreeNode = getParent();
      if (syntaxTreeNode != null && !(syntaxTreeNode instanceof Instruction) && !(syntaxTreeNode instanceof TopLevelElement))
        instructionList.append(iLOAD); 
      BranchHandle branchHandle = instructionList.append(new GOTO(null));
      this._loop = instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(iLOAD);
      localVariableGen.setEnd(this._loop);
      instructionList.append(new GOTO(instructionHandle));
      branchHandle.setTarget(instructionList.append(NOP));
      this._left.backPatchFalseList(this._loop);
      this._trueList.append(this._left._trueList);
    } else {
      instructionList.append(POP2);
    } 
    if (this._right instanceof AncestorPattern) {
      AncestorPattern ancestorPattern = (AncestorPattern)this._right;
      this._falseList.backPatch(ancestorPattern.getLoopHandle());
    } 
    this._trueList.append(this._right._trueList);
    this._falseList.append(this._right._falseList);
  }
  
  public String toString() { return "AncestorPattern(" + this._left + ", " + this._right + ')'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AncestorPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */