package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class FullServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase {
  public FullServantCacheLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR) { super(paramORB, paramInt, paramIOR); }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass) {
    OAInvocationInfo oAInvocationInfo1 = getCachedInfo();
    if (!checkForCompatibleServant(oAInvocationInfo1, paramClass))
      return null; 
    OAInvocationInfo oAInvocationInfo2 = new OAInvocationInfo(oAInvocationInfo1, paramString);
    this.orb.pushInvocationInfo(oAInvocationInfo2);
    try {
      oAInvocationInfo2.oa().enter();
    } catch (OADestroyed oADestroyed) {
      throw this.wrapper.preinvokePoaDestroyed(oADestroyed);
    } 
    return oAInvocationInfo2;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) {
    OAInvocationInfo oAInvocationInfo = getCachedInfo();
    oAInvocationInfo.oa().exit();
    this.orb.popInvocationInfo();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\FullServantCacheLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */