package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.IIOPHelper;
import java.io.IOException;
import java.rmi.Remote;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import javax.security.auth.Subject;

public class RMIIIOPServerImpl extends RMIServerImpl {
  private final Map<String, ?> env;
  
  private final AccessControlContext callerACC;
  
  public RMIIIOPServerImpl(Map<String, ?> paramMap) throws IOException {
    super(paramMap);
    this.env = (paramMap == null) ? Collections.emptyMap() : paramMap;
    this.callerACC = AccessController.getContext();
  }
  
  protected void export() throws IOException { IIOPHelper.exportObject(this); }
  
  protected String getProtocol() { return "iiop"; }
  
  public Remote toStub() throws IOException { return IIOPHelper.toStub(this); }
  
  protected RMIConnection makeClient(String paramString, Subject paramSubject) throws IOException {
    if (paramString == null)
      throw new NullPointerException("Null connectionId"); 
    RMIConnectionImpl rMIConnectionImpl = new RMIConnectionImpl(this, paramString, getDefaultClassLoader(), paramSubject, this.env);
    IIOPHelper.exportObject(rMIConnectionImpl);
    return rMIConnectionImpl;
  }
  
  protected void closeClient(RMIConnection paramRMIConnection) throws IOException { IIOPHelper.unexportObject(paramRMIConnection); }
  
  protected void closeServer() throws IOException { IIOPHelper.unexportObject(this); }
  
  RMIConnection doNewClient(final Object credentials) throws IOException {
    if (this.callerACC == null)
      throw new SecurityException("AccessControlContext cannot be null"); 
    try {
      return (RMIConnection)AccessController.doPrivileged(new PrivilegedExceptionAction<RMIConnection>() {
            public RMIConnection run() throws IOException { return RMIIIOPServerImpl.this.superDoNewClient(credentials); }
          },  this.callerACC);
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getCause();
    } 
  }
  
  RMIConnection superDoNewClient(Object paramObject) throws IOException { return super.doNewClient(paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIIIOPServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */