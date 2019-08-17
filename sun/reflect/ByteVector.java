package sun.reflect;

interface ByteVector {
  int getLength();
  
  byte get(int paramInt);
  
  void put(int paramInt, byte paramByte);
  
  void add(byte paramByte);
  
  void trim();
  
  byte[] getData();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\ByteVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */