package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class ContextMatchStepPattern extends StepPattern {
  static final long serialVersionUID = -1888092779313211942L;
  
  public ContextMatchStepPattern(int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt2); }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    if (paramXPathContext.getIteratorRoot() == paramXPathContext.getCurrentNode())
      return getStaticScore(); 
    this;
    return SCORE_NONE;
  }
  
  public XObject executeRelativePathPattern(XPathContext paramXPathContext, StepPattern paramStepPattern) throws TransformerException {
    XObject xObject = NodeTest.SCORE_NONE;
    int i = paramXPathContext.getCurrentNode();
    DTM dTM = paramXPathContext.getDTM(i);
    if (null != dTM) {
      int j = paramXPathContext.getCurrentNode();
      int k = this.m_axis;
      boolean bool = WalkerFactory.isDownwardAxisOfMany(k);
      boolean bool1 = (dTM.getNodeType(paramXPathContext.getIteratorRoot()) == 2) ? 1 : 0;
      if (11 == k && bool1)
        k = 15; 
      DTMAxisTraverser dTMAxisTraverser = dTM.getAxisTraverser(k);
      int m;
      for (m = dTMAxisTraverser.first(i); -1 != m; m = dTMAxisTraverser.next(i, m)) {
        try {
          paramXPathContext.pushCurrentNode(m);
          xObject = execute(paramXPathContext);
          if (xObject != NodeTest.SCORE_NONE) {
            if (executePredicates(paramXPathContext, dTM, i))
              return xObject; 
            xObject = NodeTest.SCORE_NONE;
          } 
          if (bool && bool1 && 1 == dTM.getNodeType(m)) {
            byte b1 = 2;
            for (byte b2 = 0; b2 < 2; b2++) {
              DTMAxisTraverser dTMAxisTraverser1 = dTM.getAxisTraverser(b1);
              int n;
              for (n = dTMAxisTraverser1.first(m); -1 != n; n = dTMAxisTraverser1.next(m, n)) {
                try {
                  paramXPathContext.pushCurrentNode(n);
                  xObject = execute(paramXPathContext);
                  if (xObject != NodeTest.SCORE_NONE && xObject != NodeTest.SCORE_NONE)
                    return xObject; 
                } finally {
                  paramXPathContext.popCurrentNode();
                } 
              } 
              b1 = 9;
            } 
          } 
        } finally {
          paramXPathContext.popCurrentNode();
        } 
      } 
    } 
    return xObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\patterns\ContextMatchStepPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */