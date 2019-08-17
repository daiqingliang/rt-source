package javax.xml.transform.sax;

import javax.xml.transform.Templates;
import org.xml.sax.ContentHandler;

public interface TemplatesHandler extends ContentHandler {
  Templates getTemplates();
  
  void setSystemId(String paramString);
  
  String getSystemId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\sax\TemplatesHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */