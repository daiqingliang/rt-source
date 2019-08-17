package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class OldJIDLObjectKeyTemplate extends OldObjectKeyTemplateBase {
  public static final byte NULL_PATCH_VERSION = 0;
  
  byte patchVersion = 0;
  
  public OldJIDLObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder) {
    this(paramORB, paramInt1, paramInt2, paramInputStream);
    paramOctetSeqHolder.value = readObjectKey(paramInputStream);
    if (paramInt1 == -1347695873 && paramOctetSeqHolder.value.length > ((CDRInputStream)paramInputStream).getPosition()) {
      this.patchVersion = paramInputStream.read_octet();
      if (this.patchVersion == 1) {
        setORBVersion(ORBVersionFactory.getJDK1_3_1_01());
      } else if (this.patchVersion > 1) {
        setORBVersion(ORBVersionFactory.getORBVersion());
      } else {
        throw this.wrapper.invalidJdk131PatchLevel(new Integer(this.patchVersion));
      } 
    } 
  }
  
  public OldJIDLObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, int paramInt3) { super(paramORB, paramInt1, paramInt2, paramInt3, "", JIDL_OAID); }
  
  public OldJIDLObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream) { this(paramORB, paramInt1, paramInt2, paramInputStream.read_long()); }
  
  protected void writeTemplate(OutputStream paramOutputStream) {
    paramOutputStream.write_long(getMagic());
    paramOutputStream.write_long(getSubcontractId());
    paramOutputStream.write_long(getServerId());
  }
  
  public void write(ObjectId paramObjectId, OutputStream paramOutputStream) {
    super.write(paramObjectId, paramOutputStream);
    if (this.patchVersion != 0)
      paramOutputStream.write_octet(this.patchVersion); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\OldJIDLObjectKeyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */