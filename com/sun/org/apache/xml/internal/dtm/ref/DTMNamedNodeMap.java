package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DTMNamedNodeMap implements NamedNodeMap {
  DTM dtm;
  
  int element;
  
  short m_count = -1;
  
  public DTMNamedNodeMap(DTM paramDTM, int paramInt) {
    this.dtm = paramDTM;
    this.element = paramInt;
  }
  
  public int getLength() {
    if (this.m_count == -1) {
      short s = 0;
      for (int i = this.dtm.getFirstAttribute(this.element); i != -1; i = this.dtm.getNextAttribute(i))
        s = (short)(s + true); 
      this.m_count = s;
    } 
    return this.m_count;
  }
  
  public Node getNamedItem(String paramString) {
    for (int i = this.dtm.getFirstAttribute(this.element); i != -1; i = this.dtm.getNextAttribute(i)) {
      if (this.dtm.getNodeName(i).equals(paramString))
        return this.dtm.getNode(i); 
    } 
    return null;
  }
  
  public Node item(int paramInt) {
    byte b = 0;
    for (int i = this.dtm.getFirstAttribute(this.element); i != -1; i = this.dtm.getNextAttribute(i)) {
      if (b == paramInt)
        return this.dtm.getNode(i); 
      b++;
    } 
    return null;
  }
  
  public Node setNamedItem(Node paramNode) { throw new DTMException((short)7); }
  
  public Node removeNamedItem(String paramString) { throw new DTMException((short)7); }
  
  public Node getNamedItemNS(String paramString1, String paramString2) {
    Node node = null;
    int i;
    for (i = this.dtm.getFirstAttribute(this.element); i != -1; i = this.dtm.getNextAttribute(i)) {
      if (paramString2.equals(this.dtm.getLocalName(i))) {
        String str = this.dtm.getNamespaceURI(i);
        if ((paramString1 == null && str == null) || (paramString1 != null && paramString1.equals(str))) {
          node = this.dtm.getNode(i);
          break;
        } 
      } 
    } 
    return node;
  }
  
  public Node setNamedItemNS(Node paramNode) { throw new DTMException((short)7); }
  
  public Node removeNamedItemNS(String paramString1, String paramString2) { throw new DTMException((short)7); }
  
  public class DTMException extends DOMException {
    static final long serialVersionUID = -8290238117162437678L;
    
    public DTMException(short param1Short, String param1String) { super(param1Short, param1String); }
    
    public DTMException(short param1Short) { super(param1Short, ""); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNamedNodeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */