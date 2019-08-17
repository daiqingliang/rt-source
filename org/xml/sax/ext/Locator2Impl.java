package org.xml.sax.ext;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class Locator2Impl extends LocatorImpl implements Locator2 {
  private String encoding;
  
  private String version;
  
  public Locator2Impl() {}
  
  public Locator2Impl(Locator paramLocator) {
    super(paramLocator);
    if (paramLocator instanceof Locator2) {
      Locator2 locator2 = (Locator2)paramLocator;
      this.version = locator2.getXMLVersion();
      this.encoding = locator2.getEncoding();
    } 
  }
  
  public String getXMLVersion() { return this.version; }
  
  public String getEncoding() { return this.encoding; }
  
  public void setXMLVersion(String paramString) { this.version = paramString; }
  
  public void setEncoding(String paramString) { this.encoding = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\ext\Locator2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */