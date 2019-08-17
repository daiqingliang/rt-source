package jdk.internal.org.objectweb.asm;

public class Attribute {
  public final String type;
  
  byte[] value;
  
  Attribute next;
  
  protected Attribute(String paramString) { this.type = paramString; }
  
  public boolean isUnknown() { return true; }
  
  public boolean isCodeAttribute() { return false; }
  
  protected Label[] getLabels() { return null; }
  
  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel) {
    Attribute attribute = new Attribute(this.type);
    attribute.value = new byte[paramInt2];
    System.arraycopy(paramClassReader.b, paramInt1, attribute.value, 0, paramInt2);
    return attribute;
  }
  
  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) {
    ByteVector byteVector = new ByteVector();
    byteVector.data = this.value;
    byteVector.length = this.value.length;
    return byteVector;
  }
  
  final int getCount() {
    byte b = 0;
    for (Attribute attribute = this; attribute != null; attribute = attribute.next)
      b++; 
    return b;
  }
  
  final int getSize(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) {
    Attribute attribute = this;
    int i = 0;
    while (attribute != null) {
      paramClassWriter.newUTF8(attribute.type);
      i += (attribute.write(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3)).length + 6;
      attribute = attribute.next;
    } 
    return i;
  }
  
  final void put(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, ByteVector paramByteVector) {
    for (Attribute attribute = this; attribute != null; attribute = attribute.next) {
      ByteVector byteVector = attribute.write(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
      paramByteVector.putShort(paramClassWriter.newUTF8(attribute.type)).putInt(byteVector.length);
      paramByteVector.putByteArray(byteVector.data, 0, byteVector.length);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */