package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.Vector;

public final class ContentModel implements Serializable {
  public int type;
  
  public Object content;
  
  public ContentModel next;
  
  private boolean[] valSet;
  
  private boolean[] val;
  
  public ContentModel() {}
  
  public ContentModel(Element paramElement) { this(0, paramElement, null); }
  
  public ContentModel(int paramInt, ContentModel paramContentModel) { this(paramInt, paramContentModel, null); }
  
  public ContentModel(int paramInt, Object paramObject, ContentModel paramContentModel) {
    this.type = paramInt;
    this.content = paramObject;
    this.next = paramContentModel;
  }
  
  public boolean empty() {
    ContentModel contentModel;
    switch (this.type) {
      case 42:
      case 63:
        return true;
      case 43:
      case 124:
        for (contentModel = (ContentModel)this.content; contentModel != null; contentModel = contentModel.next) {
          if (contentModel.empty())
            return true; 
        } 
        return false;
      case 38:
      case 44:
        for (contentModel = (ContentModel)this.content; contentModel != null; contentModel = contentModel.next) {
          if (!contentModel.empty())
            return false; 
        } 
        return true;
    } 
    return false;
  }
  
  public void getElements(Vector<Element> paramVector) {
    ContentModel contentModel;
    switch (this.type) {
      case 42:
      case 43:
      case 63:
        ((ContentModel)this.content).getElements(paramVector);
        return;
      case 38:
      case 44:
      case 124:
        for (contentModel = (ContentModel)this.content; contentModel != null; contentModel = contentModel.next)
          contentModel.getElements(paramVector); 
        return;
    } 
    paramVector.addElement((Element)this.content);
  }
  
  public boolean first(Object paramObject) {
    ContentModel contentModel2;
    ContentModel contentModel1;
    Element element;
    switch (this.type) {
      case 42:
      case 43:
      case 63:
        return ((ContentModel)this.content).first(paramObject);
      case 44:
        for (contentModel1 = (ContentModel)this.content; contentModel1 != null; contentModel1 = contentModel1.next) {
          if (contentModel1.first(paramObject))
            return true; 
          if (!contentModel1.empty())
            return false; 
        } 
        return false;
      case 38:
      case 124:
        if (this.valSet == null || this.valSet.length <= (element = (Element)paramObject).getMaxIndex()) {
          this.valSet = new boolean[Element.getMaxIndex() + 1];
          this.val = new boolean[this.valSet.length];
        } 
        if (this.valSet[element.index])
          return this.val[element.index]; 
        for (contentModel2 = (ContentModel)this.content; contentModel2 != null; contentModel2 = contentModel2.next) {
          if (contentModel2.first(paramObject)) {
            this.val[element.index] = true;
            break;
          } 
        } 
        this.valSet[element.index] = true;
        return this.val[element.index];
    } 
    return (this.content == paramObject);
  }
  
  public Element first() {
    switch (this.type) {
      case 38:
      case 42:
      case 63:
      case 124:
        return null;
      case 43:
      case 44:
        return ((ContentModel)this.content).first();
    } 
    return (Element)this.content;
  }
  
  public String toString() {
    ContentModel contentModel;
    String str;
    char[] arrayOfChar;
    switch (this.type) {
      case 42:
        return this.content + "*";
      case 63:
        return this.content + "?";
      case 43:
        return this.content + "+";
      case 38:
      case 44:
      case 124:
        arrayOfChar = new char[] { ' ', (char)this.type, ' ' };
        str = "";
        for (contentModel = (ContentModel)this.content; contentModel != null; contentModel = contentModel.next) {
          str = str + contentModel;
          if (contentModel.next != null)
            str = str + new String(arrayOfChar); 
        } 
        return "(" + str + ")";
    } 
    return this.content.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\ContentModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */