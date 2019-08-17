package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.utils.IntVector;
import org.w3c.dom.Node;

public class DTMAxisIterNodeList extends DTMNodeListBase {
  private DTM m_dtm;
  
  private DTMAxisIterator m_iter;
  
  private IntVector m_cachedNodes;
  
  private int m_last = -1;
  
  private DTMAxisIterNodeList() {}
  
  public DTMAxisIterNodeList(DTM paramDTM, DTMAxisIterator paramDTMAxisIterator) {
    if (paramDTMAxisIterator == null) {
      this.m_last = 0;
    } else {
      this.m_cachedNodes = new IntVector();
      this.m_dtm = paramDTM;
    } 
    this.m_iter = paramDTMAxisIterator;
  }
  
  public DTMAxisIterator getDTMAxisIterator() { return this.m_iter; }
  
  public Node item(int paramInt) {
    if (this.m_iter != null) {
      int i = 0;
      int j = this.m_cachedNodes.size();
      if (j > paramInt) {
        i = this.m_cachedNodes.elementAt(paramInt);
        return this.m_dtm.getNode(i);
      } 
      if (this.m_last == -1) {
        while (j <= paramInt && (i = this.m_iter.next()) != -1) {
          this.m_cachedNodes.addElement(i);
          j++;
        } 
        if (i == -1) {
          this.m_last = j;
        } else {
          return this.m_dtm.getNode(i);
        } 
      } 
    } 
    return null;
  }
  
  public int getLength() {
    if (this.m_last == -1) {
      int i;
      while ((i = this.m_iter.next()) != -1)
        this.m_cachedNodes.addElement(i); 
      this.m_last = this.m_cachedNodes.size();
    } 
    return this.m_last;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMAxisIterNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */