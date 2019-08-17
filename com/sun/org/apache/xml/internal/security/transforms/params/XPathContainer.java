package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XPathContainer extends SignatureElementProxy implements TransformParam {
  public XPathContainer(Document paramDocument) { super(paramDocument); }
  
  public void setXPath(String paramString) {
    if (this.constructionElement.getChildNodes() != null) {
      NodeList nodeList = this.constructionElement.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++)
        this.constructionElement.removeChild(nodeList.item(b)); 
    } 
    Text text = this.doc.createTextNode(paramString);
    this.constructionElement.appendChild(text);
  }
  
  public String getXPath() { return getTextFromTextChild(); }
  
  public String getBaseLocalName() { return "XPath"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\XPathContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */