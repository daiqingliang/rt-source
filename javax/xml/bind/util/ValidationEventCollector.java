package javax.xml.bind.util;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class ValidationEventCollector implements ValidationEventHandler {
  private final List<ValidationEvent> events = new ArrayList();
  
  public ValidationEvent[] getEvents() { return (ValidationEvent[])this.events.toArray(new ValidationEvent[this.events.size()]); }
  
  public void reset() { this.events.clear(); }
  
  public boolean hasEvents() { return !this.events.isEmpty(); }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent) {
    this.events.add(paramValidationEvent);
    null = true;
    switch (paramValidationEvent.getSeverity()) {
      case 0:
        return true;
      case 1:
        return true;
      case 2:
        return false;
    } 
    _assert(false, Messages.format("ValidationEventCollector.UnrecognizedSeverity", Integer.valueOf(paramValidationEvent.getSeverity())));
    return SYNTHETIC_LOCAL_VARIABLE_2;
  }
  
  private static void _assert(boolean paramBoolean, String paramString) {
    if (!paramBoolean)
      throw new InternalError(paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bin\\util\ValidationEventCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */