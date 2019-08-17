package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import java.io.Closeable;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.PolicyFactory;

public interface PIHandler extends Closeable {
  void initialize();
  
  void destroyInterceptors();
  
  void objectAdapterCreated(ObjectAdapter paramObjectAdapter);
  
  void adapterManagerStateChanged(int paramInt, short paramShort);
  
  void adapterStateChanged(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate, short paramShort);
  
  void disableInterceptorsThisThread();
  
  void enableInterceptorsThisThread();
  
  void invokeClientPIStartingPoint();
  
  Exception invokeClientPIEndingPoint(int paramInt, Exception paramException);
  
  Exception makeCompletedClientRequest(int paramInt, Exception paramException);
  
  void initiateClientPIRequest(boolean paramBoolean);
  
  void cleanupClientPIRequest();
  
  void setClientPIInfo(RequestImpl paramRequestImpl);
  
  void setClientPIInfo(CorbaMessageMediator paramCorbaMessageMediator);
  
  void invokeServerPIStartingPoint();
  
  void invokeServerPIIntermediatePoint();
  
  void invokeServerPIEndingPoint(ReplyMessage paramReplyMessage);
  
  void initializeServerPIInfo(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate);
  
  void setServerPIInfo(Object paramObject, String paramString);
  
  void setServerPIInfo(Exception paramException);
  
  void setServerPIInfo(NVList paramNVList);
  
  void setServerPIExceptionInfo(Any paramAny);
  
  void setServerPIInfo(Any paramAny);
  
  void cleanupServerPIRequest();
  
  Policy create_policy(int paramInt, Any paramAny) throws PolicyError;
  
  void register_interceptor(Interceptor paramInterceptor, int paramInt) throws DuplicateName;
  
  Current getPICurrent();
  
  void registerPolicyFactory(int paramInt, PolicyFactory paramPolicyFactory);
  
  int allocateServerRequestId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\protocol\PIHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */