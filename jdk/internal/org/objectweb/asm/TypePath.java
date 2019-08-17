package jdk.internal.org.objectweb.asm;

public class TypePath {
  public static final int ARRAY_ELEMENT = 0;
  
  public static final int INNER_TYPE = 1;
  
  public static final int WILDCARD_BOUND = 2;
  
  public static final int TYPE_ARGUMENT = 3;
  
  byte[] b;
  
  int offset;
  
  TypePath(byte[] paramArrayOfByte, int paramInt) {
    this.b = paramArrayOfByte;
    this.offset = paramInt;
  }
  
  public int getLength() { return this.b[this.offset]; }
  
  public int getStep(int paramInt) { return this.b[this.offset + 2 * paramInt + 1]; }
  
  public int getStepArgument(int paramInt) { return this.b[this.offset + 2 * paramInt + 2]; }
  
  public static TypePath fromString(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return null; 
    int i = paramString.length();
    ByteVector byteVector = new ByteVector(i);
    byteVector.putByte(0);
    byte b1 = 0;
    while (b1 < i) {
      char c = paramString.charAt(b1++);
      if (c == '[') {
        byteVector.put11(0, 0);
        continue;
      } 
      if (c == '.') {
        byteVector.put11(1, 0);
        continue;
      } 
      if (c == '*') {
        byteVector.put11(2, 0);
        continue;
      } 
      if (c >= '0' && c <= '9') {
        char c1 = c - '0';
        while (b1 < i && (c = paramString.charAt(b1)) >= '0' && c <= '9') {
          c1 = c1 * '\n' + c - '0';
          b1++;
        } 
        if (b1 < i && paramString.charAt(b1) == ';')
          b1++; 
        byteVector.put11(3, c1);
      } 
    } 
    byteVector.data[0] = (byte)(byteVector.length / 2);
    return new TypePath(byteVector.data, 0);
  }
  
  public String toString() {
    int i = getLength();
    StringBuilder stringBuilder = new StringBuilder(i * 2);
    for (byte b1 = 0; b1 < i; b1++) {
      switch (getStep(b1)) {
        case 0:
          stringBuilder.append('[');
          break;
        case 1:
          stringBuilder.append('.');
          break;
        case 2:
          stringBuilder.append('*');
          break;
        case 3:
          stringBuilder.append(getStepArgument(b1)).append(';');
          break;
        default:
          stringBuilder.append('_');
          break;
      } 
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\TypePath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */