package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import org.w3c.dom.Node;

public class DTMNodeList extends DTMNodeListBase {
  private DTMIterator m_iter;
  
  private DTMNodeList() {}
  
  public DTMNodeList(DTMIterator paramDTMIterator) {
    if (paramDTMIterator != null) {
      int i = paramDTMIterator.getCurrentPos();
      try {
        this.m_iter = paramDTMIterator.cloneWithReset();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        this.m_iter = paramDTMIterator;
      } 
      this.m_iter.setShouldCacheNodes(true);
      this.m_iter.runTo(-1);
      this.m_iter.setCurrentPos(i);
    } 
  }
  
  public DTMIterator getDTMIterator() { return this.m_iter; }
  
  public Node item(int paramInt) {
    if (this.m_iter != null) {
      int i = this.m_iter.item(paramInt);
      return (i == -1) ? null : this.m_iter.getDTM(i).getNode(i);
    } 
    return null;
  }
  
  public int getLength() { return (this.m_iter != null) ? this.m_iter.getLength() : 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */