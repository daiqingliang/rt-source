package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.orb.ORBVersion;

public interface IIOPProfile extends TaggedProfile {
  ORBVersion getORBVersion();
  
  Object getServant();
  
  GIOPVersion getGIOPVersion();
  
  String getCodebase();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\iiop\IIOPProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */