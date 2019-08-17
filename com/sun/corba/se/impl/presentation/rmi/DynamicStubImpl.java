package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class DynamicStubImpl extends ObjectImpl implements DynamicStub, Serializable {
  private static final long serialVersionUID = 4852612040012087675L;
  
  private String[] typeIds;
  
  private StubIORImpl ior;
  
  private DynamicStub self = null;
  
  public void setSelf(DynamicStub paramDynamicStub) { this.self = paramDynamicStub; }
  
  public DynamicStub getSelf() { return this.self; }
  
  public DynamicStubImpl(String[] paramArrayOfString) {
    this.typeIds = paramArrayOfString;
    this.ior = null;
  }
  
  public void setDelegate(Delegate paramDelegate) { _set_delegate(paramDelegate); }
  
  public Delegate getDelegate() { return _get_delegate(); }
  
  public ORB getORB() { return _orb(); }
  
  public String[] _ids() { return this.typeIds; }
  
  public String[] getTypeIds() { return _ids(); }
  
  public void connect(ORB paramORB) throws RemoteException { this.ior = StubConnectImpl.connect(this.ior, this.self, this, paramORB); }
  
  public boolean isLocal() { return _is_local(); }
  
  public OutputStream request(String paramString, boolean paramBoolean) { return _request(paramString, paramBoolean); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.ior = new StubIORImpl();
    this.ior.doRead(paramObjectInputStream);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.ior == null)
      this.ior = new StubIORImpl(this); 
    this.ior.doWrite(paramObjectOutputStream);
  }
  
  public Object readResolve() {
    String str1 = this.ior.getRepositoryId();
    String str2 = RepositoryId.cache.getId(str1).getClassName();
    Class clazz = null;
    try {
      clazz = JDKBridge.loadClass(str2, null, null);
    } catch (ClassNotFoundException classNotFoundException) {}
    PresentationManager presentationManager = ORB.getPresentationManager();
    PresentationManager.ClassData classData = presentationManager.getClassData(clazz);
    InvocationHandlerFactoryImpl invocationHandlerFactoryImpl = (InvocationHandlerFactoryImpl)classData.getInvocationHandlerFactory();
    return invocationHandlerFactoryImpl.getInvocationHandler(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\DynamicStubImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */