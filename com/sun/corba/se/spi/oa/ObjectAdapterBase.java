package com.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public abstract class ObjectAdapterBase extends LocalObject implements ObjectAdapter {
  private ORB orb;
  
  private final POASystemException _iorWrapper;
  
  private final POASystemException _invocationWrapper;
  
  private final POASystemException _lifecycleWrapper;
  
  private final OMGSystemException _omgInvocationWrapper;
  
  private final OMGSystemException _omgLifecycleWrapper;
  
  private IORTemplate iortemp;
  
  private byte[] adapterId;
  
  private ObjectReferenceTemplate adapterTemplate;
  
  private ObjectReferenceFactory currentFactory;
  
  public ObjectAdapterBase(ORB paramORB) {
    this.orb = paramORB;
    this._lifecycleWrapper = (this._iorWrapper = POASystemException.get(paramORB, "oa.ior")).get(paramORB, "oa.lifecycle");
    this._omgLifecycleWrapper = OMGSystemException.get(paramORB, "oa.lifecycle");
    this._invocationWrapper = POASystemException.get(paramORB, "oa.invocation");
    this._omgInvocationWrapper = OMGSystemException.get(paramORB, "oa.invocation");
  }
  
  public final POASystemException iorWrapper() { return this._iorWrapper; }
  
  public final POASystemException lifecycleWrapper() { return this._lifecycleWrapper; }
  
  public final OMGSystemException omgLifecycleWrapper() { return this._omgLifecycleWrapper; }
  
  public final POASystemException invocationWrapper() { return this._invocationWrapper; }
  
  public final OMGSystemException omgInvocationWrapper() { return this._omgInvocationWrapper; }
  
  public final void initializeTemplate(ObjectKeyTemplate paramObjectKeyTemplate, boolean paramBoolean, Policies paramPolicies, String paramString1, String paramString2, ObjectAdapterId paramObjectAdapterId) {
    this.adapterId = paramObjectKeyTemplate.getAdapterId();
    this.iortemp = IORFactories.makeIORTemplate(paramObjectKeyTemplate);
    this.orb.getCorbaTransportManager().addToIORTemplate(this.iortemp, paramPolicies, paramString1, paramString2, paramObjectAdapterId);
    this.adapterTemplate = IORFactories.makeObjectReferenceTemplate(this.orb, this.iortemp);
    this.currentFactory = this.adapterTemplate;
    if (paramBoolean) {
      PIHandler pIHandler = this.orb.getPIHandler();
      if (pIHandler != null)
        pIHandler.objectAdapterCreated(this); 
    } 
    this.iortemp.makeImmutable();
  }
  
  public final Object makeObject(String paramString, byte[] paramArrayOfByte) { return this.currentFactory.make_object(paramString, paramArrayOfByte); }
  
  public final byte[] getAdapterId() { return this.adapterId; }
  
  public final ORB getORB() { return this.orb; }
  
  public abstract Policy getEffectivePolicy(int paramInt);
  
  public final IORTemplate getIORTemplate() { return this.iortemp; }
  
  public abstract int getManagerId();
  
  public abstract short getState();
  
  public final ObjectReferenceTemplate getAdapterTemplate() { return this.adapterTemplate; }
  
  public final ObjectReferenceFactory getCurrentFactory() { return this.currentFactory; }
  
  public final void setCurrentFactory(ObjectReferenceFactory paramObjectReferenceFactory) { this.currentFactory = paramObjectReferenceFactory; }
  
  public abstract Object getLocalServant(byte[] paramArrayOfByte);
  
  public abstract void getInvocationServant(OAInvocationInfo paramOAInvocationInfo);
  
  public abstract void returnServant();
  
  public abstract void enter();
  
  public abstract void exit();
  
  protected abstract ObjectCopierFactory getObjectCopierFactory();
  
  public OAInvocationInfo makeInvocationInfo(byte[] paramArrayOfByte) {
    OAInvocationInfo oAInvocationInfo = new OAInvocationInfo(this, paramArrayOfByte);
    oAInvocationInfo.setCopierFactory(getObjectCopierFactory());
    return oAInvocationInfo;
  }
  
  public abstract String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\oa\ObjectAdapterBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */