package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class MinimalServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase {
  public MinimalServantCacheLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR) { super(paramORB, paramInt, paramIOR); }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass) {
    OAInvocationInfo oAInvocationInfo = getCachedInfo();
    return checkForCompatibleServant(oAInvocationInfo, paramClass) ? oAInvocationInfo : null;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\MinimalServantCacheLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */