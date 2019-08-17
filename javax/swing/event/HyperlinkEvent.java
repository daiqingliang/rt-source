package javax.swing.event;

import java.awt.event.InputEvent;
import java.net.URL;
import java.util.EventObject;
import javax.swing.text.Element;

public class HyperlinkEvent extends EventObject {
  private EventType type;
  
  private URL u;
  
  private String desc;
  
  private Element sourceElement;
  
  private InputEvent inputEvent;
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL) { this(paramObject, paramEventType, paramURL, null); }
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL, String paramString) { this(paramObject, paramEventType, paramURL, paramString, null); }
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL, String paramString, Element paramElement) {
    super(paramObject);
    this.type = paramEventType;
    this.u = paramURL;
    this.desc = paramString;
    this.sourceElement = paramElement;
  }
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL, String paramString, Element paramElement, InputEvent paramInputEvent) {
    super(paramObject);
    this.type = paramEventType;
    this.u = paramURL;
    this.desc = paramString;
    this.sourceElement = paramElement;
    this.inputEvent = paramInputEvent;
  }
  
  public EventType getEventType() { return this.type; }
  
  public String getDescription() { return this.desc; }
  
  public URL getURL() { return this.u; }
  
  public Element getSourceElement() { return this.sourceElement; }
  
  public InputEvent getInputEvent() { return this.inputEvent; }
  
  public static final class EventType {
    public static final EventType ENTERED = new EventType("ENTERED");
    
    public static final EventType EXITED = new EventType("EXITED");
    
    public static final EventType ACTIVATED = new EventType("ACTIVATED");
    
    private String typeString;
    
    private EventType(String param1String) { this.typeString = param1String; }
    
    public String toString() { return this.typeString; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\HyperlinkEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */