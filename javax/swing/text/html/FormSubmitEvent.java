package javax.swing.text.html;

import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

public class FormSubmitEvent extends HTMLFrameHyperlinkEvent {
  private MethodType method;
  
  private String data;
  
  FormSubmitEvent(Object paramObject, HyperlinkEvent.EventType paramEventType, URL paramURL, Element paramElement, String paramString1, MethodType paramMethodType, String paramString2) {
    super(paramObject, paramEventType, paramURL, paramElement, paramString1);
    this.method = paramMethodType;
    this.data = paramString2;
  }
  
  public MethodType getMethod() { return this.method; }
  
  public String getData() { return this.data; }
  
  public enum MethodType {
    GET, POST;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\FormSubmitEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */