package org.omg.CORBA.portable;

public class ApplicationException extends Exception {
  private String id;
  
  private InputStream ins;
  
  public ApplicationException(String paramString, InputStream paramInputStream) {
    this.id = paramString;
    this.ins = paramInputStream;
  }
  
  public String getId() { return this.id; }
  
  public InputStream getInputStream() { return this.ins; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\ApplicationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */