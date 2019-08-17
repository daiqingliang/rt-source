package javax.xml.bind.helpers;

import java.net.URL;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;

public class DefaultValidationEventHandler implements ValidationEventHandler {
  public boolean handleEvent(ValidationEvent paramValidationEvent) {
    if (paramValidationEvent == null)
      throw new IllegalArgumentException(); 
    String str1 = null;
    boolean bool = false;
    switch (paramValidationEvent.getSeverity()) {
      case 0:
        str1 = Messages.format("DefaultValidationEventHandler.Warning");
        bool = true;
        str2 = getLocation(paramValidationEvent);
        System.out.println(Messages.format("DefaultValidationEventHandler.SeverityMessage", str1, paramValidationEvent.getMessage(), str2));
        return bool;
      case 1:
        str1 = Messages.format("DefaultValidationEventHandler.Error");
        bool = false;
        str2 = getLocation(paramValidationEvent);
        System.out.println(Messages.format("DefaultValidationEventHandler.SeverityMessage", str1, paramValidationEvent.getMessage(), str2));
        return bool;
      case 2:
        str1 = Messages.format("DefaultValidationEventHandler.FatalError");
        bool = false;
        str2 = getLocation(paramValidationEvent);
        System.out.println(Messages.format("DefaultValidationEventHandler.SeverityMessage", str1, paramValidationEvent.getMessage(), str2));
        return bool;
    } 
    assert false : Messages.format("DefaultValidationEventHandler.UnrecognizedSeverity", Integer.valueOf(paramValidationEvent.getSeverity()));
    String str2 = getLocation(paramValidationEvent);
    System.out.println(Messages.format("DefaultValidationEventHandler.SeverityMessage", str1, paramValidationEvent.getMessage(), str2));
    return bool;
  }
  
  private String getLocation(ValidationEvent paramValidationEvent) {
    StringBuffer stringBuffer = new StringBuffer();
    ValidationEventLocator validationEventLocator = paramValidationEvent.getLocator();
    if (validationEventLocator != null) {
      URL uRL = validationEventLocator.getURL();
      Object object = validationEventLocator.getObject();
      Node node = validationEventLocator.getNode();
      int i = validationEventLocator.getLineNumber();
      if (uRL != null || i != -1) {
        stringBuffer.append("line " + i);
        if (uRL != null)
          stringBuffer.append(" of " + uRL); 
      } else if (object != null) {
        stringBuffer.append(" obj: " + object.toString());
      } else if (node != null) {
        stringBuffer.append(" node: " + node.toString());
      } 
    } else {
      stringBuffer.append(Messages.format("DefaultValidationEventHandler.LocationUnavailable"));
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\helpers\DefaultValidationEventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */