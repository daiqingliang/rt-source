package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.activation.POANameHelper;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class POAObjectKeyTemplate extends NewObjectKeyTemplateBase {
  public POAObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream) {
    super(paramORB, paramInt1, paramInt2, paramInputStream.read_long(), paramInputStream.read_string(), new ObjectAdapterIdArray(POANameHelper.read(paramInputStream)));
    setORBVersion(paramInputStream);
  }
  
  public POAObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder) {
    super(paramORB, paramInt1, paramInt2, paramInputStream.read_long(), paramInputStream.read_string(), new ObjectAdapterIdArray(POANameHelper.read(paramInputStream)));
    paramOctetSeqHolder.value = readObjectKey(paramInputStream);
    setORBVersion(paramInputStream);
  }
  
  public POAObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, String paramString, ObjectAdapterId paramObjectAdapterId) {
    super(paramORB, -1347695872, paramInt1, paramInt2, paramString, paramObjectAdapterId);
    setORBVersion(ORBVersionFactory.getORBVersion());
  }
  
  public void writeTemplate(OutputStream paramOutputStream) {
    paramOutputStream.write_long(getMagic());
    paramOutputStream.write_long(getSubcontractId());
    paramOutputStream.write_long(getServerId());
    paramOutputStream.write_string(getORBId());
    getObjectAdapterId().write(paramOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\POAObjectKeyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */