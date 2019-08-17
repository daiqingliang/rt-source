package sun.management.jmxremote;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import sun.misc.ObjectInputFilter;
import sun.rmi.registry.RegistryImpl;

public class SingleEntryRegistry extends RegistryImpl {
  private final String name;
  
  private final Remote object;
  
  private static final long serialVersionUID = -4897238949499730950L;
  
  SingleEntryRegistry(int paramInt, String paramString, Remote paramRemote) throws RemoteException {
    super(paramInt, null, null, SingleEntryRegistry::singleRegistryFilter);
    this.name = paramString;
    this.object = paramRemote;
  }
  
  SingleEntryRegistry(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory, String paramString, Remote paramRemote) throws RemoteException {
    super(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory, SingleEntryRegistry::singleRegistryFilter);
    this.name = paramString;
    this.object = paramRemote;
  }
  
  public String[] list() { return new String[] { this.name }; }
  
  public Remote lookup(String paramString) throws NotBoundException {
    if (paramString.equals(this.name))
      return this.object; 
    throw new NotBoundException("Not bound: \"" + paramString + "\" (only bound name is \"" + this.name + "\")");
  }
  
  public void bind(String paramString, Remote paramRemote) throws AccessException { throw new AccessException("Cannot modify this registry"); }
  
  public void rebind(String paramString, Remote paramRemote) throws AccessException { throw new AccessException("Cannot modify this registry"); }
  
  public void unbind(String paramString) throws AccessException { throw new AccessException("Cannot modify this registry"); }
  
  private static ObjectInputFilter.Status singleRegistryFilter(ObjectInputFilter.FilterInfo paramFilterInfo) { return (paramFilterInfo.serialClass() != null || paramFilterInfo.depth() > 2L || paramFilterInfo.references() > 4L || paramFilterInfo.arrayLength() >= 0L) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.ALLOWED; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jmxremote\SingleEntryRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */