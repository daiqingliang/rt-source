package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import javax.naming.Name;
import javax.naming.NamingException;

public final class CorbanameUrl {
  private String stringName;
  
  private String location;
  
  public String getStringName() { return this.stringName; }
  
  public Name getCosName() throws NamingException { return CNCtx.parser.parse(this.stringName); }
  
  public String getLocation() { return "corbaloc:" + this.location; }
  
  public CorbanameUrl(String paramString) throws MalformedURLException {
    if (!paramString.startsWith("corbaname:"))
      throw new MalformedURLException("Invalid corbaname URL: " + paramString); 
    byte b = 10;
    int i = paramString.indexOf('#', b);
    if (i < 0) {
      i = paramString.length();
      this.stringName = "";
    } else {
      this.stringName = UrlUtil.decode(paramString.substring(i + 1));
    } 
    this.location = paramString.substring(b, i);
    int j = this.location.indexOf("/");
    if (j >= 0) {
      if (j == this.location.length() - 1)
        this.location += "NameService"; 
    } else {
      this.location += "/NameService";
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\CorbanameUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */