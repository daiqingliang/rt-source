package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class UnionChildIterator extends ChildTestIterator {
  static final long serialVersionUID = 3500298482193003495L;
  
  private PredicatedNodeTest[] m_nodeTests = null;
  
  public UnionChildIterator() { super(null); }
  
  public void addNodeTest(PredicatedNodeTest paramPredicatedNodeTest) {
    if (null == this.m_nodeTests) {
      this.m_nodeTests = new PredicatedNodeTest[1];
      this.m_nodeTests[0] = paramPredicatedNodeTest;
    } else {
      PredicatedNodeTest[] arrayOfPredicatedNodeTest = this.m_nodeTests;
      int i = this.m_nodeTests.length;
      this.m_nodeTests = new PredicatedNodeTest[i + 1];
      System.arraycopy(arrayOfPredicatedNodeTest, 0, this.m_nodeTests, 0, i);
      this.m_nodeTests[i] = paramPredicatedNodeTest;
    } 
    paramPredicatedNodeTest.exprSetParent(this);
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    super.fixupVariables(paramVector, paramInt);
    if (this.m_nodeTests != null)
      for (byte b = 0; b < this.m_nodeTests.length; b++)
        this.m_nodeTests[b].fixupVariables(paramVector, paramInt);  
  }
  
  public short acceptNode(int paramInt) {
    xPathContext = getXPathContext();
    try {
      xPathContext.pushCurrentNode(paramInt);
      for (byte b = 0; b < this.m_nodeTests.length; b++) {
        PredicatedNodeTest predicatedNodeTest = this.m_nodeTests[b];
        XObject xObject = predicatedNodeTest.execute(xPathContext, paramInt);
        if (xObject != NodeTest.SCORE_NONE)
          if (predicatedNodeTest.getPredicateCount() > 0) {
            if (predicatedNodeTest.executePredicates(paramInt, xPathContext))
              return 1; 
          } else {
            return 1;
          }  
      } 
    } catch (TransformerException transformerException) {
      throw new RuntimeException(transformerException.getMessage());
    } finally {
      xPathContext.popCurrentNode();
    } 
    return 3;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\UnionChildIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */