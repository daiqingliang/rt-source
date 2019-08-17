package com.sun.jndi.cosnaming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class CNCtxFactory implements InitialContextFactory {
  public Context getInitialContext(Hashtable<?, ?> paramHashtable) throws NamingException { return new CNCtx(paramHashtable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\CNCtxFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */