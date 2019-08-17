package javax.xml.bind.helpers;

import javax.xml.bind.ParseConversionEvent;
import javax.xml.bind.ValidationEventLocator;

public class ParseConversionEventImpl extends ValidationEventImpl implements ParseConversionEvent {
  public ParseConversionEventImpl(int paramInt, String paramString, ValidationEventLocator paramValidationEventLocator) { super(paramInt, paramString, paramValidationEventLocator); }
  
  public ParseConversionEventImpl(int paramInt, String paramString, ValidationEventLocator paramValidationEventLocator, Throwable paramThrowable) { super(paramInt, paramString, paramValidationEventLocator, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\helpers\ParseConversionEventImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */