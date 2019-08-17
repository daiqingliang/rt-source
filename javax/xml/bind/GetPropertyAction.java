package javax.xml.bind;

import java.security.PrivilegedAction;

final class GetPropertyAction extends Object implements PrivilegedAction<String> {
  private final String propertyName;
  
  public GetPropertyAction(String paramString) { this.propertyName = paramString; }
  
  public String run() { return System.getProperty(this.propertyName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */