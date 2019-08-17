package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public class DescendantIterator extends LocPathIterator {
  static final long serialVersionUID = -1190338607743976938L;
  
  protected DTMAxisTraverser m_traverser;
  
  protected int m_axis;
  
  protected int m_extendedTypeID;
  
  DescendantIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    super(paramCompiler, paramInt1, paramInt2, false);
    int i = OpMap.getFirstChildPos(paramInt1);
    int j = paramCompiler.getOp(i);
    boolean bool1 = (42 == j) ? 1 : 0;
    boolean bool2 = false;
    if (48 == j) {
      bool1 = true;
    } else if (50 == j) {
      bool2 = true;
      int n = paramCompiler.getNextStepPos(i);
      if (paramCompiler.getOp(n) == 42)
        bool1 = true; 
    } 
    int k = i;
    while (true) {
      k = paramCompiler.getNextStepPos(k);
      if (k > 0) {
        int n = paramCompiler.getOp(k);
        if (-1 != n) {
          i = k;
          continue;
        } 
      } 
      break;
    } 
    if ((paramInt2 & 0x10000) != 0)
      bool1 = false; 
    if (bool2) {
      if (bool1) {
        this.m_axis = 18;
      } else {
        this.m_axis = 17;
      } 
    } else if (bool1) {
      this.m_axis = 5;
    } else {
      this.m_axis = 4;
    } 
    int m = paramCompiler.getWhatToShow(i);
    if (0 == (m & 0x43) || m == -1) {
      initNodeTest(m);
    } else {
      initNodeTest(m, paramCompiler.getStepNS(i), paramCompiler.getStepLocalName(i));
    } 
    initPredicateInfo(paramCompiler, i);
  }
  
  public DescendantIterator() {
    super(null);
    this.m_axis = 18;
    byte b = -1;
    initNodeTest(b);
  }
  
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    DescendantIterator descendantIterator = (DescendantIterator)super.cloneWithReset();
    descendantIterator.m_traverser = this.m_traverser;
    descendantIterator.resetProximityPositions();
    return descendantIterator;
  }
  
  public int nextNode() {
    byte b;
    Object object;
    int i;
    if (this.m_foundLast)
      return -1; 
    if (-1 == this.m_lastFetched)
      resetProximityPositions(); 
    if (-1 != this.m_stackFrame) {
      object = this.m_execContext.getVarStack();
      b = object.getStackFrame();
      object.setStackFrame(this.m_stackFrame);
    } else {
      object = null;
      b = 0;
    } 
    do {
      if (0 == this.m_extendedTypeID) {
        i = this.m_lastFetched = (-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
      } else {
        i = this.m_lastFetched = (-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context, this.m_extendedTypeID) : this.m_traverser.next(this.m_context, this.m_lastFetched, this.m_extendedTypeID);
      } 
    } while (-1 != i && 1 != acceptNode(i) && i != -1);
    if (-1 != i) {
      this.m_pos++;
      int j = i;
      if (-1 != this.m_stackFrame)
        object.setStackFrame(b); 
      return j;
    } 
    this.m_foundLast = true;
    byte b1 = -1;
    if (-1 != this.m_stackFrame)
      object.setStackFrame(b); 
    return b1;
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_axis);
    String str1 = getLocalName();
    String str2 = getNamespace();
    int i = this.m_whatToShow;
    if (-1 == i || "*".equals(str1) || "*".equals(str2)) {
      this.m_extendedTypeID = 0;
    } else {
      int j = getNodeTypeTest(i);
      this.m_extendedTypeID = this.m_cdtm.getExpandedTypeID(str2, str1, j);
    } 
  }
  
  public int asNode(XPathContext paramXPathContext) throws TransformerException {
    if (getPredicateCount() > 0)
      return super.asNode(paramXPathContext); 
    int i = paramXPathContext.getCurrentNode();
    DTM dTM = paramXPathContext.getDTM(i);
    DTMAxisTraverser dTMAxisTraverser = dTM.getAxisTraverser(this.m_axis);
    String str1 = getLocalName();
    String str2 = getNamespace();
    int j = this.m_whatToShow;
    if (-1 == j || str1 == "*" || str2 == "*")
      return dTMAxisTraverser.first(i); 
    int k = getNodeTypeTest(j);
    int m = dTM.getExpandedTypeID(str2, str1, k);
    return dTMAxisTraverser.first(i, m);
  }
  
  public void detach() {
    if (this.m_allowDetach) {
      this.m_traverser = null;
      this.m_extendedTypeID = 0;
      super.detach();
    } 
  }
  
  public int getAxis() { return this.m_axis; }
  
  public boolean deepEquals(Expression paramExpression) { return !super.deepEquals(paramExpression) ? false : (!(this.m_axis != ((DescendantIterator)paramExpression).m_axis)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\DescendantIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */