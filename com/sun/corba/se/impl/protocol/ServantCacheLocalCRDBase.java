package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;

public abstract class ServantCacheLocalCRDBase extends LocalClientRequestDispatcherBase {
  private OAInvocationInfo cachedInfo;
  
  protected POASystemException wrapper;
  
  protected ServantCacheLocalCRDBase(ORB paramORB, int paramInt, IOR paramIOR) {
    super(paramORB, paramInt, paramIOR);
    this.wrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  protected OAInvocationInfo getCachedInfo() {
    if (!this.servantIsLocal)
      throw this.wrapper.servantMustBeLocal(); 
    if (this.cachedInfo == null) {
      objectAdapter = this.oaf.find(this.oaid);
      this.cachedInfo = objectAdapter.makeInvocationInfo(this.objectId);
      this.orb.pushInvocationInfo(this.cachedInfo);
      try {
        objectAdapter.enter();
        objectAdapter.getInvocationServant(this.cachedInfo);
      } catch (ForwardException forwardException) {
        throw this.wrapper.illegalForwardRequest(forwardException);
      } catch (OADestroyed oADestroyed) {
        throw this.wrapper.adapterDestroyed(oADestroyed);
      } finally {
        objectAdapter.returnServant();
        objectAdapter.exit();
        this.orb.popInvocationInfo();
      } 
    } 
    return this.cachedInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\ServantCacheLocalCRDBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */