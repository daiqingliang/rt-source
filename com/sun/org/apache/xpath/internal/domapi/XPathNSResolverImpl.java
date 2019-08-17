package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathNSResolver;

class XPathNSResolverImpl extends PrefixResolverDefault implements XPathNSResolver {
  public XPathNSResolverImpl(Node paramNode) { super(paramNode); }
  
  public String lookupNamespaceURI(String paramString) { return getNamespaceForPrefix(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathNSResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */