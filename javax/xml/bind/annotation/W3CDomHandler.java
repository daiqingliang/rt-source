package javax.xml.bind.annotation;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class W3CDomHandler extends Object implements DomHandler<Element, DOMResult> {
  private DocumentBuilder builder;
  
  public W3CDomHandler() { this.builder = null; }
  
  public W3CDomHandler(DocumentBuilder paramDocumentBuilder) {
    if (paramDocumentBuilder == null)
      throw new IllegalArgumentException(); 
    this.builder = paramDocumentBuilder;
  }
  
  public DocumentBuilder getBuilder() { return this.builder; }
  
  public void setBuilder(DocumentBuilder paramDocumentBuilder) { this.builder = paramDocumentBuilder; }
  
  public DOMResult createUnmarshaller(ValidationEventHandler paramValidationEventHandler) { return (this.builder == null) ? new DOMResult() : new DOMResult(this.builder.newDocument()); }
  
  public Element getElement(DOMResult paramDOMResult) {
    Node node = paramDOMResult.getNode();
    if (node instanceof Document)
      return ((Document)node).getDocumentElement(); 
    if (node instanceof Element)
      return (Element)node; 
    if (node instanceof org.w3c.dom.DocumentFragment)
      return (Element)node.getChildNodes().item(0); 
    throw new IllegalStateException(node.toString());
  }
  
  public Source marshal(Element paramElement, ValidationEventHandler paramValidationEventHandler) { return new DOMSource(paramElement); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\W3CDomHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */