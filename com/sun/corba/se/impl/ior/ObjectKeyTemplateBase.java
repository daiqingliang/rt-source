package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class ObjectKeyTemplateBase implements ObjectKeyTemplate {
  public static final String JIDL_ORB_ID = "";
  
  private static final String[] JIDL_OAID_STRINGS = { "TransientObjectAdapter" };
  
  public static final ObjectAdapterId JIDL_OAID = new ObjectAdapterIdArray(JIDL_OAID_STRINGS);
  
  private ORB orb;
  
  protected IORSystemException wrapper;
  
  private ORBVersion version;
  
  private int magic;
  
  private int scid;
  
  private int serverid;
  
  private String orbid;
  
  private ObjectAdapterId oaid;
  
  private byte[] adapterId;
  
  public byte[] getAdapterId() { return (byte[])this.adapterId.clone(); }
  
  private byte[] computeAdapterId() {
    ByteBuffer byteBuffer = new ByteBuffer();
    byteBuffer.append(getServerId());
    byteBuffer.append(this.orbid);
    byteBuffer.append(this.oaid.getNumLevels());
    for (String str : this.oaid)
      byteBuffer.append(str); 
    byteBuffer.trimToSize();
    return byteBuffer.toArray();
  }
  
  public ObjectKeyTemplateBase(ORB paramORB, int paramInt1, int paramInt2, int paramInt3, String paramString, ObjectAdapterId paramObjectAdapterId) {
    this.orb = paramORB;
    this.wrapper = IORSystemException.get(paramORB, "oa.ior");
    this.magic = paramInt1;
    this.scid = paramInt2;
    this.serverid = paramInt3;
    this.orbid = paramString;
    this.oaid = paramObjectAdapterId;
    this.adapterId = computeAdapterId();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ObjectKeyTemplateBase))
      return false; 
    ObjectKeyTemplateBase objectKeyTemplateBase = (ObjectKeyTemplateBase)paramObject;
    return (this.magic == objectKeyTemplateBase.magic && this.scid == objectKeyTemplateBase.scid && this.serverid == objectKeyTemplateBase.serverid && this.version.equals(objectKeyTemplateBase.version) && this.orbid.equals(objectKeyTemplateBase.orbid) && this.oaid.equals(objectKeyTemplateBase.oaid));
  }
  
  public int hashCode() {
    null = 17;
    null = 37 * null + this.magic;
    null = 37 * null + this.scid;
    null = 37 * null + this.serverid;
    null = 37 * null + this.version.hashCode();
    null = 37 * null + this.orbid.hashCode();
    return 37 * null + this.oaid.hashCode();
  }
  
  public int getSubcontractId() { return this.scid; }
  
  public int getServerId() { return this.serverid; }
  
  public String getORBId() { return this.orbid; }
  
  public ObjectAdapterId getObjectAdapterId() { return this.oaid; }
  
  public void write(ObjectId paramObjectId, OutputStream paramOutputStream) {
    writeTemplate(paramOutputStream);
    paramObjectId.write(paramOutputStream);
  }
  
  public void write(OutputStream paramOutputStream) { writeTemplate(paramOutputStream); }
  
  protected abstract void writeTemplate(OutputStream paramOutputStream);
  
  protected int getMagic() { return this.magic; }
  
  public void setORBVersion(ORBVersion paramORBVersion) { this.version = paramORBVersion; }
  
  public ORBVersion getORBVersion() { return this.version; }
  
  protected byte[] readObjectKey(InputStream paramInputStream) {
    int i = paramInputStream.read_long();
    byte[] arrayOfByte = new byte[i];
    paramInputStream.read_octet_array(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(ORB paramORB, ObjectId paramObjectId) { return paramORB.getRequestDispatcherRegistry().getServerRequestDispatcher(this.scid); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectKeyTemplateBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */