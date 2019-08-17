package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class NodeTest extends Expression {
  static final long serialVersionUID = -5736721866747906182L;
  
  public static final String WILD = "*";
  
  public static final String SUPPORTS_PRE_STRIPPING = "http://xml.apache.org/xpath/features/whitespace-pre-stripping";
  
  protected int m_whatToShow;
  
  public static final int SHOW_BYFUNCTION = 65536;
  
  String m_namespace;
  
  protected String m_name;
  
  XNumber m_score;
  
  public static final XNumber SCORE_NODETEST = new XNumber(-0.5D);
  
  public static final XNumber SCORE_NSWILD = new XNumber(-0.25D);
  
  public static final XNumber SCORE_QNAME = new XNumber(0.0D);
  
  public static final XNumber SCORE_OTHER = new XNumber(0.5D);
  
  public static final XNumber SCORE_NONE = new XNumber(Double.NEGATIVE_INFINITY);
  
  private boolean m_isTotallyWild;
  
  public int getWhatToShow() { return this.m_whatToShow; }
  
  public void setWhatToShow(int paramInt) { this.m_whatToShow = paramInt; }
  
  public String getNamespace() { return this.m_namespace; }
  
  public void setNamespace(String paramString) { this.m_namespace = paramString; }
  
  public String getLocalName() { return (null == this.m_name) ? "" : this.m_name; }
  
  public void setLocalName(String paramString) { this.m_name = paramString; }
  
  public NodeTest(int paramInt, String paramString1, String paramString2) { initNodeTest(paramInt, paramString1, paramString2); }
  
  public NodeTest(int paramInt) { initNodeTest(paramInt); }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!isSameClass(paramExpression))
      return false; 
    NodeTest nodeTest = (NodeTest)paramExpression;
    if (null != nodeTest.m_name) {
      if (null == this.m_name)
        return false; 
      if (!nodeTest.m_name.equals(this.m_name))
        return false; 
    } else if (null != this.m_name) {
      return false;
    } 
    if (null != nodeTest.m_namespace) {
      if (null == this.m_namespace)
        return false; 
      if (!nodeTest.m_namespace.equals(this.m_namespace))
        return false; 
    } else if (null != this.m_namespace) {
      return false;
    } 
    return (this.m_whatToShow != nodeTest.m_whatToShow) ? false : (!(this.m_isTotallyWild != nodeTest.m_isTotallyWild));
  }
  
  public NodeTest() {}
  
  public void initNodeTest(int paramInt) {
    this.m_whatToShow = paramInt;
    calcScore();
  }
  
  public void initNodeTest(int paramInt, String paramString1, String paramString2) {
    this.m_whatToShow = paramInt;
    this.m_namespace = paramString1;
    this.m_name = paramString2;
    calcScore();
  }
  
  public XNumber getStaticScore() { return this.m_score; }
  
  public void setStaticScore(XNumber paramXNumber) { this.m_score = paramXNumber; }
  
  protected void calcScore() {
    if (this.m_namespace == null && this.m_name == null) {
      this.m_score = SCORE_NODETEST;
    } else if ((this.m_namespace == "*" || this.m_namespace == null) && this.m_name == "*") {
      this.m_score = SCORE_NODETEST;
    } else if (this.m_namespace != "*" && this.m_name == "*") {
      this.m_score = SCORE_NSWILD;
    } else {
      this.m_score = SCORE_QNAME;
    } 
    this.m_isTotallyWild = (this.m_namespace == null && this.m_name == "*");
  }
  
  public double getDefaultScore() { return this.m_score.num(); }
  
  public static int getNodeTypeTest(int paramInt) { return (0 != (paramInt & true)) ? 1 : ((0 != (paramInt & 0x2)) ? 2 : ((0 != (paramInt & 0x4)) ? 3 : ((0 != (paramInt & 0x100)) ? 9 : ((0 != (paramInt & 0x400)) ? 11 : ((0 != (paramInt & 0x1000)) ? 13 : ((0 != (paramInt & 0x80)) ? 8 : ((0 != (paramInt & 0x40)) ? 7 : ((0 != (paramInt & 0x200)) ? 10 : ((0 != (paramInt & 0x20)) ? 6 : ((0 != (paramInt & 0x10)) ? 5 : ((0 != (paramInt & 0x800)) ? 12 : ((0 != (paramInt & 0x8)) ? 4 : 0)))))))))))); }
  
  public static void debugWhatToShow(int paramInt) {
    Vector vector = new Vector();
    if (0 != (paramInt & 0x2))
      vector.addElement("SHOW_ATTRIBUTE"); 
    if (0 != (paramInt & 0x1000))
      vector.addElement("SHOW_NAMESPACE"); 
    if (0 != (paramInt & 0x8))
      vector.addElement("SHOW_CDATA_SECTION"); 
    if (0 != (paramInt & 0x80))
      vector.addElement("SHOW_COMMENT"); 
    if (0 != (paramInt & 0x100))
      vector.addElement("SHOW_DOCUMENT"); 
    if (0 != (paramInt & 0x400))
      vector.addElement("SHOW_DOCUMENT_FRAGMENT"); 
    if (0 != (paramInt & 0x200))
      vector.addElement("SHOW_DOCUMENT_TYPE"); 
    if (0 != (paramInt & true))
      vector.addElement("SHOW_ELEMENT"); 
    if (0 != (paramInt & 0x20))
      vector.addElement("SHOW_ENTITY"); 
    if (0 != (paramInt & 0x10))
      vector.addElement("SHOW_ENTITY_REFERENCE"); 
    if (0 != (paramInt & 0x800))
      vector.addElement("SHOW_NOTATION"); 
    if (0 != (paramInt & 0x40))
      vector.addElement("SHOW_PROCESSING_INSTRUCTION"); 
    if (0 != (paramInt & 0x4))
      vector.addElement("SHOW_TEXT"); 
    int i = vector.size();
    for (byte b = 0; b < i; b++) {
      if (b)
        System.out.print(" | "); 
      System.out.print(vector.elementAt(b));
    } 
    if (0 == i)
      System.out.print("empty whatToShow: " + paramInt); 
    System.out.println();
  }
  
  private static final boolean subPartMatch(String paramString1, String paramString2) { return (paramString1 == paramString2 || (null != paramString1 && (paramString2 == "*" || paramString1.equals(paramString2)))); }
  
  private static final boolean subPartMatchNS(String paramString1, String paramString2) { return (paramString1 == paramString2 || (null != paramString1 && ((paramString1.length() > 0) ? (paramString2 == "*" || paramString1.equals(paramString2)) : (null == paramString2)))); }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    String str;
    DTM dTM = paramXPathContext.getDTM(paramInt);
    short s = dTM.getNodeType(paramInt);
    if (this.m_whatToShow == -1)
      return this.m_score; 
    int i = this.m_whatToShow & 1 << s - 1;
    switch (i) {
      case 256:
      case 1024:
        return SCORE_OTHER;
      case 128:
        return this.m_score;
      case 4:
      case 8:
        return this.m_score;
      case 64:
        return subPartMatch(dTM.getNodeName(paramInt), this.m_name) ? this.m_score : SCORE_NONE;
      case 4096:
        str = dTM.getLocalName(paramInt);
        return subPartMatch(str, this.m_name) ? this.m_score : SCORE_NONE;
      case 1:
      case 2:
        return (this.m_isTotallyWild || (subPartMatchNS(dTM.getNamespaceURI(paramInt), this.m_namespace) && subPartMatch(dTM.getLocalName(paramInt), this.m_name))) ? this.m_score : SCORE_NONE;
    } 
    return SCORE_NONE;
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2) throws TransformerException {
    String str;
    if (this.m_whatToShow == -1)
      return this.m_score; 
    int i = this.m_whatToShow & 1 << paramDTM.getNodeType(paramInt1) - 1;
    switch (i) {
      case 256:
      case 1024:
        return SCORE_OTHER;
      case 128:
        return this.m_score;
      case 4:
      case 8:
        return this.m_score;
      case 64:
        return subPartMatch(paramDTM.getNodeName(paramInt1), this.m_name) ? this.m_score : SCORE_NONE;
      case 4096:
        str = paramDTM.getLocalName(paramInt1);
        return subPartMatch(str, this.m_name) ? this.m_score : SCORE_NONE;
      case 1:
      case 2:
        return (this.m_isTotallyWild || (subPartMatchNS(paramDTM.getNamespaceURI(paramInt1), this.m_namespace) && subPartMatch(paramDTM.getLocalName(paramInt1), this.m_name))) ? this.m_score : SCORE_NONE;
    } 
    return SCORE_NONE;
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return execute(paramXPathContext, paramXPathContext.getCurrentNode()); }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) { assertion(false, "callVisitors should not be called for this object!!!"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\patterns\NodeTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */