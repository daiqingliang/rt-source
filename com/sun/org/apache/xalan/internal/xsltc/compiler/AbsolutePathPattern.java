package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class AbsolutePathPattern extends LocationPathPattern {
  private final RelativePathPattern _left;
  
  public AbsolutePathPattern(RelativePathPattern paramRelativePathPattern) {
    this._left = paramRelativePathPattern;
    if (paramRelativePathPattern != null)
      paramRelativePathPattern.setParent(this); 
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    if (this._left != null)
      this._left.setParser(paramParser); 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return (this._left == null) ? Type.Root : this._left.typeCheck(paramSymbolTable); }
  
  public boolean isWildcard() { return false; }
  
  public StepPattern getKernelPattern() { return (this._left != null) ? this._left.getKernelPattern() : null; }
  
  public void reduceKernelPattern() { this._left.reduceKernelPattern(); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._left != null)
      if (this._left instanceof StepPattern) {
        LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable2("apptmp", Util.getJCRefType("I"), null);
        instructionList.append(DUP);
        localVariableGen.setStart(instructionList.append(new ISTORE(localVariableGen.getIndex())));
        this._left.translate(paramClassGenerator, paramMethodGenerator);
        instructionList.append(paramMethodGenerator.loadDOM());
        localVariableGen.setEnd(instructionList.append(new ILOAD(localVariableGen.getIndex())));
        paramMethodGenerator.removeLocalVariable(localVariableGen);
      } else {
        this._left.translate(paramClassGenerator, paramMethodGenerator);
      }  
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
    InstructionHandle instructionHandle = instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(SWAP);
    instructionList.append(new INVOKEINTERFACE(i, 2));
    if (this._left instanceof AncestorPattern) {
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
    } 
    instructionList.append(new INVOKEINTERFACE(j, 2));
    instructionList.append(new PUSH(constantPoolGen, 9));
    BranchHandle branchHandle = instructionList.append(new IF_ICMPEQ(null));
    this._falseList.add(instructionList.append(new GOTO_W(null)));
    branchHandle.setTarget(instructionList.append(NOP));
    if (this._left != null) {
      this._left.backPatchTrueList(instructionHandle);
      if (this._left instanceof AncestorPattern) {
        AncestorPattern ancestorPattern = (AncestorPattern)this._left;
        this._falseList.backPatch(ancestorPattern.getLoopHandle());
      } 
      this._falseList.append(this._left._falseList);
    } 
  }
  
  public String toString() { return "absolutePathPattern(" + ((this._left != null) ? this._left.toString() : ")"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AbsolutePathPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */