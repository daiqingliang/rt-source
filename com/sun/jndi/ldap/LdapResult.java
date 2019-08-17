package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.Control;

public final class LdapResult {
  int msgId;
  
  public int status;
  
  String matchedDN;
  
  String errorMessage;
  
  Vector<Vector<String>> referrals = null;
  
  LdapReferralException refEx = null;
  
  Vector<LdapEntry> entries = null;
  
  Vector<Control> resControls = null;
  
  public byte[] serverCreds = null;
  
  String extensionId = null;
  
  byte[] extensionValue = null;
  
  boolean compareToSearchResult(String paramString) {
    LdapEntry ldapEntry;
    BasicAttributes basicAttributes;
    null = false;
    switch (this.status) {
      case 6:
        this.status = 0;
        this.entries = new Vector(1, 1);
        basicAttributes = new BasicAttributes(true);
        ldapEntry = new LdapEntry(paramString, basicAttributes);
        this.entries.addElement(ldapEntry);
        return true;
      case 5:
        this.status = 0;
        this.entries = new Vector(0);
        return true;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */