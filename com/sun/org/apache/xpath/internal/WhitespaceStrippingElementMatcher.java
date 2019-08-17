package com.sun.org.apache.xpath.internal;

import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;

public interface WhitespaceStrippingElementMatcher {
  boolean shouldStripWhiteSpace(XPathContext paramXPathContext, Element paramElement) throws TransformerException;
  
  boolean canStripWhiteSpace();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\WhitespaceStrippingElementMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */