package com.sun.jndi.ldap;

import java.io.IOException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.SortResponseControl;

public class DefaultResponseControlFactory extends ControlFactory {
  public Control getControlInstance(Control paramControl) throws NamingException {
    String str = paramControl.getID();
    try {
      if (str.equals("1.2.840.113556.1.4.474"))
        return new SortResponseControl(str, paramControl.isCritical(), paramControl.getEncodedValue()); 
      if (str.equals("1.2.840.113556.1.4.319"))
        return new PagedResultsResponseControl(str, paramControl.isCritical(), paramControl.getEncodedValue()); 
      if (str.equals("2.16.840.1.113730.3.4.7"))
        return new EntryChangeResponseControl(str, paramControl.isCritical(), paramControl.getEncodedValue()); 
    } catch (IOException iOException) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(iOException);
      throw namingException;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\DefaultResponseControlFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */