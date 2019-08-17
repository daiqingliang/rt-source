package sun.nio.ch;

import java.nio.ByteOrder;
import sun.misc.Unsafe;

class NativeObject {
  protected static final Unsafe unsafe = Unsafe.getUnsafe();
  
  protected long allocationAddress;
  
  private final long address;
  
  private static ByteOrder byteOrder = null;
  
  private static int pageSize = -1;
  
  NativeObject(long paramLong) {
    this.allocationAddress = paramLong;
    this.address = paramLong;
  }
  
  NativeObject(long paramLong1, long paramLong2) {
    this.allocationAddress = paramLong1;
    this.address = paramLong1 + paramLong2;
  }
  
  protected NativeObject(int paramInt, boolean paramBoolean) {
    if (!paramBoolean) {
      this.allocationAddress = unsafe.allocateMemory(paramInt);
      this.address = this.allocationAddress;
    } else {
      int i = pageSize();
      long l = unsafe.allocateMemory((paramInt + i));
      this.allocationAddress = l;
      this.address = l + i - (l & (i - 1));
    } 
  }
  
  long address() { return this.address; }
  
  long allocationAddress() { return this.allocationAddress; }
  
  NativeObject subObject(int paramInt) { return new NativeObject(paramInt + this.address); }
  
  NativeObject getObject(int paramInt) {
    long l = 0L;
    switch (addressSize()) {
      case 8:
        l = unsafe.getLong(paramInt + this.address);
        return new NativeObject(l);
      case 4:
        l = (unsafe.getInt(paramInt + this.address) & 0xFFFFFFFF);
        return new NativeObject(l);
    } 
    throw new InternalError("Address size not supported");
  }
  
  void putObject(int paramInt, NativeObject paramNativeObject) {
    switch (addressSize()) {
      case 8:
        putLong(paramInt, paramNativeObject.address);
        return;
      case 4:
        putInt(paramInt, (int)(paramNativeObject.address & 0xFFFFFFFFFFFFFFFFL));
        return;
    } 
    throw new InternalError("Address size not supported");
  }
  
  final byte getByte(int paramInt) { return unsafe.getByte(paramInt + this.address); }
  
  final void putByte(int paramInt, byte paramByte) { unsafe.putByte(paramInt + this.address, paramByte); }
  
  final short getShort(int paramInt) { return unsafe.getShort(paramInt + this.address); }
  
  final void putShort(int paramInt, short paramShort) { unsafe.putShort(paramInt + this.address, paramShort); }
  
  final char getChar(int paramInt) { return unsafe.getChar(paramInt + this.address); }
  
  final void putChar(int paramInt, char paramChar) { unsafe.putChar(paramInt + this.address, paramChar); }
  
  final int getInt(int paramInt) { return unsafe.getInt(paramInt + this.address); }
  
  final void putInt(int paramInt1, int paramInt2) { unsafe.putInt(paramInt1 + this.address, paramInt2); }
  
  final long getLong(int paramInt) { return unsafe.getLong(paramInt + this.address); }
  
  final void putLong(int paramInt, long paramLong) { unsafe.putLong(paramInt + this.address, paramLong); }
  
  final float getFloat(int paramInt) { return unsafe.getFloat(paramInt + this.address); }
  
  final void putFloat(int paramInt, float paramFloat) { unsafe.putFloat(paramInt + this.address, paramFloat); }
  
  final double getDouble(int paramInt) { return unsafe.getDouble(paramInt + this.address); }
  
  final void putDouble(int paramInt, double paramDouble) { unsafe.putDouble(paramInt + this.address, paramDouble); }
  
  static int addressSize() { return unsafe.addressSize(); }
  
  static ByteOrder byteOrder() {
    if (byteOrder != null)
      return byteOrder; 
    l = unsafe.allocateMemory(8L);
    try {
      unsafe.putLong(l, 72623859790382856L);
      byte b = unsafe.getByte(l);
      switch (b) {
        case 1:
          byteOrder = ByteOrder.BIG_ENDIAN;
          break;
        case 8:
          byteOrder = ByteOrder.LITTLE_ENDIAN;
          break;
        default:
          assert false;
          break;
      } 
    } finally {
      unsafe.freeMemory(l);
    } 
    return byteOrder;
  }
  
  static int pageSize() {
    if (pageSize == -1)
      pageSize = unsafe.pageSize(); 
    return pageSize;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\NativeObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */