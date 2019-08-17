package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.transport.CorbaContactInfoListIteratorImpl;
import com.sun.corba.se.impl.transport.SharedCDRContactInfoImpl;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class SocketFactoryContactInfoListIteratorImpl extends CorbaContactInfoListIteratorImpl {
  private SocketInfo socketInfoCookie;
  
  public SocketFactoryContactInfoListIteratorImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList) { super(paramORB, paramCorbaContactInfoList, null, null); }
  
  public boolean hasNext() { return true; }
  
  public Object next() { return this.contactInfoList.getEffectiveTargetIOR().getProfile().isLocal() ? new SharedCDRContactInfoImpl(this.orb, this.contactInfoList, this.contactInfoList.getEffectiveTargetIOR(), this.orb.getORBData().getGIOPAddressDisposition()) : new SocketFactoryContactInfoImpl(this.orb, this.contactInfoList, this.contactInfoList.getEffectiveTargetIOR(), this.orb.getORBData().getGIOPAddressDisposition(), this.socketInfoCookie); }
  
  public boolean reportException(ContactInfo paramContactInfo, RuntimeException paramRuntimeException) {
    this.failureContactInfo = (CorbaContactInfo)paramContactInfo;
    this.failureException = paramRuntimeException;
    if (paramRuntimeException instanceof org.omg.CORBA.COMM_FAILURE) {
      if (paramRuntimeException.getCause() instanceof GetEndPointInfoAgainException) {
        this.socketInfoCookie = ((GetEndPointInfoAgainException)paramRuntimeException.getCause()).getEndPointInfo();
        return true;
      } 
      SystemException systemException = (SystemException)paramRuntimeException;
      if (systemException.completed == CompletionStatus.COMPLETED_NO && this.contactInfoList.getEffectiveTargetIOR() != this.contactInfoList.getTargetIOR()) {
        this.contactInfoList.setEffectiveTargetIOR(this.contactInfoList.getTargetIOR());
        return true;
      } 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryContactInfoListIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */