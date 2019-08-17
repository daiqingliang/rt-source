package com.sun.org.apache.bcel.internal.generic;

public final class BranchHandle extends InstructionHandle {
  private BranchInstruction bi;
  
  private static BranchHandle bh_list = null;
  
  private BranchHandle(BranchInstruction paramBranchInstruction) {
    super(paramBranchInstruction);
    this.bi = paramBranchInstruction;
  }
  
  static final BranchHandle getBranchHandle(BranchInstruction paramBranchInstruction) {
    if (bh_list == null)
      return new BranchHandle(paramBranchInstruction); 
    BranchHandle branchHandle = bh_list;
    bh_list = (BranchHandle)branchHandle.next;
    branchHandle.setInstruction(paramBranchInstruction);
    return branchHandle;
  }
  
  protected void addHandle() {
    this.next = bh_list;
    bh_list = this;
  }
  
  public int getPosition() { return this.bi.position; }
  
  void setPosition(int paramInt) { this.i_position = this.bi.position = paramInt; }
  
  protected int updatePosition(int paramInt1, int paramInt2) {
    int i = this.bi.updatePosition(paramInt1, paramInt2);
    this.i_position = this.bi.position;
    return i;
  }
  
  public void setTarget(InstructionHandle paramInstructionHandle) { this.bi.setTarget(paramInstructionHandle); }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) { this.bi.updateTarget(paramInstructionHandle1, paramInstructionHandle2); }
  
  public InstructionHandle getTarget() { return this.bi.getTarget(); }
  
  public void setInstruction(Instruction paramInstruction) {
    super.setInstruction(paramInstruction);
    if (!(paramInstruction instanceof BranchInstruction))
      throw new ClassGenException("Assigning " + paramInstruction + " to branch handle which is not a branch instruction"); 
    this.bi = (BranchInstruction)paramInstruction;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\BranchHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */