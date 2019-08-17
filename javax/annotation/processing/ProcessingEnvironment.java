package javax.annotation.processing;

import java.util.Locale;
import java.util.Map;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public interface ProcessingEnvironment {
  Map<String, String> getOptions();
  
  Messager getMessager();
  
  Filer getFiler();
  
  Elements getElementUtils();
  
  Types getTypeUtils();
  
  SourceVersion getSourceVersion();
  
  Locale getLocale();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\annotation\processing\ProcessingEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */