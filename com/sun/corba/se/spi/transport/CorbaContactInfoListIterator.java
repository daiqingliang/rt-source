package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfoListIterator;
import com.sun.corba.se.spi.ior.IOR;

public interface CorbaContactInfoListIterator extends ContactInfoListIterator {
  void reportAddrDispositionRetry(CorbaContactInfo paramCorbaContactInfo, short paramShort);
  
  void reportRedirect(CorbaContactInfo paramCorbaContactInfo, IOR paramIOR);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\transport\CorbaContactInfoListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */