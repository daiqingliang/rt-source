package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ContactInfo;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;

public interface ClientRequestDispatcher {
  OutputObject beginRequest(Object paramObject, String paramString, boolean paramBoolean, ContactInfo paramContactInfo);
  
  InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject) throws ApplicationException, RemarshalException;
  
  void endRequest(Broker paramBroker, Object paramObject, InputObject paramInputObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\protocol\ClientRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */