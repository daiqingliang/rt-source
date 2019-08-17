package javax.xml.transform;

public interface Result {
  public static final String PI_DISABLE_OUTPUT_ESCAPING = "javax.xml.transform.disable-output-escaping";
  
  public static final String PI_ENABLE_OUTPUT_ESCAPING = "javax.xml.transform.enable-output-escaping";
  
  void setSystemId(String paramString);
  
  String getSystemId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\Result.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */