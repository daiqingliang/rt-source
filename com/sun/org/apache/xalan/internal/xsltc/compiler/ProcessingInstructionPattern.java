package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class ProcessingInstructionPattern extends StepPattern {
  private String _name = null;
  
  private boolean _typeChecked = false;
  
  public ProcessingInstructionPattern(String paramString) {
    super(3, 7, null);
    this._name = paramString;
  }
  
  public double getDefaultPriority() { return (this._name != null) ? 0.0D : -0.5D; }
  
  public String toString() { return (this._predicates == null) ? ("processing-instruction(" + this._name + ")") : ("processing-instruction(" + this._name + ")" + this._predicates); }
  
  public void reduceKernelPattern() { this._typeChecked = true; }
  
  public boolean isWildcard() { return false; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (hasPredicates()) {
      int i = this._predicates.size();
      for (byte b = 0; b < i; b++) {
        Predicate predicate = (Predicate)this._predicates.elementAt(b);
        predicate.typeCheck(paramSymbolTable);
      } 
    } 
    return Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeName", "(I)Ljava/lang/String;");
    int j = constantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(SWAP);
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    if (!this._typeChecked) {
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      int k = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      instructionList.append(new INVOKEINTERFACE(k, 2));
      instructionList.append(new PUSH(constantPoolGen, 7));
      this._falseList.add(instructionList.append(new IF_ICMPEQ(null)));
    } 
    instructionList.append(new PUSH(constantPoolGen, this._name));
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(new INVOKEINTERFACE(i, 2));
    instructionList.append(new INVOKEVIRTUAL(j));
    this._falseList.add(instructionList.append(new IFEQ(null)));
    if (hasPredicates()) {
      int k = this._predicates.size();
      for (byte b = 0; b < k; b++) {
        Predicate predicate = (Predicate)this._predicates.elementAt(b);
        Expression expression = predicate.getExpr();
        expression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
        this._trueList.append(expression._trueList);
        this._falseList.append(expression._falseList);
      } 
    } 
    InstructionHandle instructionHandle = instructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchTrueList(instructionHandle);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    instructionHandle = instructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchFalseList(instructionHandle);
    this._falseList.add(instructionList.append(new GOTO(null)));
    branchHandle.setTarget(instructionList.append(NOP));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ProcessingInstructionPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */