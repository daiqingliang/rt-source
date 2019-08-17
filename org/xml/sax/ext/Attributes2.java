package org.xml.sax.ext;

import org.xml.sax.Attributes;

public interface Attributes2 extends Attributes {
  boolean isDeclared(int paramInt);
  
  boolean isDeclared(String paramString);
  
  boolean isDeclared(String paramString1, String paramString2);
  
  boolean isSpecified(int paramInt);
  
  boolean isSpecified(String paramString1, String paramString2);
  
  boolean isSpecified(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\ext\Attributes2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */