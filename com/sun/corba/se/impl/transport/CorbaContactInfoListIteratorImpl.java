package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class CorbaContactInfoListIteratorImpl implements CorbaContactInfoListIterator {
  protected ORB orb;
  
  protected CorbaContactInfoList contactInfoList;
  
  protected CorbaContactInfo successContactInfo;
  
  protected CorbaContactInfo failureContactInfo;
  
  protected RuntimeException failureException;
  
  protected Iterator effectiveTargetIORIterator;
  
  protected CorbaContactInfo previousContactInfo;
  
  protected boolean isAddrDispositionRetry;
  
  protected IIOPPrimaryToContactInfo primaryToContactInfo;
  
  protected ContactInfo primaryContactInfo;
  
  protected List listOfContactInfos;
  
  public CorbaContactInfoListIteratorImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, ContactInfo paramContactInfo, List paramList) {
    this.orb = paramORB;
    this.contactInfoList = paramCorbaContactInfoList;
    this.primaryContactInfo = paramContactInfo;
    if (paramList != null)
      this.effectiveTargetIORIterator = paramList.iterator(); 
    this.listOfContactInfos = paramList;
    this.previousContactInfo = null;
    this.isAddrDispositionRetry = false;
    this.successContactInfo = null;
    this.failureContactInfo = null;
    this.failureException = null;
    this.primaryToContactInfo = paramORB.getORBData().getIIOPPrimaryToContactInfo();
  }
  
  public boolean hasNext() {
    boolean bool;
    if (this.isAddrDispositionRetry)
      return true; 
    if (this.primaryToContactInfo != null) {
      bool = this.primaryToContactInfo.hasNext(this.primaryContactInfo, this.previousContactInfo, this.listOfContactInfos);
    } else {
      bool = this.effectiveTargetIORIterator.hasNext();
    } 
    return bool;
  }
  
  public Object next() {
    if (this.isAddrDispositionRetry) {
      this.isAddrDispositionRetry = false;
      return this.previousContactInfo;
    } 
    if (this.primaryToContactInfo != null) {
      this.previousContactInfo = (CorbaContactInfo)this.primaryToContactInfo.next(this.primaryContactInfo, this.previousContactInfo, this.listOfContactInfos);
    } else {
      this.previousContactInfo = (CorbaContactInfo)this.effectiveTargetIORIterator.next();
    } 
    return this.previousContactInfo;
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
  
  public ContactInfoList getContactInfoList() { return this.contactInfoList; }
  
  public void reportSuccess(ContactInfo paramContactInfo) { this.successContactInfo = (CorbaContactInfo)paramContactInfo; }
  
  public boolean reportException(ContactInfo paramContactInfo, RuntimeException paramRuntimeException) {
    this.failureContactInfo = (CorbaContactInfo)paramContactInfo;
    this.failureException = paramRuntimeException;
    if (paramRuntimeException instanceof org.omg.CORBA.COMM_FAILURE) {
      SystemException systemException = (SystemException)paramRuntimeException;
      if (systemException.completed == CompletionStatus.COMPLETED_NO) {
        if (hasNext())
          return true; 
        if (this.contactInfoList.getEffectiveTargetIOR() != this.contactInfoList.getTargetIOR()) {
          updateEffectiveTargetIOR(this.contactInfoList.getTargetIOR());
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public RuntimeException getFailureException() { return (this.failureException == null) ? ORBUtilSystemException.get(this.orb, "rpc.transport").invalidContactInfoListIteratorFailureException() : this.failureException; }
  
  public void reportAddrDispositionRetry(CorbaContactInfo paramCorbaContactInfo, short paramShort) {
    this.previousContactInfo.setAddressingDisposition(paramShort);
    this.isAddrDispositionRetry = true;
  }
  
  public void reportRedirect(CorbaContactInfo paramCorbaContactInfo, IOR paramIOR) { updateEffectiveTargetIOR(paramIOR); }
  
  public void updateEffectiveTargetIOR(IOR paramIOR) {
    this.contactInfoList.setEffectiveTargetIOR(paramIOR);
    ((CorbaInvocationInfo)this.orb.getInvocationInfo()).setContactInfoListIterator(this.contactInfoList.iterator());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaContactInfoListIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */