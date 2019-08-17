package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public final class TypeCodeOutputStream extends EncapsOutputStream {
  private OutputStream enclosure = null;
  
  private Map typeMap = null;
  
  private boolean isEncapsulation = false;
  
  public TypeCodeOutputStream(ORB paramORB) { super(paramORB, false); }
  
  public TypeCodeOutputStream(ORB paramORB, boolean paramBoolean) { super(paramORB, paramBoolean); }
  
  public InputStream create_input_stream() { return EncapsInputStreamFactory.newTypeCodeInputStream((ORB)orb(), getByteBuffer(), getIndex(), isLittleEndian(), getGIOPVersion()); }
  
  public void setEnclosingOutputStream(OutputStream paramOutputStream) { this.enclosure = paramOutputStream; }
  
  public TypeCodeOutputStream getTopLevelStream() { return (this.enclosure == null) ? this : ((this.enclosure instanceof TypeCodeOutputStream) ? ((TypeCodeOutputStream)this.enclosure).getTopLevelStream() : this); }
  
  public int getTopLevelPosition() {
    if (this.enclosure != null && this.enclosure instanceof TypeCodeOutputStream) {
      int i = ((TypeCodeOutputStream)this.enclosure).getTopLevelPosition() + getPosition();
      if (this.isEncapsulation)
        i += 4; 
      return i;
    } 
    return getPosition();
  }
  
  public void addIDAtPosition(String paramString, int paramInt) {
    if (this.typeMap == null)
      this.typeMap = new HashMap(16); 
    this.typeMap.put(paramString, new Integer(paramInt));
  }
  
  public int getPositionForID(String paramString) {
    if (this.typeMap == null)
      throw this.wrapper.refTypeIndirType(CompletionStatus.COMPLETED_NO); 
    return ((Integer)this.typeMap.get(paramString)).intValue();
  }
  
  public void writeRawBuffer(OutputStream paramOutputStream, int paramInt) {
    paramOutputStream.write_long(paramInt);
    ByteBuffer byteBuffer = getByteBuffer();
    if (byteBuffer.hasArray()) {
      paramOutputStream.write_octet_array(byteBuffer.array(), 4, getIndex() - 4);
    } else {
      byte[] arrayOfByte = new byte[byteBuffer.limit()];
      for (byte b = 0; b < arrayOfByte.length; b++)
        arrayOfByte[b] = byteBuffer.get(b); 
      paramOutputStream.write_octet_array(arrayOfByte, 4, getIndex() - 4);
    } 
  }
  
  public TypeCodeOutputStream createEncapsulation(ORB paramORB) {
    TypeCodeOutputStream typeCodeOutputStream = OutputStreamFactory.newTypeCodeOutputStream((ORB)paramORB, isLittleEndian());
    typeCodeOutputStream.setEnclosingOutputStream(this);
    typeCodeOutputStream.makeEncapsulation();
    return typeCodeOutputStream;
  }
  
  protected void makeEncapsulation() {
    putEndian();
    this.isEncapsulation = true;
  }
  
  public static TypeCodeOutputStream wrapOutputStream(OutputStream paramOutputStream) {
    boolean bool = (paramOutputStream instanceof CDROutputStream) ? ((CDROutputStream)paramOutputStream).isLittleEndian() : 0;
    TypeCodeOutputStream typeCodeOutputStream = OutputStreamFactory.newTypeCodeOutputStream((ORB)paramOutputStream.orb(), bool);
    typeCodeOutputStream.setEnclosingOutputStream(paramOutputStream);
    return typeCodeOutputStream;
  }
  
  public int getPosition() { return getIndex(); }
  
  public int getRealIndex(int paramInt) { return getTopLevelPosition(); }
  
  public byte[] getTypeCodeBuffer() {
    ByteBuffer byteBuffer = getByteBuffer();
    byte[] arrayOfByte = new byte[getIndex() - 4];
    for (byte b = 0; b < arrayOfByte.length; b++)
      arrayOfByte[b] = byteBuffer.get(b + 4); 
    return arrayOfByte;
  }
  
  public void printTypeMap() {
    System.out.println("typeMap = {");
    for (String str : this.typeMap.keySet()) {
      Integer integer = (Integer)this.typeMap.get(str);
      System.out.println("  key = " + str + ", value = " + integer);
    } 
    System.out.println("}");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\TypeCodeOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */