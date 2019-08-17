package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplateBase;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public class IIOPProfileTemplateImpl extends TaggedProfileTemplateBase implements IIOPProfileTemplate {
  private ORB orb;
  
  private GIOPVersion giopVersion;
  
  private IIOPAddress primary;
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof IIOPProfileTemplateImpl))
      return false; 
    IIOPProfileTemplateImpl iIOPProfileTemplateImpl = (IIOPProfileTemplateImpl)paramObject;
    return (super.equals(paramObject) && this.giopVersion.equals(iIOPProfileTemplateImpl.giopVersion) && this.primary.equals(iIOPProfileTemplateImpl.primary));
  }
  
  public int hashCode() { return super.hashCode() ^ this.giopVersion.hashCode() ^ this.primary.hashCode(); }
  
  public TaggedProfile create(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId) { return IIOPFactories.makeIIOPProfile(this.orb, paramObjectKeyTemplate, paramObjectId, this); }
  
  public GIOPVersion getGIOPVersion() { return this.giopVersion; }
  
  public IIOPAddress getPrimaryAddress() { return this.primary; }
  
  public IIOPProfileTemplateImpl(ORB paramORB, GIOPVersion paramGIOPVersion, IIOPAddress paramIIOPAddress) {
    this.orb = paramORB;
    this.giopVersion = paramGIOPVersion;
    this.primary = paramIIOPAddress;
    if (this.giopVersion.getMinor() == 0)
      makeImmutable(); 
  }
  
  public IIOPProfileTemplateImpl(InputStream paramInputStream) {
    byte b1 = paramInputStream.read_octet();
    byte b2 = paramInputStream.read_octet();
    this.giopVersion = GIOPVersion.getInstance(b1, b2);
    this.primary = new IIOPAddressImpl(paramInputStream);
    this.orb = (ORB)paramInputStream.orb();
    if (b2 > 0)
      EncapsulationUtility.readIdentifiableSequence(this, this.orb.getTaggedComponentFactoryFinder(), paramInputStream); 
    makeImmutable();
  }
  
  public void write(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, OutputStream paramOutputStream) {
    this.giopVersion.write(paramOutputStream);
    this.primary.write(paramOutputStream);
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramOutputStream.orb(), ((CDROutputStream)paramOutputStream).isLittleEndian());
    paramObjectKeyTemplate.write(paramObjectId, encapsOutputStream);
    EncapsulationUtility.writeOutputStream(encapsOutputStream, paramOutputStream);
    if (this.giopVersion.getMinor() > 0)
      EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream); 
  }
  
  public void writeContents(OutputStream paramOutputStream) {
    this.giopVersion.write(paramOutputStream);
    this.primary.write(paramOutputStream);
    if (this.giopVersion.getMinor() > 0)
      EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream); 
  }
  
  public int getId() { return 0; }
  
  public boolean isEquivalent(TaggedProfileTemplate paramTaggedProfileTemplate) {
    if (!(paramTaggedProfileTemplate instanceof IIOPProfileTemplateImpl))
      return false; 
    IIOPProfileTemplateImpl iIOPProfileTemplateImpl = (IIOPProfileTemplateImpl)paramTaggedProfileTemplate;
    return this.primary.equals(iIOPProfileTemplateImpl.primary);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPProfileTemplateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */