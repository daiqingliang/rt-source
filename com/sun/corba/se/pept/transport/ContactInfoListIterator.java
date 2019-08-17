package com.sun.corba.se.pept.transport;

import java.util.Iterator;

public interface ContactInfoListIterator extends Iterator {
  ContactInfoList getContactInfoList();
  
  void reportSuccess(ContactInfo paramContactInfo);
  
  boolean reportException(ContactInfo paramContactInfo, RuntimeException paramRuntimeException);
  
  RuntimeException getFailureException();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\ContactInfoListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */