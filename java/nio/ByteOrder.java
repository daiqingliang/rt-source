package java.nio;

public final class ByteOrder {
  private String name;
  
  public static final ByteOrder BIG_ENDIAN = new ByteOrder("BIG_ENDIAN");
  
  public static final ByteOrder LITTLE_ENDIAN = new ByteOrder("LITTLE_ENDIAN");
  
  private ByteOrder(String paramString) { this.name = paramString; }
  
  public static ByteOrder nativeOrder() { return Bits.byteOrder(); }
  
  public String toString() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\ByteOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */