package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import sun.rmi.server.Util;
import sun.rmi.transport.ObjectTable;

public abstract class RemoteObject implements Remote, Serializable {
  protected RemoteRef ref = null;
  
  private static final long serialVersionUID = -3215090123894869218L;
  
  protected RemoteObject() {}
  
  protected RemoteObject(RemoteRef paramRemoteRef) {}
  
  public RemoteRef getRef() { return this.ref; }
  
  public static Remote toStub(Remote paramRemote) throws NoSuchObjectException { return (paramRemote instanceof RemoteStub || (paramRemote != null && Proxy.isProxyClass(paramRemote.getClass()) && Proxy.getInvocationHandler(paramRemote) instanceof RemoteObjectInvocationHandler)) ? paramRemote : ObjectTable.getStub(paramRemote); }
  
  public int hashCode() { return (this.ref == null) ? super.hashCode() : this.ref.remoteHashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof RemoteObject) ? ((this.ref == null) ? ((paramObject == this)) : this.ref.remoteEquals(((RemoteObject)paramObject).ref)) : ((paramObject != null) ? paramObject.equals(this) : 0); }
  
  public String toString() {
    String str = Util.getUnqualifiedName(getClass());
    return (this.ref == null) ? str : (str + "[" + this.ref.remoteToString() + "]");
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    if (this.ref == null)
      throw new MarshalException("Invalid remote object"); 
    String str = this.ref.getRefClass(paramObjectOutputStream);
    if (str == null || str.length() == 0) {
      paramObjectOutputStream.writeUTF("");
      paramObjectOutputStream.writeObject(this.ref);
    } else {
      paramObjectOutputStream.writeUTF(str);
      this.ref.writeExternal(paramObjectOutputStream);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    String str = paramObjectInputStream.readUTF();
    if (str == null || str.length() == 0) {
      this.ref = (RemoteRef)paramObjectInputStream.readObject();
    } else {
      String str1 = "sun.rmi.server." + str;
      Class clazz = Class.forName(str1);
      try {
        this.ref = (RemoteRef)clazz.newInstance();
      } catch (InstantiationException instantiationException) {
        throw new ClassNotFoundException(str1, instantiationException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new ClassNotFoundException(str1, illegalAccessException);
      } catch (ClassCastException classCastException) {
        throw new ClassNotFoundException(str1, classCastException);
      } 
      this.ref.readExternal(paramObjectInputStream);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\RemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */