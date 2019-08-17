package com.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.spi.orb.ORB;

public class OADefault {
  public static ObjectAdapterFactory makePOAFactory(ORB paramORB) {
    POAFactory pOAFactory = new POAFactory();
    pOAFactory.init(paramORB);
    return pOAFactory;
  }
  
  public static ObjectAdapterFactory makeTOAFactory(ORB paramORB) {
    TOAFactory tOAFactory = new TOAFactory();
    tOAFactory.init(paramORB);
    return tOAFactory;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\oa\OADefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */