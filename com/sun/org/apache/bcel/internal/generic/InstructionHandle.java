package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class InstructionHandle implements Serializable {
  InstructionHandle next;
  
  InstructionHandle prev;
  
  Instruction instruction;
  
  protected int i_position = -1;
  
  private HashSet targeters;
  
  private HashMap attributes;
  
  private static InstructionHandle ih_list = null;
  
  public final InstructionHandle getNext() { return this.next; }
  
  public final InstructionHandle getPrev() { return this.prev; }
  
  public final Instruction getInstruction() { return this.instruction; }
  
  public void setInstruction(Instruction paramInstruction) {
    if (paramInstruction == null)
      throw new ClassGenException("Assigning null to handle"); 
    if (getClass() != BranchHandle.class && paramInstruction instanceof BranchInstruction)
      throw new ClassGenException("Assigning branch instruction " + paramInstruction + " to plain handle"); 
    if (this.instruction != null)
      this.instruction.dispose(); 
    this.instruction = paramInstruction;
  }
  
  public Instruction swapInstruction(Instruction paramInstruction) {
    Instruction instruction1 = this.instruction;
    this.instruction = paramInstruction;
    return instruction1;
  }
  
  protected InstructionHandle(Instruction paramInstruction) { setInstruction(paramInstruction); }
  
  static final InstructionHandle getInstructionHandle(Instruction paramInstruction) {
    if (ih_list == null)
      return new InstructionHandle(paramInstruction); 
    InstructionHandle instructionHandle = ih_list;
    ih_list = instructionHandle.next;
    instructionHandle.setInstruction(paramInstruction);
    return instructionHandle;
  }
  
  protected int updatePosition(int paramInt1, int paramInt2) {
    this.i_position += paramInt1;
    return 0;
  }
  
  public int getPosition() { return this.i_position; }
  
  void setPosition(int paramInt) { this.i_position = paramInt; }
  
  protected void addHandle() {
    this.next = ih_list;
    ih_list = this;
  }
  
  void dispose() {
    this.next = this.prev = null;
    this.instruction.dispose();
    this.instruction = null;
    this.i_position = -1;
    this.attributes = null;
    removeAllTargeters();
    addHandle();
  }
  
  public void removeAllTargeters() {
    if (this.targeters != null)
      this.targeters.clear(); 
  }
  
  public void removeTargeter(InstructionTargeter paramInstructionTargeter) { this.targeters.remove(paramInstructionTargeter); }
  
  public void addTargeter(InstructionTargeter paramInstructionTargeter) {
    if (this.targeters == null)
      this.targeters = new HashSet(); 
    this.targeters.add(paramInstructionTargeter);
  }
  
  public boolean hasTargeters() { return (this.targeters != null && this.targeters.size() > 0); }
  
  public InstructionTargeter[] getTargeters() {
    if (!hasTargeters())
      return null; 
    InstructionTargeter[] arrayOfInstructionTargeter = new InstructionTargeter[this.targeters.size()];
    this.targeters.toArray(arrayOfInstructionTargeter);
    return arrayOfInstructionTargeter;
  }
  
  public String toString(boolean paramBoolean) { return Utility.format(this.i_position, 4, false, ' ') + ": " + this.instruction.toString(paramBoolean); }
  
  public String toString() { return toString(true); }
  
  public void addAttribute(Object paramObject1, Object paramObject2) {
    if (this.attributes == null)
      this.attributes = new HashMap(3); 
    this.attributes.put(paramObject1, paramObject2);
  }
  
  public void removeAttribute(Object paramObject) {
    if (this.attributes != null)
      this.attributes.remove(paramObject); 
  }
  
  public Object getAttribute(Object paramObject) { return (this.attributes != null) ? this.attributes.get(paramObject) : null; }
  
  public Collection getAttributes() { return this.attributes.values(); }
  
  public void accept(Visitor paramVisitor) { this.instruction.accept(paramVisitor); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */