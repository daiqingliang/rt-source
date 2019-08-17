package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class RequestImpl extends Request {
  protected Object _target;
  
  protected String _opName;
  
  protected NVList _arguments;
  
  protected ExceptionList _exceptions;
  
  private NamedValue _result;
  
  protected Environment _env;
  
  private Context _ctx;
  
  private ContextList _ctxList;
  
  protected ORB _orb;
  
  private ORBUtilSystemException _wrapper;
  
  protected boolean _isOneWay = false;
  
  private int[] _paramCodes;
  
  private long[] _paramLongs;
  
  private Object[] _paramObjects;
  
  protected boolean gotResponse = false;
  
  public RequestImpl(ORB paramORB, Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList) {
    this._orb = paramORB;
    this._wrapper = ORBUtilSystemException.get(paramORB, "oa.invocation");
    this._target = paramObject;
    this._ctx = paramContext;
    this._opName = paramString;
    if (paramNVList == null) {
      this._arguments = new NVListImpl(this._orb);
    } else {
      this._arguments = paramNVList;
    } 
    this._result = paramNamedValue;
    if (paramExceptionList == null) {
      this._exceptions = new ExceptionListImpl();
    } else {
      this._exceptions = paramExceptionList;
    } 
    if (paramContextList == null) {
      this._ctxList = new ContextListImpl(this._orb);
    } else {
      this._ctxList = paramContextList;
    } 
    this._env = new EnvironmentImpl();
  }
  
  public Object target() { return this._target; }
  
  public String operation() { return this._opName; }
  
  public NVList arguments() { return this._arguments; }
  
  public NamedValue result() { return this._result; }
  
  public Environment env() { return this._env; }
  
  public ExceptionList exceptions() { return this._exceptions; }
  
  public ContextList contexts() { return this._ctxList; }
  
  public Context ctx() {
    if (this._ctx == null)
      this._ctx = new ContextImpl(this._orb); 
    return this._ctx;
  }
  
  public void ctx(Context paramContext) { this._ctx = paramContext; }
  
  public Any add_in_arg() { return this._arguments.add(1).value(); }
  
  public Any add_named_in_arg(String paramString) { return this._arguments.add_item(paramString, 1).value(); }
  
  public Any add_inout_arg() { return this._arguments.add(3).value(); }
  
  public Any add_named_inout_arg(String paramString) { return this._arguments.add_item(paramString, 3).value(); }
  
  public Any add_out_arg() { return this._arguments.add(2).value(); }
  
  public Any add_named_out_arg(String paramString) { return this._arguments.add_item(paramString, 2).value(); }
  
  public void set_return_type(TypeCode paramTypeCode) {
    if (this._result == null)
      this._result = new NamedValueImpl(this._orb); 
    this._result.value().type(paramTypeCode);
  }
  
  public Any return_value() {
    if (this._result == null)
      this._result = new NamedValueImpl(this._orb); 
    return this._result.value();
  }
  
  public void add_exception(TypeCode paramTypeCode) { this._exceptions.add(paramTypeCode); }
  
  public void invoke() { doInvocation(); }
  
  public void send_oneway() {
    this._isOneWay = true;
    doInvocation();
  }
  
  public void send_deferred() {
    AsynchInvoke asynchInvoke = new AsynchInvoke(this._orb, this, false);
    (new Thread(asynchInvoke)).start();
  }
  
  public boolean poll_response() { return this.gotResponse; }
  
  public void get_response() {
    while (!this.gotResponse) {
      try {
        wait();
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  protected void doInvocation() {
    delegate = StubAdapter.getDelegate(this._target);
    this._orb.getPIHandler().initiateClientPIRequest(true);
    this._orb.getPIHandler().setClientPIInfo(this);
    inputStream = null;
    try {
      OutputStream outputStream = delegate.request(null, this._opName, !this._isOneWay);
      try {
        for (byte b = 0; b < this._arguments.count(); b++) {
          NamedValue namedValue = this._arguments.item(b);
          switch (namedValue.flags()) {
            case 1:
              namedValue.value().write_value(outputStream);
              break;
            case 3:
              namedValue.value().write_value(outputStream);
              break;
          } 
        } 
      } catch (Bounds bounds) {
        throw this._wrapper.boundsErrorInDiiRequest(bounds);
      } 
      inputStream = delegate.invoke(null, outputStream);
    } catch (ApplicationException applicationException) {
    
    } catch (RemarshalException remarshalException) {
      doInvocation();
    } catch (SystemException systemException) {
      this._env.exception(systemException);
      throw systemException;
    } finally {
      delegate.releaseReply(null, inputStream);
    } 
  }
  
  public void unmarshalReply(InputStream paramInputStream) {
    if (this._result != null) {
      Any any = this._result.value();
      TypeCode typeCode = any.type();
      if (typeCode.kind().value() != 1)
        any.read_value(paramInputStream, typeCode); 
    } 
    try {
      for (byte b = 0; b < this._arguments.count(); b++) {
        Any any;
        NamedValue namedValue = this._arguments.item(b);
        switch (namedValue.flags()) {
          case 2:
          case 3:
            any = namedValue.value();
            any.read_value(paramInputStream, any.type());
            break;
        } 
      } 
    } catch (Bounds bounds) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\RequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */