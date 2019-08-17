package javax.xml.transform;

public interface SourceLocator {
  String getPublicId();
  
  String getSystemId();
  
  int getLineNumber();
  
  int getColumnNumber();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\SourceLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */