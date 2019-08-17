package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IdentifiableBase;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Iterator;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public class IIOPProfileImpl extends IdentifiableBase implements IIOPProfile {
  private ORB orb;
  
  private IORSystemException wrapper;
  
  private ObjectId oid;
  
  private IIOPProfileTemplate proftemp;
  
  private ObjectKeyTemplate oktemp;
  
  protected String codebase = null;
  
  protected boolean cachedCodebase = false;
  
  private boolean checkedIsLocal = false;
  
  private boolean cachedIsLocal = false;
  
  private GIOPVersion giopVersion = null;
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof IIOPProfileImpl))
      return false; 
    IIOPProfileImpl iIOPProfileImpl = (IIOPProfileImpl)paramObject;
    return (this.oid.equals(iIOPProfileImpl.oid) && this.proftemp.equals(iIOPProfileImpl.proftemp) && this.oktemp.equals(iIOPProfileImpl.oktemp));
  }
  
  public int hashCode() { return this.oid.hashCode() ^ this.proftemp.hashCode() ^ this.oktemp.hashCode(); }
  
  public ObjectId getObjectId() { return this.oid; }
  
  public TaggedProfileTemplate getTaggedProfileTemplate() { return this.proftemp; }
  
  public ObjectKeyTemplate getObjectKeyTemplate() { return this.oktemp; }
  
  private IIOPProfileImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = IORSystemException.get(paramORB, "oa.ior");
  }
  
  public IIOPProfileImpl(ORB paramORB, ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, IIOPProfileTemplate paramIIOPProfileTemplate) {
    this(paramORB);
    this.oktemp = paramObjectKeyTemplate;
    this.oid = paramObjectId;
    this.proftemp = paramIIOPProfileTemplate;
  }
  
  public IIOPProfileImpl(InputStream paramInputStream) {
    this((ORB)paramInputStream.orb());
    init(paramInputStream);
  }
  
  public IIOPProfileImpl(ORB paramORB, TaggedProfile paramTaggedProfile) {
    this(paramORB);
    if (paramTaggedProfile == null || paramTaggedProfile.tag != 0 || paramTaggedProfile.profile_data == null)
      throw this.wrapper.invalidTaggedProfile(); 
    EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(paramORB, paramTaggedProfile.profile_data, paramTaggedProfile.profile_data.length);
    encapsInputStream.consumeEndian();
    init(encapsInputStream);
  }
  
  private void init(InputStream paramInputStream) {
    GIOPVersion gIOPVersion = new GIOPVersion();
    gIOPVersion.read(paramInputStream);
    IIOPAddressImpl iIOPAddressImpl = new IIOPAddressImpl(paramInputStream);
    byte[] arrayOfByte = EncapsulationUtility.readOctets(paramInputStream);
    ObjectKey objectKey = this.orb.getObjectKeyFactory().create(arrayOfByte);
    this.oktemp = objectKey.getTemplate();
    this.oid = objectKey.getId();
    this.proftemp = IIOPFactories.makeIIOPProfileTemplate(this.orb, gIOPVersion, iIOPAddressImpl);
    if (gIOPVersion.getMinor() > 0)
      EncapsulationUtility.readIdentifiableSequence(this.proftemp, this.orb.getTaggedComponentFactoryFinder(), paramInputStream); 
    if (uncachedGetCodeBase() == null) {
      JavaCodebaseComponent javaCodebaseComponent = LocalCodeBaseSingletonHolder.comp;
      if (javaCodebaseComponent != null) {
        if (gIOPVersion.getMinor() > 0)
          this.proftemp.add(javaCodebaseComponent); 
        this.codebase = javaCodebaseComponent.getURLs();
      } 
      this.cachedCodebase = true;
    } 
  }
  
  public void writeContents(OutputStream paramOutputStream) { this.proftemp.write(this.oktemp, this.oid, paramOutputStream); }
  
  public int getId() { return this.proftemp.getId(); }
  
  public boolean isEquivalent(TaggedProfile paramTaggedProfile) {
    if (!(paramTaggedProfile instanceof IIOPProfile))
      return false; 
    IIOPProfile iIOPProfile = (IIOPProfile)paramTaggedProfile;
    return (this.oid.equals(iIOPProfile.getObjectId()) && this.proftemp.isEquivalent(iIOPProfile.getTaggedProfileTemplate()) && this.oktemp.equals(iIOPProfile.getObjectKeyTemplate()));
  }
  
  public ObjectKey getObjectKey() { return IORFactories.makeObjectKey(this.oktemp, this.oid); }
  
  public TaggedProfile getIOPProfile() {
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.orb);
    encapsOutputStream.write_long(getId());
    write(encapsOutputStream);
    InputStream inputStream = (InputStream)encapsOutputStream.create_input_stream();
    return TaggedProfileHelper.read(inputStream);
  }
  
  private String uncachedGetCodeBase() {
    Iterator iterator = this.proftemp.iteratorById(25);
    if (iterator.hasNext()) {
      JavaCodebaseComponent javaCodebaseComponent = (JavaCodebaseComponent)iterator.next();
      return javaCodebaseComponent.getURLs();
    } 
    return null;
  }
  
  public String getCodebase() {
    if (!this.cachedCodebase) {
      this.cachedCodebase = true;
      this.codebase = uncachedGetCodeBase();
    } 
    return this.codebase;
  }
  
  public ORBVersion getORBVersion() { return this.oktemp.getORBVersion(); }
  
  public boolean isLocal() {
    if (!this.checkedIsLocal) {
      this.checkedIsLocal = true;
      String str = this.proftemp.getPrimaryAddress().getHost();
      this.cachedIsLocal = (this.orb.isLocalHost(str) && this.orb.isLocalServerId(this.oktemp.getSubcontractId(), this.oktemp.getServerId()) && this.orb.getLegacyServerSocketManager().legacyIsLocalServerPort(this.proftemp.getPrimaryAddress().getPort()));
    } 
    return this.cachedIsLocal;
  }
  
  public Object getServant() {
    if (!isLocal())
      return null; 
    RequestDispatcherRegistry requestDispatcherRegistry = this.orb.getRequestDispatcherRegistry();
    ObjectAdapterFactory objectAdapterFactory = requestDispatcherRegistry.getObjectAdapterFactory(this.oktemp.getSubcontractId());
    ObjectAdapterId objectAdapterId = this.oktemp.getObjectAdapterId();
    ObjectAdapter objectAdapter = null;
    try {
      objectAdapter = objectAdapterFactory.find(objectAdapterId);
    } catch (SystemException systemException) {
      this.wrapper.getLocalServantFailure(systemException, objectAdapterId.toString());
      return null;
    } 
    byte[] arrayOfByte = this.oid.getId();
    return objectAdapter.getLocalServant(arrayOfByte);
  }
  
  public GIOPVersion getGIOPVersion() { return this.proftemp.getGIOPVersion(); }
  
  public void makeImmutable() { this.proftemp.makeImmutable(); }
  
  private static class LocalCodeBaseSingletonHolder {
    public static JavaCodebaseComponent comp;
    
    static  {
      String str = JDKBridge.getLocalCodebase();
      if (str == null) {
        comp = null;
      } else {
        comp = IIOPFactories.makeJavaCodebaseComponent(str);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPProfileImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */