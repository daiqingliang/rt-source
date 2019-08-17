package com.sun.java.util.jar.pack;

import java.util.Arrays;
import java.util.Collection;

class Code extends Attribute.Holder {
  Package.Class.Method m;
  
  private static final ConstantPool.Entry[] noRefs = ConstantPool.noRefs;
  
  int max_stack;
  
  int max_locals;
  
  ConstantPool.Entry[] handler_class = noRefs;
  
  int[] handler_start = Constants.noInts;
  
  int[] handler_end = Constants.noInts;
  
  int[] handler_catch = Constants.noInts;
  
  byte[] bytes;
  
  Fixups fixups;
  
  Object insnMap;
  
  static final boolean shrinkMaps = true;
  
  public Code(Package.Class.Method paramMethod) { this.m = paramMethod; }
  
  public Package.Class.Method getMethod() { return this.m; }
  
  public Package.Class thisClass() { return this.m.thisClass(); }
  
  public Package getPackage() { return this.m.thisClass().getPackage(); }
  
  public ConstantPool.Entry[] getCPMap() { return this.m.getCPMap(); }
  
  int getLength() { return this.bytes.length; }
  
  int getMaxStack() { return this.max_stack; }
  
  void setMaxStack(int paramInt) { this.max_stack = paramInt; }
  
  int getMaxNALocals() {
    int i = this.m.getArgumentSize();
    return this.max_locals - i;
  }
  
  void setMaxNALocals(int paramInt) {
    int i = this.m.getArgumentSize();
    this.max_locals = i + paramInt;
  }
  
  int getHandlerCount() {
    assert this.handler_class.length == this.handler_start.length;
    assert this.handler_class.length == this.handler_end.length;
    assert this.handler_class.length == this.handler_catch.length;
    return this.handler_class.length;
  }
  
  void setHandlerCount(int paramInt) {
    if (paramInt > 0) {
      this.handler_class = new ConstantPool.Entry[paramInt];
      this.handler_start = new int[paramInt];
      this.handler_end = new int[paramInt];
      this.handler_catch = new int[paramInt];
    } 
  }
  
  void setBytes(byte[] paramArrayOfByte) {
    this.bytes = paramArrayOfByte;
    if (this.fixups != null)
      this.fixups.setBytes(paramArrayOfByte); 
  }
  
  void setInstructionMap(int[] paramArrayOfInt, int paramInt) { this.insnMap = allocateInstructionMap(paramArrayOfInt, paramInt); }
  
  void setInstructionMap(int[] paramArrayOfInt) { setInstructionMap(paramArrayOfInt, paramArrayOfInt.length); }
  
  int[] getInstructionMap() { return expandInstructionMap(getInsnMap()); }
  
  void addFixups(Collection<Fixups.Fixup> paramCollection) {
    if (this.fixups == null)
      this.fixups = new Fixups(this.bytes); 
    assert this.fixups.getBytes() == this.bytes;
    this.fixups.addAll(paramCollection);
  }
  
  public void trimToSize() {
    if (this.fixups != null) {
      this.fixups.trimToSize();
      if (this.fixups.size() == 0)
        this.fixups = null; 
    } 
    super.trimToSize();
  }
  
  protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection) {
    int i = (getPackage()).verbose;
    if (i > 2)
      System.out.println("Reference scan " + this); 
    paramCollection.addAll(Arrays.asList(this.handler_class));
    if (this.fixups != null) {
      this.fixups.visitRefs(paramCollection);
    } else {
      ConstantPool.Entry[] arrayOfEntry = getCPMap();
      for (Instruction instruction = instructionAt(0); instruction != null; instruction = instruction.next()) {
        if (i > 4)
          System.out.println(instruction); 
        int j = instruction.getCPIndex();
        if (j >= 0)
          paramCollection.add(arrayOfEntry[j]); 
      } 
    } 
    super.visitRefs(paramInt, paramCollection);
  }
  
  private Object allocateInstructionMap(int[] paramArrayOfInt, int paramInt) {
    int i = getLength();
    if (i <= 255) {
      byte[] arrayOfByte = new byte[paramInt + 1];
      for (byte b = 0; b < paramInt; b++)
        arrayOfByte[b] = (byte)(paramArrayOfInt[b] + -128); 
      arrayOfByte[paramInt] = (byte)(i + -128);
      return arrayOfByte;
    } 
    if (i < 65535) {
      short[] arrayOfShort = new short[paramInt + 1];
      for (byte b = 0; b < paramInt; b++)
        arrayOfShort[b] = (short)(paramArrayOfInt[b] + -32768); 
      arrayOfShort[paramInt] = (short)(i + -32768);
      return arrayOfShort;
    } 
    int[] arrayOfInt = Arrays.copyOf(paramArrayOfInt, paramInt + 1);
    arrayOfInt[paramInt] = i;
    return arrayOfInt;
  }
  
  private int[] expandInstructionMap(Object paramObject) {
    int[] arrayOfInt;
    if (paramObject instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject;
      arrayOfInt = new int[arrayOfByte.length - 1];
      for (byte b = 0; b < arrayOfInt.length; b++)
        arrayOfInt[b] = arrayOfByte[b] - Byte.MIN_VALUE; 
    } else if (paramObject instanceof short[]) {
      short[] arrayOfShort = (short[])paramObject;
      arrayOfInt = new int[arrayOfShort.length - 1];
      for (byte b = 0; b < arrayOfInt.length; b++)
        arrayOfInt[b] = arrayOfShort[b] - -128; 
    } else {
      int[] arrayOfInt1 = (int[])paramObject;
      arrayOfInt = Arrays.copyOfRange(arrayOfInt1, 0, arrayOfInt1.length - 1);
    } 
    return arrayOfInt;
  }
  
  Object getInsnMap() {
    if (this.insnMap != null)
      return this.insnMap; 
    int[] arrayOfInt = new int[getLength()];
    byte b = 0;
    for (Instruction instruction = instructionAt(0); instruction != null; instruction = instruction.next())
      arrayOfInt[b++] = instruction.getPC(); 
    this.insnMap = allocateInstructionMap(arrayOfInt, b);
    return this.insnMap;
  }
  
  public int encodeBCI(int paramInt) {
    int j;
    int i;
    if (paramInt <= 0 || paramInt > getLength())
      return paramInt; 
    Object object = getInsnMap();
    if (object instanceof byte[]) {
      byte[] arrayOfByte = (byte[])object;
      j = arrayOfByte.length;
      i = Arrays.binarySearch(arrayOfByte, (byte)(paramInt + -128));
    } else if (object instanceof short[]) {
      short[] arrayOfShort = (short[])object;
      j = arrayOfShort.length;
      i = Arrays.binarySearch(arrayOfShort, (short)(paramInt + -32768));
    } else {
      int[] arrayOfInt = (int[])object;
      j = arrayOfInt.length;
      i = Arrays.binarySearch(arrayOfInt, paramInt);
    } 
    assert i != -1;
    assert i != 0;
    assert i != j;
    assert i != -j - 1;
    return (i >= 0) ? i : (j + paramInt - -i - 1);
  }
  
  public int decodeBCI(int paramInt) {
    int j;
    int i;
    if (paramInt <= 0 || paramInt > getLength())
      return paramInt; 
    Object object = getInsnMap();
    if (object instanceof byte[]) {
      byte[] arrayOfByte = (byte[])object;
      j = arrayOfByte.length;
      if (paramInt < j)
        return arrayOfByte[paramInt] - Byte.MIN_VALUE; 
      i = Arrays.binarySearch(arrayOfByte, (byte)(paramInt + -128));
      if (i < 0)
        i = -i - 1; 
      int k = paramInt - j + -128;
      while (arrayOfByte[i - 1] - i - 1 > k)
        i--; 
    } else if (object instanceof short[]) {
      short[] arrayOfShort = (short[])object;
      j = arrayOfShort.length;
      if (paramInt < j)
        return arrayOfShort[paramInt] - Short.MIN_VALUE; 
      i = Arrays.binarySearch(arrayOfShort, (short)(paramInt + -32768));
      if (i < 0)
        i = -i - 1; 
      int k = paramInt - j + -32768;
      while (arrayOfShort[i - 1] - i - 1 > k)
        i--; 
    } else {
      int[] arrayOfInt = (int[])object;
      j = arrayOfInt.length;
      if (paramInt < j)
        return arrayOfInt[paramInt]; 
      i = Arrays.binarySearch(arrayOfInt, paramInt);
      if (i < 0)
        i = -i - 1; 
      int k = paramInt - j;
      while (arrayOfInt[i - 1] - i - 1 > k)
        i--; 
    } 
    return paramInt - j + i;
  }
  
  public void finishRefs(ConstantPool.Index paramIndex) {
    if (this.fixups != null) {
      this.fixups.finishRefs(paramIndex);
      this.fixups = null;
    } 
  }
  
  Instruction instructionAt(int paramInt) { return Instruction.at(this.bytes, paramInt); }
  
  static boolean flagsRequireCode(int paramInt) { return ((paramInt & 0x500) == 0); }
  
  public String toString() { return this.m + ".Code"; }
  
  public int getInt(int paramInt) { return Instruction.getInt(this.bytes, paramInt); }
  
  public int getShort(int paramInt) { return Instruction.getShort(this.bytes, paramInt); }
  
  public int getByte(int paramInt) { return Instruction.getByte(this.bytes, paramInt); }
  
  void setInt(int paramInt1, int paramInt2) { Instruction.setInt(this.bytes, paramInt1, paramInt2); }
  
  void setShort(int paramInt1, int paramInt2) { Instruction.setShort(this.bytes, paramInt1, paramInt2); }
  
  void setByte(int paramInt1, int paramInt2) { Instruction.setByte(this.bytes, paramInt1, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Code.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */