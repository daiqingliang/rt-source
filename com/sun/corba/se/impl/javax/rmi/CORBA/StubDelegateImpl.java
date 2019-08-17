package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.StubConnectImpl;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.StubDelegate;
import org.omg.CORBA.ORB;

public class StubDelegateImpl implements StubDelegate {
  static UtilSystemException wrapper = UtilSystemException.get("rmiiiop");
  
  private StubIORImpl ior = null;
  
  public StubIORImpl getIOR() { return this.ior; }
  
  private void init(Stub paramStub) {
    if (this.ior == null)
      this.ior = new StubIORImpl(paramStub); 
  }
  
  public int hashCode(Stub paramStub) {
    init(paramStub);
    return this.ior.hashCode();
  }
  
  public boolean equals(Stub paramStub, Object paramObject) {
    if (paramStub == paramObject)
      return true; 
    if (!(paramObject instanceof Stub))
      return false; 
    Stub stub = (Stub)paramObject;
    return (stub.hashCode() != paramStub.hashCode()) ? false : paramStub.toString().equals(stub.toString());
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof StubDelegateImpl))
      return false; 
    StubDelegateImpl stubDelegateImpl = (StubDelegateImpl)paramObject;
    return (this.ior == null) ? ((this.ior == stubDelegateImpl.ior)) : this.ior.equals(stubDelegateImpl.ior);
  }
  
  public int hashCode() { return (this.ior == null) ? 0 : this.ior.hashCode(); }
  
  public String toString(Stub paramStub) { return (this.ior == null) ? null : this.ior.toString(); }
  
  public void connect(Stub paramStub, ORB paramORB) throws RemoteException { this.ior = StubConnectImpl.connect(this.ior, paramStub, paramStub, paramORB); }
  
  public void readObject(Stub paramStub, ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (this.ior == null)
      this.ior = new StubIORImpl(); 
    this.ior.doRead(paramObjectInputStream);
  }
  
  public void writeObject(Stub paramStub, ObjectOutputStream paramObjectOutputStream) throws IOException {
    init(paramStub);
    this.ior.doWrite(paramObjectOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\javax\rmi\CORBA\StubDelegateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */