package javax.xml.bind;

public class UnmarshalException extends JAXBException {
  public UnmarshalException(String paramString) { this(paramString, null, null); }
  
  public UnmarshalException(String paramString1, String paramString2) { this(paramString1, paramString2, null); }
  
  public UnmarshalException(Throwable paramThrowable) { this(null, null, paramThrowable); }
  
  public UnmarshalException(String paramString, Throwable paramThrowable) { this(paramString, null, paramThrowable); }
  
  public UnmarshalException(String paramString1, String paramString2, Throwable paramThrowable) { super(paramString1, paramString2, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\UnmarshalException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */