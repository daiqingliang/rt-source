package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.CodeSetConversion;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class CorbaClientRequestDispatcherImpl implements ClientRequestDispatcher {
  private ConcurrentMap<ContactInfo, Object> locks = new ConcurrentHashMap();
  
  public OutputObject beginRequest(Object paramObject, String paramString, boolean paramBoolean, ContactInfo paramContactInfo) {
    oRB = null;
    try {
      CorbaContactInfo corbaContactInfo = (CorbaContactInfo)paramContactInfo;
      oRB = (ORB)paramContactInfo.getBroker();
      if (oRB.subcontractDebugFlag)
        dprint(".beginRequest->: op/" + paramString); 
      oRB.getPIHandler().initiateClientPIRequest(false);
      CorbaConnection corbaConnection = null;
      Object object = this.locks.get(paramContactInfo);
      if (object == null) {
        Object object1 = new Object();
        object = this.locks.putIfAbsent(paramContactInfo, object1);
        if (object == null)
          object = object1; 
      } 
      synchronized (object) {
        if (paramContactInfo.isConnectionBased()) {
          if (paramContactInfo.shouldCacheConnection())
            corbaConnection = (CorbaConnection)oRB.getTransportManager().getOutboundConnectionCache(paramContactInfo).get(paramContactInfo); 
          if (corbaConnection != null) {
            if (oRB.subcontractDebugFlag)
              dprint(".beginRequest: op/" + paramString + ": Using cached connection: " + corbaConnection); 
          } else {
            try {
              corbaConnection = (CorbaConnection)paramContactInfo.createConnection();
              if (oRB.subcontractDebugFlag)
                dprint(".beginRequest: op/" + paramString + ": Using created connection: " + corbaConnection); 
            } catch (RuntimeException runtimeException) {
              if (oRB.subcontractDebugFlag)
                dprint(".beginRequest: op/" + paramString + ": failed to create connection: " + runtimeException); 
              boolean bool = getContactInfoListIterator(oRB).reportException(paramContactInfo, runtimeException);
              if (bool) {
                if (getContactInfoListIterator(oRB).hasNext()) {
                  paramContactInfo = (ContactInfo)getContactInfoListIterator(oRB).next();
                  unregisterWaiter(oRB);
                  return beginRequest(paramObject, paramString, paramBoolean, paramContactInfo);
                } 
                throw runtimeException;
              } 
              throw runtimeException;
            } 
            if (corbaConnection.shouldRegisterReadEvent()) {
              oRB.getTransportManager().getSelector(0).registerForEvent(corbaConnection.getEventHandler());
              corbaConnection.setState("ESTABLISHED");
            } 
            if (paramContactInfo.shouldCacheConnection()) {
              OutboundConnectionCache outboundConnectionCache = oRB.getTransportManager().getOutboundConnectionCache(paramContactInfo);
              outboundConnectionCache.stampTime(corbaConnection);
              outboundConnectionCache.put(paramContactInfo, corbaConnection);
            } 
          } 
        } 
      } 
      CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramContactInfo.createMessageMediator(oRB, paramContactInfo, corbaConnection, paramString, paramBoolean);
      if (oRB.subcontractDebugFlag)
        dprint(".beginRequest: " + opAndId(corbaMessageMediator) + ": created message mediator: " + corbaMessageMediator); 
      oRB.getInvocationInfo().setMessageMediator(corbaMessageMediator);
      if (corbaConnection != null && corbaConnection.getCodeSetContext() == null)
        performCodeSetNegotiation(corbaMessageMediator); 
      addServiceContexts(corbaMessageMediator);
      OutputObject outputObject = paramContactInfo.createOutputObject(corbaMessageMediator);
      if (oRB.subcontractDebugFlag)
        dprint(".beginRequest: " + opAndId(corbaMessageMediator) + ": created output object: " + outputObject); 
      registerWaiter(corbaMessageMediator);
      synchronized (object) {
        if (paramContactInfo.isConnectionBased() && paramContactInfo.shouldCacheConnection()) {
          OutboundConnectionCache outboundConnectionCache = oRB.getTransportManager().getOutboundConnectionCache(paramContactInfo);
          outboundConnectionCache.reclaim();
        } 
      } 
      oRB.getPIHandler().setClientPIInfo(corbaMessageMediator);
      try {
        oRB.getPIHandler().invokeClientPIStartingPoint();
      } catch (RemarshalException remarshalException) {
        if (oRB.subcontractDebugFlag)
          dprint(".beginRequest: " + opAndId(corbaMessageMediator) + ": Remarshal"); 
        if (getContactInfoListIterator(oRB).hasNext()) {
          paramContactInfo = (ContactInfo)getContactInfoListIterator(oRB).next();
          if (oRB.subcontractDebugFlag)
            dprint("RemarshalException: hasNext true\ncontact info " + paramContactInfo); 
          oRB.getPIHandler().makeCompletedClientRequest(3, null);
          unregisterWaiter(oRB);
          oRB.getPIHandler().cleanupClientPIRequest();
          return beginRequest(paramObject, paramString, paramBoolean, paramContactInfo);
        } 
        if (oRB.subcontractDebugFlag)
          dprint("RemarshalException: hasNext false"); 
        ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(oRB, "rpc.protocol");
        throw oRBUtilSystemException.remarshalWithNowhereToGo();
      } 
      corbaMessageMediator.initializeMessage();
      if (oRB.subcontractDebugFlag)
        dprint(".beginRequest: " + opAndId(corbaMessageMediator) + ": initialized message"); 
      return outputObject;
    } finally {
      if (oRB.subcontractDebugFlag)
        dprint(".beginRequest<-: op/" + paramString); 
    } 
  }
  
  public InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject) throws ApplicationException, RemarshalException {
    oRB = null;
    corbaMessageMediator = null;
    try {
      corbaMessageMediator = (CorbaMessageMediator)paramOutputObject.getMessageMediator();
      oRB = (ORB)corbaMessageMediator.getBroker();
      if (oRB.subcontractDebugFlag)
        dprint(".marshalingComplete->: " + opAndId(corbaMessageMediator)); 
      InputObject inputObject = marshalingComplete1(oRB, corbaMessageMediator);
      return processResponse(oRB, corbaMessageMediator, inputObject);
    } finally {
      if (oRB.subcontractDebugFlag)
        dprint(".marshalingComplete<-: " + opAndId(corbaMessageMediator)); 
    } 
  }
  
  public InputObject marshalingComplete1(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator) throws ApplicationException, RemarshalException {
    try {
      paramCorbaMessageMediator.finishSendingRequest();
      if (paramORB.subcontractDebugFlag)
        dprint(".marshalingComplete: " + opAndId(paramCorbaMessageMediator) + ": finished sending request"); 
      return paramCorbaMessageMediator.waitForResponse();
    } catch (RuntimeException runtimeException) {
      if (paramORB.subcontractDebugFlag)
        dprint(".marshalingComplete: " + opAndId(paramCorbaMessageMediator) + ": exception: " + runtimeException.toString()); 
      boolean bool = getContactInfoListIterator(paramORB).reportException(paramCorbaMessageMediator.getContactInfo(), runtimeException);
      Exception exception = paramORB.getPIHandler().invokeClientPIEndingPoint(2, runtimeException);
      if (bool) {
        if (exception == runtimeException) {
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
        } else {
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception);
        } 
      } else {
        if (exception instanceof RuntimeException)
          throw (RuntimeException)exception; 
        if (exception instanceof RemarshalException)
          throw (RemarshalException)exception; 
        throw runtimeException;
      } 
      return null;
    } 
  }
  
  protected InputObject processResponse(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator, InputObject paramInputObject) throws ApplicationException, RemarshalException {
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    if (paramORB.subcontractDebugFlag)
      dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": response received"); 
    if (paramCorbaMessageMediator.getConnection() != null)
      ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setPostInitialContexts(); 
    Exception exception = null;
    if (paramCorbaMessageMediator.isOneWay()) {
      getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
      exception = paramORB.getPIHandler().invokeClientPIEndingPoint(0, exception);
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception);
      return null;
    } 
    consumeServiceContexts(paramORB, paramCorbaMessageMediator);
    ((CDRInputObject)paramInputObject).performORBVersionSpecificInit();
    if (paramCorbaMessageMediator.isSystemExceptionReply()) {
      SystemException systemException = paramCorbaMessageMediator.getSystemExceptionReply();
      if (paramORB.subcontractDebugFlag)
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received system exception: " + systemException); 
      boolean bool = getContactInfoListIterator(paramORB).reportException(paramCorbaMessageMediator.getContactInfo(), systemException);
      if (bool) {
        exception = paramORB.getPIHandler().invokeClientPIEndingPoint(2, systemException);
        if (systemException == exception) {
          exception = null;
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
          throw oRBUtilSystemException.statementNotReachable1();
        } 
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception);
        throw oRBUtilSystemException.statementNotReachable2();
      } 
      ServiceContexts serviceContexts = paramCorbaMessageMediator.getReplyServiceContexts();
      if (serviceContexts != null) {
        UEInfoServiceContext uEInfoServiceContext = (UEInfoServiceContext)serviceContexts.get(9);
        if (uEInfoServiceContext != null) {
          Throwable throwable = uEInfoServiceContext.getUE();
          UnknownException unknownException = new UnknownException(throwable);
          exception = paramORB.getPIHandler().invokeClientPIEndingPoint(2, unknownException);
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception);
          throw oRBUtilSystemException.statementNotReachable3();
        } 
      } 
      exception = paramORB.getPIHandler().invokeClientPIEndingPoint(2, systemException);
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception);
      throw oRBUtilSystemException.statementNotReachable4();
    } 
    if (paramCorbaMessageMediator.isUserExceptionReply()) {
      if (paramORB.subcontractDebugFlag)
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received user exception"); 
      getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
      String str = peekUserExceptionId(paramInputObject);
      Exception exception1 = null;
      if (paramCorbaMessageMediator.isDIIRequest()) {
        exception = paramCorbaMessageMediator.unmarshalDIIUserException(str, (InputStream)paramInputObject);
        exception1 = paramORB.getPIHandler().invokeClientPIEndingPoint(1, exception);
        paramCorbaMessageMediator.setDIIException(exception1);
      } else {
        ApplicationException applicationException = new ApplicationException(str, (InputStream)paramInputObject);
        exception = applicationException;
        exception1 = paramORB.getPIHandler().invokeClientPIEndingPoint(1, applicationException);
      } 
      if (exception1 != exception)
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception1); 
      if (exception1 instanceof ApplicationException)
        throw (ApplicationException)exception1; 
      return paramInputObject;
    } 
    if (paramCorbaMessageMediator.isLocationForwardReply()) {
      if (paramORB.subcontractDebugFlag)
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received location forward"); 
      getContactInfoListIterator(paramORB).reportRedirect((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo(), paramCorbaMessageMediator.getForwardedIOR());
      Exception exception1 = paramORB.getPIHandler().invokeClientPIEndingPoint(3, null);
      if (!(exception1 instanceof RemarshalException))
        exception = exception1; 
      if (exception != null)
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception); 
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
      throw oRBUtilSystemException.statementNotReachable5();
    } 
    if (paramCorbaMessageMediator.isDifferentAddrDispositionRequestedReply()) {
      if (paramORB.subcontractDebugFlag)
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received different addressing dispostion request"); 
      getContactInfoListIterator(paramORB).reportAddrDispositionRetry((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo(), paramCorbaMessageMediator.getAddrDispositionReply());
      Exception exception1 = paramORB.getPIHandler().invokeClientPIEndingPoint(5, null);
      if (!(exception1 instanceof RemarshalException))
        exception = exception1; 
      if (exception != null)
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception); 
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
      throw oRBUtilSystemException.statementNotReachable6();
    } 
    if (paramORB.subcontractDebugFlag)
      dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received normal response"); 
    getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
    paramCorbaMessageMediator.handleDIIReply((InputStream)paramInputObject);
    exception = paramORB.getPIHandler().invokeClientPIEndingPoint(0, null);
    continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, exception);
    return paramInputObject;
  }
  
  protected void continueOrThrowSystemOrRemarshal(CorbaMessageMediator paramCorbaMessageMediator, Exception paramException) throws SystemException, RemarshalException {
    ORB oRB = (ORB)paramCorbaMessageMediator.getBroker();
    if (paramException == null)
      return; 
    if (paramException instanceof RemarshalException) {
      oRB.getInvocationInfo().setIsRetryInvocation(true);
      unregisterWaiter(oRB);
      if (oRB.subcontractDebugFlag)
        dprint(".continueOrThrowSystemOrRemarshal: " + opAndId(paramCorbaMessageMediator) + ": throwing Remarshal"); 
      throw (RemarshalException)paramException;
    } 
    if (oRB.subcontractDebugFlag)
      dprint(".continueOrThrowSystemOrRemarshal: " + opAndId(paramCorbaMessageMediator) + ": throwing sex:" + paramException); 
    throw (SystemException)paramException;
  }
  
  protected CorbaContactInfoListIterator getContactInfoListIterator(ORB paramORB) { return (CorbaContactInfoListIterator)((CorbaInvocationInfo)paramORB.getInvocationInfo()).getContactInfoListIterator(); }
  
  protected void registerWaiter(CorbaMessageMediator paramCorbaMessageMediator) {
    if (paramCorbaMessageMediator.getConnection() != null)
      paramCorbaMessageMediator.getConnection().registerWaiter(paramCorbaMessageMediator); 
  }
  
  protected void unregisterWaiter(ORB paramORB) {
    MessageMediator messageMediator = paramORB.getInvocationInfo().getMessageMediator();
    if (messageMediator != null && messageMediator.getConnection() != null)
      messageMediator.getConnection().unregisterWaiter(messageMediator); 
  }
  
  protected void addServiceContexts(CorbaMessageMediator paramCorbaMessageMediator) {
    ORB oRB = (ORB)paramCorbaMessageMediator.getBroker();
    CorbaConnection corbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
    GIOPVersion gIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
    ServiceContexts serviceContexts = paramCorbaMessageMediator.getRequestServiceContexts();
    addCodeSetServiceContext(corbaConnection, serviceContexts, gIOPVersion);
    serviceContexts.put(MaxStreamFormatVersionServiceContext.singleton);
    ORBVersionServiceContext oRBVersionServiceContext = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
    serviceContexts.put(oRBVersionServiceContext);
    if (corbaConnection != null && !corbaConnection.isPostInitialContexts()) {
      SendingContextServiceContext sendingContextServiceContext = new SendingContextServiceContext(oRB.getFVDCodeBaseIOR());
      serviceContexts.put(sendingContextServiceContext);
    } 
  }
  
  protected void consumeServiceContexts(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator) {
    ServiceContexts serviceContexts = paramCorbaMessageMediator.getReplyServiceContexts();
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    if (serviceContexts == null)
      return; 
    ServiceContext serviceContext = serviceContexts.get(6);
    if (serviceContext != null) {
      SendingContextServiceContext sendingContextServiceContext = (SendingContextServiceContext)serviceContext;
      IOR iOR = sendingContextServiceContext.getIOR();
      try {
        if (paramCorbaMessageMediator.getConnection() != null)
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeBaseIOR(iOR); 
      } catch (ThreadDeath threadDeath) {
        throw threadDeath;
      } catch (Throwable throwable) {
        throw oRBUtilSystemException.badStringifiedIor(throwable);
      } 
    } 
    serviceContext = serviceContexts.get(1313165056);
    if (serviceContext != null) {
      ORBVersionServiceContext oRBVersionServiceContext = (ORBVersionServiceContext)serviceContext;
      ORBVersion oRBVersion = oRBVersionServiceContext.getVersion();
      paramORB.setORBVersion(oRBVersion);
    } 
    getExceptionDetailMessage(paramCorbaMessageMediator, oRBUtilSystemException);
  }
  
  protected void getExceptionDetailMessage(CorbaMessageMediator paramCorbaMessageMediator, ORBUtilSystemException paramORBUtilSystemException) {
    ServiceContext serviceContext = paramCorbaMessageMediator.getReplyServiceContexts().get(14);
    if (serviceContext == null)
      return; 
    if (!(serviceContext instanceof UnknownServiceContext))
      throw paramORBUtilSystemException.badExceptionDetailMessageServiceContextType(); 
    byte[] arrayOfByte = ((UnknownServiceContext)serviceContext).getData();
    EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream((ORB)paramCorbaMessageMediator.getBroker(), arrayOfByte, arrayOfByte.length);
    encapsInputStream.consumeEndian();
    String str = "----------BEGIN server-side stack trace----------\n" + encapsInputStream.read_wstring() + "\n----------END server-side stack trace----------";
    paramCorbaMessageMediator.setReplyExceptionDetailMessage(str);
  }
  
  public void endRequest(Broker paramBroker, Object paramObject, InputObject paramInputObject) {
    oRB = (ORB)paramBroker;
    try {
      if (oRB.subcontractDebugFlag)
        dprint(".endRequest->"); 
      MessageMediator messageMediator = oRB.getInvocationInfo().getMessageMediator();
      if (messageMediator != null) {
        if (messageMediator.getConnection() != null)
          ((CorbaMessageMediator)messageMediator).sendCancelRequestIfFinalFragmentNotSent(); 
        InputObject inputObject = messageMediator.getInputObject();
        if (inputObject != null)
          inputObject.close(); 
        OutputObject outputObject = messageMediator.getOutputObject();
        if (outputObject != null)
          outputObject.close(); 
      } 
      unregisterWaiter(oRB);
      oRB.getPIHandler().cleanupClientPIRequest();
    } catch (IOException iOException) {
      if (oRB.subcontractDebugFlag)
        dprint(".endRequest: ignoring IOException - " + iOException.toString()); 
    } finally {
      if (oRB.subcontractDebugFlag)
        dprint(".endRequest<-"); 
    } 
  }
  
  protected void performCodeSetNegotiation(CorbaMessageMediator paramCorbaMessageMediator) {
    CorbaConnection corbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
    IOR iOR = ((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo()).getEffectiveTargetIOR();
    GIOPVersion gIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
    if (corbaConnection != null && corbaConnection.getCodeSetContext() == null && !gIOPVersion.equals(GIOPVersion.V1_0))
      synchronized (corbaConnection) {
        if (corbaConnection.getCodeSetContext() != null)
          return; 
        IIOPProfileTemplate iIOPProfileTemplate = (IIOPProfileTemplate)iOR.getProfile().getTaggedProfileTemplate();
        Iterator iterator = iIOPProfileTemplate.iteratorById(1);
        if (!iterator.hasNext())
          return; 
        CodeSetComponentInfo codeSetComponentInfo = ((CodeSetsComponent)iterator.next()).getCodeSetComponentInfo();
        CodeSetComponentInfo.CodeSetContext codeSetContext = CodeSetConversion.impl().negotiate(corbaConnection.getBroker().getORBData().getCodeSetComponentInfo(), codeSetComponentInfo);
        corbaConnection.setCodeSetContext(codeSetContext);
      }  
  }
  
  protected void addCodeSetServiceContext(CorbaConnection paramCorbaConnection, ServiceContexts paramServiceContexts, GIOPVersion paramGIOPVersion) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0) || paramCorbaConnection == null)
      return; 
    CodeSetComponentInfo.CodeSetContext codeSetContext = null;
    if (paramCorbaConnection.getBroker().getORBData().alwaysSendCodeSetServiceContext() || !paramCorbaConnection.isPostInitialContexts())
      codeSetContext = paramCorbaConnection.getCodeSetContext(); 
    if (codeSetContext == null)
      return; 
    CodeSetServiceContext codeSetServiceContext = new CodeSetServiceContext(codeSetContext);
    paramServiceContexts.put(codeSetServiceContext);
  }
  
  protected String peekUserExceptionId(InputObject paramInputObject) {
    CDRInputObject cDRInputObject = (CDRInputObject)paramInputObject;
    cDRInputObject.mark(2147483647);
    String str = cDRInputObject.read_string();
    cDRInputObject.reset();
    return str;
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaClientRequestDispatcherImpl", paramString); }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator) { return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\CorbaClientRequestDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */