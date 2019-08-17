package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.ContactInfoList;

public interface ClientDelegate {
  Broker getBroker();
  
  ContactInfoList getContactInfoList();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\protocol\ClientDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */