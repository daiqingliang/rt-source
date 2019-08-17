package java.security.spec;

public abstract class EncodedKeySpec implements KeySpec {
  private byte[] encodedKey;
  
  public EncodedKeySpec(byte[] paramArrayOfByte) { this.encodedKey = (byte[])paramArrayOfByte.clone(); }
  
  public byte[] getEncoded() { return (byte[])this.encodedKey.clone(); }
  
  public abstract String getFormat();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\EncodedKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */