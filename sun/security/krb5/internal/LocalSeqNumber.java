package sun.security.krb5.internal;

import sun.security.krb5.Confounder;

public class LocalSeqNumber implements SeqNumber {
  private int lastSeqNumber;
  
  public LocalSeqNumber() { randInit(); }
  
  public LocalSeqNumber(int paramInt) { init(paramInt); }
  
  public LocalSeqNumber(Integer paramInteger) { init(paramInteger.intValue()); }
  
  public void randInit() {
    byte[] arrayOfByte = Confounder.bytes(4);
    arrayOfByte[0] = (byte)(arrayOfByte[0] & 0x3F);
    byte b = arrayOfByte[3] & 0xFF | (arrayOfByte[2] & 0xFF) << 8 | (arrayOfByte[1] & 0xFF) << 16 | (arrayOfByte[0] & 0xFF) << 24;
    if (b == 0)
      b = 1; 
    this.lastSeqNumber = b;
  }
  
  public void init(int paramInt) { this.lastSeqNumber = paramInt; }
  
  public int current() { return this.lastSeqNumber; }
  
  public int next() { return this.lastSeqNumber + 1; }
  
  public int step() { return ++this.lastSeqNumber; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\LocalSeqNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */