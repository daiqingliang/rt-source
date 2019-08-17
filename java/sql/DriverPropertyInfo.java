package java.sql;

public class DriverPropertyInfo {
  public String name;
  
  public String description = null;
  
  public boolean required = false;
  
  public String value = null;
  
  public String[] choices = null;
  
  public DriverPropertyInfo(String paramString1, String paramString2) {
    this.name = paramString1;
    this.value = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\DriverPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */