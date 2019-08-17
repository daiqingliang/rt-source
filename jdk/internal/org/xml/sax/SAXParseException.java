package jdk.internal.org.xml.sax;

public class SAXParseException extends SAXException {
  private String publicId;
  
  private String systemId;
  
  private int lineNumber;
  
  private int columnNumber;
  
  static final long serialVersionUID = -5651165872476709336L;
  
  public SAXParseException(String paramString, Locator paramLocator) {
    super(paramString);
    if (paramLocator != null) {
      init(paramLocator.getPublicId(), paramLocator.getSystemId(), paramLocator.getLineNumber(), paramLocator.getColumnNumber());
    } else {
      init(null, null, -1, -1);
    } 
  }
  
  public SAXParseException(String paramString, Locator paramLocator, Exception paramException) {
    super(paramString, paramException);
    if (paramLocator != null) {
      init(paramLocator.getPublicId(), paramLocator.getSystemId(), paramLocator.getLineNumber(), paramLocator.getColumnNumber());
    } else {
      init(null, null, -1, -1);
    } 
  }
  
  public SAXParseException(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2) {
    super(paramString1);
    init(paramString2, paramString3, paramInt1, paramInt2);
  }
  
  public SAXParseException(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, Exception paramException) {
    super(paramString1, paramException);
    init(paramString2, paramString3, paramInt1, paramInt2);
  }
  
  private void init(String paramString1, String paramString2, int paramInt1, int paramInt2) {
    this.publicId = paramString1;
    this.systemId = paramString2;
    this.lineNumber = paramInt1;
    this.columnNumber = paramInt2;
  }
  
  public String getPublicId() { return this.publicId; }
  
  public String getSystemId() { return this.systemId; }
  
  public int getLineNumber() { return this.lineNumber; }
  
  public int getColumnNumber() { return this.columnNumber; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(getClass().getName());
    String str = getLocalizedMessage();
    if (this.publicId != null)
      stringBuilder.append("publicId: ").append(this.publicId); 
    if (this.systemId != null)
      stringBuilder.append("; systemId: ").append(this.systemId); 
    if (this.lineNumber != -1)
      stringBuilder.append("; lineNumber: ").append(this.lineNumber); 
    if (this.columnNumber != -1)
      stringBuilder.append("; columnNumber: ").append(this.columnNumber); 
    if (str != null)
      stringBuilder.append("; ").append(str); 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\xml\sax\SAXParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */