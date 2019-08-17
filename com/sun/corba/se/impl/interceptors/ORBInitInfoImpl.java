package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.legacy.interceptor.ORBInitInfoExt;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public final class ORBInitInfoImpl extends LocalObject implements ORBInitInfo, ORBInitInfoExt {
  private ORB orb;
  
  private InterceptorsSystemException wrapper;
  
  private ORBUtilSystemException orbutilWrapper;
  
  private OMGSystemException omgWrapper;
  
  private String[] args;
  
  private String orbId;
  
  private CodecFactory codecFactory;
  
  private int stage = 0;
  
  public static final int STAGE_PRE_INIT = 0;
  
  public static final int STAGE_POST_INIT = 1;
  
  public static final int STAGE_CLOSED = 2;
  
  private static final String MESSAGE_ORBINITINFO_INVALID = "ORBInitInfo object is only valid during ORB_init";
  
  ORBInitInfoImpl(ORB paramORB, String[] paramArrayOfString, String paramString, CodecFactory paramCodecFactory) {
    this.orb = paramORB;
    this.wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    this.orbutilWrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.omgWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    this.args = paramArrayOfString;
    this.orbId = paramString;
    this.codecFactory = paramCodecFactory;
  }
  
  public ORB getORB() { return this.orb; }
  
  void setStage(int paramInt) { this.stage = paramInt; }
  
  private void checkStage() {
    if (this.stage == 2)
      throw this.wrapper.orbinitinfoInvalid(); 
  }
  
  public String[] arguments() {
    checkStage();
    return this.args;
  }
  
  public String orb_id() {
    checkStage();
    return this.orbId;
  }
  
  public CodecFactory codec_factory() {
    checkStage();
    return this.codecFactory;
  }
  
  public void register_initial_reference(String paramString, Object paramObject) throws InvalidName {
    checkStage();
    if (paramString == null)
      nullParam(); 
    if (paramObject == null)
      throw this.omgWrapper.rirWithNullObject(); 
    try {
      this.orb.register_initial_reference(paramString, paramObject);
    } catch (InvalidName invalidName) {
      InvalidName invalidName1 = new InvalidName(invalidName.getMessage());
      invalidName1.initCause(invalidName);
      throw invalidName1;
    } 
  }
  
  public Object resolve_initial_references(String paramString) throws InvalidName {
    checkStage();
    if (paramString == null)
      nullParam(); 
    if (this.stage == 0)
      throw this.wrapper.rirInvalidPreInit(); 
    Object object = null;
    try {
      object = this.orb.resolve_initial_references(paramString);
    } catch (InvalidName invalidName) {
      throw new InvalidName();
    } 
    return object;
  }
  
  public void add_client_request_interceptor_with_policy(ClientRequestInterceptor paramClientRequestInterceptor, Policy[] paramArrayOfPolicy) throws DuplicateName { add_client_request_interceptor(paramClientRequestInterceptor); }
  
  public void add_client_request_interceptor(ClientRequestInterceptor paramClientRequestInterceptor) throws DuplicateName {
    checkStage();
    if (paramClientRequestInterceptor == null)
      nullParam(); 
    this.orb.getPIHandler().register_interceptor(paramClientRequestInterceptor, 0);
  }
  
  public void add_server_request_interceptor_with_policy(ServerRequestInterceptor paramServerRequestInterceptor, Policy[] paramArrayOfPolicy) throws DuplicateName, PolicyError { add_server_request_interceptor(paramServerRequestInterceptor); }
  
  public void add_server_request_interceptor(ServerRequestInterceptor paramServerRequestInterceptor) throws DuplicateName {
    checkStage();
    if (paramServerRequestInterceptor == null)
      nullParam(); 
    this.orb.getPIHandler().register_interceptor(paramServerRequestInterceptor, 1);
  }
  
  public void add_ior_interceptor_with_policy(IORInterceptor paramIORInterceptor, Policy[] paramArrayOfPolicy) throws DuplicateName, PolicyError { add_ior_interceptor(paramIORInterceptor); }
  
  public void add_ior_interceptor(IORInterceptor paramIORInterceptor) throws DuplicateName {
    checkStage();
    if (paramIORInterceptor == null)
      nullParam(); 
    this.orb.getPIHandler().register_interceptor(paramIORInterceptor, 2);
  }
  
  public int allocate_slot_id() {
    checkStage();
    return ((PICurrent)this.orb.getPIHandler().getPICurrent()).allocateSlotId();
  }
  
  public void register_policy_factory(int paramInt, PolicyFactory paramPolicyFactory) {
    checkStage();
    if (paramPolicyFactory == null)
      nullParam(); 
    this.orb.getPIHandler().registerPolicyFactory(paramInt, paramPolicyFactory);
  }
  
  private void nullParam() { throw this.orbutilWrapper.nullParam(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\ORBInitInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */