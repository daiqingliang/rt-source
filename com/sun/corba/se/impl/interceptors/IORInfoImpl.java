package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.TaggedComponent;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.legacy.interceptor.IORInfoExt;
import com.sun.corba.se.spi.legacy.interceptor.UnknownType;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public final class IORInfoImpl extends LocalObject implements IORInfo, IORInfoExt {
  private static final int STATE_INITIAL = 0;
  
  private static final int STATE_ESTABLISHED = 1;
  
  private static final int STATE_DONE = 2;
  
  private int state = 0;
  
  private ObjectAdapter adapter;
  
  private ORB orb;
  
  private ORBUtilSystemException orbutilWrapper;
  
  private InterceptorsSystemException wrapper;
  
  private OMGSystemException omgWrapper;
  
  IORInfoImpl(ObjectAdapter paramObjectAdapter) {
    this.orb = paramObjectAdapter.getORB();
    this.orbutilWrapper = ORBUtilSystemException.get(this.orb, "rpc.protocol");
    this.wrapper = InterceptorsSystemException.get(this.orb, "rpc.protocol");
    this.omgWrapper = OMGSystemException.get(this.orb, "rpc.protocol");
    this.adapter = paramObjectAdapter;
  }
  
  public Policy get_effective_policy(int paramInt) {
    checkState(0, 1);
    return this.adapter.getEffectivePolicy(paramInt);
  }
  
  public void add_ior_component(TaggedComponent paramTaggedComponent) {
    checkState(0);
    if (paramTaggedComponent == null)
      nullParam(); 
    addIORComponentToProfileInternal(paramTaggedComponent, this.adapter.getIORTemplate().iterator());
  }
  
  public void add_ior_component_to_profile(TaggedComponent paramTaggedComponent, int paramInt) {
    checkState(0);
    if (paramTaggedComponent == null)
      nullParam(); 
    addIORComponentToProfileInternal(paramTaggedComponent, this.adapter.getIORTemplate().iteratorById(paramInt));
  }
  
  public int getServerPort(String paramString) throws UnknownType {
    checkState(0, 1);
    int i = this.orb.getLegacyServerSocketManager().legacyGetTransientOrPersistentServerPort(paramString);
    if (i == -1)
      throw new UnknownType(); 
    return i;
  }
  
  public ObjectAdapter getObjectAdapter() { return this.adapter; }
  
  public int manager_id() {
    checkState(0, 1);
    return this.adapter.getManagerId();
  }
  
  public short state() {
    checkState(0, 1);
    return this.adapter.getState();
  }
  
  public ObjectReferenceTemplate adapter_template() {
    checkState(1);
    return this.adapter.getAdapterTemplate();
  }
  
  public ObjectReferenceFactory current_factory() {
    checkState(1);
    return this.adapter.getCurrentFactory();
  }
  
  public void current_factory(ObjectReferenceFactory paramObjectReferenceFactory) {
    checkState(1);
    this.adapter.setCurrentFactory(paramObjectReferenceFactory);
  }
  
  private void addIORComponentToProfileInternal(TaggedComponent paramTaggedComponent, Iterator paramIterator) {
    TaggedComponentFactoryFinder taggedComponentFactoryFinder = this.orb.getTaggedComponentFactoryFinder();
    TaggedComponent taggedComponent = taggedComponentFactoryFinder.create(this.orb, paramTaggedComponent);
    boolean bool = false;
    while (paramIterator.hasNext()) {
      bool = true;
      TaggedProfileTemplate taggedProfileTemplate = (TaggedProfileTemplate)paramIterator.next();
      taggedProfileTemplate.add(taggedComponent);
    } 
    if (!bool)
      throw this.omgWrapper.invalidProfileId(); 
  }
  
  private void nullParam() { throw this.orbutilWrapper.nullParam(); }
  
  private void checkState(int paramInt) {
    if (paramInt != this.state)
      throw this.wrapper.badState1(new Integer(paramInt), new Integer(this.state)); 
  }
  
  private void checkState(int paramInt1, int paramInt2) {
    if (paramInt1 != this.state && paramInt2 != this.state)
      throw this.wrapper.badState2(new Integer(paramInt1), new Integer(paramInt2), new Integer(this.state)); 
  }
  
  void makeStateEstablished() {
    checkState(0);
    this.state = 1;
  }
  
  void makeStateDone() {
    checkState(1);
    this.state = 2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\IORInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */