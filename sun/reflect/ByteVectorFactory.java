package sun.reflect;

class ByteVectorFactory {
  static ByteVector create() { return new ByteVectorImpl(); }
  
  static ByteVector create(int paramInt) { return new ByteVectorImpl(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\ByteVectorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */