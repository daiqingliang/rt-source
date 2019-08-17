package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class StubConnectImpl {
  static UtilSystemException wrapper = UtilSystemException.get("rmiiiop");
  
  public static StubIORImpl connect(StubIORImpl paramStubIORImpl, Object paramObject, ObjectImpl paramObjectImpl, ORB paramORB) throws RemoteException {
    Delegate delegate = null;
    try {
      try {
        delegate = StubAdapter.getDelegate(paramObjectImpl);
        if (delegate.orb(paramObjectImpl) != paramORB)
          throw wrapper.connectWrongOrb(); 
      } catch (BAD_OPERATION bAD_OPERATION) {
        if (paramStubIORImpl == null) {
          Tie tie = Utility.getAndForgetTie(paramObject);
          if (tie == null)
            throw wrapper.connectNoTie(); 
          ORB oRB = paramORB;
          try {
            oRB = tie.orb();
          } catch (BAD_OPERATION bAD_OPERATION1) {
            tie.orb(paramORB);
          } catch (BAD_INV_ORDER bAD_INV_ORDER) {
            tie.orb(paramORB);
          } 
          if (oRB != paramORB)
            throw wrapper.connectTieWrongOrb(); 
          delegate = StubAdapter.getDelegate(tie);
          CORBAObjectImpl cORBAObjectImpl = new CORBAObjectImpl();
          cORBAObjectImpl._set_delegate(delegate);
          paramStubIORImpl = new StubIORImpl(cORBAObjectImpl);
        } else {
          delegate = paramStubIORImpl.getDelegate(paramORB);
        } 
        StubAdapter.setDelegate(paramObjectImpl, delegate);
      } 
    } catch (SystemException systemException) {
      throw new RemoteException("CORBA SystemException", systemException);
    } 
    return paramStubIORImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubConnectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */