package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import java.util.Iterator;
import java.util.Vector;

public final class FlowList {
  private Vector _elements = null;
  
  public FlowList() {}
  
  public FlowList(InstructionHandle paramInstructionHandle) { this._elements.addElement(paramInstructionHandle); }
  
  public FlowList(FlowList paramFlowList) {}
  
  public FlowList add(InstructionHandle paramInstructionHandle) {
    if (this._elements == null)
      this._elements = new Vector(); 
    this._elements.addElement(paramInstructionHandle);
    return this;
  }
  
  public FlowList append(FlowList paramFlowList) {
    if (this._elements == null) {
      this._elements = paramFlowList._elements;
    } else {
      Vector vector = paramFlowList._elements;
      if (vector != null) {
        int i = vector.size();
        for (byte b = 0; b < i; b++)
          this._elements.addElement(vector.elementAt(b)); 
      } 
    } 
    return this;
  }
  
  public void backPatch(InstructionHandle paramInstructionHandle) {
    if (this._elements != null) {
      int i = this._elements.size();
      for (byte b = 0; b < i; b++) {
        BranchHandle branchHandle = (BranchHandle)this._elements.elementAt(b);
        branchHandle.setTarget(paramInstructionHandle);
      } 
      this._elements.clear();
    } 
  }
  
  public FlowList copyAndRedirect(InstructionList paramInstructionList1, InstructionList paramInstructionList2) {
    FlowList flowList = new FlowList();
    if (this._elements == null)
      return flowList; 
    int i = this._elements.size();
    Iterator iterator1 = paramInstructionList1.iterator();
    Iterator iterator2 = paramInstructionList2.iterator();
    while (iterator1.hasNext()) {
      InstructionHandle instructionHandle1 = (InstructionHandle)iterator1.next();
      InstructionHandle instructionHandle2 = (InstructionHandle)iterator2.next();
      for (byte b = 0; b < i; b++) {
        if (this._elements.elementAt(b) == instructionHandle1)
          flowList.add(instructionHandle2); 
      } 
    } 
    return flowList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FlowList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */