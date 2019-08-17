package com.sun.org.apache.bcel.internal.generic;

public final class SWITCH implements CompoundInstruction {
  private int[] match;
  
  private InstructionHandle[] targets;
  
  private Select instruction;
  
  private int match_length;
  
  public SWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle, int paramInt) {
    this.match = (int[])paramArrayOfInt.clone();
    this.targets = (InstructionHandle[])paramArrayOfInstructionHandle.clone();
    if ((this.match_length = paramArrayOfInt.length) < 2) {
      this.instruction = new TABLESWITCH(paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle);
    } else {
      sort(0, this.match_length - 1);
      if (matchIsOrdered(paramInt)) {
        fillup(paramInt, paramInstructionHandle);
        this.instruction = new TABLESWITCH(this.match, this.targets, paramInstructionHandle);
      } else {
        this.instruction = new LOOKUPSWITCH(this.match, this.targets, paramInstructionHandle);
      } 
    } 
  }
  
  public SWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle) { this(paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle, 1); }
  
  private final void fillup(int paramInt, InstructionHandle paramInstructionHandle) {
    int i = this.match_length + this.match_length * paramInt;
    int[] arrayOfInt = new int[i];
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[i];
    byte b1 = 1;
    arrayOfInt[0] = this.match[0];
    arrayOfInstructionHandle[0] = this.targets[0];
    for (byte b2 = 1; b2 < this.match_length; b2++) {
      int j = this.match[b2 - true];
      int k = this.match[b2] - j;
      for (int m = 1; m < k; m++) {
        arrayOfInt[b1] = j + m;
        arrayOfInstructionHandle[b1] = paramInstructionHandle;
        b1++;
      } 
      arrayOfInt[b1] = this.match[b2];
      arrayOfInstructionHandle[b1] = this.targets[b2];
      b1++;
    } 
    this.match = new int[b1];
    this.targets = new InstructionHandle[b1];
    System.arraycopy(arrayOfInt, 0, this.match, 0, b1);
    System.arraycopy(arrayOfInstructionHandle, 0, this.targets, 0, b1);
  }
  
  private final void sort(int paramInt1, int paramInt2) {
    int i = paramInt1;
    int j = paramInt2;
    int k = this.match[(paramInt1 + paramInt2) / 2];
    do {
      while (this.match[i] < k)
        i++; 
      while (k < this.match[j])
        j--; 
      if (i > j)
        continue; 
      int m = this.match[i];
      this.match[i] = this.match[j];
      this.match[j] = m;
      InstructionHandle instructionHandle = this.targets[i];
      this.targets[i] = this.targets[j];
      this.targets[j] = instructionHandle;
      i++;
      j--;
    } while (i <= j);
    if (paramInt1 < j)
      sort(paramInt1, j); 
    if (i < paramInt2)
      sort(i, paramInt2); 
  }
  
  private final boolean matchIsOrdered(int paramInt) {
    for (byte b = 1; b < this.match_length; b++) {
      if (this.match[b] - this.match[b - true] > paramInt)
        return false; 
    } 
    return true;
  }
  
  public final InstructionList getInstructionList() { return new InstructionList(this.instruction); }
  
  public final Instruction getInstruction() { return this.instruction; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\SWITCH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */