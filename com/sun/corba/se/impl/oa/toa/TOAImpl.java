package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate;
import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.pept.protocol.ClientDelegate;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.portable.Delegate;
import org.omg.PortableInterceptor.ObjectReferenceFactory;

public class TOAImpl extends ObjectAdapterBase implements TOA {
  private TransientObjectManager servants;
  
  public TOAImpl(ORB paramORB, TransientObjectManager paramTransientObjectManager, String paramString) {
    super(paramORB);
    this.servants = paramTransientObjectManager;
    int i = getORB().getTransientServerId();
    byte b = 2;
    JIDLObjectKeyTemplate jIDLObjectKeyTemplate = new JIDLObjectKeyTemplate(paramORB, b, i);
    Policies policies = Policies.defaultPolicies;
    initializeTemplate(jIDLObjectKeyTemplate, true, policies, paramString, null, jIDLObjectKeyTemplate.getObjectAdapterId());
  }
  
  public ObjectCopierFactory getObjectCopierFactory() {
    CopierManager copierManager = getORB().getCopierManager();
    return copierManager.getDefaultObjectCopierFactory();
  }
  
  public Object getLocalServant(byte[] paramArrayOfByte) { return (Object)this.servants.lookupServant(paramArrayOfByte); }
  
  public void getInvocationServant(OAInvocationInfo paramOAInvocationInfo) {
    Object object = this.servants.lookupServant(paramOAInvocationInfo.id());
    if (object == null)
      object = new NullServantImpl(lifecycleWrapper().nullServant()); 
    paramOAInvocationInfo.setServant(object);
  }
  
  public void returnServant() {}
  
  public String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte) { return StubAdapter.getTypeIds(paramObject); }
  
  public Policy getEffectivePolicy(int paramInt) { return null; }
  
  public int getManagerId() { return -1; }
  
  public short getState() { return 1; }
  
  public void enter() {}
  
  public void exit() {}
  
  public void connect(Object paramObject) {
    byte[] arrayOfByte = this.servants.storeServant(paramObject, null);
    String str = StubAdapter.getTypeIds(paramObject)[0];
    ObjectReferenceFactory objectReferenceFactory = getCurrentFactory();
    Object object = objectReferenceFactory.make_object(str, arrayOfByte);
    Delegate delegate = StubAdapter.getDelegate(object);
    CorbaContactInfoList corbaContactInfoList = (CorbaContactInfoList)((ClientDelegate)delegate).getContactInfoList();
    LocalClientRequestDispatcher localClientRequestDispatcher = corbaContactInfoList.getLocalClientRequestDispatcher();
    if (localClientRequestDispatcher instanceof JIDLLocalCRDImpl) {
      JIDLLocalCRDImpl jIDLLocalCRDImpl = (JIDLLocalCRDImpl)localClientRequestDispatcher;
      jIDLLocalCRDImpl.setServant(paramObject);
    } else {
      throw new RuntimeException("TOAImpl.connect can not be called on " + localClientRequestDispatcher);
    } 
    StubAdapter.setDelegate(paramObject, delegate);
  }
  
  public void disconnect(Object paramObject) {
    Delegate delegate = StubAdapter.getDelegate(paramObject);
    CorbaContactInfoList corbaContactInfoList = (CorbaContactInfoList)((ClientDelegate)delegate).getContactInfoList();
    LocalClientRequestDispatcher localClientRequestDispatcher = corbaContactInfoList.getLocalClientRequestDispatcher();
    if (localClientRequestDispatcher instanceof JIDLLocalCRDImpl) {
      JIDLLocalCRDImpl jIDLLocalCRDImpl = (JIDLLocalCRDImpl)localClientRequestDispatcher;
      byte[] arrayOfByte = jIDLLocalCRDImpl.getObjectId();
      this.servants.deleteServant(arrayOfByte);
      jIDLLocalCRDImpl.unexport();
    } else {
      throw new RuntimeException("TOAImpl.disconnect can not be called on " + localClientRequestDispatcher);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\toa\TOAImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */