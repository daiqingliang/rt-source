package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.Node;

public final class DOMInputSource extends XMLInputSource {
  private Node fNode;
  
  public DOMInputSource() { this(null); }
  
  public DOMInputSource(Node paramNode) {
    super(null, getSystemIdFromNode(paramNode), null);
    this.fNode = paramNode;
  }
  
  public DOMInputSource(Node paramNode, String paramString) {
    super(null, paramString, null);
    this.fNode = paramNode;
  }
  
  public Node getNode() { return this.fNode; }
  
  public void setNode(Node paramNode) { this.fNode = paramNode; }
  
  private static String getSystemIdFromNode(Node paramNode) {
    if (paramNode != null)
      try {
        return paramNode.getBaseURI();
      } catch (NoSuchMethodError noSuchMethodError) {
        return null;
      } catch (Exception exception) {
        return null;
      }  
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\DOMInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */