package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectId;
import java.util.Arrays;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class ObjectIdImpl implements ObjectId {
  private byte[] id;
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ObjectIdImpl))
      return false; 
    ObjectIdImpl objectIdImpl = (ObjectIdImpl)paramObject;
    return Arrays.equals(this.id, objectIdImpl.id);
  }
  
  public int hashCode() {
    byte b = 17;
    for (byte b1 = 0; b1 < this.id.length; b1++)
      b = 37 * b + this.id[b1]; 
    return b;
  }
  
  public ObjectIdImpl(byte[] paramArrayOfByte) { this.id = paramArrayOfByte; }
  
  public byte[] getId() { return this.id; }
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_long(this.id.length);
    paramOutputStream.write_octet_array(this.id, 0, this.id.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectIdImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */