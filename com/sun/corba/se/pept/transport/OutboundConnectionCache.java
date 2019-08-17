package com.sun.corba.se.pept.transport;

public interface OutboundConnectionCache extends ConnectionCache {
  Connection get(ContactInfo paramContactInfo);
  
  void put(ContactInfo paramContactInfo, Connection paramConnection);
  
  void remove(ContactInfo paramContactInfo);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\OutboundConnectionCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */