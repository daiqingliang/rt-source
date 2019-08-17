package com.sun.java.util.jar.pack;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

final class Fixups extends AbstractCollection<Fixups.Fixup> {
  byte[] bytes;
  
  int head;
  
  int tail;
  
  int size;
  
  ConstantPool.Entry[] entries;
  
  int[] bigDescs;
  
  private static final int MINBIGSIZE = 1;
  
  private static final int[] noBigDescs = { 1 };
  
  private static final int LOC_SHIFT = 1;
  
  private static final int FMT_MASK = 1;
  
  private static final byte UNUSED_BYTE = 0;
  
  private static final byte OVERFLOW_BYTE = -1;
  
  private static final int BIGSIZE = 0;
  
  private static final int U2_FORMAT = 0;
  
  private static final int U1_FORMAT = 1;
  
  private static final int SPECIAL_LOC = 0;
  
  private static final int SPECIAL_FMT = 0;
  
  Fixups(byte[] paramArrayOfByte) {
    this.bytes = paramArrayOfByte;
    this.entries = new ConstantPool.Entry[3];
    this.bigDescs = noBigDescs;
  }
  
  Fixups() { this((byte[])null); }
  
  Fixups(byte[] paramArrayOfByte, Collection<Fixup> paramCollection) {
    this(paramArrayOfByte);
    addAll(paramCollection);
  }
  
  Fixups(Collection<Fixup> paramCollection) {
    this((byte[])null);
    addAll(paramCollection);
  }
  
  public int size() { return this.size; }
  
  public void trimToSize() {
    if (this.size != this.entries.length) {
      ConstantPool.Entry[] arrayOfEntry = this.entries;
      this.entries = new ConstantPool.Entry[this.size];
      System.arraycopy(arrayOfEntry, 0, this.entries, 0, this.size);
    } 
    int i = this.bigDescs[0];
    if (i == 1) {
      this.bigDescs = noBigDescs;
    } else if (i != this.bigDescs.length) {
      int[] arrayOfInt = this.bigDescs;
      this.bigDescs = new int[i];
      System.arraycopy(arrayOfInt, 0, this.bigDescs, 0, i);
    } 
  }
  
  public void visitRefs(Collection<ConstantPool.Entry> paramCollection) {
    for (byte b = 0; b < this.size; b++)
      paramCollection.add(this.entries[b]); 
  }
  
  public void clear() {
    if (this.bytes != null)
      for (Fixup fixup : this)
        storeIndex(fixup.location(), fixup.format(), 0);  
    this.size = 0;
    if (this.bigDescs != noBigDescs)
      this.bigDescs[0] = 1; 
  }
  
  public byte[] getBytes() { return this.bytes; }
  
  public void setBytes(byte[] paramArrayOfByte) {
    if (this.bytes == paramArrayOfByte)
      return; 
    ArrayList arrayList = null;
    assert (arrayList = new ArrayList(this)) != null;
    if (this.bytes == null || paramArrayOfByte == null) {
      ArrayList arrayList1 = new ArrayList(this);
      clear();
      this.bytes = paramArrayOfByte;
      addAll(arrayList1);
    } else {
      this.bytes = paramArrayOfByte;
    } 
    assert arrayList.equals(new ArrayList(this));
  }
  
  static int fmtLen(int paramInt) { return 1 + (paramInt - 1) / -1; }
  
  static int descLoc(int paramInt) { return paramInt >>> 1; }
  
  static int descFmt(int paramInt) { return paramInt & true; }
  
  static int descEnd(int paramInt) { return descLoc(paramInt) + fmtLen(descFmt(paramInt)); }
  
  static int makeDesc(int paramInt1, int paramInt2) {
    int i = paramInt1 << 1 | paramInt2;
    assert descLoc(i) == paramInt1;
    assert descFmt(i) == paramInt2;
    return i;
  }
  
  int fetchDesc(int paramInt1, int paramInt2) {
    byte b2;
    byte b1 = this.bytes[paramInt1];
    assert b1 != -1;
    if (paramInt2 == 0) {
      byte b = this.bytes[paramInt1 + 1];
      b2 = ((b1 & 0xFF) << 8) + (b & 0xFF);
    } else {
      b2 = b1 & 0xFF;
    } 
    return b2 + (paramInt1 << 1);
  }
  
  boolean storeDesc(int paramInt1, int paramInt2, int paramInt3) {
    byte b2;
    byte b1;
    if (this.bytes == null)
      return false; 
    int i = paramInt3 - (paramInt1 << 1);
    switch (paramInt2) {
      case 0:
        assert this.bytes[paramInt1 + 0] == 0;
        assert this.bytes[paramInt1 + 1] == 0;
        b1 = (byte)(i >> 8);
        b2 = (byte)(i >> 0);
        if (i == (i & 0xFFFF) && b1 != -1) {
          this.bytes[paramInt1 + 0] = b1;
          this.bytes[paramInt1 + 1] = b2;
          assert fetchDesc(paramInt1, paramInt2) == paramInt3;
          return true;
        } 
        break;
      case 1:
        assert this.bytes[paramInt1] == 0;
        b1 = (byte)i;
        if (i == (i & 0xFF) && b1 != -1) {
          this.bytes[paramInt1] = b1;
          assert fetchDesc(paramInt1, paramInt2) == paramInt3;
          return true;
        } 
        break;
      default:
        assert false;
        break;
    } 
    this.bytes[paramInt1] = -1;
    this.bytes[paramInt1 + 1] = (byte)this.bigDescs[0];
    assert paramInt2 == 1 || (byte)this.bigDescs[0] != 999;
    return false;
  }
  
  void storeIndex(int paramInt1, int paramInt2, int paramInt3) { storeIndex(this.bytes, paramInt1, paramInt2, paramInt3); }
  
  static void storeIndex(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) {
    switch (paramInt2) {
      case 0:
        assert paramInt3 == (paramInt3 & 0xFFFF) : paramInt3;
        paramArrayOfByte[paramInt1 + 0] = (byte)(paramInt3 >> 8);
        paramArrayOfByte[paramInt1 + 1] = (byte)(paramInt3 >> 0);
        return;
      case 1:
        assert paramInt3 == (paramInt3 & 0xFF) : paramInt3;
        paramArrayOfByte[paramInt1] = (byte)paramInt3;
        return;
    } 
    assert false;
  }
  
  void addU1(int paramInt, ConstantPool.Entry paramEntry) { add(paramInt, 1, paramEntry); }
  
  void addU2(int paramInt, ConstantPool.Entry paramEntry) { add(paramInt, 0, paramEntry); }
  
  public Iterator<Fixup> iterator() { return new Itr(null); }
  
  public void add(int paramInt1, int paramInt2, ConstantPool.Entry paramEntry) { addDesc(makeDesc(paramInt1, paramInt2), paramEntry); }
  
  public boolean add(Fixup paramFixup) {
    addDesc(paramFixup.desc, paramFixup.entry);
    return true;
  }
  
  public boolean addAll(Collection<? extends Fixup> paramCollection) {
    if (paramCollection instanceof Fixups) {
      Fixups fixups = (Fixups)paramCollection;
      if (fixups.size == 0)
        return false; 
      if (this.size == 0 && this.entries.length < fixups.size)
        growEntries(fixups.size); 
      ConstantPool.Entry[] arrayOfEntry = fixups.entries;
      fixups.getClass();
      Itr itr = new Itr(null);
      while (itr.hasNext()) {
        int i = itr.index;
        addDesc(itr.nextDesc(), arrayOfEntry[i]);
      } 
      return true;
    } 
    return super.addAll(paramCollection);
  }
  
  private void addDesc(int paramInt, ConstantPool.Entry paramEntry) {
    if (this.entries.length == this.size)
      growEntries(this.size * 2); 
    this.entries[this.size] = paramEntry;
    if (this.size == 0) {
      this.head = this.tail = paramInt;
    } else {
      int i = this.tail;
      int j = descLoc(i);
      int k = descFmt(i);
      int m = fmtLen(k);
      int n = descLoc(paramInt);
      if (n < j + m)
        badOverlap(n); 
      this.tail = paramInt;
      if (!storeDesc(j, k, paramInt)) {
        int i1 = this.bigDescs[0];
        if (this.bigDescs.length == i1)
          growBigDescs(); 
        this.bigDescs[i1++] = paramInt;
        this.bigDescs[0] = i1;
      } 
    } 
    this.size++;
  }
  
  private void badOverlap(int paramInt) { throw new IllegalArgumentException("locs must be ascending and must not overlap:  " + paramInt + " >> " + this); }
  
  private void growEntries(int paramInt) {
    ConstantPool.Entry[] arrayOfEntry = this.entries;
    this.entries = new ConstantPool.Entry[Math.max(3, paramInt)];
    System.arraycopy(arrayOfEntry, 0, this.entries, 0, arrayOfEntry.length);
  }
  
  private void growBigDescs() {
    int[] arrayOfInt = this.bigDescs;
    this.bigDescs = new int[arrayOfInt.length * 2];
    System.arraycopy(arrayOfInt, 0, this.bigDescs, 0, arrayOfInt.length);
  }
  
  static Object addRefWithBytes(Object paramObject, byte[] paramArrayOfByte, ConstantPool.Entry paramEntry) { return add(paramObject, paramArrayOfByte, 0, 0, paramEntry); }
  
  static Object addRefWithLoc(Object paramObject, int paramInt, ConstantPool.Entry paramEntry) { return add(paramObject, null, paramInt, 0, paramEntry); }
  
  private static Object add(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ConstantPool.Entry paramEntry) {
    Fixups fixups;
    if (paramObject == null) {
      if (paramInt1 == 0 && paramInt2 == 0)
        return paramEntry; 
      fixups = new Fixups(paramArrayOfByte);
    } else if (!(paramObject instanceof Fixups)) {
      ConstantPool.Entry entry = (ConstantPool.Entry)paramObject;
      fixups = new Fixups(paramArrayOfByte);
      fixups.add(0, 0, entry);
    } else {
      fixups = (Fixups)paramObject;
      assert fixups.bytes == paramArrayOfByte;
    } 
    fixups.add(paramInt1, paramInt2, paramEntry);
    return fixups;
  }
  
  public static void setBytes(Object paramObject, byte[] paramArrayOfByte) {
    if (paramObject instanceof Fixups) {
      Fixups fixups = (Fixups)paramObject;
      fixups.setBytes(paramArrayOfByte);
    } 
  }
  
  public static Object trimToSize(Object paramObject) {
    if (paramObject instanceof Fixups) {
      Fixups fixups = (Fixups)paramObject;
      fixups.trimToSize();
      if (fixups.size() == 0)
        paramObject = null; 
    } 
    return paramObject;
  }
  
  public static void visitRefs(Object paramObject, Collection<ConstantPool.Entry> paramCollection) {
    if (paramObject != null)
      if (!(paramObject instanceof Fixups)) {
        paramCollection.add((ConstantPool.Entry)paramObject);
      } else {
        Fixups fixups = (Fixups)paramObject;
        fixups.visitRefs(paramCollection);
      }  
  }
  
  public static void finishRefs(Object paramObject, byte[] paramArrayOfByte, ConstantPool.Index paramIndex) {
    if (paramObject == null)
      return; 
    if (!(paramObject instanceof Fixups)) {
      int i = paramIndex.indexOf((ConstantPool.Entry)paramObject);
      storeIndex(paramArrayOfByte, 0, 0, i);
      return;
    } 
    Fixups fixups = (Fixups)paramObject;
    assert fixups.bytes == paramArrayOfByte;
    fixups.finishRefs(paramIndex);
  }
  
  void finishRefs(ConstantPool.Index paramIndex) {
    if (isEmpty())
      return; 
    for (Fixup fixup : this) {
      int i = paramIndex.indexOf(fixup.entry);
      storeIndex(fixup.location(), fixup.format(), i);
    } 
    this.bytes = null;
    clear();
  }
  
  public static class Fixup extends Object implements Comparable<Fixup> {
    int desc;
    
    ConstantPool.Entry entry;
    
    Fixup(int param1Int, ConstantPool.Entry param1Entry) {
      this.desc = param1Int;
      this.entry = param1Entry;
    }
    
    public Fixup(int param1Int1, int param1Int2, ConstantPool.Entry param1Entry) {
      this.desc = Fixups.makeDesc(param1Int1, param1Int2);
      this.entry = param1Entry;
    }
    
    public int location() { return Fixups.descLoc(this.desc); }
    
    public int format() { return Fixups.descFmt(this.desc); }
    
    public ConstantPool.Entry entry() { return this.entry; }
    
    public int compareTo(Fixup param1Fixup) { return location() - param1Fixup.location(); }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Fixup))
        return false; 
      Fixup fixup = (Fixup)param1Object;
      return (this.desc == fixup.desc && this.entry == fixup.entry);
    }
    
    public int hashCode() {
      null = 7;
      null = 59 * null + this.desc;
      return 59 * null + Objects.hashCode(this.entry);
    }
    
    public String toString() { return "@" + location() + ((format() == 1) ? ".1" : "") + "=" + this.entry; }
  }
  
  private class Itr extends Object implements Iterator<Fixup> {
    int index = 0;
    
    int bigIndex = 1;
    
    int next = Fixups.this.head;
    
    private Itr() {}
    
    public boolean hasNext() { return (this.index < Fixups.this.size); }
    
    public void remove() { throw new UnsupportedOperationException(); }
    
    public Fixups.Fixup next() {
      int i = this.index;
      return new Fixups.Fixup(nextDesc(), Fixups.this.entries[i]);
    }
    
    int nextDesc() {
      this.index++;
      int i = this.next;
      if (this.index < Fixups.this.size) {
        int j = Fixups.descLoc(i);
        int k = Fixups.descFmt(i);
        if (Fixups.this.bytes != null && Fixups.this.bytes[j] != -1) {
          this.next = Fixups.this.fetchDesc(j, k);
        } else {
          assert k == 1 || Fixups.this.bytes == null || Fixups.this.bytes[j + 1] == (byte)this.bigIndex;
          this.next = Fixups.this.bigDescs[this.bigIndex++];
        } 
      } 
      return i;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Fixups.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */