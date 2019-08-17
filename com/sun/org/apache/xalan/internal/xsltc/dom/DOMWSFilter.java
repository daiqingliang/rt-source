package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import java.util.HashMap;
import java.util.Map;

public class DOMWSFilter implements DTMWSFilter {
  private AbstractTranslet m_translet;
  
  private StripFilter m_filter;
  
  private Map<DTM, short[]> m_mappings;
  
  private DTM m_currentDTM;
  
  private short[] m_currentMapping;
  
  public DOMWSFilter(AbstractTranslet paramAbstractTranslet) {
    this.m_translet = paramAbstractTranslet;
    this.m_mappings = new HashMap();
    if (paramAbstractTranslet instanceof StripFilter)
      this.m_filter = (StripFilter)paramAbstractTranslet; 
  }
  
  public short getShouldStripSpace(int paramInt, DTM paramDTM) {
    if (this.m_filter != null && paramDTM instanceof DOM) {
      DOM dOM = (DOM)paramDTM;
      short s = 0;
      if (paramDTM instanceof DOMEnhancedForDTM) {
        short[] arrayOfShort;
        DOMEnhancedForDTM dOMEnhancedForDTM = (DOMEnhancedForDTM)paramDTM;
        if (paramDTM == this.m_currentDTM) {
          arrayOfShort = this.m_currentMapping;
        } else {
          arrayOfShort = (short[])this.m_mappings.get(paramDTM);
          if (arrayOfShort == null) {
            arrayOfShort = dOMEnhancedForDTM.getMapping(this.m_translet.getNamesArray(), this.m_translet.getUrisArray(), this.m_translet.getTypesArray());
            this.m_mappings.put(paramDTM, arrayOfShort);
            this.m_currentDTM = paramDTM;
            this.m_currentMapping = arrayOfShort;
          } 
        } 
        int i = dOMEnhancedForDTM.getExpandedTypeID(paramInt);
        if (i >= 0 && i < arrayOfShort.length) {
          s = arrayOfShort[i];
        } else {
          s = -1;
        } 
      } else {
        return 3;
      } 
      return this.m_filter.stripSpace(dOM, paramInt, s) ? 2 : 1;
    } 
    return 1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\DOMWSFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */