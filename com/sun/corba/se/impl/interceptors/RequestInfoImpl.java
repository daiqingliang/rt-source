package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.legacy.interceptor.RequestInfoExt;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.ServiceContextHelper;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.RequestInfo;
import sun.corba.OutputStreamFactory;
import sun.corba.SharedSecrets;

public abstract class RequestInfoImpl extends LocalObject implements RequestInfo, RequestInfoExt {
  protected ORB myORB;
  
  protected InterceptorsSystemException wrapper;
  
  protected OMGSystemException stdWrapper;
  
  protected int flowStackIndex = 0;
  
  protected int startingPointCall;
  
  protected int intermediatePointCall;
  
  protected int endingPointCall;
  
  protected short replyStatus = -1;
  
  protected static final short UNINITIALIZED = -1;
  
  protected int currentExecutionPoint;
  
  protected static final int EXECUTION_POINT_STARTING = 0;
  
  protected static final int EXECUTION_POINT_INTERMEDIATE = 1;
  
  protected static final int EXECUTION_POINT_ENDING = 2;
  
  protected boolean alreadyExecuted;
  
  protected Connection connection;
  
  protected ServiceContexts serviceContexts;
  
  protected ForwardRequest forwardRequest;
  
  protected IOR forwardRequestIOR;
  
  protected SlotTable slotTable;
  
  protected Exception exception;
  
  protected static final int MID_REQUEST_ID = 0;
  
  protected static final int MID_OPERATION = 1;
  
  protected static final int MID_ARGUMENTS = 2;
  
  protected static final int MID_EXCEPTIONS = 3;
  
  protected static final int MID_CONTEXTS = 4;
  
  protected static final int MID_OPERATION_CONTEXT = 5;
  
  protected static final int MID_RESULT = 6;
  
  protected static final int MID_RESPONSE_EXPECTED = 7;
  
  protected static final int MID_SYNC_SCOPE = 8;
  
  protected static final int MID_REPLY_STATUS = 9;
  
  protected static final int MID_FORWARD_REFERENCE = 10;
  
  protected static final int MID_GET_SLOT = 11;
  
  protected static final int MID_GET_REQUEST_SERVICE_CONTEXT = 12;
  
  protected static final int MID_GET_REPLY_SERVICE_CONTEXT = 13;
  
  protected static final int MID_RI_LAST = 13;
  
  void reset() {
    this.flowStackIndex = 0;
    this.startingPointCall = 0;
    this.intermediatePointCall = 0;
    this.endingPointCall = 0;
    setReplyStatus((short)-1);
    this.currentExecutionPoint = 0;
    this.alreadyExecuted = false;
    this.connection = null;
    this.serviceContexts = null;
    this.forwardRequest = null;
    this.forwardRequestIOR = null;
    this.exception = null;
  }
  
  public RequestInfoImpl(ORB paramORB) {
    this.myORB = paramORB;
    this.wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    this.stdWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    PICurrent pICurrent = (PICurrent)paramORB.getPIHandler().getPICurrent();
    this.slotTable = pICurrent.getSlotTable();
  }
  
  public abstract int request_id();
  
  public abstract String operation();
  
  public abstract Parameter[] arguments();
  
  public abstract TypeCode[] exceptions();
  
  public abstract String[] contexts();
  
  public abstract String[] operation_context();
  
  public abstract Any result();
  
  public abstract boolean response_expected();
  
  public short sync_scope() {
    checkAccess(8);
    return 1;
  }
  
  public short reply_status() {
    checkAccess(9);
    return this.replyStatus;
  }
  
  public abstract Object forward_reference();
  
  public Any get_slot(int paramInt) throws InvalidSlot { return this.slotTable.get_slot(paramInt); }
  
  public abstract ServiceContext get_request_service_context(int paramInt);
  
  public abstract ServiceContext get_reply_service_context(int paramInt);
  
  public Connection connection() { return this.connection; }
  
  private void insertApplicationException(ApplicationException paramApplicationException, Any paramAny) throws UNKNOWN {
    try {
      RepositoryId repositoryId = RepositoryId.cache.getId(paramApplicationException.getId());
      String str1 = repositoryId.getClassName();
      String str2 = str1 + "Helper";
      Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(str2);
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = InputStream.class;
      Method method = clazz.getMethod("read", arrayOfClass);
      inputStream = paramApplicationException.getInputStream();
      inputStream.mark(0);
      UserException userException = null;
      try {
        arrayOfObject = new Object[1];
        arrayOfObject[0] = inputStream;
        userException = (UserException)method.invoke(null, arrayOfObject);
      } finally {
        try {
          inputStream.reset();
        } catch (IOException iOException) {
          throw this.wrapper.markAndResetFailed(iOException);
        } 
      } 
      insertUserException(userException, paramAny);
    } catch (ClassNotFoundException classNotFoundException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, classNotFoundException);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, noSuchMethodException);
    } catch (SecurityException securityException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, securityException);
    } catch (IllegalAccessException illegalAccessException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, illegalAccessException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, illegalArgumentException);
    } catch (InvocationTargetException invocationTargetException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, invocationTargetException);
    } 
  }
  
  private void insertUserException(UserException paramUserException, Any paramAny) throws UNKNOWN {
    try {
      if (paramUserException != null) {
        Class clazz1 = paramUserException.getClass();
        String str1 = clazz1.getName();
        String str2 = str1 + "Helper";
        Class clazz2 = SharedSecrets.getJavaCorbaAccess().loadClass(str2);
        Class[] arrayOfClass = new Class[2];
        arrayOfClass[0] = Any.class;
        arrayOfClass[1] = clazz1;
        Method method = clazz2.getMethod("insert", arrayOfClass);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = paramAny;
        arrayOfObject[1] = paramUserException;
        method.invoke(null, arrayOfObject);
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, classNotFoundException);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, noSuchMethodException);
    } catch (SecurityException securityException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, securityException);
    } catch (IllegalAccessException illegalAccessException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, illegalAccessException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, illegalArgumentException);
    } catch (InvocationTargetException invocationTargetException) {
      throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, invocationTargetException);
    } 
  }
  
  protected Parameter[] nvListToParameterArray(NVList paramNVList) {
    int i = paramNVList.count();
    Parameter[] arrayOfParameter = new Parameter[i];
    try {
      for (byte b = 0; b < i; b++) {
        Parameter parameter = new Parameter();
        arrayOfParameter[b] = parameter;
        NamedValue namedValue = paramNVList.item(b);
        (arrayOfParameter[b]).argument = namedValue.value();
        (arrayOfParameter[b]).mode = ParameterMode.from_int(namedValue.flags() - 1);
      } 
    } catch (Exception exception1) {
      throw this.wrapper.exceptionInArguments(exception1);
    } 
    return arrayOfParameter;
  }
  
  protected Any exceptionToAny(Exception paramException) {
    Any any = this.myORB.create_any();
    if (paramException == null)
      throw this.wrapper.exceptionWasNull2(); 
    if (paramException instanceof SystemException) {
      ORBUtility.insertSystemException((SystemException)paramException, any);
    } else if (paramException instanceof ApplicationException) {
      try {
        ApplicationException applicationException = (ApplicationException)paramException;
        insertApplicationException(applicationException, any);
      } catch (UNKNOWN uNKNOWN) {
        ORBUtility.insertSystemException(uNKNOWN, any);
      } 
    } else if (paramException instanceof UserException) {
      try {
        UserException userException = (UserException)paramException;
        insertUserException(userException, any);
      } catch (UNKNOWN uNKNOWN) {
        ORBUtility.insertSystemException(uNKNOWN, any);
      } 
    } 
    return any;
  }
  
  protected ServiceContext getServiceContext(HashMap paramHashMap, ServiceContexts paramServiceContexts, int paramInt) {
    ServiceContext serviceContext = null;
    Integer integer = new Integer(paramInt);
    serviceContext = (ServiceContext)paramHashMap.get(integer);
    if (serviceContext == null) {
      ServiceContext serviceContext1 = paramServiceContexts.get(paramInt);
      if (serviceContext1 == null)
        throw this.stdWrapper.invalidServiceContextId(); 
      EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.myORB);
      serviceContext1.write(encapsOutputStream, GIOPVersion.V1_2);
      InputStream inputStream = encapsOutputStream.create_input_stream();
      serviceContext = ServiceContextHelper.read(inputStream);
      paramHashMap.put(integer, serviceContext);
    } 
    return serviceContext;
  }
  
  protected void addServiceContext(HashMap paramHashMap, ServiceContexts paramServiceContexts, ServiceContext paramServiceContext, boolean paramBoolean) {
    int i = 0;
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.myORB);
    InputStream inputStream = null;
    UnknownServiceContext unknownServiceContext = null;
    ServiceContextHelper.write(encapsOutputStream, paramServiceContext);
    inputStream = encapsOutputStream.create_input_stream();
    unknownServiceContext = new UnknownServiceContext(inputStream.read_long(), (InputStream)inputStream);
    i = unknownServiceContext.getId();
    if (paramServiceContexts.get(i) != null)
      if (paramBoolean) {
        paramServiceContexts.delete(i);
      } else {
        throw this.stdWrapper.serviceContextAddFailed(new Integer(i));
      }  
    paramServiceContexts.put(unknownServiceContext);
    paramHashMap.put(new Integer(i), paramServiceContext);
  }
  
  protected void setFlowStackIndex(int paramInt) { this.flowStackIndex = paramInt; }
  
  protected int getFlowStackIndex() { return this.flowStackIndex; }
  
  protected void setEndingPointCall(int paramInt) { this.endingPointCall = paramInt; }
  
  protected int getEndingPointCall() { return this.endingPointCall; }
  
  protected void setIntermediatePointCall(int paramInt) { this.intermediatePointCall = paramInt; }
  
  protected int getIntermediatePointCall() { return this.intermediatePointCall; }
  
  protected void setStartingPointCall(int paramInt) { this.startingPointCall = paramInt; }
  
  protected int getStartingPointCall() { return this.startingPointCall; }
  
  protected boolean getAlreadyExecuted() { return this.alreadyExecuted; }
  
  protected void setAlreadyExecuted(boolean paramBoolean) { this.alreadyExecuted = paramBoolean; }
  
  protected void setReplyStatus(short paramShort) { this.replyStatus = paramShort; }
  
  protected short getReplyStatus() { return this.replyStatus; }
  
  protected void setForwardRequest(ForwardRequest paramForwardRequest) {
    this.forwardRequest = paramForwardRequest;
    this.forwardRequestIOR = null;
  }
  
  protected void setForwardRequest(IOR paramIOR) {
    this.forwardRequestIOR = paramIOR;
    this.forwardRequest = null;
  }
  
  protected ForwardRequest getForwardRequestException() {
    if (this.forwardRequest == null && this.forwardRequestIOR != null) {
      Object object = iorToObject(this.forwardRequestIOR);
      this.forwardRequest = new ForwardRequest(object);
    } 
    return this.forwardRequest;
  }
  
  protected IOR getForwardRequestIOR() {
    if (this.forwardRequestIOR == null && this.forwardRequest != null)
      this.forwardRequestIOR = ORBUtility.getIOR(this.forwardRequest.forward); 
    return this.forwardRequestIOR;
  }
  
  protected void setException(Exception paramException) { this.exception = paramException; }
  
  Exception getException() { return this.exception; }
  
  protected void setCurrentExecutionPoint(int paramInt) { this.currentExecutionPoint = paramInt; }
  
  protected abstract void checkAccess(int paramInt);
  
  void setSlotTable(SlotTable paramSlotTable) { this.slotTable = paramSlotTable; }
  
  protected Object iorToObject(IOR paramIOR) { return ORBUtility.makeObjectReference(paramIOR); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\RequestInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */