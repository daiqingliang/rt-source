package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.ObjectVector;
import javax.xml.transform.TransformerException;

public class OpMap {
  protected String m_currentPattern;
  
  static final int MAXTOKENQUEUESIZE = 500;
  
  static final int BLOCKTOKENQUEUESIZE = 500;
  
  ObjectVector m_tokenQueue = new ObjectVector(500, 500);
  
  OpMapVector m_opMap = null;
  
  public static final int MAPINDEX_LENGTH = 1;
  
  public String toString() { return this.m_currentPattern; }
  
  public String getPatternString() { return this.m_currentPattern; }
  
  public ObjectVector getTokenQueue() { return this.m_tokenQueue; }
  
  public Object getToken(int paramInt) { return this.m_tokenQueue.elementAt(paramInt); }
  
  public int getTokenQueueSize() { return this.m_tokenQueue.size(); }
  
  public OpMapVector getOpMap() { return this.m_opMap; }
  
  void shrink() {
    int i = this.m_opMap.elementAt(1);
    this.m_opMap.setToSize(i + 4);
    this.m_opMap.setElementAt(0, i);
    this.m_opMap.setElementAt(0, i + 1);
    this.m_opMap.setElementAt(0, i + 2);
    i = this.m_tokenQueue.size();
    this.m_tokenQueue.setToSize(i + 4);
    this.m_tokenQueue.setElementAt(null, i);
    this.m_tokenQueue.setElementAt(null, i + 1);
    this.m_tokenQueue.setElementAt(null, i + 2);
  }
  
  public int getOp(int paramInt) { return this.m_opMap.elementAt(paramInt); }
  
  public void setOp(int paramInt1, int paramInt2) { this.m_opMap.setElementAt(paramInt2, paramInt1); }
  
  public int getNextOpPos(int paramInt) { return paramInt + this.m_opMap.elementAt(paramInt + 1); }
  
  public int getNextStepPos(int paramInt) {
    int i = getOp(paramInt);
    if (i >= 37 && i <= 53)
      return getNextOpPos(paramInt); 
    if (i >= 22 && i <= 25) {
      int j;
      for (j = getNextOpPos(paramInt); 29 == getOp(j); j = getNextOpPos(j));
      i = getOp(j);
      return (i < 37 || i > 53) ? -1 : j;
    } 
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_UNKNOWN_STEP", new Object[] { String.valueOf(i) }));
  }
  
  public static int getNextOpPos(int[] paramArrayOfInt, int paramInt) { return paramInt + paramArrayOfInt[paramInt + 1]; }
  
  public int getFirstPredicateOpPos(int paramInt) {
    int i = this.m_opMap.elementAt(paramInt);
    if (i >= 37 && i <= 53)
      return paramInt + this.m_opMap.elementAt(paramInt + 2); 
    if (i >= 22 && i <= 25)
      return paramInt + this.m_opMap.elementAt(paramInt + 1); 
    if (-2 == i)
      return -2; 
    error("ER_UNKNOWN_OPCODE", new Object[] { String.valueOf(i) });
    return -1;
  }
  
  public void error(String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    throw new TransformerException(str);
  }
  
  public static int getFirstChildPos(int paramInt) { return paramInt + 2; }
  
  public int getArgLength(int paramInt) { return this.m_opMap.elementAt(paramInt + 1); }
  
  public int getArgLengthOfStep(int paramInt) { return this.m_opMap.elementAt(paramInt + 1 + 1) - 3; }
  
  public static int getFirstChildPosOfStep(int paramInt) { return paramInt + 3; }
  
  public int getStepTestType(int paramInt) { return this.m_opMap.elementAt(paramInt + 3); }
  
  public String getStepNS(int paramInt) {
    int i = getArgLengthOfStep(paramInt);
    if (i == 3) {
      int j = this.m_opMap.elementAt(paramInt + 4);
      return (j >= 0) ? (String)this.m_tokenQueue.elementAt(j) : ((-3 == j) ? "*" : null);
    } 
    return null;
  }
  
  public String getStepLocalName(int paramInt) {
    int j;
    int i = getArgLengthOfStep(paramInt);
    switch (i) {
      case 0:
        j = -2;
        break;
      case 1:
        j = -3;
        break;
      case 2:
        j = this.m_opMap.elementAt(paramInt + 4);
        break;
      case 3:
        j = this.m_opMap.elementAt(paramInt + 5);
        break;
      default:
        j = -2;
        break;
    } 
    return (j >= 0) ? this.m_tokenQueue.elementAt(j).toString() : ((-3 == j) ? "*" : null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\OpMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */