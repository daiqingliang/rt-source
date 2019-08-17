package com.sun.jndi.ldap;

import javax.naming.NamingEnumeration;

interface ReferralEnumeration<T> extends NamingEnumeration<T> {
  void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\ReferralEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */