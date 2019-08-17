package com.sun.jndi.ldap;

import javax.naming.Binding;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

class BindingWithControls extends Binding implements HasControls {
  private Control[] controls;
  
  private static final long serialVersionUID = 9117274533692320040L;
  
  public BindingWithControls(String paramString, Object paramObject, Control[] paramArrayOfControl) {
    super(paramString, paramObject);
    this.controls = paramArrayOfControl;
  }
  
  public Control[] getControls() throws NamingException { return this.controls; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\BindingWithControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */