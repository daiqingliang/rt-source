package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CorbalocURL extends INSURLBase {
  static NamingSystemException wrapper = NamingSystemException.get("naming.read");
  
  public CorbalocURL(String paramString) {
    String str = paramString;
    if (str != null) {
      try {
        str = Utility.cleanEscapes(str);
      } catch (Exception exception) {
        badAddress(exception);
      } 
      int i = str.indexOf('/');
      if (i == -1)
        i = str.length(); 
      if (i == 0)
        badAddress(null); 
      StringTokenizer stringTokenizer = new StringTokenizer(str.substring(0, i), ",");
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        IIOPEndpointInfo iIOPEndpointInfo = null;
        if (str1.startsWith("iiop:")) {
          iIOPEndpointInfo = handleIIOPColon(str1);
        } else if (str1.startsWith("rir:")) {
          handleRIRColon(str1);
          this.rirFlag = true;
        } else if (str1.startsWith(":")) {
          iIOPEndpointInfo = handleColon(str1);
        } else {
          badAddress(null);
        } 
        if (!this.rirFlag) {
          if (this.theEndpointInfo == null)
            this.theEndpointInfo = new ArrayList(); 
          this.theEndpointInfo.add(iIOPEndpointInfo);
        } 
      } 
      if (str.length() > i + 1)
        this.theKeyString = str.substring(i + 1); 
    } 
  }
  
  private void badAddress(Throwable paramThrowable) { throw wrapper.insBadAddress(paramThrowable); }
  
  private IIOPEndpointInfo handleIIOPColon(String paramString) {
    paramString = paramString.substring(4);
    return handleColon(paramString);
  }
  
  private IIOPEndpointInfo handleColon(String paramString) {
    paramString = paramString.substring(1);
    String str = paramString;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "@");
    IIOPEndpointInfo iIOPEndpointInfo = new IIOPEndpointInfo();
    int i = stringTokenizer.countTokens();
    if (i == 0 || i > 2)
      badAddress(null); 
    if (i == 2) {
      String str1 = stringTokenizer.nextToken();
      int j = str1.indexOf('.');
      if (j == -1)
        badAddress(null); 
      try {
        iIOPEndpointInfo.setVersion(Integer.parseInt(str1.substring(0, j)), Integer.parseInt(str1.substring(j + 1)));
        str = stringTokenizer.nextToken();
      } catch (Throwable throwable) {
        badAddress(throwable);
      } 
    } 
    try {
      int j = str.indexOf('[');
      if (j != -1) {
        String str1 = getIPV6Port(str);
        if (str1 != null)
          iIOPEndpointInfo.setPort(Integer.parseInt(str1)); 
        iIOPEndpointInfo.setHost(getIPV6Host(str));
        return iIOPEndpointInfo;
      } 
      stringTokenizer = new StringTokenizer(str, ":");
      if (stringTokenizer.countTokens() == 2) {
        iIOPEndpointInfo.setHost(stringTokenizer.nextToken());
        iIOPEndpointInfo.setPort(Integer.parseInt(stringTokenizer.nextToken()));
      } else if (str != null && str.length() != 0) {
        iIOPEndpointInfo.setHost(str);
      } 
    } catch (Throwable throwable) {
      badAddress(throwable);
    } 
    Utility.validateGIOPVersion(iIOPEndpointInfo);
    return iIOPEndpointInfo;
  }
  
  private void handleRIRColon(String paramString) {
    if (paramString.length() != 4)
      badAddress(null); 
  }
  
  private String getIPV6Port(String paramString) {
    int i = paramString.indexOf(']');
    if (i + 1 != paramString.length()) {
      if (paramString.charAt(i + 1) != ':')
        throw new RuntimeException("Host and Port is not separated by ':'"); 
      return paramString.substring(i + 2);
    } 
    return null;
  }
  
  private String getIPV6Host(String paramString) {
    int i = paramString.indexOf(']');
    return paramString.substring(1, i);
  }
  
  public boolean isCorbanameURL() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\namingutil\CorbalocURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */