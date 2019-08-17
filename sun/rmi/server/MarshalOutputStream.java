package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.rmi.transport.ObjectTable;
import sun.rmi.transport.Target;

public class MarshalOutputStream extends ObjectOutputStream {
  public MarshalOutputStream(OutputStream paramOutputStream) throws IOException { this(paramOutputStream, 1); }
  
  public MarshalOutputStream(OutputStream paramOutputStream, int paramInt) throws IOException {
    super(paramOutputStream);
    useProtocolVersion(paramInt);
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            MarshalOutputStream.this.enableReplaceObject(true);
            return null;
          }
        });
  }
  
  protected final Object replaceObject(Object paramObject) throws IOException {
    if (paramObject instanceof Remote && !(paramObject instanceof java.rmi.server.RemoteStub)) {
      Target target = ObjectTable.getTarget((Remote)paramObject);
      if (target != null)
        return target.getStub(); 
    } 
    return paramObject;
  }
  
  protected void annotateClass(Class<?> paramClass) throws IOException { writeLocation(RMIClassLoader.getClassAnnotation(paramClass)); }
  
  protected void annotateProxyClass(Class<?> paramClass) throws IOException { annotateClass(paramClass); }
  
  protected void writeLocation(String paramString) throws IOException { writeObject(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\MarshalOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */