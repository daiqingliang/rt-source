package com.sun.jndi.dns;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

class DnsNameParser implements NameParser {
  public Name parse(String paramString) throws NamingException { return new DnsName(paramString); }
  
  public boolean equals(Object paramObject) { return paramObject instanceof DnsNameParser; }
  
  public int hashCode() { return DnsNameParser.class.hashCode() + 1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\DnsNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */