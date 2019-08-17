package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.BootstrapServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.CorbaClientRequestDispatcherImpl;
import com.sun.corba.se.impl.protocol.CorbaServerRequestDispatcherImpl;
import com.sun.corba.se.impl.protocol.FullServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.INSServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.InfoOnlyServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.impl.protocol.MinimalServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.POALocalCRDImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public final class RequestDispatcherDefault {
  public static ClientRequestDispatcher makeClientRequestDispatcher() { return new CorbaClientRequestDispatcherImpl(); }
  
  public static CorbaServerRequestDispatcher makeServerRequestDispatcher(ORB paramORB) { return new CorbaServerRequestDispatcherImpl(paramORB); }
  
  public static CorbaServerRequestDispatcher makeBootstrapServerRequestDispatcher(ORB paramORB) { return new BootstrapServerRequestDispatcher(paramORB); }
  
  public static CorbaServerRequestDispatcher makeINSServerRequestDispatcher(ORB paramORB) { return new INSServerRequestDispatcher(paramORB); }
  
  public static LocalClientRequestDispatcherFactory makeMinimalServantCacheLocalClientRequestDispatcherFactory(final ORB orb) { return new LocalClientRequestDispatcherFactory() {
        public LocalClientRequestDispatcher create(int param1Int, IOR param1IOR) { return new MinimalServantCacheLocalCRDImpl(orb, param1Int, param1IOR); }
      }; }
  
  public static LocalClientRequestDispatcherFactory makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(final ORB orb) { return new LocalClientRequestDispatcherFactory() {
        public LocalClientRequestDispatcher create(int param1Int, IOR param1IOR) { return new InfoOnlyServantCacheLocalCRDImpl(orb, param1Int, param1IOR); }
      }; }
  
  public static LocalClientRequestDispatcherFactory makeFullServantCacheLocalClientRequestDispatcherFactory(final ORB orb) { return new LocalClientRequestDispatcherFactory() {
        public LocalClientRequestDispatcher create(int param1Int, IOR param1IOR) { return new FullServantCacheLocalCRDImpl(orb, param1Int, param1IOR); }
      }; }
  
  public static LocalClientRequestDispatcherFactory makeJIDLLocalClientRequestDispatcherFactory(final ORB orb) { return new LocalClientRequestDispatcherFactory() {
        public LocalClientRequestDispatcher create(int param1Int, IOR param1IOR) { return new JIDLLocalCRDImpl(orb, param1Int, param1IOR); }
      }; }
  
  public static LocalClientRequestDispatcherFactory makePOALocalClientRequestDispatcherFactory(final ORB orb) { return new LocalClientRequestDispatcherFactory() {
        public LocalClientRequestDispatcher create(int param1Int, IOR param1IOR) { return new POALocalCRDImpl(orb, param1Int, param1IOR); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\protocol\RequestDispatcherDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */