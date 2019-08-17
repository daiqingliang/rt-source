package com.sun.jndi.rmi.registry;

import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

class AtomicNameParser implements NameParser {
  private static final Properties syntax = new Properties();
  
  public Name parse(String paramString) throws NamingException { return new CompoundName(paramString, syntax); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\rmi\registry\AtomicNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */