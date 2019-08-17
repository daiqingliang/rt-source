package javax.swing.text;

import java.util.Enumeration;
import java.util.Stack;

public class ElementIterator implements Cloneable {
  private Element root;
  
  private Stack<StackItem> elementStack = null;
  
  public ElementIterator(Document paramDocument) { this.root = paramDocument.getDefaultRootElement(); }
  
  public ElementIterator(Element paramElement) { this.root = paramElement; }
  
  public Object clone() {
    try {
      ElementIterator elementIterator = new ElementIterator(this.root);
      if (this.elementStack != null) {
        elementIterator.elementStack = new Stack();
        for (byte b = 0; b < this.elementStack.size(); b++) {
          StackItem stackItem1 = (StackItem)this.elementStack.elementAt(b);
          StackItem stackItem2 = (StackItem)stackItem1.clone();
          elementIterator.elementStack.push(stackItem2);
        } 
      } 
      return elementIterator;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public Element first() {
    if (this.root == null)
      return null; 
    this.elementStack = new Stack();
    if (this.root.getElementCount() != 0)
      this.elementStack.push(new StackItem(this.root, null)); 
    return this.root;
  }
  
  public int depth() { return (this.elementStack == null) ? 0 : this.elementStack.size(); }
  
  public Element current() {
    if (this.elementStack == null)
      return first(); 
    if (!this.elementStack.empty()) {
      StackItem stackItem;
      Element element = stackItem.getElement();
      int i = stackItem.getIndex();
      return (i == -1) ? element : element.getElement(i);
    } 
    return null;
  }
  
  public Element next() {
    if (this.elementStack == null)
      return first(); 
    if (this.elementStack.isEmpty())
      return null; 
    StackItem stackItem;
    Element element = stackItem.getElement();
    int i = stackItem.getIndex();
    if (i + 1 < element.getElementCount()) {
      Element element1 = element.getElement(i + 1);
      if (element1.isLeaf()) {
        stackItem.incrementIndex();
      } else {
        this.elementStack.push(new StackItem(element1, null));
      } 
      return element1;
    } 
    this.elementStack.pop();
    if (!this.elementStack.isEmpty()) {
      StackItem stackItem1;
      stackItem1.incrementIndex();
      return next();
    } 
    return null;
  }
  
  public Element previous() {
    int i;
    if (this.elementStack == null || (i = this.elementStack.size()) == 0)
      return null; 
    StackItem stackItem;
    Element element = stackItem.getElement();
    int j = stackItem.getIndex();
    if (j > 0)
      return getDeepestLeaf(element.getElement(--j)); 
    if (j == 0)
      return element; 
    if (j == -1) {
      if (i == 1)
        return null; 
      StackItem stackItem1 = (StackItem)this.elementStack.pop();
      stackItem = (StackItem)this.elementStack.peek();
      this.elementStack.push(stackItem1);
      element = stackItem.getElement();
      j = stackItem.getIndex();
      return (j == -1) ? element : getDeepestLeaf(element.getElement(j));
    } 
    return null;
  }
  
  private Element getDeepestLeaf(Element paramElement) {
    if (paramElement.isLeaf())
      return paramElement; 
    int i = paramElement.getElementCount();
    return (i == 0) ? paramElement : getDeepestLeaf(paramElement.getElement(i - 1));
  }
  
  private void dumpTree() {
    Element element;
    while ((element = next()) != null) {
      System.out.println("elem: " + element.getName());
      AttributeSet attributeSet = element.getAttributes();
      String str = "";
      Enumeration enumeration = attributeSet.getAttributeNames();
      while (enumeration.hasMoreElements()) {
        Object object1 = enumeration.nextElement();
        Object object2 = attributeSet.getAttribute(object1);
        if (object2 instanceof AttributeSet) {
          str = str + object1 + "=**AttributeSet** ";
          continue;
        } 
        str = str + object1 + "=" + object2 + " ";
      } 
      System.out.println("attributes: " + str);
    } 
  }
  
  private class StackItem implements Cloneable {
    Element item;
    
    int childIndex;
    
    private StackItem(Element param1Element) {
      this.item = param1Element;
      this.childIndex = -1;
    }
    
    private void incrementIndex() { this.childIndex++; }
    
    private Element getElement() { return this.item; }
    
    private int getIndex() { return this.childIndex; }
    
    protected Object clone() { return super.clone(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\ElementIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */