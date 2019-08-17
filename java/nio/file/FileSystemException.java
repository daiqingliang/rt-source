package java.nio.file;

import java.io.IOException;

public class FileSystemException extends IOException {
  static final long serialVersionUID = -3055425747967319812L;
  
  private final String file;
  
  private final String other;
  
  public FileSystemException(String paramString) {
    super((String)null);
    this.file = paramString;
    this.other = null;
  }
  
  public FileSystemException(String paramString1, String paramString2, String paramString3) {
    super(paramString3);
    this.file = paramString1;
    this.other = paramString2;
  }
  
  public String getFile() { return this.file; }
  
  public String getOtherFile() { return this.other; }
  
  public String getReason() { return super.getMessage(); }
  
  public String getMessage() {
    if (this.file == null && this.other == null)
      return getReason(); 
    StringBuilder stringBuilder = new StringBuilder();
    if (this.file != null)
      stringBuilder.append(this.file); 
    if (this.other != null) {
      stringBuilder.append(" -> ");
      stringBuilder.append(this.other);
    } 
    if (getReason() != null) {
      stringBuilder.append(": ");
      stringBuilder.append(getReason());
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\FileSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */