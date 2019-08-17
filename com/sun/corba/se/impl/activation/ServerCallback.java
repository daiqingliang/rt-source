package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation._ServerImplBase;
import java.lang.reflect.Method;
import org.omg.CORBA.ORB;

class ServerCallback extends _ServerImplBase {
  private ORB orb;
  
  private Method installMethod;
  
  private Method uninstallMethod;
  
  private Method shutdownMethod;
  
  private Object[] methodArgs;
  
  ServerCallback(ORB paramORB, Method paramMethod1, Method paramMethod2, Method paramMethod3) {
    this.orb = paramORB;
    this.installMethod = paramMethod1;
    this.uninstallMethod = paramMethod2;
    this.shutdownMethod = paramMethod3;
    paramORB.connect(this);
    this.methodArgs = new Object[] { paramORB };
  }
  
  private void invokeMethod(Method paramMethod) {
    if (paramMethod != null)
      try {
        paramMethod.invoke(null, this.methodArgs);
      } catch (Exception exception) {
        ServerMain.logError("could not invoke " + paramMethod.getName() + " method: " + exception.getMessage());
      }  
  }
  
  public void shutdown() {
    ServerMain.logInformation("Shutdown starting");
    invokeMethod(this.shutdownMethod);
    this.orb.shutdown(true);
    ServerMain.logTerminal("Shutdown completed", 0);
  }
  
  public void install() {
    ServerMain.logInformation("Install starting");
    invokeMethod(this.installMethod);
    ServerMain.logInformation("Install completed");
  }
  
  public void uninstall() {
    ServerMain.logInformation("uninstall starting");
    invokeMethod(this.uninstallMethod);
    ServerMain.logInformation("uninstall completed");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ServerCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */