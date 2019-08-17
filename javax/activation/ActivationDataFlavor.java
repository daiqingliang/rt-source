package javax.activation;

import java.awt.datatransfer.DataFlavor;

public class ActivationDataFlavor extends DataFlavor {
  private String mimeType = null;
  
  private MimeType mimeObject = null;
  
  private String humanPresentableName = null;
  
  private Class representationClass = null;
  
  public ActivationDataFlavor(Class paramClass, String paramString1, String paramString2) {
    super(paramString1, paramString2);
    this.mimeType = paramString1;
    this.humanPresentableName = paramString2;
    this.representationClass = paramClass;
  }
  
  public ActivationDataFlavor(Class paramClass, String paramString) {
    super(paramClass, paramString);
    this.mimeType = super.getMimeType();
    this.representationClass = paramClass;
    this.humanPresentableName = paramString;
  }
  
  public ActivationDataFlavor(String paramString1, String paramString2) {
    super(paramString1, paramString2);
    this.mimeType = paramString1;
    try {
      this.representationClass = Class.forName("java.io.InputStream");
    } catch (ClassNotFoundException classNotFoundException) {}
    this.humanPresentableName = paramString2;
  }
  
  public String getMimeType() { return this.mimeType; }
  
  public Class getRepresentationClass() { return this.representationClass; }
  
  public String getHumanPresentableName() { return this.humanPresentableName; }
  
  public void setHumanPresentableName(String paramString) { this.humanPresentableName = paramString; }
  
  public boolean equals(DataFlavor paramDataFlavor) { return (isMimeTypeEqual(paramDataFlavor) && paramDataFlavor.getRepresentationClass() == this.representationClass); }
  
  public boolean isMimeTypeEqual(String paramString) {
    MimeType mimeType1 = null;
    try {
      if (this.mimeObject == null)
        this.mimeObject = new MimeType(this.mimeType); 
      mimeType1 = new MimeType(paramString);
    } catch (MimeTypeParseException mimeTypeParseException) {
      return this.mimeType.equalsIgnoreCase(paramString);
    } 
    return this.mimeObject.match(mimeType1);
  }
  
  protected String normalizeMimeTypeParameter(String paramString1, String paramString2) { return paramString2; }
  
  protected String normalizeMimeType(String paramString) { return paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\ActivationDataFlavor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */