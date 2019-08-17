package com.sun.corba.se.impl.naming.namingutil;

public class INSURLHandler {
  private static INSURLHandler insURLHandler = null;
  
  private static final int CORBALOC_PREFIX_LENGTH = 9;
  
  private static final int CORBANAME_PREFIX_LENGTH = 10;
  
  public static INSURLHandler getINSURLHandler() {
    if (insURLHandler == null)
      insURLHandler = new INSURLHandler(); 
    return insURLHandler;
  }
  
  public INSURL parseURL(String paramString) {
    String str = paramString;
    return (str.startsWith("corbaloc:") == true) ? new CorbalocURL(str.substring(9)) : ((str.startsWith("corbaname:") == true) ? new CorbanameURL(str.substring(10)) : null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\namingutil\INSURLHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */