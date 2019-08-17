package com.sun.xml.internal.fastinfoset.algorithm;

public final class BuiltInEncodingAlgorithmFactory {
  private static final BuiltInEncodingAlgorithm[] table = new BuiltInEncodingAlgorithm[10];
  
  public static final HexadecimalEncodingAlgorithm hexadecimalEncodingAlgorithm = new HexadecimalEncodingAlgorithm();
  
  public static final BASE64EncodingAlgorithm base64EncodingAlgorithm = new BASE64EncodingAlgorithm();
  
  public static final BooleanEncodingAlgorithm booleanEncodingAlgorithm = new BooleanEncodingAlgorithm();
  
  public static final ShortEncodingAlgorithm shortEncodingAlgorithm = new ShortEncodingAlgorithm();
  
  public static final IntEncodingAlgorithm intEncodingAlgorithm = new IntEncodingAlgorithm();
  
  public static final LongEncodingAlgorithm longEncodingAlgorithm = new LongEncodingAlgorithm();
  
  public static final FloatEncodingAlgorithm floatEncodingAlgorithm = new FloatEncodingAlgorithm();
  
  public static final DoubleEncodingAlgorithm doubleEncodingAlgorithm = new DoubleEncodingAlgorithm();
  
  public static final UUIDEncodingAlgorithm uuidEncodingAlgorithm = new UUIDEncodingAlgorithm();
  
  public static BuiltInEncodingAlgorithm getAlgorithm(int paramInt) { return table[paramInt]; }
  
  static  {
    table[0] = hexadecimalEncodingAlgorithm;
    table[1] = base64EncodingAlgorithm;
    table[2] = shortEncodingAlgorithm;
    table[3] = intEncodingAlgorithm;
    table[4] = longEncodingAlgorithm;
    table[5] = booleanEncodingAlgorithm;
    table[6] = floatEncodingAlgorithm;
    table[7] = doubleEncodingAlgorithm;
    table[8] = uuidEncodingAlgorithm;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\BuiltInEncodingAlgorithmFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */