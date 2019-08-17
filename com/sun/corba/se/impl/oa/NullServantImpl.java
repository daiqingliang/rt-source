package com.sun.corba.se.impl.oa;

import com.sun.corba.se.spi.oa.NullServant;
import org.omg.CORBA.SystemException;

public class NullServantImpl implements NullServant {
  private SystemException sysex;
  
  public NullServantImpl(SystemException paramSystemException) { this.sysex = paramSystemException; }
  
  public SystemException getException() { return this.sysex; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\NullServantImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */