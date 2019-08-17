package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

public final class ATR implements Serializable {
  private static final long serialVersionUID = 6695383790847736493L;
  
  private byte[] atr;
  
  private int startHistorical;
  
  private int nHistorical;
  
  public ATR(byte[] paramArrayOfByte) {
    this.atr = (byte[])paramArrayOfByte.clone();
    parse();
  }
  
  private void parse() {
    if (this.atr.length < 2)
      return; 
    if (this.atr[0] != 59 && this.atr[0] != 63)
      return; 
    byte b1 = (this.atr[1] & 0xF0) >> 4;
    byte b2 = this.atr[1] & 0xF;
    byte b = 2;
    while (b1 != 0 && b < this.atr.length) {
      if ((b1 & true) != 0)
        b++; 
      if ((b1 & 0x2) != 0)
        b++; 
      if ((b1 & 0x4) != 0)
        b++; 
      if ((b1 & 0x8) != 0) {
        if (b >= this.atr.length)
          return; 
        b1 = (this.atr[b++] & 0xF0) >> 4;
        continue;
      } 
      b1 = 0;
    } 
    byte b3 = b + b2;
    if (b3 == this.atr.length || b3 == this.atr.length - 1) {
      this.startHistorical = b;
      this.nHistorical = b2;
    } 
  }
  
  public byte[] getBytes() { return (byte[])this.atr.clone(); }
  
  public byte[] getHistoricalBytes() {
    byte[] arrayOfByte = new byte[this.nHistorical];
    System.arraycopy(this.atr, this.startHistorical, arrayOfByte, 0, this.nHistorical);
    return arrayOfByte;
  }
  
  public String toString() { return "ATR: " + this.atr.length + " bytes"; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ATR))
      return false; 
    ATR aTR = (ATR)paramObject;
    return Arrays.equals(this.atr, aTR.atr);
  }
  
  public int hashCode() { return Arrays.hashCode(this.atr); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.atr = (byte[])paramObjectInputStream.readUnshared();
    parse();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\ATR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */