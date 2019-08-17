package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.axes.NodeSequence;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class XNodeSet extends NodeSequence {
  static final long serialVersionUID = 1916026368035639667L;
  
  static final LessThanComparator S_LT = new LessThanComparator();
  
  static final LessThanOrEqualComparator S_LTE = new LessThanOrEqualComparator();
  
  static final GreaterThanComparator S_GT = new GreaterThanComparator();
  
  static final GreaterThanOrEqualComparator S_GTE = new GreaterThanOrEqualComparator();
  
  static final EqualComparator S_EQ = new EqualComparator();
  
  static final NotEqualComparator S_NEQ = new NotEqualComparator();
  
  protected XNodeSet() {}
  
  public XNodeSet(DTMIterator paramDTMIterator) {
    if (paramDTMIterator instanceof XNodeSet) {
      XNodeSet xNodeSet = (XNodeSet)paramDTMIterator;
      setIter(xNodeSet.m_iter);
      this.m_dtmMgr = xNodeSet.m_dtmMgr;
      this.m_last = xNodeSet.m_last;
      if (!xNodeSet.hasCache())
        xNodeSet.setShouldCacheNodes(true); 
      setObject(xNodeSet.getIteratorCache());
    } else {
      setIter(paramDTMIterator);
    } 
  }
  
  public XNodeSet(XNodeSet paramXNodeSet) {
    setIter(paramXNodeSet.m_iter);
    this.m_dtmMgr = paramXNodeSet.m_dtmMgr;
    this.m_last = paramXNodeSet.m_last;
    if (!paramXNodeSet.hasCache())
      paramXNodeSet.setShouldCacheNodes(true); 
    setObject(paramXNodeSet.m_obj);
  }
  
  public XNodeSet(DTMManager paramDTMManager) { this(-1, paramDTMManager); }
  
  public XNodeSet(int paramInt, DTMManager paramDTMManager) {
    super(new NodeSetDTM(paramDTMManager));
    this.m_dtmMgr = paramDTMManager;
    if (-1 != paramInt) {
      ((NodeSetDTM)this.m_obj).addNode(paramInt);
      this.m_last = 1;
    } else {
      this.m_last = 0;
    } 
  }
  
  public int getType() { return 4; }
  
  public String getTypeString() { return "#NODESET"; }
  
  public double getNumberFromNode(int paramInt) {
    XMLString xMLString = this.m_dtmMgr.getDTM(paramInt).getStringValue(paramInt);
    return xMLString.toDouble();
  }
  
  public double num() {
    int i = item(0);
    return (i != -1) ? getNumberFromNode(i) : NaND;
  }
  
  public double numWithSideEffects() {
    int i = nextNode();
    return (i != -1) ? getNumberFromNode(i) : NaND;
  }
  
  public boolean bool() { return (item(0) != -1); }
  
  public boolean boolWithSideEffects() { return (nextNode() != -1); }
  
  public XMLString getStringFromNode(int paramInt) { return (-1 != paramInt) ? this.m_dtmMgr.getDTM(paramInt).getStringValue(paramInt) : XString.EMPTYSTRING; }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler) throws SAXException {
    int i = item(0);
    if (i != -1)
      this.m_dtmMgr.getDTM(i).dispatchCharactersEvents(i, paramContentHandler, false); 
  }
  
  public XMLString xstr() {
    int i = item(0);
    return (i != -1) ? getStringFromNode(i) : XString.EMPTYSTRING;
  }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer) {
    XString xString = (XString)xstr();
    xString.appendToFsb(paramFastStringBuffer);
  }
  
  public String str() {
    int i = item(0);
    return (i != -1) ? getStringFromNode(i).toString() : "";
  }
  
  public Object object() { return (null == this.m_obj) ? this : this.m_obj; }
  
  public NodeIterator nodeset() throws TransformerException { return new DTMNodeIterator(iter()); }
  
  public NodeList nodelist() throws TransformerException {
    DTMNodeList dTMNodeList = new DTMNodeList(this);
    XNodeSet xNodeSet = (XNodeSet)dTMNodeList.getDTMIterator();
    SetVector(xNodeSet.getVector());
    return dTMNodeList;
  }
  
  public DTMIterator iterRaw() { return this; }
  
  public void release(DTMIterator paramDTMIterator) {}
  
  public DTMIterator iter() {
    try {
      return hasCache() ? cloneWithReset() : this;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new RuntimeException(cloneNotSupportedException.getMessage());
    } 
  }
  
  public XObject getFresh() {
    try {
      return hasCache() ? (XObject)cloneWithReset() : this;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new RuntimeException(cloneNotSupportedException.getMessage());
    } 
  }
  
  public NodeSetDTM mutableNodeset() {
    NodeSetDTM nodeSetDTM;
    if (this.m_obj instanceof NodeSetDTM) {
      nodeSetDTM = (NodeSetDTM)this.m_obj;
    } else {
      nodeSetDTM = new NodeSetDTM(iter());
      setObject(nodeSetDTM);
      setCurrentPos(0);
    } 
    return nodeSetDTM;
  }
  
  public boolean compare(XObject paramXObject, Comparator paramComparator) throws TransformerException {
    boolean bool = false;
    int i = paramXObject.getType();
    if (4 == i) {
      DTMIterator dTMIterator1 = iterRaw();
      DTMIterator dTMIterator2 = ((XNodeSet)paramXObject).iterRaw();
      Vector vector = null;
      int j;
      while (-1 != (j = dTMIterator1.nextNode())) {
        XMLString xMLString = getStringFromNode(j);
        if (null == vector) {
          int m;
          while (-1 != (m = dTMIterator2.nextNode())) {
            XMLString xMLString1 = getStringFromNode(m);
            if (paramComparator.compareStrings(xMLString, xMLString1)) {
              bool = true;
              break;
            } 
            if (null == vector)
              vector = new Vector(); 
            vector.addElement(xMLString1);
          } 
          continue;
        } 
        int k = vector.size();
        for (byte b = 0; b < k; b++) {
          if (paramComparator.compareStrings(xMLString, (XMLString)vector.elementAt(b))) {
            bool = true;
            break;
          } 
        } 
      } 
      dTMIterator1.reset();
      dTMIterator2.reset();
    } else if (1 == i) {
      double d1 = bool() ? 1.0D : 0.0D;
      double d2 = paramXObject.num();
      bool = paramComparator.compareNumbers(d1, d2);
    } else if (2 == i) {
      DTMIterator dTMIterator = iterRaw();
      double d = paramXObject.num();
      int j;
      while (-1 != (j = dTMIterator.nextNode())) {
        double d1 = getNumberFromNode(j);
        if (paramComparator.compareNumbers(d1, d)) {
          bool = true;
          break;
        } 
      } 
      dTMIterator.reset();
    } else if (5 == i) {
      XMLString xMLString = paramXObject.xstr();
      DTMIterator dTMIterator = iterRaw();
      int j;
      while (-1 != (j = dTMIterator.nextNode())) {
        XMLString xMLString1 = getStringFromNode(j);
        if (paramComparator.compareStrings(xMLString1, xMLString)) {
          bool = true;
          break;
        } 
      } 
      dTMIterator.reset();
    } else if (3 == i) {
      XMLString xMLString = paramXObject.xstr();
      DTMIterator dTMIterator = iterRaw();
      int j;
      while (-1 != (j = dTMIterator.nextNode())) {
        XMLString xMLString1 = getStringFromNode(j);
        if (paramComparator.compareStrings(xMLString1, xMLString)) {
          bool = true;
          break;
        } 
      } 
      dTMIterator.reset();
    } else {
      bool = paramComparator.compareNumbers(num(), paramXObject.num());
    } 
    return bool;
  }
  
  public boolean lessThan(XObject paramXObject) throws TransformerException { return compare(paramXObject, S_LT); }
  
  public boolean lessThanOrEqual(XObject paramXObject) throws TransformerException { return compare(paramXObject, S_LTE); }
  
  public boolean greaterThan(XObject paramXObject) throws TransformerException { return compare(paramXObject, S_GT); }
  
  public boolean greaterThanOrEqual(XObject paramXObject) throws TransformerException { return compare(paramXObject, S_GTE); }
  
  public boolean equals(XObject paramXObject) throws TransformerException {
    try {
      return compare(paramXObject, S_EQ);
    } catch (TransformerException transformerException) {
      throw new WrappedRuntimeException(transformerException);
    } 
  }
  
  public boolean notEquals(XObject paramXObject) throws TransformerException { return compare(paramXObject, S_NEQ); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XNodeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */