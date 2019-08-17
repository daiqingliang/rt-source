package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class OldPOAObjectKeyTemplate extends OldObjectKeyTemplateBase {
  public OldPOAObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream) { this(paramORB, paramInt1, paramInt2, paramInputStream.read_long(), paramInputStream.read_long(), paramInputStream.read_long()); }
  
  public OldPOAObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder) {
    this(paramORB, paramInt1, paramInt2, paramInputStream);
    paramOctetSeqHolder.value = readObjectKey(paramInputStream);
  }
  
  public OldPOAObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramORB, paramInt1, paramInt2, paramInt3, Integer.toString(paramInt4), new ObjectAdapterIdNumber(paramInt5)); }
  
  public void writeTemplate(OutputStream paramOutputStream) {
    paramOutputStream.write_long(getMagic());
    paramOutputStream.write_long(getSubcontractId());
    paramOutputStream.write_long(getServerId());
    int i = Integer.parseInt(getORBId());
    paramOutputStream.write_long(i);
    ObjectAdapterIdNumber objectAdapterIdNumber = (ObjectAdapterIdNumber)getObjectAdapterId();
    int j = objectAdapterIdNumber.getOldPOAId();
    paramOutputStream.write_long(j);
  }
  
  public ORBVersion getORBVersion() {
    if (getMagic() == -1347695874)
      return ORBVersionFactory.getOLD(); 
    if (getMagic() == -1347695873)
      return ORBVersionFactory.getNEW(); 
    throw new INTERNAL();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\OldPOAObjectKeyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */