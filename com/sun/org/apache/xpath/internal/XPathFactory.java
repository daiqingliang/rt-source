package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;

public interface XPathFactory {
  XPath create(String paramString, SourceLocator paramSourceLocator, PrefixResolver paramPrefixResolver, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\XPathFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */