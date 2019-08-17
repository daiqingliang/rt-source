package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

abstract class Expression extends SyntaxTreeNode {
  protected Type _type;
  
  protected FlowList _trueList = new FlowList();
  
  protected FlowList _falseList = new FlowList();
  
  public Type getType() { return this._type; }
  
  public abstract String toString();
  
  public boolean hasPositionCall() { return false; }
  
  public boolean hasLastCall() { return false; }
  
  public Object evaluateAtCompileTime() { return null; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return typeCheckContents(paramSymbolTable); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ErrorMsg errorMsg = new ErrorMsg("NOT_IMPLEMENTED_ERR", getClass(), this);
    getParser().reportError(2, errorMsg);
  }
  
  public final InstructionList compile(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList2 = paramMethodGenerator.getInstructionList();
    InstructionList instructionList1;
    paramMethodGenerator.setInstructionList(instructionList1 = new InstructionList());
    translate(paramClassGenerator, paramMethodGenerator);
    paramMethodGenerator.setInstructionList(instructionList2);
    return instructionList1;
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    translate(paramClassGenerator, paramMethodGenerator);
    if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType)
      desynthesize(paramClassGenerator, paramMethodGenerator); 
  }
  
  public void startIterator(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (!(this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType))
      return; 
    Expression expression = this;
    if (expression instanceof CastExpr)
      expression = ((CastExpr)expression).getExpr(); 
    if (!(expression instanceof VariableRefBase)) {
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      instructionList.append(paramMethodGenerator.loadContextNode());
      instructionList.append(paramMethodGenerator.setStartNode());
    } 
  }
  
  public void synthesize(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._trueList.backPatch(instructionList.append(ICONST_1));
    BranchHandle branchHandle = instructionList.append(new GOTO_W(null));
    this._falseList.backPatch(instructionList.append(ICONST_0));
    branchHandle.setTarget(instructionList.append(NOP));
  }
  
  public void desynthesize(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._falseList.add(instructionList.append(new IFEQ(null)));
  }
  
  public FlowList getFalseList() { return this._falseList; }
  
  public FlowList getTrueList() { return this._trueList; }
  
  public void backPatchFalseList(InstructionHandle paramInstructionHandle) { this._falseList.backPatch(paramInstructionHandle); }
  
  public void backPatchTrueList(InstructionHandle paramInstructionHandle) { this._trueList.backPatch(paramInstructionHandle); }
  
  public MethodType lookupPrimop(SymbolTable paramSymbolTable, String paramString, MethodType paramMethodType) {
    MethodType methodType = null;
    Vector vector = paramSymbolTable.lookupPrimop(paramString);
    if (vector != null) {
      int i = vector.size();
      int j = Integer.MAX_VALUE;
      for (byte b = 0; b < i; b++) {
        MethodType methodType1 = (MethodType)vector.elementAt(b);
        if (methodType1.argsCount() == paramMethodType.argsCount()) {
          if (methodType == null)
            methodType = methodType1; 
          int k = paramMethodType.distanceTo(methodType1);
          if (k < j) {
            j = k;
            methodType = methodType1;
          } 
        } 
      } 
    } 
    return methodType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Expression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */