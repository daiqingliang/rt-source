package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationsException extends JAXBException {
  private final List<IllegalAnnotationException> errors;
  
  private static final long serialVersionUID = 1L;
  
  public IllegalAnnotationsException(List<IllegalAnnotationException> paramList) {
    super(paramList.size() + " counts of IllegalAnnotationExceptions");
    assert !paramList.isEmpty() : "there must be at least one error";
    this.errors = Collections.unmodifiableList(new ArrayList(paramList));
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(super.toString());
    stringBuilder.append('\n');
    for (IllegalAnnotationException illegalAnnotationException : this.errors)
      stringBuilder.append(illegalAnnotationException.toString()).append('\n'); 
    return stringBuilder.toString();
  }
  
  public List<IllegalAnnotationException> getErrors() { return this.errors; }
  
  public static class Builder implements ErrorHandler {
    private final List<IllegalAnnotationException> list = new ArrayList();
    
    public void error(IllegalAnnotationException param1IllegalAnnotationException) { this.list.add(param1IllegalAnnotationException); }
    
    public void check() {
      if (this.list.isEmpty())
        return; 
      throw new IllegalAnnotationsException(this.list);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\IllegalAnnotationsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */