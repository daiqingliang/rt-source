package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

final class LdapEntry {
  String DN;
  
  Attributes attributes;
  
  Vector<Control> respCtls = null;
  
  LdapEntry(String paramString, Attributes paramAttributes) {
    this.DN = paramString;
    this.attributes = paramAttributes;
  }
  
  LdapEntry(String paramString, Attributes paramAttributes, Vector<Control> paramVector) {
    this.DN = paramString;
    this.attributes = paramAttributes;
    this.respCtls = paramVector;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */