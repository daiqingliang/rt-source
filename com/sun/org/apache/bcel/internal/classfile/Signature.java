package com.sun.org.apache.bcel.internal.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Signature extends Attribute {
  private int signature_index;
  
  public Signature(Signature paramSignature) { this(paramSignature.getNameIndex(), paramSignature.getLength(), paramSignature.getSignatureIndex(), paramSignature.getConstantPool()); }
  
  Signature(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException { this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramConstantPool); }
  
  public Signature(int paramInt1, int paramInt2, int paramInt3, ConstantPool paramConstantPool) {
    super((byte)10, paramInt1, paramInt2, paramConstantPool);
    this.signature_index = paramInt3;
  }
  
  public void accept(Visitor paramVisitor) {
    System.err.println("Visiting non-standard Signature object");
    paramVisitor.visitSignature(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream) throws IOException {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.signature_index);
  }
  
  public final int getSignatureIndex() { return this.signature_index; }
  
  public final void setSignatureIndex(int paramInt) { this.signature_index = paramInt; }
  
  public final String getSignature() {
    ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
    return constantUtf8.getBytes();
  }
  
  private static boolean identStart(int paramInt) { return (paramInt == 84 || paramInt == 76); }
  
  private static boolean identPart(int paramInt) { return (paramInt == 47 || paramInt == 59); }
  
  private static final void matchIdent(MyByteArrayInputStream paramMyByteArrayInputStream, StringBuffer paramStringBuffer) {
    int i;
    if ((i = paramMyByteArrayInputStream.read()) == -1)
      throw new RuntimeException("Illegal signature: " + paramMyByteArrayInputStream.getData() + " no ident, reaching EOF"); 
    if (!identStart(i)) {
      StringBuffer stringBuffer1 = new StringBuffer();
      byte b = 1;
      while (Character.isJavaIdentifierPart((char)i)) {
        stringBuffer1.append((char)i);
        b++;
        i = paramMyByteArrayInputStream.read();
      } 
      if (i == 58) {
        paramMyByteArrayInputStream.skip("Ljava/lang/Object".length());
        paramStringBuffer.append(stringBuffer1);
        i = paramMyByteArrayInputStream.read();
        paramMyByteArrayInputStream.unread();
      } else {
        for (byte b1 = 0; b1 < b; b1++)
          paramMyByteArrayInputStream.unread(); 
      } 
      return;
    } 
    StringBuffer stringBuffer = new StringBuffer();
    i = paramMyByteArrayInputStream.read();
    do {
      stringBuffer.append((char)i);
      i = paramMyByteArrayInputStream.read();
    } while (i != -1 && (Character.isJavaIdentifierPart((char)i) || i == 47));
    paramStringBuffer.append(stringBuffer.toString().replace('/', '.'));
    if (i != -1)
      paramMyByteArrayInputStream.unread(); 
  }
  
  private static final void matchGJIdent(MyByteArrayInputStream paramMyByteArrayInputStream, StringBuffer paramStringBuffer) {
    matchIdent(paramMyByteArrayInputStream, paramStringBuffer);
    int i = paramMyByteArrayInputStream.read();
    if (i == 60 || i == 40) {
      paramStringBuffer.append((char)i);
      matchGJIdent(paramMyByteArrayInputStream, paramStringBuffer);
      while ((i = paramMyByteArrayInputStream.read()) != 62 && i != 41) {
        if (i == -1)
          throw new RuntimeException("Illegal signature: " + paramMyByteArrayInputStream.getData() + " reaching EOF"); 
        paramStringBuffer.append(", ");
        paramMyByteArrayInputStream.unread();
        matchGJIdent(paramMyByteArrayInputStream, paramStringBuffer);
      } 
      paramStringBuffer.append((char)i);
    } else {
      paramMyByteArrayInputStream.unread();
    } 
    i = paramMyByteArrayInputStream.read();
    if (identStart(i)) {
      paramMyByteArrayInputStream.unread();
      matchGJIdent(paramMyByteArrayInputStream, paramStringBuffer);
    } else {
      if (i == 41) {
        paramMyByteArrayInputStream.unread();
        return;
      } 
      if (i != 59)
        throw new RuntimeException("Illegal signature: " + paramMyByteArrayInputStream.getData() + " read " + (char)i); 
    } 
  }
  
  public static String translate(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    matchGJIdent(new MyByteArrayInputStream(paramString), stringBuffer);
    return stringBuffer.toString();
  }
  
  public static final boolean isFormalParameterList(String paramString) { return (paramString.startsWith("<") && paramString.indexOf(':') > 0); }
  
  public static final boolean isActualParameterList(String paramString) { return (paramString.startsWith("L") && paramString.endsWith(">;")); }
  
  public final String toString() {
    String str = getSignature();
    return "Signature(" + str + ")";
  }
  
  public Attribute copy(ConstantPool paramConstantPool) { return (Signature)clone(); }
  
  private static final class MyByteArrayInputStream extends ByteArrayInputStream {
    MyByteArrayInputStream(String param1String) { super(param1String.getBytes()); }
    
    final int mark() { return this.pos; }
    
    final String getData() { return new String(this.buf); }
    
    final void reset(int param1Int) { this.pos = param1Int; }
    
    final void unread() {
      if (this.pos > 0)
        this.pos--; 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Signature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */