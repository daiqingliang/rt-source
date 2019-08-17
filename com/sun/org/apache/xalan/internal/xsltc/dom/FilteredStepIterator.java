package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class FilteredStepIterator extends StepIterator {
  private Filter _filter;
  
  public FilteredStepIterator(DTMAxisIterator paramDTMAxisIterator1, DTMAxisIterator paramDTMAxisIterator2, Filter paramFilter) {
    super(paramDTMAxisIterator1, paramDTMAxisIterator2);
    this._filter = paramFilter;
  }
  
  public int next() {
    int i;
    while ((i = super.next()) != -1) {
      if (this._filter.test(i))
        return returnNode(i); 
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\FilteredStepIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */