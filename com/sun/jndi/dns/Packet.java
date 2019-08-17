package com.sun.jndi.dns;

class Packet {
  byte[] buf;
  
  Packet(int paramInt) { this.buf = new byte[paramInt]; }
  
  Packet(byte[] paramArrayOfByte, int paramInt) {
    this.buf = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, this.buf, 0, paramInt);
  }
  
  void putInt(int paramInt1, int paramInt2) {
    this.buf[paramInt2 + 0] = (byte)(paramInt1 >> 24);
    this.buf[paramInt2 + 1] = (byte)(paramInt1 >> 16);
    this.buf[paramInt2 + 2] = (byte)(paramInt1 >> 8);
    this.buf[paramInt2 + 3] = (byte)paramInt1;
  }
  
  void putShort(int paramInt1, int paramInt2) {
    this.buf[paramInt2 + 0] = (byte)(paramInt1 >> 8);
    this.buf[paramInt2 + 1] = (byte)paramInt1;
  }
  
  void putByte(int paramInt1, int paramInt2) { this.buf[paramInt2] = (byte)paramInt1; }
  
  void putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) { System.arraycopy(paramArrayOfByte, paramInt1, this.buf, paramInt2, paramInt3); }
  
  int length() { return this.buf.length; }
  
  byte[] getData() { return this.buf; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\Packet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */