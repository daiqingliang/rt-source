package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.protocol.NotLocalLocalCRDImpl;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CorbaContactInfoListImpl implements CorbaContactInfoList {
  protected ORB orb;
  
  protected LocalClientRequestDispatcher LocalClientRequestDispatcher;
  
  protected IOR targetIOR;
  
  protected IOR effectiveTargetIOR;
  
  protected List effectiveTargetIORContactInfoList;
  
  protected ContactInfo primaryContactInfo;
  
  public CorbaContactInfoListImpl(ORB paramORB) { this.orb = paramORB; }
  
  public CorbaContactInfoListImpl(ORB paramORB, IOR paramIOR) {
    this(paramORB);
    setTargetIOR(paramIOR);
  }
  
  public Iterator iterator() {
    createContactInfoList();
    return new CorbaContactInfoListIteratorImpl(this.orb, this, this.primaryContactInfo, this.effectiveTargetIORContactInfoList);
  }
  
  public void setTargetIOR(IOR paramIOR) {
    this.targetIOR = paramIOR;
    setEffectiveTargetIOR(paramIOR);
  }
  
  public IOR getTargetIOR() { return this.targetIOR; }
  
  public void setEffectiveTargetIOR(IOR paramIOR) {
    this.effectiveTargetIOR = paramIOR;
    this.effectiveTargetIORContactInfoList = null;
    if (this.primaryContactInfo != null && this.orb.getORBData().getIIOPPrimaryToContactInfo() != null)
      this.orb.getORBData().getIIOPPrimaryToContactInfo().reset(this.primaryContactInfo); 
    this.primaryContactInfo = null;
    setLocalSubcontract();
  }
  
  public IOR getEffectiveTargetIOR() { return this.effectiveTargetIOR; }
  
  public LocalClientRequestDispatcher getLocalClientRequestDispatcher() { return this.LocalClientRequestDispatcher; }
  
  public int hashCode() { return this.targetIOR.hashCode(); }
  
  protected void createContactInfoList() {
    if (this.effectiveTargetIORContactInfoList != null)
      return; 
    this.effectiveTargetIORContactInfoList = new ArrayList();
    IIOPProfile iIOPProfile = this.effectiveTargetIOR.getProfile();
    String str = ((IIOPProfileTemplate)iIOPProfile.getTaggedProfileTemplate()).getPrimaryAddress().getHost().toLowerCase();
    int i = ((IIOPProfileTemplate)iIOPProfile.getTaggedProfileTemplate()).getPrimaryAddress().getPort();
    this.primaryContactInfo = createContactInfo("IIOP_CLEAR_TEXT", str, i);
    if (iIOPProfile.isLocal()) {
      SharedCDRContactInfoImpl sharedCDRContactInfoImpl = new SharedCDRContactInfoImpl(this.orb, this, this.effectiveTargetIOR, this.orb.getORBData().getGIOPAddressDisposition());
      this.effectiveTargetIORContactInfoList.add(sharedCDRContactInfoImpl);
    } else {
      addRemoteContactInfos(this.effectiveTargetIOR, this.effectiveTargetIORContactInfoList);
    } 
  }
  
  protected void addRemoteContactInfos(IOR paramIOR, List paramList) {
    List list = this.orb.getORBData().getIORToSocketInfo().getSocketInfo(paramIOR);
    for (SocketInfo socketInfo : list) {
      String str1 = socketInfo.getType();
      String str2 = socketInfo.getHost().toLowerCase();
      int i = socketInfo.getPort();
      ContactInfo contactInfo = createContactInfo(str1, str2, i);
      paramList.add(contactInfo);
    } 
  }
  
  protected ContactInfo createContactInfo(String paramString1, String paramString2, int paramInt) { return new SocketOrChannelContactInfoImpl(this.orb, this, this.effectiveTargetIOR, this.orb.getORBData().getGIOPAddressDisposition(), paramString1, paramString2, paramInt); }
  
  protected void setLocalSubcontract() {
    if (!this.effectiveTargetIOR.getProfile().isLocal()) {
      this.LocalClientRequestDispatcher = new NotLocalLocalCRDImpl();
      return;
    } 
    int i = this.effectiveTargetIOR.getProfile().getObjectKeyTemplate().getSubcontractId();
    LocalClientRequestDispatcherFactory localClientRequestDispatcherFactory = this.orb.getRequestDispatcherRegistry().getLocalClientRequestDispatcherFactory(i);
    this.LocalClientRequestDispatcher = localClientRequestDispatcherFactory.create(i, this.effectiveTargetIOR);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaContactInfoListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */