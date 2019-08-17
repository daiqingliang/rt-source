package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.impl.orbutil.HexOutputStream;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;
import sun.corba.OutputStreamFactory;

public class IORImpl extends IdentifiableContainerBase implements IOR {
  private String typeId;
  
  private ORB factory = null;
  
  private boolean isCachedHashValue = false;
  
  private int cachedHashValue;
  
  IORSystemException wrapper;
  
  private IORTemplateList iortemps = null;
  
  public ORB getORB() { return this.factory; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof IOR))
      return false; 
    IOR iOR = (IOR)paramObject;
    return (super.equals(paramObject) && this.typeId.equals(iOR.getTypeId()));
  }
  
  public int hashCode() {
    if (!this.isCachedHashValue) {
      this.cachedHashValue = super.hashCode() ^ this.typeId.hashCode();
      this.isCachedHashValue = true;
    } 
    return this.cachedHashValue;
  }
  
  public IORImpl(ORB paramORB) { this(paramORB, ""); }
  
  public IORImpl(ORB paramORB, String paramString) {
    this.factory = paramORB;
    this.wrapper = IORSystemException.get(paramORB, "oa.ior");
    this.typeId = paramString;
  }
  
  public IORImpl(ORB paramORB, String paramString, IORTemplate paramIORTemplate, ObjectId paramObjectId) {
    this(paramORB, paramString);
    this.iortemps = IORFactories.makeIORTemplateList();
    this.iortemps.add(paramIORTemplate);
    addTaggedProfiles(paramIORTemplate, paramObjectId);
    makeImmutable();
  }
  
  private void addTaggedProfiles(IORTemplate paramIORTemplate, ObjectId paramObjectId) {
    ObjectKeyTemplate objectKeyTemplate = paramIORTemplate.getObjectKeyTemplate();
    for (TaggedProfileTemplate taggedProfileTemplate : paramIORTemplate) {
      TaggedProfile taggedProfile = taggedProfileTemplate.create(objectKeyTemplate, paramObjectId);
      add(taggedProfile);
    } 
  }
  
  public IORImpl(ORB paramORB, String paramString, IORTemplateList paramIORTemplateList, ObjectId paramObjectId) {
    this(paramORB, paramString);
    this.iortemps = paramIORTemplateList;
    for (IORTemplate iORTemplate : paramIORTemplateList)
      addTaggedProfiles(iORTemplate, paramObjectId); 
    makeImmutable();
  }
  
  public IORImpl(InputStream paramInputStream) {
    this((ORB)paramInputStream.orb(), paramInputStream.read_string());
    IdentifiableFactoryFinder identifiableFactoryFinder = this.factory.getTaggedProfileFactoryFinder();
    EncapsulationUtility.readIdentifiableSequence(this, identifiableFactoryFinder, paramInputStream);
    makeImmutable();
  }
  
  public String getTypeId() { return this.typeId; }
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_string(this.typeId);
    EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream);
  }
  
  public String stringify() {
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.factory);
    encapsOutputStream.putEndian();
    write((OutputStream)encapsOutputStream);
    StringWriter stringWriter = new StringWriter();
    try {
      encapsOutputStream.writeTo(new HexOutputStream(stringWriter));
    } catch (IOException iOException) {
      throw this.wrapper.stringifyWriteError(iOException);
    } 
    return "IOR:" + stringWriter;
  }
  
  public void makeImmutable() {
    makeElementsImmutable();
    if (this.iortemps != null)
      this.iortemps.makeImmutable(); 
    super.makeImmutable();
  }
  
  public IOR getIOPIOR() {
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.factory);
    write(encapsOutputStream);
    InputStream inputStream = (InputStream)encapsOutputStream.create_input_stream();
    return IORHelper.read(inputStream);
  }
  
  public boolean isNil() { return (size() == 0); }
  
  public boolean isEquivalent(IOR paramIOR) {
    Iterator iterator1 = iterator();
    Iterator iterator2 = paramIOR.iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      TaggedProfile taggedProfile1 = (TaggedProfile)iterator1.next();
      TaggedProfile taggedProfile2 = (TaggedProfile)iterator2.next();
      if (!taggedProfile1.isEquivalent(taggedProfile2))
        return false; 
    } 
    return (iterator1.hasNext() == iterator2.hasNext());
  }
  
  private void initializeIORTemplateList() {
    HashMap hashMap = new HashMap();
    this.iortemps = IORFactories.makeIORTemplateList();
    Iterator iterator = iterator();
    ObjectId objectId = null;
    while (iterator.hasNext()) {
      TaggedProfile taggedProfile = (TaggedProfile)iterator.next();
      TaggedProfileTemplate taggedProfileTemplate = taggedProfile.getTaggedProfileTemplate();
      ObjectKeyTemplate objectKeyTemplate = taggedProfile.getObjectKeyTemplate();
      if (objectId == null) {
        objectId = taggedProfile.getObjectId();
      } else if (!objectId.equals(taggedProfile.getObjectId())) {
        throw this.wrapper.badOidInIorTemplateList();
      } 
      IORTemplate iORTemplate = (IORTemplate)hashMap.get(objectKeyTemplate);
      if (iORTemplate == null) {
        iORTemplate = IORFactories.makeIORTemplate(objectKeyTemplate);
        hashMap.put(objectKeyTemplate, iORTemplate);
        this.iortemps.add(iORTemplate);
      } 
      iORTemplate.add(taggedProfileTemplate);
    } 
    this.iortemps.makeImmutable();
  }
  
  public IORTemplateList getIORTemplates() {
    if (this.iortemps == null)
      initializeIORTemplateList(); 
    return this.iortemps;
  }
  
  public IIOPProfile getProfile() {
    IIOPProfile iIOPProfile = null;
    Iterator iterator = iteratorById(0);
    if (iterator.hasNext())
      iIOPProfile = (IIOPProfile)iterator.next(); 
    if (iIOPProfile != null)
      return iIOPProfile; 
    throw this.wrapper.iorMustHaveIiopProfile();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\IORImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */