package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.ServerRequestImpl;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import org.omg.PortableServer.DynamicImplementation;

public class CorbaServerRequestDispatcherImpl implements CorbaServerRequestDispatcher {
  protected ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private POASystemException poaWrapper;
  
  public CorbaServerRequestDispatcherImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.poaWrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  public IOR locate(ObjectKey paramObjectKey) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".locate->"); 
      ObjectKeyTemplate objectKeyTemplate = paramObjectKey.getTemplate();
      try {
        checkServerId(paramObjectKey);
      } catch (ForwardException forwardException) {
        return forwardException.getIOR();
      } 
      findObjectAdapter(objectKeyTemplate);
      return null;
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".locate<-"); 
    } 
  }
  
  public void dispatch(MessageMediator paramMessageMediator) {
    corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".dispatch->: " + opAndId(corbaMessageMediator)); 
      consumeServiceContexts(corbaMessageMediator);
      ((MarshalInputStream)corbaMessageMediator.getInputObject()).performORBVersionSpecificInit();
      ObjectKey objectKey = corbaMessageMediator.getObjectKey();
      try {
        checkServerId(objectKey);
      } catch (ForwardException forwardException) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatch: " + opAndId(corbaMessageMediator) + ": bad server id"); 
        corbaMessageMediator.getProtocolHandler().createLocationForward(corbaMessageMediator, forwardException.getIOR(), null);
        return;
      } 
      String str = corbaMessageMediator.getOperationName();
      ObjectAdapter objectAdapter = null;
      try {
        byte[] arrayOfByte = objectKey.getId().getId();
        ObjectKeyTemplate objectKeyTemplate = objectKey.getTemplate();
        objectAdapter = findObjectAdapter(objectKeyTemplate);
        Object object = getServantWithPI(corbaMessageMediator, objectAdapter, arrayOfByte, objectKeyTemplate, str);
        dispatchToServant(object, corbaMessageMediator, arrayOfByte, objectAdapter);
      } catch (ForwardException forwardException) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatch: " + opAndId(corbaMessageMediator) + ": ForwardException caught"); 
        corbaMessageMediator.getProtocolHandler().createLocationForward(corbaMessageMediator, forwardException.getIOR(), null);
      } catch (OADestroyed oADestroyed) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatch: " + opAndId(corbaMessageMediator) + ": OADestroyed exception caught"); 
        dispatch(corbaMessageMediator);
      } catch (RequestCanceledException requestCanceledException) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatch: " + opAndId(corbaMessageMediator) + ": RequestCanceledException caught"); 
        throw requestCanceledException;
      } catch (UnknownException unknownException) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatch: " + opAndId(corbaMessageMediator) + ": UnknownException caught " + unknownException); 
        if (unknownException.originalEx instanceof RequestCanceledException)
          throw (RequestCanceledException)unknownException.originalEx; 
        ServiceContexts serviceContexts = new ServiceContexts(this.orb);
        UEInfoServiceContext uEInfoServiceContext = new UEInfoServiceContext(unknownException.originalEx);
        serviceContexts.put(uEInfoServiceContext);
        UNKNOWN uNKNOWN = this.wrapper.unknownExceptionInDispatch(CompletionStatus.COMPLETED_MAYBE, unknownException);
        corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, uNKNOWN, serviceContexts);
      } catch (Throwable throwable) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatch: " + opAndId(corbaMessageMediator) + ": other exception " + throwable); 
        corbaMessageMediator.getProtocolHandler().handleThrowableDuringServerDispatch(corbaMessageMediator, throwable, CompletionStatus.COMPLETED_MAYBE);
      } 
      return;
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".dispatch<-: " + opAndId(corbaMessageMediator)); 
    } 
  }
  
  private void releaseServant(ObjectAdapter paramObjectAdapter) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".releaseServant->"); 
      if (paramObjectAdapter == null) {
        if (this.orb.subcontractDebugFlag)
          dprint(".releaseServant: null object adapter"); 
        return;
      } 
      try {
        paramObjectAdapter.returnServant();
      } finally {
        paramObjectAdapter.exit();
        this.orb.popInvocationInfo();
      } 
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".releaseServant<-"); 
    } 
  }
  
  private Object getServant(ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, String paramString) throws OADestroyed {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".getServant->"); 
      OAInvocationInfo oAInvocationInfo = paramObjectAdapter.makeInvocationInfo(paramArrayOfByte);
      oAInvocationInfo.setOperation(paramString);
      this.orb.pushInvocationInfo(oAInvocationInfo);
      paramObjectAdapter.getInvocationServant(oAInvocationInfo);
      return oAInvocationInfo.getServantContainer();
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".getServant<-"); 
    } 
  }
  
  protected Object getServantWithPI(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate, String paramString) throws OADestroyed {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".getServantWithPI->"); 
      this.orb.getPIHandler().initializeServerPIInfo(paramCorbaMessageMediator, paramObjectAdapter, paramArrayOfByte, paramObjectKeyTemplate);
      this.orb.getPIHandler().invokeServerPIStartingPoint();
      paramObjectAdapter.enter();
      if (paramCorbaMessageMediator != null)
        paramCorbaMessageMediator.setExecuteReturnServantInResponseConstructor(true); 
      Object object = getServant(paramObjectAdapter, paramArrayOfByte, paramString);
      String str = "unknown";
      if (object instanceof NullServant) {
        handleNullServant(paramString, (NullServant)object);
      } else {
        str = paramObjectAdapter.getInterfaces(object, paramArrayOfByte)[0];
      } 
      this.orb.getPIHandler().setServerPIInfo(object, str);
      if ((object != null && !(object instanceof DynamicImplementation) && !(object instanceof DynamicImplementation)) || SpecialMethod.getSpecialMethod(paramString) != null)
        this.orb.getPIHandler().invokeServerPIIntermediatePoint(); 
      return object;
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".getServantWithPI<-"); 
    } 
  }
  
  protected void checkServerId(ObjectKey paramObjectKey) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".checkServerId->"); 
      ObjectKeyTemplate objectKeyTemplate = paramObjectKey.getTemplate();
      int i = objectKeyTemplate.getServerId();
      int j = objectKeyTemplate.getSubcontractId();
      if (!this.orb.isLocalServerId(j, i)) {
        if (this.orb.subcontractDebugFlag)
          dprint(".checkServerId: bad server id"); 
        this.orb.handleBadServerId(paramObjectKey);
      } 
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".checkServerId<-"); 
    } 
  }
  
  private ObjectAdapter findObjectAdapter(ObjectKeyTemplate paramObjectKeyTemplate) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".findObjectAdapter->"); 
      RequestDispatcherRegistry requestDispatcherRegistry = this.orb.getRequestDispatcherRegistry();
      int i = paramObjectKeyTemplate.getSubcontractId();
      ObjectAdapterFactory objectAdapterFactory = requestDispatcherRegistry.getObjectAdapterFactory(i);
      if (objectAdapterFactory == null) {
        if (this.orb.subcontractDebugFlag)
          dprint(".findObjectAdapter: failed to find ObjectAdapterFactory"); 
        throw this.wrapper.noObjectAdapterFactory();
      } 
      ObjectAdapterId objectAdapterId = paramObjectKeyTemplate.getObjectAdapterId();
      ObjectAdapter objectAdapter = objectAdapterFactory.find(objectAdapterId);
      if (objectAdapter == null) {
        if (this.orb.subcontractDebugFlag)
          dprint(".findObjectAdapter: failed to find ObjectAdaptor"); 
        throw this.wrapper.badAdapterId();
      } 
      return objectAdapter;
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".findObjectAdapter<-"); 
    } 
  }
  
  protected void handleNullServant(String paramString, NullServant paramNullServant) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".handleNullServant->: " + paramString); 
      SpecialMethod specialMethod = SpecialMethod.getSpecialMethod(paramString);
      if (specialMethod == null || !specialMethod.isNonExistentMethod()) {
        if (this.orb.subcontractDebugFlag)
          dprint(".handleNullServant: " + paramString + ": throwing OBJECT_NOT_EXIST"); 
        throw paramNullServant.getException();
      } 
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".handleNullServant<-: " + paramString); 
    } 
  }
  
  protected void consumeServiceContexts(CorbaMessageMediator paramCorbaMessageMediator) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".consumeServiceContexts->: " + opAndId(paramCorbaMessageMediator)); 
      ServiceContexts serviceContexts = paramCorbaMessageMediator.getRequestServiceContexts();
      GIOPVersion gIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
      boolean bool = processCodeSetContext(paramCorbaMessageMediator, serviceContexts);
      if (this.orb.subcontractDebugFlag) {
        dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": GIOP version: " + gIOPVersion);
        dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": as code set context? " + bool);
      } 
      ServiceContext serviceContext = serviceContexts.get(6);
      if (serviceContext != null) {
        SendingContextServiceContext sendingContextServiceContext = (SendingContextServiceContext)serviceContext;
        IOR iOR = sendingContextServiceContext.getIOR();
        try {
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeBaseIOR(iOR);
        } catch (ThreadDeath threadDeath) {
          throw threadDeath;
        } catch (Throwable throwable) {
          throw this.wrapper.badStringifiedIor(throwable);
        } 
      } 
      boolean bool1 = false;
      if (gIOPVersion.equals(GIOPVersion.V1_0) && bool) {
        if (this.orb.subcontractDebugFlag)
          dprint(".consumeServiceCOntexts: " + opAndId(paramCorbaMessageMediator) + ": Determined to be an old Sun ORB"); 
        this.orb.setORBVersion(ORBVersionFactory.getOLD());
      } else {
        bool1 = true;
      } 
      serviceContext = serviceContexts.get(1313165056);
      if (serviceContext != null) {
        ORBVersionServiceContext oRBVersionServiceContext = (ORBVersionServiceContext)serviceContext;
        ORBVersion oRBVersion = oRBVersionServiceContext.getVersion();
        this.orb.setORBVersion(oRBVersion);
        bool1 = false;
      } 
      if (bool1) {
        if (this.orb.subcontractDebugFlag)
          dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": Determined to be a foreign ORB"); 
        this.orb.setORBVersion(ORBVersionFactory.getFOREIGN());
      } 
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".consumeServiceContexts<-: " + opAndId(paramCorbaMessageMediator)); 
    } 
  }
  
  protected CorbaMessageMediator dispatchToServant(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".dispatchToServant->: " + opAndId(paramCorbaMessageMediator)); 
      CorbaMessageMediator corbaMessageMediator = null;
      String str = paramCorbaMessageMediator.getOperationName();
      SpecialMethod specialMethod = SpecialMethod.getSpecialMethod(str);
      if (specialMethod != null) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling special method"); 
        corbaMessageMediator = specialMethod.invoke(paramObject, paramCorbaMessageMediator, paramArrayOfByte, paramObjectAdapter);
        return corbaMessageMediator;
      } 
      if (paramObject instanceof DynamicImplementation) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling old style DSI type servant"); 
        DynamicImplementation dynamicImplementation = (DynamicImplementation)paramObject;
        ServerRequestImpl serverRequestImpl = new ServerRequestImpl(paramCorbaMessageMediator, this.orb);
        dynamicImplementation.invoke(serverRequestImpl);
        corbaMessageMediator = handleDynamicResult(serverRequestImpl, paramCorbaMessageMediator);
      } else if (paramObject instanceof DynamicImplementation) {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling POA DSI type servant"); 
        DynamicImplementation dynamicImplementation = (DynamicImplementation)paramObject;
        ServerRequestImpl serverRequestImpl = new ServerRequestImpl(paramCorbaMessageMediator, this.orb);
        dynamicImplementation.invoke(serverRequestImpl);
        corbaMessageMediator = handleDynamicResult(serverRequestImpl, paramCorbaMessageMediator);
      } else {
        if (this.orb.subcontractDebugFlag)
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling invoke handler type servant"); 
        InvokeHandler invokeHandler = (InvokeHandler)paramObject;
        OutputStream outputStream = invokeHandler._invoke(str, (InputStream)paramCorbaMessageMediator.getInputObject(), paramCorbaMessageMediator);
        corbaMessageMediator = (CorbaMessageMediator)((OutputObject)outputStream).getMessageMediator();
      } 
      return corbaMessageMediator;
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".dispatchToServant<-: " + opAndId(paramCorbaMessageMediator)); 
    } 
  }
  
  protected CorbaMessageMediator handleDynamicResult(ServerRequestImpl paramServerRequestImpl, CorbaMessageMediator paramCorbaMessageMediator) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".handleDynamicResult->: " + opAndId(paramCorbaMessageMediator)); 
      CorbaMessageMediator corbaMessageMediator = null;
      Any any = paramServerRequestImpl.checkResultCalled();
      if (any == null) {
        if (this.orb.subcontractDebugFlag)
          dprint(".handleDynamicResult: " + opAndId(paramCorbaMessageMediator) + ": handling normal result"); 
        corbaMessageMediator = sendingReply(paramCorbaMessageMediator);
        OutputStream outputStream = (OutputStream)corbaMessageMediator.getOutputObject();
        paramServerRequestImpl.marshalReplyParams(outputStream);
      } else {
        if (this.orb.subcontractDebugFlag)
          dprint(".handleDynamicResult: " + opAndId(paramCorbaMessageMediator) + ": handling error"); 
        corbaMessageMediator = sendingReply(paramCorbaMessageMediator, any);
      } 
      return corbaMessageMediator;
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".handleDynamicResult<-: " + opAndId(paramCorbaMessageMediator)); 
    } 
  }
  
  protected CorbaMessageMediator sendingReply(CorbaMessageMediator paramCorbaMessageMediator) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".sendingReply->: " + opAndId(paramCorbaMessageMediator)); 
      ServiceContexts serviceContexts = new ServiceContexts(this.orb);
      return paramCorbaMessageMediator.getProtocolHandler().createResponse(paramCorbaMessageMediator, serviceContexts);
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".sendingReply<-: " + opAndId(paramCorbaMessageMediator)); 
    } 
  }
  
  protected CorbaMessageMediator sendingReply(CorbaMessageMediator paramCorbaMessageMediator, Any paramAny) {
    try {
      CorbaMessageMediator corbaMessageMediator;
      if (this.orb.subcontractDebugFlag)
        dprint(".sendingReply/Any->: " + opAndId(paramCorbaMessageMediator)); 
      ServiceContexts serviceContexts = new ServiceContexts(this.orb);
      String str = null;
      try {
        str = paramAny.type().id();
      } catch (BadKind badKind) {
        throw this.wrapper.problemWithExceptionTypecode(badKind);
      } 
      if (ORBUtility.isSystemException(str)) {
        if (this.orb.subcontractDebugFlag)
          dprint(".sendingReply/Any: " + opAndId(paramCorbaMessageMediator) + ": handling system exception"); 
        InputStream inputStream = paramAny.create_input_stream();
        SystemException systemException = ORBUtility.readSystemException(inputStream);
        corbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, systemException, serviceContexts);
      } else {
        if (this.orb.subcontractDebugFlag)
          dprint(".sendingReply/Any: " + opAndId(paramCorbaMessageMediator) + ": handling user exception"); 
        corbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createUserExceptionResponse(paramCorbaMessageMediator, serviceContexts);
        OutputStream outputStream = (OutputStream)corbaMessageMediator.getOutputObject();
        paramAny.write_value(outputStream);
      } 
      return corbaMessageMediator;
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".sendingReply/Any<-: " + opAndId(paramCorbaMessageMediator)); 
    } 
  }
  
  protected boolean processCodeSetContext(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts) {
    try {
      if (this.orb.subcontractDebugFlag)
        dprint(".processCodeSetContext->: " + opAndId(paramCorbaMessageMediator)); 
      ServiceContext serviceContext = paramServiceContexts.get(1);
      if (serviceContext != null) {
        if (paramCorbaMessageMediator.getConnection() == null)
          return true; 
        if (paramCorbaMessageMediator.getGIOPVersion().equals(GIOPVersion.V1_0))
          return true; 
        CodeSetServiceContext codeSetServiceContext = (CodeSetServiceContext)serviceContext;
        CodeSetComponentInfo.CodeSetContext codeSetContext = codeSetServiceContext.getCodeSetContext();
        if (((CorbaConnection)paramCorbaMessageMediator.getConnection()).getCodeSetContext() == null) {
          if (this.orb.subcontractDebugFlag)
            dprint(".processCodeSetContext: " + opAndId(paramCorbaMessageMediator) + ": Setting code sets to: " + codeSetContext); 
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeSetContext(codeSetContext);
          if (codeSetContext.getCharCodeSet() != OSFCodeSetRegistry.ISO_8859_1.getNumber())
            ((MarshalInputStream)paramCorbaMessageMediator.getInputObject()).resetCodeSetConverters(); 
        } 
      } 
      return (serviceContext != null);
    } finally {
      if (this.orb.subcontractDebugFlag)
        dprint(".processCodeSetContext<-: " + opAndId(paramCorbaMessageMediator)); 
    } 
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaServerRequestDispatcherImpl", paramString); }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator) { return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\CorbaServerRequestDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */