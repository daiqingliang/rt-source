package sun.misc;

public class CRC16 {
  public int value = 0;
  
  public void update(byte paramByte) {
    byte b = paramByte;
    for (byte b1 = 7; b1 >= 0; b1--) {
      b <<= 1;
      int i = b >>> 8 & true;
      if ((this.value & 0x8000) != 0) {
        this.value = (this.value << 1) + i ^ 0x1021;
      } else {
        this.value = (this.value << 1) + i;
      } 
    } 
    this.value &= 0xFFFF;
  }
  
  public void reset() { this.value = 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\CRC16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */