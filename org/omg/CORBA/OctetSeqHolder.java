package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class OctetSeqHolder implements Streamable {
  public byte[] value = null;
  
  public OctetSeqHolder() {}
  
  public OctetSeqHolder(byte[] paramArrayOfByte) { this.value = paramArrayOfByte; }
  
  public void _read(InputStream paramInputStream) { this.value = OctetSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { OctetSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return OctetSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\OctetSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */