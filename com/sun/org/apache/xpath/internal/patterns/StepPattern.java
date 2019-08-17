package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class StepPattern extends NodeTest implements SubContextList, ExpressionOwner {
  static final long serialVersionUID = 9071668960168152644L;
  
  protected int m_axis;
  
  String m_targetString;
  
  StepPattern m_relativePathPattern;
  
  Expression[] m_predicates;
  
  private static final boolean DEBUG_MATCHES = false;
  
  public StepPattern(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3) {
    super(paramInt1, paramString1, paramString2);
    this.m_axis = paramInt2;
  }
  
  public StepPattern(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt1);
    this.m_axis = paramInt2;
  }
  
  public void calcTargetString() {
    int i = getWhatToShow();
    switch (i) {
      case 128:
        this.m_targetString = "#comment";
        return;
      case 4:
      case 8:
      case 12:
        this.m_targetString = "#text";
        return;
      case -1:
        this.m_targetString = "*";
        return;
      case 256:
      case 1280:
        this.m_targetString = "/";
        return;
      case 1:
        this;
        if ("*" == this.m_name) {
          this.m_targetString = "*";
        } else {
          this.m_targetString = this.m_name;
        } 
        return;
    } 
    this.m_targetString = "*";
  }
  
  public String getTargetString() { return this.m_targetString; }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    super.fixupVariables(paramVector, paramInt);
    if (null != this.m_predicates)
      for (byte b = 0; b < this.m_predicates.length; b++)
        this.m_predicates[b].fixupVariables(paramVector, paramInt);  
    if (null != this.m_relativePathPattern)
      this.m_relativePathPattern.fixupVariables(paramVector, paramInt); 
  }
  
  public void setRelativePathPattern(StepPattern paramStepPattern) {
    this.m_relativePathPattern = paramStepPattern;
    paramStepPattern.exprSetParent(this);
    calcScore();
  }
  
  public StepPattern getRelativePathPattern() { return this.m_relativePathPattern; }
  
  public Expression[] getPredicates() { return this.m_predicates; }
  
  public boolean canTraverseOutsideSubtree() {
    int i = getPredicateCount();
    for (byte b = 0; b < i; b++) {
      if (getPredicate(b).canTraverseOutsideSubtree())
        return true; 
    } 
    return false;
  }
  
  public Expression getPredicate(int paramInt) { return this.m_predicates[paramInt]; }
  
  public final int getPredicateCount() { return (null == this.m_predicates) ? 0 : this.m_predicates.length; }
  
  public void setPredicates(Expression[] paramArrayOfExpression) {
    this.m_predicates = paramArrayOfExpression;
    if (null != paramArrayOfExpression)
      for (byte b = 0; b < paramArrayOfExpression.length; b++)
        paramArrayOfExpression[b].exprSetParent(this);  
    calcScore();
  }
  
  public void calcScore() {
    if (getPredicateCount() > 0 || null != this.m_relativePathPattern) {
      this.m_score = SCORE_OTHER;
    } else {
      super.calcScore();
    } 
    if (null == this.m_targetString)
      calcTargetString(); 
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    DTM dTM = paramXPathContext.getDTM(paramInt);
    if (dTM != null) {
      int i = dTM.getExpandedTypeID(paramInt);
      return execute(paramXPathContext, paramInt, dTM, i);
    } 
    return NodeTest.SCORE_NONE;
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return execute(paramXPathContext, paramXPathContext.getCurrentNode()); }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2) throws TransformerException {
    if (this.m_whatToShow == 65536)
      return (null != this.m_relativePathPattern) ? this.m_relativePathPattern.execute(paramXPathContext) : NodeTest.SCORE_NONE; 
    XObject xObject = super.execute(paramXPathContext, paramInt1, paramDTM, paramInt2);
    return (xObject == NodeTest.SCORE_NONE) ? NodeTest.SCORE_NONE : ((getPredicateCount() != 0 && !executePredicates(paramXPathContext, paramDTM, paramInt1)) ? NodeTest.SCORE_NONE : ((null != this.m_relativePathPattern) ? this.m_relativePathPattern.executeRelativePathPattern(paramXPathContext, paramDTM, paramInt1) : xObject));
  }
  
  private final boolean checkProximityPosition(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2, int paramInt3) {
    try {
      DTMAxisTraverser dTMAxisTraverser = paramDTM.getAxisTraverser(12);
      int i;
      for (i = dTMAxisTraverser.first(paramInt2); -1 != i; i = dTMAxisTraverser.next(paramInt2, i)) {
        try {
          paramXPathContext.pushCurrentNode(i);
          if (NodeTest.SCORE_NONE != super.execute(paramXPathContext, i)) {
            boolean bool = true;
            try {
              paramXPathContext.pushSubContextList(this);
              for (byte b = 0; b < paramInt1; b++) {
                paramXPathContext.pushPredicatePos(b);
                try {
                  xObject = this.m_predicates[b].execute(paramXPathContext);
                  try {
                    if (2 == xObject.getType())
                      throw new Error("Why: Should never have been called"); 
                    if (!xObject.boolWithSideEffects()) {
                      bool = false;
                      xObject.detach();
                      paramXPathContext.popPredicatePos();
                      break;
                    } 
                  } finally {
                    xObject.detach();
                  } 
                } finally {
                  paramXPathContext.popPredicatePos();
                } 
              } 
            } finally {
              paramXPathContext.popSubContextList();
            } 
            if (bool)
              paramInt3--; 
            if (paramInt3 < 1)
              return false; 
          } 
        } finally {
          paramXPathContext.popCurrentNode();
        } 
      } 
    } catch (TransformerException transformerException) {
      throw new RuntimeException(transformerException.getMessage());
    } 
    return (paramInt3 == 1);
  }
  
  private final int getProximityPosition(XPathContext paramXPathContext, int paramInt, boolean paramBoolean) {
    byte b = 0;
    int i = paramXPathContext.getCurrentNode();
    DTM dTM = paramXPathContext.getDTM(i);
    int j = dTM.getParent(i);
    try {
      DTMAxisTraverser dTMAxisTraverser = dTM.getAxisTraverser(3);
      int k;
      for (k = dTMAxisTraverser.first(j); -1 != k; k = dTMAxisTraverser.next(j, k)) {
        try {
          paramXPathContext.pushCurrentNode(k);
          if (NodeTest.SCORE_NONE != super.execute(paramXPathContext, k)) {
            boolean bool = true;
            try {
              paramXPathContext.pushSubContextList(this);
              for (byte b1 = 0; b1 < paramInt; b1++) {
                paramXPathContext.pushPredicatePos(b1);
                try {
                  xObject = this.m_predicates[b1].execute(paramXPathContext);
                  try {
                    if (2 == xObject.getType()) {
                      if (b + true != (int)xObject.numWithSideEffects()) {
                        bool = false;
                        xObject.detach();
                        paramXPathContext.popPredicatePos();
                        break;
                      } 
                    } else if (!xObject.boolWithSideEffects()) {
                      bool = false;
                      xObject.detach();
                      paramXPathContext.popPredicatePos();
                      break;
                    } 
                  } finally {
                    xObject.detach();
                  } 
                } finally {
                  paramXPathContext.popPredicatePos();
                } 
              } 
            } finally {
              paramXPathContext.popSubContextList();
            } 
            if (bool)
              b++; 
            if (!paramBoolean && k == i)
              return b; 
          } 
        } finally {
          paramXPathContext.popCurrentNode();
        } 
      } 
    } catch (TransformerException transformerException) {
      throw new RuntimeException(transformerException.getMessage());
    } 
    return b;
  }
  
  public int getProximityPosition(XPathContext paramXPathContext) { return getProximityPosition(paramXPathContext, paramXPathContext.getPredicatePos(), false); }
  
  public int getLastPos(XPathContext paramXPathContext) { return getProximityPosition(paramXPathContext, paramXPathContext.getPredicatePos(), true); }
  
  protected final XObject executeRelativePathPattern(XPathContext paramXPathContext, DTM paramDTM, int paramInt) throws TransformerException {
    XObject xObject = NodeTest.SCORE_NONE;
    int i = paramInt;
    DTMAxisTraverser dTMAxisTraverser = paramDTM.getAxisTraverser(this.m_axis);
    int j;
    for (j = dTMAxisTraverser.first(i); -1 != j; j = dTMAxisTraverser.next(i, j)) {
      try {
        paramXPathContext.pushCurrentNode(j);
        xObject = execute(paramXPathContext);
        if (xObject != NodeTest.SCORE_NONE) {
          paramXPathContext.popCurrentNode();
          break;
        } 
      } finally {
        paramXPathContext.popCurrentNode();
      } 
    } 
    return xObject;
  }
  
  protected final boolean executePredicates(XPathContext paramXPathContext, DTM paramDTM, int paramInt) throws TransformerException {
    boolean bool = true;
    boolean bool1 = false;
    int i = getPredicateCount();
    try {
      paramXPathContext.pushSubContextList(this);
      for (byte b = 0; b < i; b++) {
        paramXPathContext.pushPredicatePos(b);
        try {
          xObject = this.m_predicates[b].execute(paramXPathContext);
          try {
            if (2 == xObject.getType()) {
              int j = (int)xObject.num();
              if (bool1) {
                bool = (j == 1);
                xObject.detach();
                paramXPathContext.popPredicatePos();
                break;
              } 
              bool1 = true;
              if (!checkProximityPosition(paramXPathContext, b, paramDTM, paramInt, j)) {
                bool = false;
                xObject.detach();
                paramXPathContext.popPredicatePos();
                break;
              } 
            } else if (!xObject.boolWithSideEffects()) {
              bool = false;
              xObject.detach();
              paramXPathContext.popPredicatePos();
              break;
            } 
          } finally {
            xObject.detach();
          } 
        } finally {
          paramXPathContext.popPredicatePos();
        } 
      } 
    } finally {
      paramXPathContext.popSubContextList();
    } 
    return bool;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (StepPattern stepPattern = this; stepPattern != null; stepPattern = stepPattern.m_relativePathPattern) {
      if (stepPattern != this)
        stringBuffer.append("/"); 
      stringBuffer.append(Axis.getNames(stepPattern.m_axis));
      stringBuffer.append("::");
      if (20480 == stepPattern.m_whatToShow) {
        stringBuffer.append("doc()");
      } else if (65536 == stepPattern.m_whatToShow) {
        stringBuffer.append("function()");
      } else if (-1 == stepPattern.m_whatToShow) {
        stringBuffer.append("node()");
      } else if (4 == stepPattern.m_whatToShow) {
        stringBuffer.append("text()");
      } else if (64 == stepPattern.m_whatToShow) {
        stringBuffer.append("processing-instruction(");
        if (null != stepPattern.m_name)
          stringBuffer.append(stepPattern.m_name); 
        stringBuffer.append(")");
      } else if (128 == stepPattern.m_whatToShow) {
        stringBuffer.append("comment()");
      } else if (null != stepPattern.m_name) {
        if (2 == stepPattern.m_whatToShow)
          stringBuffer.append("@"); 
        if (null != stepPattern.m_namespace) {
          stringBuffer.append("{");
          stringBuffer.append(stepPattern.m_namespace);
          stringBuffer.append("}");
        } 
        stringBuffer.append(stepPattern.m_name);
      } else if (2 == stepPattern.m_whatToShow) {
        stringBuffer.append("@");
      } else if (1280 == stepPattern.m_whatToShow) {
        stringBuffer.append("doc-root()");
      } else {
        stringBuffer.append('?').append(Integer.toHexString(stepPattern.m_whatToShow));
      } 
      if (null != stepPattern.m_predicates)
        for (byte b = 0; b < stepPattern.m_predicates.length; b++) {
          stringBuffer.append("[");
          stringBuffer.append(stepPattern.m_predicates[b]);
          stringBuffer.append("]");
        }  
    } 
    return stringBuffer.toString();
  }
  
  public double getMatchScore(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    paramXPathContext.pushCurrentNode(paramInt);
    paramXPathContext.pushCurrentExpressionNode(paramInt);
    try {
      XObject xObject = execute(paramXPathContext);
      return xObject.num();
    } finally {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popCurrentExpressionNode();
    } 
  }
  
  public void setAxis(int paramInt) { this.m_axis = paramInt; }
  
  public int getAxis() { return this.m_axis; }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitMatchPattern(paramExpressionOwner, this))
      callSubtreeVisitors(paramXPathVisitor); 
  }
  
  protected void callSubtreeVisitors(XPathVisitor paramXPathVisitor) {
    if (null != this.m_predicates) {
      int i = this.m_predicates.length;
      for (byte b = 0; b < i; b++) {
        PredOwner predOwner = new PredOwner(b);
        if (paramXPathVisitor.visitPredicate(predOwner, this.m_predicates[b]))
          this.m_predicates[b].callVisitors(predOwner, paramXPathVisitor); 
      } 
    } 
    if (null != this.m_relativePathPattern)
      this.m_relativePathPattern.callVisitors(this, paramXPathVisitor); 
  }
  
  public Expression getExpression() { return this.m_relativePathPattern; }
  
  public void setExpression(Expression paramExpression) {
    paramExpression.exprSetParent(this);
    this.m_relativePathPattern = (StepPattern)paramExpression;
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    StepPattern stepPattern = (StepPattern)paramExpression;
    if (null != this.m_predicates) {
      int i = this.m_predicates.length;
      if (null == stepPattern.m_predicates || stepPattern.m_predicates.length != i)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (!this.m_predicates[b].deepEquals(stepPattern.m_predicates[b]))
          return false; 
      } 
    } else if (null != stepPattern.m_predicates) {
      return false;
    } 
    if (null != this.m_relativePathPattern) {
      if (!this.m_relativePathPattern.deepEquals(stepPattern.m_relativePathPattern))
        return false; 
    } else if (stepPattern.m_relativePathPattern != null) {
      return false;
    } 
    return true;
  }
  
  class PredOwner implements ExpressionOwner {
    int m_index;
    
    PredOwner(int param1Int) { this.m_index = param1Int; }
    
    public Expression getExpression() { return StepPattern.this.m_predicates[this.m_index]; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(StepPattern.this);
      StepPattern.this.m_predicates[this.m_index] = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\patterns\StepPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */