package javax.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DiagnosticCollector<S> extends Object implements DiagnosticListener<S> {
  private List<Diagnostic<? extends S>> diagnostics = Collections.synchronizedList(new ArrayList());
  
  public void report(Diagnostic<? extends S> paramDiagnostic) {
    paramDiagnostic.getClass();
    this.diagnostics.add(paramDiagnostic);
  }
  
  public List<Diagnostic<? extends S>> getDiagnostics() { return Collections.unmodifiableList(this.diagnostics); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\DiagnosticCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */