package javax.xml.soap;

public class MimeHeader {
  private String name;
  
  private String value;
  
  public MimeHeader(String paramString1, String paramString2) {
    this.name = paramString1;
    this.value = paramString2;
  }
  
  public String getName() { return this.name; }
  
  public String getValue() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\MimeHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */