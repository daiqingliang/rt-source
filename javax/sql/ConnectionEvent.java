package javax.sql;

import java.sql.SQLException;
import java.util.EventObject;

public class ConnectionEvent extends EventObject {
  private SQLException ex = null;
  
  static final long serialVersionUID = -4843217645290030002L;
  
  public ConnectionEvent(PooledConnection paramPooledConnection) { super(paramPooledConnection); }
  
  public ConnectionEvent(PooledConnection paramPooledConnection, SQLException paramSQLException) {
    super(paramPooledConnection);
    this.ex = paramSQLException;
  }
  
  public SQLException getSQLException() { return this.ex; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\ConnectionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */