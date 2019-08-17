package com.sun.xml.internal.ws.org.objectweb.asm;

public class Label {
  static final int DEBUG = 1;
  
  static final int RESOLVED = 2;
  
  static final int RESIZED = 4;
  
  static final int PUSHED = 8;
  
  static final int TARGET = 16;
  
  static final int STORE = 32;
  
  static final int REACHABLE = 64;
  
  static final int JSR = 128;
  
  static final int RET = 256;
  
  static final int SUBROUTINE = 512;
  
  static final int VISITED = 1024;
  
  public Object info;
  
  int status;
  
  int line;
  
  int position;
  
  private int referenceCount;
  
  private int[] srcAndRefPositions;
  
  int inputStackTop;
  
  int outputStackMax;
  
  Frame frame;
  
  Label successor;
  
  Edge successors;
  
  Label next;
  
  public int getOffset() {
    if ((this.status & 0x2) == 0)
      throw new IllegalStateException("Label offset position has not been resolved yet"); 
    return this.position;
  }
  
  void put(MethodWriter paramMethodWriter, ByteVector paramByteVector, int paramInt, boolean paramBoolean) {
    if ((this.status & 0x2) == 0) {
      if (paramBoolean) {
        addReference(-1 - paramInt, paramByteVector.length);
        paramByteVector.putInt(-1);
      } else {
        addReference(paramInt, paramByteVector.length);
        paramByteVector.putShort(-1);
      } 
    } else if (paramBoolean) {
      paramByteVector.putInt(this.position - paramInt);
    } else {
      paramByteVector.putShort(this.position - paramInt);
    } 
  }
  
  private void addReference(int paramInt1, int paramInt2) {
    if (this.srcAndRefPositions == null)
      this.srcAndRefPositions = new int[6]; 
    if (this.referenceCount >= this.srcAndRefPositions.length) {
      int[] arrayOfInt = new int[this.srcAndRefPositions.length + 6];
      System.arraycopy(this.srcAndRefPositions, 0, arrayOfInt, 0, this.srcAndRefPositions.length);
      this.srcAndRefPositions = arrayOfInt;
    } 
    this.srcAndRefPositions[this.referenceCount++] = paramInt1;
    this.srcAndRefPositions[this.referenceCount++] = paramInt2;
  }
  
  boolean resolve(MethodWriter paramMethodWriter, int paramInt, byte[] paramArrayOfByte) {
    boolean bool = false;
    this.status |= 0x2;
    this.position = paramInt;
    byte b = 0;
    while (b < this.referenceCount) {
      int i = this.srcAndRefPositions[b++];
      int j = this.srcAndRefPositions[b++];
      if (i >= 0) {
        int m = paramInt - i;
        if (m < -32768 || m > 32767) {
          byte b1 = paramArrayOfByte[j - 1] & 0xFF;
          if (b1 <= 168) {
            paramArrayOfByte[j - 1] = (byte)(b1 + 49);
          } else {
            paramArrayOfByte[j - 1] = (byte)(b1 + 20);
          } 
          bool = true;
        } 
        paramArrayOfByte[j++] = (byte)(m >>> 8);
        paramArrayOfByte[j] = (byte)m;
        continue;
      } 
      int k = paramInt + i + 1;
      paramArrayOfByte[j++] = (byte)(k >>> 24);
      paramArrayOfByte[j++] = (byte)(k >>> 16);
      paramArrayOfByte[j++] = (byte)(k >>> 8);
      paramArrayOfByte[j] = (byte)k;
    } 
    return bool;
  }
  
  Label getFirst() { return (this.frame == null) ? this : this.frame.owner; }
  
  boolean inSubroutine(long paramLong) { return ((this.status & 0x400) != 0) ? (((this.srcAndRefPositions[(int)(paramLong >>> 32)] & (int)paramLong) != 0)) : false; }
  
  boolean inSameSubroutine(Label paramLabel) {
    for (byte b = 0; b < this.srcAndRefPositions.length; b++) {
      if ((this.srcAndRefPositions[b] & paramLabel.srcAndRefPositions[b]) != 0)
        return true; 
    } 
    return false;
  }
  
  void addToSubroutine(long paramLong, int paramInt) {
    if ((this.status & 0x400) == 0) {
      this.status |= 0x400;
      this.srcAndRefPositions = new int[(paramInt - 1) / 32 + 1];
    } 
    this.srcAndRefPositions[(int)(paramLong >>> 32)] = this.srcAndRefPositions[(int)(paramLong >>> 32)] | (int)paramLong;
  }
  
  void visitSubroutine(Label paramLabel, long paramLong, int paramInt) {
    if (paramLabel != null) {
      if ((this.status & 0x400) != 0)
        return; 
      this.status |= 0x400;
      if ((this.status & 0x100) != 0 && !inSameSubroutine(paramLabel)) {
        Edge edge1 = new Edge();
        edge1.info = this.inputStackTop;
        edge1.successor = paramLabel.successors.successor;
        edge1.next = this.successors;
        this.successors = edge1;
      } 
    } else {
      if (inSubroutine(paramLong))
        return; 
      addToSubroutine(paramLong, paramInt);
    } 
    for (Edge edge = this.successors; edge != null; edge = edge.next) {
      if ((this.status & 0x80) == 0 || edge != this.successors.next)
        edge.successor.visitSubroutine(paramLabel, paramLong, paramInt); 
    } 
  }
  
  public String toString() { return "L" + System.identityHashCode(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */