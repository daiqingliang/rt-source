package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import java.util.Arrays;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class GenericIdentifiable implements Identifiable {
  private int id;
  
  private byte[] data;
  
  public GenericIdentifiable(int paramInt, InputStream paramInputStream) {
    this.id = paramInt;
    this.data = EncapsulationUtility.readOctets(paramInputStream);
  }
  
  public int getId() { return this.id; }
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_ulong(this.data.length);
    paramOutputStream.write_octet_array(this.data, 0, this.data.length);
  }
  
  public String toString() { return "GenericIdentifiable[id=" + getId() + "]"; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof GenericIdentifiable))
      return false; 
    GenericIdentifiable genericIdentifiable = (GenericIdentifiable)paramObject;
    return (getId() == genericIdentifiable.getId() && Arrays.equals(getData(), genericIdentifiable.getData()));
  }
  
  public int hashCode() {
    byte b = 17;
    for (byte b1 = 0; b1 < this.data.length; b1++)
      b = 37 * b + this.data[b1]; 
    return b;
  }
  
  public GenericIdentifiable(int paramInt, byte[] paramArrayOfByte) {
    this.id = paramInt;
    this.data = (byte[])paramArrayOfByte.clone();
  }
  
  public byte[] getData() { return this.data; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\GenericIdentifiable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */