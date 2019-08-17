package javax.swing.event;

import javax.swing.text.Document;
import javax.swing.text.Element;

public interface DocumentEvent {
  int getOffset();
  
  int getLength();
  
  Document getDocument();
  
  EventType getType();
  
  ElementChange getChange(Element paramElement);
  
  public static interface ElementChange {
    Element getElement();
    
    int getIndex();
    
    Element[] getChildrenRemoved();
    
    Element[] getChildrenAdded();
  }
  
  public static final class EventType {
    public static final EventType INSERT = new EventType("INSERT");
    
    public static final EventType REMOVE = new EventType("REMOVE");
    
    public static final EventType CHANGE = new EventType("CHANGE");
    
    private String typeString;
    
    private EventType(String param1String) { this.typeString = param1String; }
    
    public String toString() { return this.typeString; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\DocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */