package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathResult;

class XPathResultImpl implements XPathResult, EventListener {
  private final XObject m_resultObj;
  
  private final XPath m_xpath;
  
  private final short m_resultType;
  
  private boolean m_isInvalidIteratorState = false;
  
  private final Node m_contextNode;
  
  private NodeIterator m_iterator = null;
  
  private NodeList m_list = null;
  
  XPathResultImpl(short paramShort, XObject paramXObject, Node paramNode, XPath paramXPath) {
    if (!isValidType(paramShort)) {
      String str = XPATHMessages.createXPATHMessage("ER_INVALID_XPATH_TYPE", new Object[] { new Integer(paramShort) });
      throw new XPathException((short)2, str);
    } 
    if (null == paramXObject) {
      String str = XPATHMessages.createXPATHMessage("ER_EMPTY_XPATH_RESULT", null);
      throw new XPathException((short)1, str);
    } 
    this.m_resultObj = paramXObject;
    this.m_contextNode = paramNode;
    this.m_xpath = paramXPath;
    if (paramShort == 0) {
      this.m_resultType = getTypeFromXObject(paramXObject);
    } else {
      this.m_resultType = paramShort;
    } 
    if (this.m_resultType == 5 || this.m_resultType == 4)
      addEventListener(); 
    if (this.m_resultType == 5 || this.m_resultType == 4 || this.m_resultType == 8 || this.m_resultType == 9) {
      try {
        this.m_iterator = this.m_resultObj.nodeset();
      } catch (TransformerException transformerException) {
        String str = XPATHMessages.createXPATHMessage("ER_INCOMPATIBLE_TYPES", new Object[] { this.m_xpath.getPatternString(), getTypeString(getTypeFromXObject(this.m_resultObj)), getTypeString(this.m_resultType) });
        throw new XPathException((short)2, str);
      } 
    } else if (this.m_resultType == 6 || this.m_resultType == 7) {
      try {
        this.m_list = this.m_resultObj.nodelist();
      } catch (TransformerException transformerException) {
        String str = XPATHMessages.createXPATHMessage("ER_INCOMPATIBLE_TYPES", new Object[] { this.m_xpath.getPatternString(), getTypeString(getTypeFromXObject(this.m_resultObj)), getTypeString(this.m_resultType) });
        throw new XPathException((short)2, str);
      } 
    } 
  }
  
  public short getResultType() { return this.m_resultType; }
  
  public double getNumberValue() throws XPathException {
    if (getResultType() != 1) {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_XPATHRESULTTYPE_TO_NUMBER", new Object[] { this.m_xpath.getPatternString(), getTypeString(this.m_resultType) });
      throw new XPathException((short)2, str);
    } 
    try {
      return this.m_resultObj.num();
    } catch (Exception exception) {
      throw new XPathException((short)2, exception.getMessage());
    } 
  }
  
  public String getStringValue() throws XPathException {
    if (getResultType() != 2) {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_TO_STRING", new Object[] { this.m_xpath.getPatternString(), this.m_resultObj.getTypeString() });
      throw new XPathException((short)2, str);
    } 
    try {
      return this.m_resultObj.str();
    } catch (Exception exception) {
      throw new XPathException((short)2, exception.getMessage());
    } 
  }
  
  public boolean getBooleanValue() throws XPathException {
    if (getResultType() != 3) {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_TO_BOOLEAN", new Object[] { this.m_xpath.getPatternString(), getTypeString(this.m_resultType) });
      throw new XPathException((short)2, str);
    } 
    try {
      return this.m_resultObj.bool();
    } catch (TransformerException transformerException) {
      throw new XPathException((short)2, transformerException.getMessage());
    } 
  }
  
  public Node getSingleNodeValue() throws XPathException {
    if (this.m_resultType != 8 && this.m_resultType != 9) {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_TO_SINGLENODE", new Object[] { this.m_xpath.getPatternString(), getTypeString(this.m_resultType) });
      throw new XPathException((short)2, str);
    } 
    NodeIterator nodeIterator = null;
    try {
      nodeIterator = this.m_resultObj.nodeset();
    } catch (TransformerException transformerException) {
      throw new XPathException((short)2, transformerException.getMessage());
    } 
    if (null == nodeIterator)
      return null; 
    Node node = nodeIterator.nextNode();
    return isNamespaceNode(node) ? new XPathNamespaceImpl(node) : node;
  }
  
  public boolean getInvalidIteratorState() throws XPathException { return this.m_isInvalidIteratorState; }
  
  public int getSnapshotLength() throws XPathException {
    if (this.m_resultType != 6 && this.m_resultType != 7) {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_GET_SNAPSHOT_LENGTH", new Object[] { this.m_xpath.getPatternString(), getTypeString(this.m_resultType) });
      throw new XPathException((short)2, str);
    } 
    return this.m_list.getLength();
  }
  
  public Node iterateNext() throws XPathException {
    if (this.m_resultType != 4 && this.m_resultType != 5) {
      String str = XPATHMessages.createXPATHMessage("ER_NON_ITERATOR_TYPE", new Object[] { this.m_xpath.getPatternString(), getTypeString(this.m_resultType) });
      throw new XPathException((short)2, str);
    } 
    if (getInvalidIteratorState()) {
      String str = XPATHMessages.createXPATHMessage("ER_DOC_MUTATED", null);
      throw new DOMException((short)11, str);
    } 
    Node node = this.m_iterator.nextNode();
    if (null == node)
      removeEventListener(); 
    return isNamespaceNode(node) ? new XPathNamespaceImpl(node) : node;
  }
  
  public Node snapshotItem(int paramInt) throws XPathException {
    if (this.m_resultType != 6 && this.m_resultType != 7) {
      String str = XPATHMessages.createXPATHMessage("ER_NON_SNAPSHOT_TYPE", new Object[] { this.m_xpath.getPatternString(), getTypeString(this.m_resultType) });
      throw new XPathException((short)2, str);
    } 
    Node node = this.m_list.item(paramInt);
    return isNamespaceNode(node) ? new XPathNamespaceImpl(node) : node;
  }
  
  static boolean isValidType(short paramShort) {
    switch (paramShort) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
        return true;
    } 
    return false;
  }
  
  public void handleEvent(Event paramEvent) {
    if (paramEvent.getType().equals("DOMSubtreeModified")) {
      this.m_isInvalidIteratorState = true;
      removeEventListener();
    } 
  }
  
  private String getTypeString(int paramInt) {
    switch (paramInt) {
      case 0:
        return "ANY_TYPE";
      case 8:
        return "ANY_UNORDERED_NODE_TYPE";
      case 3:
        return "BOOLEAN";
      case 9:
        return "FIRST_ORDERED_NODE_TYPE";
      case 1:
        return "NUMBER_TYPE";
      case 5:
        return "ORDERED_NODE_ITERATOR_TYPE";
      case 7:
        return "ORDERED_NODE_SNAPSHOT_TYPE";
      case 2:
        return "STRING_TYPE";
      case 4:
        return "UNORDERED_NODE_ITERATOR_TYPE";
      case 6:
        return "UNORDERED_NODE_SNAPSHOT_TYPE";
    } 
    return "#UNKNOWN";
  }
  
  private short getTypeFromXObject(XObject paramXObject) {
    switch (paramXObject.getType()) {
      case 1:
        return 3;
      case 4:
        return 4;
      case 2:
        return 1;
      case 3:
        return 2;
      case 5:
        return 4;
      case -1:
        return 0;
    } 
    return 0;
  }
  
  private boolean isNamespaceNode(Node paramNode) { return (null != paramNode && paramNode.getNodeType() == 2 && (paramNode.getNodeName().startsWith("xmlns:") || paramNode.getNodeName().equals("xmlns"))); }
  
  private void addEventListener() {
    if (this.m_contextNode instanceof EventTarget)
      ((EventTarget)this.m_contextNode).addEventListener("DOMSubtreeModified", this, true); 
  }
  
  private void removeEventListener() {
    if (this.m_contextNode instanceof EventTarget)
      ((EventTarget)this.m_contextNode).removeEventListener("DOMSubtreeModified", this, true); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */