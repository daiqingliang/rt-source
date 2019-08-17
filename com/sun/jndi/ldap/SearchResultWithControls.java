package com.sun.jndi.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

class SearchResultWithControls extends SearchResult implements HasControls {
  private Control[] controls;
  
  private static final long serialVersionUID = 8476983938747908202L;
  
  public SearchResultWithControls(String paramString, Object paramObject, Attributes paramAttributes, boolean paramBoolean, Control[] paramArrayOfControl) {
    super(paramString, paramObject, paramAttributes, paramBoolean);
    this.controls = paramArrayOfControl;
  }
  
  public Control[] getControls() throws NamingException { return this.controls; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\SearchResultWithControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */