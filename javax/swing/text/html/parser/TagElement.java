package javax.swing.text.html.parser;

import javax.swing.text.html.HTML;

public class TagElement {
  Element elem;
  
  HTML.Tag htmlTag;
  
  boolean insertedByErrorRecovery;
  
  public TagElement(Element paramElement) { this(paramElement, false); }
  
  public TagElement(Element paramElement, boolean paramBoolean) {
    this.elem = paramElement;
    this.htmlTag = HTML.getTag(paramElement.getName());
    if (this.htmlTag == null)
      this.htmlTag = new HTML.UnknownTag(paramElement.getName()); 
    this.insertedByErrorRecovery = paramBoolean;
  }
  
  public boolean breaksFlow() { return this.htmlTag.breaksFlow(); }
  
  public boolean isPreformatted() { return this.htmlTag.isPreformatted(); }
  
  public Element getElement() { return this.elem; }
  
  public HTML.Tag getHTMLTag() { return this.htmlTag; }
  
  public boolean fictional() { return this.insertedByErrorRecovery; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\TagElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */