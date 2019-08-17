package com.sun.xml.internal.ws.wsdl.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceException;

public class InaccessibleWSDLException extends WebServiceException {
  private final List<Throwable> errors;
  
  private static final long serialVersionUID = 1L;
  
  public InaccessibleWSDLException(List<Throwable> paramList) {
    super(paramList.size() + " counts of InaccessibleWSDLException.\n");
    assert !paramList.isEmpty() : "there must be at least one error";
    this.errors = Collections.unmodifiableList(new ArrayList(paramList));
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(super.toString());
    stringBuilder.append('\n');
    for (Throwable throwable : this.errors)
      stringBuilder.append(throwable.toString()).append('\n'); 
    return stringBuilder.toString();
  }
  
  public List<Throwable> getErrors() { return this.errors; }
  
  public static class Builder implements ErrorHandler {
    private final List<Throwable> list = new ArrayList();
    
    public void error(Throwable param1Throwable) { this.list.add(param1Throwable); }
    
    public void check() {
      if (this.list.isEmpty())
        return; 
      throw new InaccessibleWSDLException(this.list);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\InaccessibleWSDLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */