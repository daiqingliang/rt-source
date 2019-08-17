package com.sun.jndi.ldap;

import javax.naming.directory.SearchControls;
import javax.naming.event.NamingListener;

final class NotifierArgs {
  static final int ADDED_MASK = 1;
  
  static final int REMOVED_MASK = 2;
  
  static final int CHANGED_MASK = 4;
  
  static final int RENAMED_MASK = 8;
  
  String name;
  
  String filter;
  
  SearchControls controls;
  
  int mask;
  
  private int sum = -1;
  
  NotifierArgs(String paramString, int paramInt, NamingListener paramNamingListener) {
    this(paramString, "(objectclass=*)", null, paramNamingListener);
    if (paramInt != 1) {
      this.controls = new SearchControls();
      this.controls.setSearchScope(paramInt);
    } 
  }
  
  NotifierArgs(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener) {
    this.name = paramString1;
    this.filter = paramString2;
    this.controls = paramSearchControls;
    if (paramNamingListener instanceof javax.naming.event.NamespaceChangeListener)
      this.mask |= 0xB; 
    if (paramNamingListener instanceof javax.naming.event.ObjectChangeListener)
      this.mask |= 0x4; 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof NotifierArgs) {
      NotifierArgs notifierArgs = (NotifierArgs)paramObject;
      return (this.mask == notifierArgs.mask && this.name.equals(notifierArgs.name) && this.filter.equals(notifierArgs.filter) && checkControls(notifierArgs.controls));
    } 
    return false;
  }
  
  private boolean checkControls(SearchControls paramSearchControls) { return (this.controls == null || paramSearchControls == null) ? ((paramSearchControls == this.controls)) : ((this.controls.getSearchScope() == paramSearchControls.getSearchScope() && this.controls.getTimeLimit() == paramSearchControls.getTimeLimit() && this.controls.getDerefLinkFlag() == paramSearchControls.getDerefLinkFlag() && this.controls.getReturningObjFlag() == paramSearchControls.getReturningObjFlag() && this.controls.getCountLimit() == paramSearchControls.getCountLimit() && checkStringArrays(this.controls.getReturningAttributes(), paramSearchControls.getReturningAttributes()))); }
  
  private static boolean checkStringArrays(String[] paramArrayOfString1, String[] paramArrayOfString2) {
    if (paramArrayOfString1 == null || paramArrayOfString2 == null)
      return (paramArrayOfString1 == paramArrayOfString2); 
    if (paramArrayOfString1.length != paramArrayOfString2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfString1.length; b++) {
      if (!paramArrayOfString1[b].equals(paramArrayOfString2[b]))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    if (this.sum == -1)
      this.sum = this.mask + this.name.hashCode() + this.filter.hashCode() + controlsCode(); 
    return this.sum;
  }
  
  private int controlsCode() {
    if (this.controls == null)
      return 0; 
    int i = this.controls.getTimeLimit() + (int)this.controls.getCountLimit() + (this.controls.getDerefLinkFlag() ? 1 : 0) + (this.controls.getReturningObjFlag() ? 1 : 0);
    String[] arrayOfString = this.controls.getReturningAttributes();
    if (arrayOfString != null)
      for (byte b = 0; b < arrayOfString.length; b++)
        i += arrayOfString[b].hashCode();  
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\NotifierArgs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */