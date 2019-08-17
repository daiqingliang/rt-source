package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.ArrayList;
import org.omg.CORBA.BAD_PARAM;

public class CorbanameURL extends INSURLBase {
  private static NamingSystemException wrapper = NamingSystemException.get("naming");
  
  public CorbanameURL(String paramString) {
    String str1 = paramString;
    try {
      str1 = Utility.cleanEscapes(str1);
    } catch (Exception exception) {
      badAddress(exception);
    } 
    int i = str1.indexOf('#');
    String str2 = null;
    if (i != -1) {
      str2 = "corbaloc:" + str1.substring(0, i) + "/";
    } else {
      str2 = "corbaloc:" + str1.substring(0, str1.length());
      if (str2.endsWith("/") != true)
        str2 = str2 + "/"; 
    } 
    try {
      INSURL iNSURL = INSURLHandler.getINSURLHandler().parseURL(str2);
      copyINSURL(iNSURL);
      if (i > -1 && i < paramString.length() - 1) {
        int j = i + 1;
        String str = str1.substring(j);
        this.theStringifiedName = str;
      } 
    } catch (Exception exception) {
      badAddress(exception);
    } 
  }
  
  private void badAddress(Throwable paramThrowable) throws BAD_PARAM { throw wrapper.insBadAddress(paramThrowable); }
  
  private void copyINSURL(INSURL paramINSURL) {
    this.rirFlag = paramINSURL.getRIRFlag();
    this.theEndpointInfo = (ArrayList)paramINSURL.getEndpointInfo();
    this.theKeyString = paramINSURL.getKeyString();
    this.theStringifiedName = paramINSURL.getStringifiedName();
  }
  
  public boolean isCorbanameURL() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\namingutil\CorbanameURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */