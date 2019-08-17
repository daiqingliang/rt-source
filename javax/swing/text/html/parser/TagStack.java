package javax.swing.text.html.parser;

import java.util.BitSet;

final class TagStack implements DTDConstants {
  TagElement tag;
  
  Element elem;
  
  ContentModelState state;
  
  TagStack next;
  
  BitSet inclusions;
  
  BitSet exclusions;
  
  boolean net;
  
  boolean pre;
  
  TagStack(TagElement paramTagElement, TagStack paramTagStack) {
    this.tag = paramTagElement;
    this.elem = paramTagElement.getElement();
    this.next = paramTagStack;
    Element element = paramTagElement.getElement();
    if (element.getContent() != null)
      this.state = new ContentModelState(element.getContent()); 
    if (paramTagStack != null) {
      this.inclusions = paramTagStack.inclusions;
      this.exclusions = paramTagStack.exclusions;
      this.pre = paramTagStack.pre;
    } 
    if (paramTagElement.isPreformatted())
      this.pre = true; 
    if (element.inclusions != null)
      if (this.inclusions != null) {
        this.inclusions = (BitSet)this.inclusions.clone();
        this.inclusions.or(element.inclusions);
      } else {
        this.inclusions = element.inclusions;
      }  
    if (element.exclusions != null)
      if (this.exclusions != null) {
        this.exclusions = (BitSet)this.exclusions.clone();
        this.exclusions.or(element.exclusions);
      } else {
        this.exclusions = element.exclusions;
      }  
  }
  
  public Element first() { return (this.state != null) ? this.state.first() : null; }
  
  public ContentModel contentModel() { return (this.state == null) ? null : this.state.getModel(); }
  
  boolean excluded(int paramInt) { return (this.exclusions != null && this.exclusions.get(this.elem.getIndex())); }
  
  boolean advance(Element paramElement) {
    if (this.exclusions != null && this.exclusions.get(paramElement.getIndex()))
      return false; 
    if (this.state != null) {
      ContentModelState contentModelState = this.state.advance(paramElement);
      if (contentModelState != null) {
        this.state = contentModelState;
        return true;
      } 
    } else if (this.elem.getType() == 19) {
      return true;
    } 
    return (this.inclusions != null && this.inclusions.get(paramElement.getIndex()));
  }
  
  boolean terminate() { return (this.state == null || this.state.terminate()); }
  
  public String toString() { return (this.next == null) ? ("<" + this.tag.getElement().getName() + ">") : (this.next + " <" + this.tag.getElement().getName() + ">"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\TagStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */