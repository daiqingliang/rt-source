package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class DTMNodeIterator implements NodeIterator {
  private DTMIterator dtm_iter;
  
  private boolean valid = true;
  
  public DTMNodeIterator(DTMIterator paramDTMIterator) {
    try {
      this.dtm_iter = (DTMIterator)paramDTMIterator.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new WrappedRuntimeException(cloneNotSupportedException);
    } 
  }
  
  public DTMIterator getDTMIterator() { return this.dtm_iter; }
  
  public void detach() { this.valid = false; }
  
  public boolean getExpandEntityReferences() { return false; }
  
  public NodeFilter getFilter() { throw new DTMDOMException((short)9); }
  
  public Node getRoot() {
    int i = this.dtm_iter.getRoot();
    return this.dtm_iter.getDTM(i).getNode(i);
  }
  
  public int getWhatToShow() { return this.dtm_iter.getWhatToShow(); }
  
  public Node nextNode() {
    if (!this.valid)
      throw new DTMDOMException((short)11); 
    int i = this.dtm_iter.nextNode();
    return (i == -1) ? null : this.dtm_iter.getDTM(i).getNode(i);
  }
  
  public Node previousNode() {
    if (!this.valid)
      throw new DTMDOMException((short)11); 
    int i = this.dtm_iter.previousNode();
    return (i == -1) ? null : this.dtm_iter.getDTM(i).getNode(i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNodeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */