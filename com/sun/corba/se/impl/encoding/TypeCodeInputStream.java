package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class TypeCodeInputStream extends EncapsInputStream implements TypeCodeReader {
  private Map typeMap = null;
  
  private InputStream enclosure = null;
  
  private boolean isEncapsulation = false;
  
  public TypeCodeInputStream(ORB paramORB, byte[] paramArrayOfByte, int paramInt) { super(paramORB, paramArrayOfByte, paramInt); }
  
  public TypeCodeInputStream(ORB paramORB, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion) { super(paramORB, paramArrayOfByte, paramInt, paramBoolean, paramGIOPVersion); }
  
  public TypeCodeInputStream(ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion) { super(paramORB, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion); }
  
  public void addTypeCodeAtPosition(TypeCodeImpl paramTypeCodeImpl, int paramInt) {
    if (this.typeMap == null)
      this.typeMap = new HashMap(16); 
    this.typeMap.put(new Integer(paramInt), paramTypeCodeImpl);
  }
  
  public TypeCodeImpl getTypeCodeAtPosition(int paramInt) { return (this.typeMap == null) ? null : (TypeCodeImpl)this.typeMap.get(new Integer(paramInt)); }
  
  public void setEnclosingInputStream(InputStream paramInputStream) { this.enclosure = paramInputStream; }
  
  public TypeCodeReader getTopLevelStream() { return (this.enclosure == null) ? this : ((this.enclosure instanceof TypeCodeReader) ? ((TypeCodeReader)this.enclosure).getTopLevelStream() : this); }
  
  public int getTopLevelPosition() {
    if (this.enclosure != null && this.enclosure instanceof TypeCodeReader) {
      int i = ((TypeCodeReader)this.enclosure).getTopLevelPosition();
      return i - getBufferLength() + getPosition();
    } 
    return getPosition();
  }
  
  public static TypeCodeInputStream readEncapsulation(InputStream paramInputStream, ORB paramORB) {
    TypeCodeInputStream typeCodeInputStream;
    int i = paramInputStream.read_long();
    byte[] arrayOfByte = new byte[i];
    paramInputStream.read_octet_array(arrayOfByte, 0, arrayOfByte.length);
    if (paramInputStream instanceof CDRInputStream) {
      typeCodeInputStream = EncapsInputStreamFactory.newTypeCodeInputStream((ORB)paramORB, arrayOfByte, arrayOfByte.length, ((CDRInputStream)paramInputStream).isLittleEndian(), ((CDRInputStream)paramInputStream).getGIOPVersion());
    } else {
      typeCodeInputStream = EncapsInputStreamFactory.newTypeCodeInputStream((ORB)paramORB, arrayOfByte, arrayOfByte.length);
    } 
    typeCodeInputStream.setEnclosingInputStream(paramInputStream);
    typeCodeInputStream.makeEncapsulation();
    return typeCodeInputStream;
  }
  
  protected void makeEncapsulation() {
    consumeEndian();
    this.isEncapsulation = true;
  }
  
  public void printTypeMap() {
    System.out.println("typeMap = {");
    for (Integer integer : this.typeMap.keySet()) {
      TypeCodeImpl typeCodeImpl = (TypeCodeImpl)this.typeMap.get(integer);
      System.out.println("  key = " + integer.intValue() + ", value = " + typeCodeImpl.description());
    } 
    System.out.println("}");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\TypeCodeInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */