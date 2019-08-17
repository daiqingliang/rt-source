package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.dom.TextImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextImpl extends TextImpl implements Text, Text {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
  
  public TextImpl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, paramString); }
  
  public String getValue() {
    String str = getNodeValue();
    return str.equals("") ? null : str;
  }
  
  public void setValue(String paramString) { setNodeValue(paramString); }
  
  public void setParentElement(SOAPElement paramSOAPElement) throws SOAPException {
    if (paramSOAPElement == null) {
      log.severe("SAAJ0126.impl.cannot.locate.ns");
      throw new SOAPException("Cannot pass NULL to setParentElement");
    } 
    ((ElementImpl)paramSOAPElement).addNode(this);
  }
  
  public SOAPElement getParentElement() { return (SOAPElement)getParentNode(); }
  
  public void detachNode() {
    Node node = getParentNode();
    if (node != null)
      node.removeChild(this); 
  }
  
  public void recycleNode() { detachNode(); }
  
  public boolean isComment() {
    String str = getNodeValue();
    return (str == null) ? false : ((str.startsWith("<!--") && str.endsWith("-->")));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\TextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */