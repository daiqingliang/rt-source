package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.RMIExporter;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.server.DeserializationChecker;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;

public class RMIJRMPServerImpl extends RMIServerImpl {
  private final ExportedWrapper exportedWrapper;
  
  private final int port;
  
  private final RMIClientSocketFactory csf;
  
  private final RMIServerSocketFactory ssf;
  
  private final Map<String, ?> env;
  
  public RMIJRMPServerImpl(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory, Map<String, ?> paramMap) throws IOException {
    super(paramMap);
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative port: " + paramInt); 
    this.port = paramInt;
    this.csf = paramRMIClientSocketFactory;
    this.ssf = paramRMIServerSocketFactory;
    this.env = (paramMap == null) ? Collections.emptyMap() : paramMap;
    String[] arrayOfString = (String[])this.env.get("jmx.remote.rmi.server.credential.types");
    ArrayList arrayList = null;
    if (arrayOfString != null) {
      arrayList = new ArrayList();
      for (String str : arrayOfString) {
        if (str == null)
          throw new IllegalArgumentException("A credential type is null."); 
        ReflectUtil.checkPackageAccess(str);
        arrayList.add(str);
      } 
    } 
    this.exportedWrapper = (arrayList != null) ? new ExportedWrapper(this, arrayList, null) : null;
  }
  
  protected void export() throws IOException {
    if (this.exportedWrapper != null) {
      export(this.exportedWrapper);
    } else {
      export(this);
    } 
  }
  
  private void export(Remote paramRemote) throws RemoteException {
    RMIExporter rMIExporter = (RMIExporter)this.env.get("com.sun.jmx.remote.rmi.exporter");
    boolean bool = EnvHelp.isServerDaemon(this.env);
    if (bool && rMIExporter != null)
      throw new IllegalArgumentException("If jmx.remote.x.daemon is specified as true, com.sun.jmx.remote.rmi.exporter cannot be used to specify an exporter!"); 
    if (bool) {
      if (this.csf == null && this.ssf == null) {
        (new UnicastServerRef(this.port)).exportObject(paramRemote, null, true);
      } else {
        (new UnicastServerRef2(this.port, this.csf, this.ssf)).exportObject(paramRemote, null, true);
      } 
    } else if (rMIExporter != null) {
      rMIExporter.exportObject(paramRemote, this.port, this.csf, this.ssf);
    } else {
      UnicastRemoteObject.exportObject(paramRemote, this.port, this.csf, this.ssf);
    } 
  }
  
  private void unexport(Remote paramRemote, boolean paramBoolean) throws NoSuchObjectException {
    RMIExporter rMIExporter = (RMIExporter)this.env.get("com.sun.jmx.remote.rmi.exporter");
    if (rMIExporter == null) {
      UnicastRemoteObject.unexportObject(paramRemote, paramBoolean);
    } else {
      rMIExporter.unexportObject(paramRemote, paramBoolean);
    } 
  }
  
  protected String getProtocol() { return "rmi"; }
  
  public Remote toStub() throws IOException { return (this.exportedWrapper != null) ? RemoteObject.toStub(this.exportedWrapper) : RemoteObject.toStub(this); }
  
  protected RMIConnection makeClient(String paramString, Subject paramSubject) throws IOException {
    if (paramString == null)
      throw new NullPointerException("Null connectionId"); 
    RMIConnectionImpl rMIConnectionImpl = new RMIConnectionImpl(this, paramString, getDefaultClassLoader(), paramSubject, this.env);
    export(rMIConnectionImpl);
    return rMIConnectionImpl;
  }
  
  protected void closeClient(RMIConnection paramRMIConnection) throws IOException { unexport(paramRMIConnection, true); }
  
  protected void closeServer() throws IOException {
    if (this.exportedWrapper != null) {
      unexport(this.exportedWrapper, true);
    } else {
      unexport(this, true);
    } 
  }
  
  private static class ExportedWrapper implements RMIServer, DeserializationChecker {
    private final RMIServer impl;
    
    private final List<String> allowedTypes;
    
    private ExportedWrapper(RMIServer param1RMIServer, List<String> param1List) {
      this.impl = param1RMIServer;
      this.allowedTypes = param1List;
    }
    
    public String getVersion() { return this.impl.getVersion(); }
    
    public RMIConnection newClient(Object param1Object) throws IOException { return this.impl.newClient(param1Object); }
    
    public void check(Method param1Method, ObjectStreamClass param1ObjectStreamClass, int param1Int1, int param1Int2) {
      String str = param1ObjectStreamClass.getName();
      if (!this.allowedTypes.contains(str))
        throw new ClassCastException("Unsupported type: " + str); 
    }
    
    public void checkProxyClass(Method param1Method, String[] param1ArrayOfString, int param1Int1, int param1Int2) {
      if (param1ArrayOfString != null && param1ArrayOfString.length > 0)
        for (String str : param1ArrayOfString) {
          if (!this.allowedTypes.contains(str))
            throw new ClassCastException("Unsupported type: " + str); 
        }  
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIJRMPServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */