package javax.xml.bind;

public class PropertyException extends JAXBException {
  public PropertyException(String paramString) { super(paramString); }
  
  public PropertyException(String paramString1, String paramString2) { super(paramString1, paramString2); }
  
  public PropertyException(Throwable paramThrowable) { super(paramThrowable); }
  
  public PropertyException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public PropertyException(String paramString1, String paramString2, Throwable paramThrowable) { super(paramString1, paramString2, paramThrowable); }
  
  public PropertyException(String paramString, Object paramObject) { super(Messages.format("PropertyException.NameValue", paramString, paramObject.toString())); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\PropertyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */