package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.RTFIterator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;

public class XRTreeFrag extends XObject implements Cloneable {
  static final long serialVersionUID = -3201553822254911567L;
  
  private DTMXRTreeFrag m_DTMXRTreeFrag;
  
  private int m_dtmRoot = -1;
  
  protected boolean m_allowRelease = false;
  
  private XMLString m_xmlStr = null;
  
  public XRTreeFrag(int paramInt, XPathContext paramXPathContext, ExpressionNode paramExpressionNode) {
    super(null);
    exprSetParent(paramExpressionNode);
    initDTM(paramInt, paramXPathContext);
  }
  
  public XRTreeFrag(int paramInt, XPathContext paramXPathContext) {
    super(null);
    initDTM(paramInt, paramXPathContext);
  }
  
  private final void initDTM(int paramInt, XPathContext paramXPathContext) {
    this.m_dtmRoot = paramInt;
    DTM dTM = paramXPathContext.getDTM(paramInt);
    if (dTM != null)
      this.m_DTMXRTreeFrag = paramXPathContext.getDTMXRTreeFrag(paramXPathContext.getDTMIdentity(dTM)); 
  }
  
  public Object object() { return (this.m_DTMXRTreeFrag.getXPathContext() != null) ? new DTMNodeIterator(new NodeSetDTM(this.m_dtmRoot, this.m_DTMXRTreeFrag.getXPathContext().getDTMManager())) : super.object(); }
  
  public XRTreeFrag(Expression paramExpression) { super(paramExpression); }
  
  public void allowDetachToRelease(boolean paramBoolean) { this.m_allowRelease = paramBoolean; }
  
  public void detach() {
    if (this.m_allowRelease) {
      this.m_DTMXRTreeFrag.destruct();
      setObject(null);
    } 
  }
  
  public int getType() { return 5; }
  
  public String getTypeString() { return "#RTREEFRAG"; }
  
  public double num() throws TransformerException {
    XMLString xMLString = xstr();
    return xMLString.toDouble();
  }
  
  public boolean bool() { return true; }
  
  public XMLString xstr() {
    if (null == this.m_xmlStr)
      this.m_xmlStr = this.m_DTMXRTreeFrag.getDTM().getStringValue(this.m_dtmRoot); 
    return this.m_xmlStr;
  }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer) {
    XString xString = (XString)xstr();
    xString.appendToFsb(paramFastStringBuffer);
  }
  
  public String str() {
    String str = this.m_DTMXRTreeFrag.getDTM().getStringValue(this.m_dtmRoot).toString();
    return (null == str) ? "" : str;
  }
  
  public int rtf() { return this.m_dtmRoot; }
  
  public DTMIterator asNodeIterator() { return new RTFIterator(this.m_dtmRoot, this.m_DTMXRTreeFrag.getXPathContext().getDTMManager()); }
  
  public NodeList convertToNodeset() { return (this.m_obj instanceof NodeList) ? (NodeList)this.m_obj : new DTMNodeList(asNodeIterator()); }
  
  public boolean equals(XObject paramXObject) {
    try {
      return (4 == paramXObject.getType()) ? paramXObject.equals(this) : ((1 == paramXObject.getType()) ? ((bool() == paramXObject.bool()) ? 1 : 0) : ((2 == paramXObject.getType()) ? ((num() == paramXObject.num()) ? 1 : 0) : ((4 == paramXObject.getType()) ? xstr().equals(paramXObject.xstr()) : ((3 == paramXObject.getType()) ? xstr().equals(paramXObject.xstr()) : ((5 == paramXObject.getType()) ? xstr().equals(paramXObject.xstr()) : super.equals(paramXObject))))));
    } catch (TransformerException transformerException) {
      throw new WrappedRuntimeException(transformerException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XRTreeFrag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */