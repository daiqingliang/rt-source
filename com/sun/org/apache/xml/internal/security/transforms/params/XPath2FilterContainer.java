package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPath2FilterContainer extends ElementProxy implements TransformParam {
  private static final String _ATT_FILTER = "Filter";
  
  private static final String _ATT_FILTER_VALUE_INTERSECT = "intersect";
  
  private static final String _ATT_FILTER_VALUE_SUBTRACT = "subtract";
  
  private static final String _ATT_FILTER_VALUE_UNION = "union";
  
  public static final String INTERSECT = "intersect";
  
  public static final String SUBTRACT = "subtract";
  
  public static final String UNION = "union";
  
  public static final String _TAG_XPATH2 = "XPath";
  
  public static final String XPathFilter2NS = "http://www.w3.org/2002/06/xmldsig-filter2";
  
  private XPath2FilterContainer() {}
  
  private XPath2FilterContainer(Document paramDocument, String paramString1, String paramString2) {
    super(paramDocument);
    this.constructionElement.setAttributeNS(null, "Filter", paramString2);
    this.constructionElement.appendChild(paramDocument.createTextNode(paramString1));
  }
  
  private XPath2FilterContainer(Element paramElement, String paramString) throws XMLSecurityException {
    super(paramElement, paramString);
    String str = this.constructionElement.getAttributeNS(null, "Filter");
    if (!str.equals("intersect") && !str.equals("subtract") && !str.equals("union")) {
      Object[] arrayOfObject = { "Filter", str, "intersect, subtract or union" };
      throw new XMLSecurityException("attributeValueIllegal", arrayOfObject);
    } 
  }
  
  public static XPath2FilterContainer newInstanceIntersect(Document paramDocument, String paramString) { return new XPath2FilterContainer(paramDocument, paramString, "intersect"); }
  
  public static XPath2FilterContainer newInstanceSubtract(Document paramDocument, String paramString) { return new XPath2FilterContainer(paramDocument, paramString, "subtract"); }
  
  public static XPath2FilterContainer newInstanceUnion(Document paramDocument, String paramString) { return new XPath2FilterContainer(paramDocument, paramString, "union"); }
  
  public static NodeList newInstances(Document paramDocument, String[][] paramArrayOfString) {
    HelperNodeList helperNodeList = new HelperNodeList();
    XMLUtils.addReturnToElement(paramDocument, helperNodeList);
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str1 = paramArrayOfString[b][0];
      String str2 = paramArrayOfString[b][1];
      if (!str1.equals("intersect") && !str1.equals("subtract") && !str1.equals("union"))
        throw new IllegalArgumentException("The type(" + b + ")=\"" + str1 + "\" is illegal"); 
      XPath2FilterContainer xPath2FilterContainer = new XPath2FilterContainer(paramDocument, str2, str1);
      helperNodeList.appendChild(xPath2FilterContainer.getElement());
      XMLUtils.addReturnToElement(paramDocument, helperNodeList);
    } 
    return helperNodeList;
  }
  
  public static XPath2FilterContainer newInstance(Element paramElement, String paramString) throws XMLSecurityException { return new XPath2FilterContainer(paramElement, paramString); }
  
  public boolean isIntersect() { return this.constructionElement.getAttributeNS(null, "Filter").equals("intersect"); }
  
  public boolean isSubtract() { return this.constructionElement.getAttributeNS(null, "Filter").equals("subtract"); }
  
  public boolean isUnion() { return this.constructionElement.getAttributeNS(null, "Filter").equals("union"); }
  
  public String getXPathFilterStr() { return getTextFromTextChild(); }
  
  public Node getXPathFilterTextNode() {
    NodeList nodeList = this.constructionElement.getChildNodes();
    int i = nodeList.getLength();
    for (byte b = 0; b < i; b++) {
      if (nodeList.item(b).getNodeType() == 3)
        return nodeList.item(b); 
    } 
    return null;
  }
  
  public final String getBaseLocalName() { return "XPath"; }
  
  public final String getBaseNamespace() { return "http://www.w3.org/2002/06/xmldsig-filter2"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\XPath2FilterContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */