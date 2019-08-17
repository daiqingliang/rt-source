package java.util.zip;

public interface Checksum {
  void update(int paramInt);
  
  void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  long getValue();
  
  void reset();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\Checksum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */