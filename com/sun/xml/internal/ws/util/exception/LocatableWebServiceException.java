package com.sun.xml.internal.ws.util.exception;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.resources.UtilMessages;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class LocatableWebServiceException extends WebServiceException {
  private final Locator[] location;
  
  public LocatableWebServiceException(String paramString, Locator... paramVarArgs) { this(paramString, null, paramVarArgs); }
  
  public LocatableWebServiceException(String paramString, Throwable paramThrowable, Locator... paramVarArgs) {
    super(appendLocationInfo(paramString, paramVarArgs), paramThrowable);
    this.location = paramVarArgs;
  }
  
  public LocatableWebServiceException(Throwable paramThrowable, Locator... paramVarArgs) { this(paramThrowable.toString(), paramThrowable, paramVarArgs); }
  
  public LocatableWebServiceException(String paramString, XMLStreamReader paramXMLStreamReader) { this(paramString, new Locator[] { toLocation(paramXMLStreamReader) }); }
  
  public LocatableWebServiceException(String paramString, Throwable paramThrowable, XMLStreamReader paramXMLStreamReader) { this(paramString, paramThrowable, new Locator[] { toLocation(paramXMLStreamReader) }); }
  
  public LocatableWebServiceException(Throwable paramThrowable, XMLStreamReader paramXMLStreamReader) { this(paramThrowable, new Locator[] { toLocation(paramXMLStreamReader) }); }
  
  @NotNull
  public List<Locator> getLocation() { return Arrays.asList(this.location); }
  
  private static String appendLocationInfo(String paramString, Locator[] paramArrayOfLocator) {
    StringBuilder stringBuilder = new StringBuilder(paramString);
    for (Locator locator : paramArrayOfLocator)
      stringBuilder.append('\n').append(UtilMessages.UTIL_LOCATION(Integer.valueOf(locator.getLineNumber()), locator.getSystemId())); 
    return stringBuilder.toString();
  }
  
  private static Locator toLocation(XMLStreamReader paramXMLStreamReader) {
    LocatorImpl locatorImpl = new LocatorImpl();
    Location location1 = paramXMLStreamReader.getLocation();
    locatorImpl.setSystemId(location1.getSystemId());
    locatorImpl.setPublicId(location1.getPublicId());
    locatorImpl.setLineNumber(location1.getLineNumber());
    locatorImpl.setColumnNumber(location1.getColumnNumber());
    return locatorImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\exception\LocatableWebServiceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */