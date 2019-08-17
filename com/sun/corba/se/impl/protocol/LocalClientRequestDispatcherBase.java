package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public abstract class LocalClientRequestDispatcherBase implements LocalClientRequestDispatcher {
  protected ORB orb;
  
  int scid;
  
  protected boolean servantIsLocal;
  
  protected ObjectAdapterFactory oaf;
  
  protected ObjectAdapterId oaid;
  
  protected byte[] objectId;
  
  private static final ThreadLocal isNextCallValid = new ThreadLocal() {
      protected Object initialValue() { return Boolean.TRUE; }
    };
  
  protected LocalClientRequestDispatcherBase(ORB paramORB, int paramInt, IOR paramIOR) {
    this.orb = paramORB;
    IIOPProfile iIOPProfile = paramIOR.getProfile();
    this.servantIsLocal = (paramORB.getORBData().isLocalOptimizationAllowed() && iIOPProfile.isLocal());
    ObjectKeyTemplate objectKeyTemplate = iIOPProfile.getObjectKeyTemplate();
    this.scid = objectKeyTemplate.getSubcontractId();
    RequestDispatcherRegistry requestDispatcherRegistry = paramORB.getRequestDispatcherRegistry();
    this.oaf = requestDispatcherRegistry.getObjectAdapterFactory(paramInt);
    this.oaid = objectKeyTemplate.getObjectAdapterId();
    ObjectId objectId1 = iIOPProfile.getObjectId();
    this.objectId = objectId1.getId();
  }
  
  public byte[] getObjectId() { return this.objectId; }
  
  public boolean is_local(Object paramObject) { return false; }
  
  public boolean useLocalInvocation(Object paramObject) {
    if (isNextCallValid.get() == Boolean.TRUE)
      return this.servantIsLocal; 
    isNextCallValid.set(Boolean.TRUE);
    return false;
  }
  
  protected boolean checkForCompatibleServant(ServantObject paramServantObject, Class paramClass) {
    if (paramServantObject == null)
      return false; 
    if (!paramClass.isInstance(paramServantObject.servant)) {
      isNextCallValid.set(Boolean.FALSE);
      return false;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\LocalClientRequestDispatcherBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */