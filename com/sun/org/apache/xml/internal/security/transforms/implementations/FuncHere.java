package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class FuncHere extends Function {
  private static final long serialVersionUID = 1L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    Node node = (Node)paramXPathContext.getOwnerObject();
    if (node == null)
      return null; 
    int i = paramXPathContext.getDTMHandleFromNode(node);
    int j = paramXPathContext.getCurrentNode();
    DTM dTM = paramXPathContext.getDTM(j);
    int k = dTM.getDocument();
    if (-1 == k)
      error(paramXPathContext, "ER_CONTEXT_HAS_NO_OWNERDOC", null); 
    Document document1 = XMLUtils.getOwnerDocument(dTM.getNode(j));
    Document document2 = XMLUtils.getOwnerDocument(node);
    if (document1 != document2)
      throw new TransformerException(I18n.translate("xpath.funcHere.documentsDiffer")); 
    XNodeSet xNodeSet = new XNodeSet(paramXPathContext.getDTMManager());
    NodeSetDTM nodeSetDTM = xNodeSet.mutableNodeset();
    int m = -1;
    switch (dTM.getNodeType(i)) {
      case 2:
      case 7:
        m = i;
        nodeSetDTM.addNode(m);
        break;
      case 3:
        m = dTM.getParent(i);
        nodeSetDTM.addNode(m);
        break;
    } 
    nodeSetDTM.detach();
    return xNodeSet;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\FuncHere.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */