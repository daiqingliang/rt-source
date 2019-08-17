package javax.tools;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.annotation.processing.Processor;

public interface JavaCompiler extends Tool, OptionChecker {
  CompilationTask getTask(Writer paramWriter, JavaFileManager paramJavaFileManager, DiagnosticListener<? super JavaFileObject> paramDiagnosticListener, Iterable<String> paramIterable1, Iterable<String> paramIterable2, Iterable<? extends JavaFileObject> paramIterable3);
  
  StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> paramDiagnosticListener, Locale paramLocale, Charset paramCharset);
  
  public static interface CompilationTask extends Callable<Boolean> {
    void setProcessors(Iterable<? extends Processor> param1Iterable);
    
    void setLocale(Locale param1Locale);
    
    Boolean call();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\JavaCompiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */