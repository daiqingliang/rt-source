package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class IORTemplateImpl extends IdentifiableContainerBase implements IORTemplate {
  private ObjectKeyTemplate oktemp;
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof IORTemplateImpl))
      return false; 
    IORTemplateImpl iORTemplateImpl = (IORTemplateImpl)paramObject;
    return (super.equals(paramObject) && this.oktemp.equals(iORTemplateImpl.getObjectKeyTemplate()));
  }
  
  public int hashCode() { return super.hashCode() ^ this.oktemp.hashCode(); }
  
  public ObjectKeyTemplate getObjectKeyTemplate() { return this.oktemp; }
  
  public IORTemplateImpl(ObjectKeyTemplate paramObjectKeyTemplate) { this.oktemp = paramObjectKeyTemplate; }
  
  public IOR makeIOR(ORB paramORB, String paramString, ObjectId paramObjectId) { return new IORImpl(paramORB, paramString, this, paramObjectId); }
  
  public boolean isEquivalent(IORFactory paramIORFactory) {
    if (!(paramIORFactory instanceof IORTemplate))
      return false; 
    IORTemplate iORTemplate = (IORTemplate)paramIORFactory;
    Iterator iterator1 = iterator();
    Iterator iterator2 = iORTemplate.iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      TaggedProfileTemplate taggedProfileTemplate1 = (TaggedProfileTemplate)iterator1.next();
      TaggedProfileTemplate taggedProfileTemplate2 = (TaggedProfileTemplate)iterator2.next();
      if (!taggedProfileTemplate1.isEquivalent(taggedProfileTemplate2))
        return false; 
    } 
    return (iterator1.hasNext() == iterator2.hasNext() && getObjectKeyTemplate().equals(iORTemplate.getObjectKeyTemplate()));
  }
  
  public void makeImmutable() {
    makeElementsImmutable();
    super.makeImmutable();
  }
  
  public void write(OutputStream paramOutputStream) {
    this.oktemp.write(paramOutputStream);
    EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream);
  }
  
  public IORTemplateImpl(InputStream paramInputStream) {
    ORB oRB = (ORB)paramInputStream.orb();
    IdentifiableFactoryFinder identifiableFactoryFinder = oRB.getTaggedProfileTemplateFactoryFinder();
    this.oktemp = oRB.getObjectKeyFactory().createTemplate(paramInputStream);
    EncapsulationUtility.readIdentifiableSequence(this, identifiableFactoryFinder, paramInputStream);
    makeImmutable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\IORTemplateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */