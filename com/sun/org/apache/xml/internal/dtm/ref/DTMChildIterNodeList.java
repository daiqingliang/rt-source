package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import org.w3c.dom.Node;

public class DTMChildIterNodeList extends DTMNodeListBase {
  private int m_firstChild;
  
  private DTM m_parentDTM;
  
  private DTMChildIterNodeList() {}
  
  public DTMChildIterNodeList(DTM paramDTM, int paramInt) {
    this.m_parentDTM = paramDTM;
    this.m_firstChild = paramDTM.getFirstChild(paramInt);
  }
  
  public Node item(int paramInt) {
    int i;
    for (i = this.m_firstChild; --paramInt >= 0 && i != -1; i = this.m_parentDTM.getNextSibling(i));
    return (i == -1) ? null : this.m_parentDTM.getNode(i);
  }
  
  public int getLength() {
    byte b = 0;
    for (int i = this.m_firstChild; i != -1; i = this.m_parentDTM.getNextSibling(i))
      b++; 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMChildIterNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */