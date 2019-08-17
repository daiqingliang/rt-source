package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ServantObject;

public class POALocalCRDImpl extends LocalClientRequestDispatcherBase {
  private ORBUtilSystemException wrapper;
  
  private POASystemException poaWrapper;
  
  public POALocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR) {
    super(paramORB, paramInt, paramIOR);
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.poaWrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  private OAInvocationInfo servantEnter(ObjectAdapter paramObjectAdapter) throws OADestroyed {
    paramObjectAdapter.enter();
    OAInvocationInfo oAInvocationInfo = paramObjectAdapter.makeInvocationInfo(this.objectId);
    this.orb.pushInvocationInfo(oAInvocationInfo);
    return oAInvocationInfo;
  }
  
  private void servantExit(ObjectAdapter paramObjectAdapter) {
    try {
      paramObjectAdapter.returnServant();
    } finally {
      paramObjectAdapter.exit();
      this.orb.popInvocationInfo();
    } 
  }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass) {
    ObjectAdapter objectAdapter = this.oaf.find(this.oaid);
    OAInvocationInfo oAInvocationInfo = null;
    try {
      oAInvocationInfo = servantEnter(objectAdapter);
      oAInvocationInfo.setOperation(paramString);
    } catch (OADestroyed oADestroyed) {
      return servant_preinvoke(paramObject, paramString, paramClass);
    } 
    try {
      objectAdapter.getInvocationServant(oAInvocationInfo);
      if (!checkForCompatibleServant(oAInvocationInfo, paramClass))
        return null; 
    } catch (Throwable throwable) {
      servantExit(objectAdapter);
      throw throwable;
    } catch (ForwardException forwardException) {
      RuntimeException runtimeException = new RuntimeException("deal with this.");
      runtimeException.initCause(forwardException);
      throw runtimeException;
    } catch (ThreadDeath threadDeath) {
      throw this.wrapper.runtimeexception(threadDeath);
    } catch (Throwable throwable) {
      if (throwable instanceof SystemException)
        throw (SystemException)throwable; 
      throw this.poaWrapper.localServantLookup(throwable);
    } 
    if (!checkForCompatibleServant(oAInvocationInfo, paramClass)) {
      servantExit(objectAdapter);
      return null;
    } 
    return oAInvocationInfo;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) {
    ObjectAdapter objectAdapter = this.orb.peekInvocationInfo().oa();
    servantExit(objectAdapter);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\POALocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */