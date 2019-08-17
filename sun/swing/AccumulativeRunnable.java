package sun.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

public abstract class AccumulativeRunnable<T> extends Object implements Runnable {
  private List<T> arguments = null;
  
  protected abstract void run(List<T> paramList);
  
  public final void run() { run(flush()); }
  
  @SafeVarargs
  public final void add(T... paramVarArgs) {
    boolean bool = true;
    if (this.arguments == null) {
      bool = false;
      this.arguments = new ArrayList();
    } 
    Collections.addAll(this.arguments, paramVarArgs);
    if (!bool)
      submit(); 
  }
  
  protected void submit() { SwingUtilities.invokeLater(this); }
  
  private final List<T> flush() {
    List list = this.arguments;
    this.arguments = null;
    return list;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\AccumulativeRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */