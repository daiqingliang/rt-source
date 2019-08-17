package org.xml.sax;

public interface Locator {
  String getPublicId();
  
  String getSystemId();
  
  int getLineNumber();
  
  int getColumnNumber();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\Locator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */