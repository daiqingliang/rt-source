package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Map;
import java.util.Vector;

final class TestSeq {
  private int _kernelType;
  
  private Vector _patterns = null;
  
  private Mode _mode = null;
  
  private Template _default = null;
  
  private InstructionList _instructionList;
  
  private InstructionHandle _start = null;
  
  public TestSeq(Vector paramVector, Mode paramMode) { this(paramVector, -2, paramMode); }
  
  public TestSeq(Vector paramVector, int paramInt, Mode paramMode) {
    this._patterns = paramVector;
    this._kernelType = paramInt;
    this._mode = paramMode;
  }
  
  public String toString() {
    int i = this._patterns.size();
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < i; b++) {
      LocationPathPattern locationPathPattern = (LocationPathPattern)this._patterns.elementAt(b);
      if (b == 0)
        stringBuffer.append("Testseq for kernel ").append(this._kernelType).append('\n'); 
      stringBuffer.append("   pattern ").append(b).append(": ").append(locationPathPattern.toString()).append('\n');
    } 
    return stringBuffer.toString();
  }
  
  public InstructionList getInstructionList() { return this._instructionList; }
  
  public double getPriority() {
    Template template = (this._patterns.size() == 0) ? this._default : ((Pattern)this._patterns.elementAt(0)).getTemplate();
    return template.getPriority();
  }
  
  public int getPosition() {
    Template template = (this._patterns.size() == 0) ? this._default : ((Pattern)this._patterns.elementAt(0)).getTemplate();
    return template.getPosition();
  }
  
  public void reduce() {
    Vector vector = new Vector();
    int i = this._patterns.size();
    for (byte b = 0; b < i; b++) {
      LocationPathPattern locationPathPattern = (LocationPathPattern)this._patterns.elementAt(b);
      locationPathPattern.reduceKernelPattern();
      if (locationPathPattern.isWildcard()) {
        this._default = locationPathPattern.getTemplate();
        break;
      } 
      vector.addElement(locationPathPattern);
    } 
    this._patterns = vector;
  }
  
  public void findTemplates(Map<Template, Object> paramMap) {
    if (this._default != null)
      paramMap.put(this._default, this); 
    for (byte b = 0; b < this._patterns.size(); b++) {
      LocationPathPattern locationPathPattern = (LocationPathPattern)this._patterns.elementAt(b);
      paramMap.put(locationPathPattern.getTemplate(), this);
    } 
  }
  
  private InstructionHandle getTemplateHandle(Template paramTemplate) { return this._mode.getTemplateInstructionHandle(paramTemplate); }
  
  private LocationPathPattern getPattern(int paramInt) { return (LocationPathPattern)this._patterns.elementAt(paramInt); }
  
  public InstructionHandle compile(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle) {
    if (this._start != null)
      return this._start; 
    int i = this._patterns.size();
    if (i == 0)
      return this._start = getTemplateHandle(this._default); 
    InstructionHandle instructionHandle = (this._default == null) ? paramInstructionHandle : getTemplateHandle(this._default);
    for (int j = i - 1; j >= 0; j--) {
      LocationPathPattern locationPathPattern = getPattern(j);
      Template template = locationPathPattern.getTemplate();
      InstructionList instructionList1 = new InstructionList();
      instructionList1.append(paramMethodGenerator.loadCurrentNode());
      InstructionList instructionList2 = paramMethodGenerator.getInstructionList(locationPathPattern);
      if (instructionList2 == null) {
        instructionList2 = locationPathPattern.compile(paramClassGenerator, paramMethodGenerator);
        paramMethodGenerator.addInstructionList(locationPathPattern, instructionList2);
      } 
      InstructionList instructionList3 = instructionList2.copy();
      FlowList flowList1 = locationPathPattern.getTrueList();
      if (flowList1 != null)
        flowList1 = flowList1.copyAndRedirect(instructionList2, instructionList3); 
      FlowList flowList2 = locationPathPattern.getFalseList();
      if (flowList2 != null)
        flowList2 = flowList2.copyAndRedirect(instructionList2, instructionList3); 
      instructionList1.append(instructionList3);
      InstructionHandle instructionHandle1 = getTemplateHandle(template);
      BranchHandle branchHandle = instructionList1.append(new GOTO_W(instructionHandle1));
      if (flowList1 != null)
        flowList1.backPatch(branchHandle); 
      if (flowList2 != null)
        flowList2.backPatch(instructionHandle); 
      instructionHandle = instructionList1.getStart();
      if (this._instructionList != null)
        instructionList1.append(this._instructionList); 
      this._instructionList = instructionList1;
    } 
    return this._start = instructionHandle;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\TestSeq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */