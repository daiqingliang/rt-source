package com.sun.org.apache.xml.internal.security.signature;

import org.w3c.dom.Node;

public interface NodeFilter {
  int isNodeInclude(Node paramNode);
  
  int isNodeIncludeDO(Node paramNode, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\NodeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */