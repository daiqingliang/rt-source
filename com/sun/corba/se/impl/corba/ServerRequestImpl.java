package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class ServerRequestImpl extends ServerRequest {
  private ORB _orb = null;
  
  private ORBUtilSystemException _wrapper = null;
  
  private String _opName = null;
  
  private NVList _arguments = null;
  
  private Context _ctx = null;
  
  private InputStream _ins = null;
  
  private boolean _paramsCalled = false;
  
  private boolean _resultSet = false;
  
  private boolean _exceptionSet = false;
  
  private Any _resultAny = null;
  
  private Any _exception = null;
  
  public ServerRequestImpl(CorbaMessageMediator paramCorbaMessageMediator, ORB paramORB) {
    this._opName = paramCorbaMessageMediator.getOperationName();
    this._ins = (InputStream)paramCorbaMessageMediator.getInputObject();
    this._ctx = null;
    this._orb = paramORB;
    this._wrapper = ORBUtilSystemException.get(paramORB, "oa.invocation");
  }
  
  public String operation() { return this._opName; }
  
  public void arguments(NVList paramNVList) {
    if (this._paramsCalled)
      throw this._wrapper.argumentsCalledMultiple(); 
    if (this._exceptionSet)
      throw this._wrapper.argumentsCalledAfterException(); 
    if (paramNVList == null)
      throw this._wrapper.argumentsCalledNullArgs(); 
    this._paramsCalled = true;
    NamedValue namedValue = null;
    for (byte b = 0; b < paramNVList.count(); b++) {
      try {
        namedValue = paramNVList.item(b);
      } catch (Bounds bounds) {
        throw this._wrapper.boundsCannotOccur(bounds);
      } 
      try {
        if (namedValue.flags() == 1 || namedValue.flags() == 3)
          namedValue.value().read_value(this._ins, namedValue.value().type()); 
      } catch (Exception exception) {
        throw this._wrapper.badArgumentsNvlist(exception);
      } 
    } 
    this._arguments = paramNVList;
    this._orb.getPIHandler().setServerPIInfo(this._arguments);
    this._orb.getPIHandler().invokeServerPIIntermediatePoint();
  }
  
  public void set_result(Any paramAny) {
    if (!this._paramsCalled)
      throw this._wrapper.argumentsNotCalled(); 
    if (this._resultSet)
      throw this._wrapper.setResultCalledMultiple(); 
    if (this._exceptionSet)
      throw this._wrapper.setResultAfterException(); 
    if (paramAny == null)
      throw this._wrapper.setResultCalledNullArgs(); 
    this._resultAny = paramAny;
    this._resultSet = true;
    this._orb.getPIHandler().setServerPIInfo(this._resultAny);
  }
  
  public void set_exception(Any paramAny) {
    if (paramAny == null)
      throw this._wrapper.setExceptionCalledNullArgs(); 
    TCKind tCKind = paramAny.type().kind();
    if (tCKind != TCKind.tk_except)
      throw this._wrapper.setExceptionCalledBadType(); 
    this._exception = paramAny;
    this._orb.getPIHandler().setServerPIExceptionInfo(this._exception);
    if (!this._exceptionSet && !this._paramsCalled)
      this._orb.getPIHandler().invokeServerPIIntermediatePoint(); 
    this._exceptionSet = true;
  }
  
  public Any checkResultCalled() {
    if (this._paramsCalled && this._resultSet)
      return null; 
    if (this._paramsCalled && !this._resultSet && !this._exceptionSet)
      try {
        TypeCode typeCode = this._orb.get_primitive_tc(TCKind.tk_void);
        this._resultAny = this._orb.create_any();
        this._resultAny.type(typeCode);
        this._resultSet = true;
        return null;
      } catch (Exception exception) {
        throw this._wrapper.dsiResultException(CompletionStatus.COMPLETED_MAYBE, exception);
      }  
    if (this._exceptionSet)
      return this._exception; 
    throw this._wrapper.dsimethodNotcalled(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void marshalReplyParams(OutputStream paramOutputStream) {
    this._resultAny.write_value(paramOutputStream);
    NamedValue namedValue = null;
    for (byte b = 0; b < this._arguments.count(); b++) {
      try {
        namedValue = this._arguments.item(b);
      } catch (Bounds bounds) {}
      if (namedValue.flags() == 2 || namedValue.flags() == 3)
        namedValue.value().write_value(paramOutputStream); 
    } 
  }
  
  public Context ctx() {
    if (!this._paramsCalled || this._resultSet || this._exceptionSet)
      throw this._wrapper.contextCalledOutOfOrder(); 
    throw this._wrapper.contextNotImplemented();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\ServerRequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */