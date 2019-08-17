package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

class StepPattern extends RelativePathPattern {
  private static final int NO_CONTEXT = 0;
  
  private static final int SIMPLE_CONTEXT = 1;
  
  private static final int GENERAL_CONTEXT = 2;
  
  protected final int _axis;
  
  protected final int _nodeType;
  
  protected Vector _predicates;
  
  private Step _step = null;
  
  private boolean _isEpsilon = false;
  
  private int _contextCase;
  
  private double _priority = Double.MAX_VALUE;
  
  public StepPattern(int paramInt1, int paramInt2, Vector paramVector) {
    this._axis = paramInt1;
    this._nodeType = paramInt2;
    this._predicates = paramVector;
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    if (this._predicates != null) {
      int i = this._predicates.size();
      for (byte b = 0; b < i; b++) {
        Predicate predicate = (Predicate)this._predicates.elementAt(b);
        predicate.setParser(paramParser);
        predicate.setParent(this);
      } 
    } 
  }
  
  public int getNodeType() { return this._nodeType; }
  
  public void setPriority(double paramDouble) { this._priority = paramDouble; }
  
  public StepPattern getKernelPattern() { return this; }
  
  public boolean isWildcard() { return (this._isEpsilon && !hasPredicates()); }
  
  public StepPattern setPredicates(Vector paramVector) {
    this._predicates = paramVector;
    return this;
  }
  
  protected boolean hasPredicates() { return (this._predicates != null && this._predicates.size() > 0); }
  
  public double getDefaultPriority() {
    if (this._priority != Double.MAX_VALUE)
      return this._priority; 
    if (hasPredicates())
      return 0.5D; 
    switch (this._nodeType) {
      case -1:
        return -0.5D;
      case 0:
        return 0.0D;
    } 
    return (this._nodeType >= 14) ? 0.0D : -0.5D;
  }
  
  public int getAxis() { return this._axis; }
  
  public void reduceKernelPattern() { this._isEpsilon = true; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("stepPattern(\"");
    stringBuffer.append(Axis.getNames(this._axis)).append("\", ").append(this._isEpsilon ? ("epsilon{" + Integer.toString(this._nodeType) + "}") : Integer.toString(this._nodeType));
    if (this._predicates != null)
      stringBuffer.append(", ").append(this._predicates.toString()); 
    return stringBuffer.append(')').toString();
  }
  
  private int analyzeCases() {
    boolean bool = true;
    int i = this._predicates.size();
    for (byte b = 0; b < i && bool; b++) {
      Predicate predicate = (Predicate)this._predicates.elementAt(b);
      if (predicate.isNthPositionFilter() || predicate.hasPositionCall() || predicate.hasLastCall())
        bool = false; 
    } 
    return bool ? 0 : ((i == 1) ? 1 : 2);
  }
  
  private String getNextFieldName() { return "__step_pattern_iter_" + getXSLTC().nextStepPatternSerial(); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (hasPredicates()) {
      int i = this._predicates.size();
      for (byte b = 0; b < i; b++) {
        Predicate predicate = (Predicate)this._predicates.elementAt(b);
        predicate.typeCheck(paramSymbolTable);
      } 
      this._contextCase = analyzeCases();
      Step step = null;
      if (this._contextCase == 1) {
        Predicate predicate = (Predicate)this._predicates.elementAt(0);
        if (predicate.isNthPositionFilter()) {
          this._contextCase = 2;
          step = new Step(this._axis, this._nodeType, this._predicates);
        } else {
          step = new Step(this._axis, this._nodeType, null);
        } 
      } else if (this._contextCase == 2) {
        int j = this._predicates.size();
        for (byte b1 = 0; b1 < j; b1++)
          ((Predicate)this._predicates.elementAt(b1)).dontOptimize(); 
        step = new Step(this._axis, this._nodeType, this._predicates);
      } 
      if (step != null) {
        step.setParser(getParser());
        step.typeCheck(paramSymbolTable);
        this._step = step;
      } 
    } 
    return (this._axis == 3) ? Type.Element : Type.Attribute;
  }
  
  private void translateKernel(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._nodeType == 1) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "isElement", "(I)Z");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
      instructionList.append(new INVOKEINTERFACE(i, 2));
      BranchHandle branchHandle = instructionList.append(new IFNE(null));
      this._falseList.add(instructionList.append(new GOTO_W(null)));
      branchHandle.setTarget(instructionList.append(NOP));
    } else if (this._nodeType == 2) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "isAttribute", "(I)Z");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
      instructionList.append(new INVOKEINTERFACE(i, 2));
      BranchHandle branchHandle = instructionList.append(new IFNE(null));
      this._falseList.add(instructionList.append(new GOTO_W(null)));
      branchHandle.setTarget(instructionList.append(NOP));
    } else {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(SWAP);
      instructionList.append(new INVOKEINTERFACE(i, 2));
      instructionList.append(new PUSH(constantPoolGen, this._nodeType));
      BranchHandle branchHandle = instructionList.append(new IF_ICMPEQ(null));
      this._falseList.add(instructionList.append(new GOTO_W(null)));
      branchHandle.setTarget(instructionList.append(NOP));
    } 
  }
  
  private void translateNoContext(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(SWAP);
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    if (!this._isEpsilon) {
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      translateKernel(paramClassGenerator, paramMethodGenerator);
    } 
    int i = this._predicates.size();
    for (byte b = 0; b < i; b++) {
      Predicate predicate = (Predicate)this._predicates.elementAt(b);
      Expression expression = predicate.getExpr();
      expression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      this._trueList.append(expression._trueList);
      this._falseList.append(expression._falseList);
    } 
    InstructionHandle instructionHandle = instructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchTrueList(instructionHandle);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    instructionHandle = instructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchFalseList(instructionHandle);
    this._falseList.add(instructionList.append(new GOTO(null)));
    branchHandle.setTarget(instructionList.append(NOP));
  }
  
  private void translateSimpleContext(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
    localVariableGen1.setStart(instructionList.append(new ISTORE(localVariableGen1.getIndex())));
    if (!this._isEpsilon) {
      instructionList.append(new ILOAD(localVariableGen1.getIndex()));
      translateKernel(paramClassGenerator, paramMethodGenerator);
    } 
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(paramMethodGenerator.loadIterator());
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MatchingIterator", "<init>", "(ILcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
    this._step.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
    instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MatchingIterator")));
    instructionList.append(DUP);
    instructionList.append(new ILOAD(localVariableGen1.getIndex()));
    localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
    instructionList.append(new INVOKESPECIAL(i));
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new ILOAD(localVariableGen1.getIndex()));
    i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    instructionList.append(new INVOKEINTERFACE(i, 2));
    instructionList.append(paramMethodGenerator.setStartNode());
    instructionList.append(paramMethodGenerator.storeIterator());
    localVariableGen1.setEnd(instructionList.append(new ILOAD(localVariableGen1.getIndex())));
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    Predicate predicate = (Predicate)this._predicates.elementAt(0);
    Expression expression = predicate.getExpr();
    expression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    InstructionHandle instructionHandle = instructionList.append(paramMethodGenerator.storeIterator());
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    expression.backPatchTrueList(instructionHandle);
    BranchHandle branchHandle = instructionList.append(new GOTO(null));
    instructionHandle = instructionList.append(paramMethodGenerator.storeIterator());
    instructionList.append(paramMethodGenerator.storeCurrentNode());
    expression.backPatchFalseList(instructionHandle);
    this._falseList.add(instructionList.append(new GOTO(null)));
    branchHandle.setTarget(instructionList.append(NOP));
  }
  
  private void translateGeneralContext(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = 0;
    BranchHandle branchHandle1 = null;
    String str = getNextFieldName();
    LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
    localVariableGen2.setStart(instructionList.append(new ISTORE(localVariableGen2.getIndex())));
    LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    if (!paramClassGenerator.isExternal()) {
      Field field = new Field(2, constantPoolGen.addUtf8(str), constantPoolGen.addUtf8("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, constantPoolGen.getConstantPool());
      paramClassGenerator.addField(field);
      i = constantPoolGen.addFieldref(paramClassGenerator.getClassName(), str, "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new GETFIELD(i));
      instructionList.append(DUP);
      localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
      branchHandle1 = instructionList.append(new IFNONNULL(null));
      instructionList.append(paramClassGenerator.loadTranslet());
    } 
    this._step.translate(paramClassGenerator, paramMethodGenerator);
    InstructionHandle instructionHandle1 = instructionList.append(new ASTORE(localVariableGen1.getIndex()));
    if (!paramClassGenerator.isExternal()) {
      instructionList.append(new ALOAD(localVariableGen1.getIndex()));
      instructionList.append(new PUTFIELD(i));
      branchHandle1.setTarget(instructionList.append(NOP));
    } else {
      localVariableGen1.setStart(instructionHandle1);
    } 
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new ILOAD(localVariableGen2.getIndex()));
    int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    instructionList.append(new INVOKEINTERFACE(j, 2));
    instructionList.append(new ALOAD(localVariableGen1.getIndex()));
    instructionList.append(SWAP);
    instructionList.append(paramMethodGenerator.setStartNode());
    LocalVariableGen localVariableGen3 = paramMethodGenerator.addLocalVariable("step_pattern_tmp3", Util.getJCRefType("I"), null, null);
    BranchHandle branchHandle2 = instructionList.append(new GOTO(null));
    InstructionHandle instructionHandle3 = instructionList.append(new ALOAD(localVariableGen1.getIndex()));
    localVariableGen3.setStart(instructionHandle3);
    InstructionHandle instructionHandle2 = instructionList.append(paramMethodGenerator.nextNode());
    instructionList.append(DUP);
    instructionList.append(new ISTORE(localVariableGen3.getIndex()));
    this._falseList.add(instructionList.append(new IFLT(null)));
    instructionList.append(new ILOAD(localVariableGen3.getIndex()));
    instructionList.append(new ILOAD(localVariableGen2.getIndex()));
    localVariableGen1.setEnd(instructionList.append(new IF_ICMPLT(instructionHandle3)));
    localVariableGen3.setEnd(instructionList.append(new ILOAD(localVariableGen3.getIndex())));
    localVariableGen2.setEnd(instructionList.append(new ILOAD(localVariableGen2.getIndex())));
    this._falseList.add(instructionList.append(new IF_ICMPNE(null)));
    branchHandle2.setTarget(instructionHandle2);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (hasPredicates()) {
      switch (this._contextCase) {
        case 0:
          translateNoContext(paramClassGenerator, paramMethodGenerator);
          return;
        case 1:
          translateSimpleContext(paramClassGenerator, paramMethodGenerator);
          return;
      } 
      translateGeneralContext(paramClassGenerator, paramMethodGenerator);
    } else if (isWildcard()) {
      instructionList.append(POP);
    } else {
      translateKernel(paramClassGenerator, paramMethodGenerator);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\StepPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */