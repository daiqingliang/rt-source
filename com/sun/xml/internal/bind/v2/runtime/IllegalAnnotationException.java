package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationException extends JAXBException {
  private final List<List<Location>> pos;
  
  private static final long serialVersionUID = 1L;
  
  public IllegalAnnotationException(String paramString, Locatable paramLocatable) {
    super(paramString);
    this.pos = build(new Locatable[] { paramLocatable });
  }
  
  public IllegalAnnotationException(String paramString, Annotation paramAnnotation) { this(paramString, cast(paramAnnotation)); }
  
  public IllegalAnnotationException(String paramString, Locatable paramLocatable1, Locatable paramLocatable2) {
    super(paramString);
    this.pos = build(new Locatable[] { paramLocatable1, paramLocatable2 });
  }
  
  public IllegalAnnotationException(String paramString, Annotation paramAnnotation1, Annotation paramAnnotation2) { this(paramString, cast(paramAnnotation1), cast(paramAnnotation2)); }
  
  public IllegalAnnotationException(String paramString, Annotation paramAnnotation, Locatable paramLocatable) { this(paramString, cast(paramAnnotation), paramLocatable); }
  
  public IllegalAnnotationException(String paramString, Throwable paramThrowable, Locatable paramLocatable) {
    super(paramString, paramThrowable);
    this.pos = build(new Locatable[] { paramLocatable });
  }
  
  private static Locatable cast(Annotation paramAnnotation) { return (paramAnnotation instanceof Locatable) ? (Locatable)paramAnnotation : null; }
  
  private List<List<Location>> build(Locatable... paramVarArgs) {
    ArrayList arrayList = new ArrayList();
    for (Locatable locatable : paramVarArgs) {
      if (locatable != null) {
        List list = convert(locatable);
        if (list != null && !list.isEmpty())
          arrayList.add(list); 
      } 
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  private List<Location> convert(Locatable paramLocatable) {
    if (paramLocatable == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    while (paramLocatable != null) {
      arrayList.add(paramLocatable.getLocation());
      paramLocatable = paramLocatable.getUpstream();
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  public List<List<Location>> getSourcePos() { return this.pos; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(getMessage());
    for (List list : this.pos) {
      stringBuilder.append("\n\tthis problem is related to the following location:");
      for (Location location : list)
        stringBuilder.append("\n\t\tat ").append(location.toString()); 
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\IllegalAnnotationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */