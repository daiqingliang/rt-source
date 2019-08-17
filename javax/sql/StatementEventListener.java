package javax.sql;

import java.util.EventListener;

public interface StatementEventListener extends EventListener {
  void statementClosed(StatementEvent paramStatementEvent);
  
  void statementErrorOccurred(StatementEvent paramStatementEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\StatementEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */