package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xpath.internal.XPathContext;

public class ReverseAxesWalker extends AxesWalker {
  static final long serialVersionUID = 2847007647832768941L;
  
  protected DTMAxisIterator m_iterator;
  
  ReverseAxesWalker(LocPathIterator paramLocPathIterator, int paramInt) { super(paramLocPathIterator, paramInt); }
  
  public void setRoot(int paramInt) {
    super.setRoot(paramInt);
    this.m_iterator = getDTM(paramInt).getAxisIterator(this.m_axis);
    this.m_iterator.setStartNode(paramInt);
  }
  
  public void detach() {
    this.m_iterator = null;
    super.detach();
  }
  
  protected int getNextNode() {
    if (this.m_foundLast)
      return -1; 
    int i = this.m_iterator.next();
    if (this.m_isFresh)
      this.m_isFresh = false; 
    if (-1 == i)
      this.m_foundLast = true; 
    return i;
  }
  
  public boolean isReverseAxes() { return true; }
  
  protected int getProximityPosition(int paramInt) {
    if (paramInt < 0)
      return -1; 
    int i = this.m_proximityPositions[paramInt];
    if (i <= 0) {
      axesWalker = wi().getLastUsedWalker();
      try {
        ReverseAxesWalker reverseAxesWalker = (ReverseAxesWalker)clone();
        reverseAxesWalker.setRoot(getRoot());
        reverseAxesWalker.setPredicateCount(paramInt);
        reverseAxesWalker.setPrevWalker(null);
        reverseAxesWalker.setNextWalker(null);
        wi().setLastUsedWalker(reverseAxesWalker);
        i++;
        int j;
        while (-1 != (j = reverseAxesWalker.nextNode()))
          i++; 
        this.m_proximityPositions[paramInt] = i;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
      
      } finally {
        wi().setLastUsedWalker(axesWalker);
      } 
    } 
    return i;
  }
  
  protected void countProximityPosition(int paramInt) {
    if (paramInt < this.m_proximityPositions.length)
      this.m_proximityPositions[paramInt] = this.m_proximityPositions[paramInt] - 1; 
  }
  
  public int getLastPos(XPathContext paramXPathContext) {
    byte b = 0;
    axesWalker = wi().getLastUsedWalker();
    try {
      ReverseAxesWalker reverseAxesWalker = (ReverseAxesWalker)clone();
      reverseAxesWalker.setRoot(getRoot());
      reverseAxesWalker.setPredicateCount(getPredicateCount() - 1);
      reverseAxesWalker.setPrevWalker(null);
      reverseAxesWalker.setNextWalker(null);
      wi().setLastUsedWalker(reverseAxesWalker);
      int i;
      while (-1 != (i = reverseAxesWalker.nextNode()))
        b++; 
    } catch (CloneNotSupportedException cloneNotSupportedException) {
    
    } finally {
      wi().setLastUsedWalker(axesWalker);
    } 
    return b;
  }
  
  public boolean isDocOrdered() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\ReverseAxesWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */