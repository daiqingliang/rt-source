package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class XNodeSetForDOM extends XNodeSet {
  static final long serialVersionUID = -8396190713754624640L;
  
  Object m_origObj;
  
  public XNodeSetForDOM(Node paramNode, DTMManager paramDTMManager) {
    this.m_dtmMgr = paramDTMManager;
    this.m_origObj = paramNode;
    int i = paramDTMManager.getDTMHandleFromNode(paramNode);
    setObject(new NodeSetDTM(paramDTMManager));
    ((NodeSetDTM)this.m_obj).addNode(i);
  }
  
  public XNodeSetForDOM(XNodeSet paramXNodeSet) {
    super(paramXNodeSet);
    if (paramXNodeSet instanceof XNodeSetForDOM)
      this.m_origObj = ((XNodeSetForDOM)paramXNodeSet).m_origObj; 
  }
  
  public XNodeSetForDOM(NodeList paramNodeList, XPathContext paramXPathContext) {
    this.m_dtmMgr = paramXPathContext.getDTMManager();
    this.m_origObj = paramNodeList;
    NodeSetDTM nodeSetDTM = new NodeSetDTM(paramNodeList, paramXPathContext);
    this.m_last = nodeSetDTM.getLength();
    setObject(nodeSetDTM);
  }
  
  public XNodeSetForDOM(NodeIterator paramNodeIterator, XPathContext paramXPathContext) {
    this.m_dtmMgr = paramXPathContext.getDTMManager();
    this.m_origObj = paramNodeIterator;
    NodeSetDTM nodeSetDTM = new NodeSetDTM(paramNodeIterator, paramXPathContext);
    this.m_last = nodeSetDTM.getLength();
    setObject(nodeSetDTM);
  }
  
  public Object object() { return this.m_origObj; }
  
  public NodeIterator nodeset() throws TransformerException { return (this.m_origObj instanceof NodeIterator) ? (NodeIterator)this.m_origObj : super.nodeset(); }
  
  public NodeList nodelist() throws TransformerException { return (this.m_origObj instanceof NodeList) ? (NodeList)this.m_origObj : super.nodelist(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XNodeSetForDOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */