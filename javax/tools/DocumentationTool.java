package javax.tools;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;

public interface DocumentationTool extends Tool, OptionChecker {
  DocumentationTask getTask(Writer paramWriter, JavaFileManager paramJavaFileManager, DiagnosticListener<? super JavaFileObject> paramDiagnosticListener, Class<?> paramClass, Iterable<String> paramIterable1, Iterable<? extends JavaFileObject> paramIterable2);
  
  StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> paramDiagnosticListener, Locale paramLocale, Charset paramCharset);
  
  public static interface DocumentationTask extends Callable<Boolean> {
    void setLocale(Locale param1Locale);
    
    Boolean call();
  }
  
  public enum Location implements JavaFileManager.Location {
    DOCUMENTATION_OUTPUT, DOCLET_PATH, TAGLET_PATH;
    
    public String getName() { return name(); }
    
    public boolean isOutputLocation() {
      switch (DocumentationTool.null.$SwitchMap$javax$tools$DocumentationTool$Location[ordinal()]) {
        case 1:
          return true;
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\DocumentationTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */