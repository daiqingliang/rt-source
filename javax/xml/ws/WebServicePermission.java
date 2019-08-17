package javax.xml.ws;

import java.security.BasicPermission;

public final class WebServicePermission extends BasicPermission {
  private static final long serialVersionUID = -146474640053770988L;
  
  public WebServicePermission(String paramString) { super(paramString); }
  
  public WebServicePermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\WebServicePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */