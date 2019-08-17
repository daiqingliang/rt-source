package javax.naming;

public class BinaryRefAddr extends RefAddr {
  private byte[] buf = null;
  
  private static final long serialVersionUID = -3415254970957330361L;
  
  public BinaryRefAddr(String paramString, byte[] paramArrayOfByte) { this(paramString, paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public BinaryRefAddr(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    super(paramString);
    this.buf = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, 0, paramInt2);
  }
  
  public Object getContent() { return this.buf; }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof BinaryRefAddr) {
      BinaryRefAddr binaryRefAddr = (BinaryRefAddr)paramObject;
      if (this.addrType.compareTo(binaryRefAddr.addrType) == 0) {
        if (this.buf == null && binaryRefAddr.buf == null)
          return true; 
        if (this.buf == null || binaryRefAddr.buf == null || this.buf.length != binaryRefAddr.buf.length)
          return false; 
        for (byte b = 0; b < this.buf.length; b++) {
          if (this.buf[b] != binaryRefAddr.buf[b])
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public int hashCode() {
    int i = this.addrType.hashCode();
    for (byte b = 0; b < this.buf.length; b++)
      i += this.buf[b]; 
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("Address Type: " + this.addrType + "\n");
    stringBuffer.append("AddressContents: ");
    for (byte b = 0; b < this.buf.length && b < 32; b++)
      stringBuffer.append(Integer.toHexString(this.buf[b]) + " "); 
    if (this.buf.length >= 32)
      stringBuffer.append(" ...\n"); 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\BinaryRefAddr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */