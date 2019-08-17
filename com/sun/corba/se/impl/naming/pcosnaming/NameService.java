package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class NameService {
  private NamingContext rootContext = null;
  
  private POA nsPOA = null;
  
  private ServantManagerImpl contextMgr;
  
  private ORB theorb;
  
  public NameService(ORB paramORB, File paramFile) throws Exception {
    this.theorb = paramORB;
    POA pOA = (POA)paramORB.resolve_initial_references("RootPOA");
    pOA.the_POAManager().activate();
    byte b = 0;
    Policy[] arrayOfPolicy = new Policy[4];
    arrayOfPolicy[b++] = pOA.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
    arrayOfPolicy[b++] = pOA.create_request_processing_policy(RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
    arrayOfPolicy[b++] = pOA.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID);
    arrayOfPolicy[b++] = pOA.create_servant_retention_policy(ServantRetentionPolicyValue.NON_RETAIN);
    this.nsPOA = pOA.create_POA("NameService", null, arrayOfPolicy);
    this.nsPOA.the_POAManager().activate();
    this.contextMgr = new ServantManagerImpl(paramORB, paramFile, this);
    String str = this.contextMgr.getRootObjectKey();
    NamingContextImpl namingContextImpl = new NamingContextImpl(paramORB, str, this, this.contextMgr);
    namingContextImpl = this.contextMgr.addContext(str, namingContextImpl);
    namingContextImpl.setServantManagerImpl(this.contextMgr);
    namingContextImpl.setORB(paramORB);
    namingContextImpl.setRootNameService(this);
    this.nsPOA.set_servant_manager(this.contextMgr);
    this.rootContext = NamingContextHelper.narrow(this.nsPOA.create_reference_with_id(str.getBytes(), NamingContextHelper.id()));
  }
  
  public NamingContext initialNamingContext() { return this.rootContext; }
  
  POA getNSPOA() { return this.nsPOA; }
  
  public NamingContext NewContext() {
    try {
      String str = this.contextMgr.getNewObjectKey();
      NamingContextImpl namingContextImpl1 = new NamingContextImpl(this.theorb, str, this, this.contextMgr);
      NamingContextImpl namingContextImpl2 = this.contextMgr.addContext(str, namingContextImpl1);
      if (namingContextImpl2 != null)
        namingContextImpl1 = namingContextImpl2; 
      namingContextImpl1.setServantManagerImpl(this.contextMgr);
      namingContextImpl1.setORB(this.theorb);
      namingContextImpl1.setRootNameService(this);
      return NamingContextHelper.narrow(this.nsPOA.create_reference_with_id(str.getBytes(), NamingContextHelper.id()));
    } catch (SystemException systemException) {
      throw systemException;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  Object getObjectReferenceFromKey(String paramString) {
    Object object = null;
    try {
      object = this.nsPOA.create_reference_with_id(paramString.getBytes(), NamingContextHelper.id());
    } catch (Exception exception) {
      object = null;
    } 
    return object;
  }
  
  String getObjectKey(Object paramObject) {
    byte[] arrayOfByte;
    try {
      arrayOfByte = this.nsPOA.reference_to_id(paramObject);
    } catch (WrongAdapter wrongAdapter) {
      return null;
    } catch (WrongPolicy wrongPolicy) {
      return null;
    } catch (Exception exception) {
      return null;
    } 
    return new String(arrayOfByte);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\NameService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */