package javax.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EventObject;

public class StatementEvent extends EventObject {
  static final long serialVersionUID = -8089573731826608315L;
  
  private SQLException exception;
  
  private PreparedStatement statement;
  
  public StatementEvent(PooledConnection paramPooledConnection, PreparedStatement paramPreparedStatement) {
    super(paramPooledConnection);
    this.statement = paramPreparedStatement;
    this.exception = null;
  }
  
  public StatementEvent(PooledConnection paramPooledConnection, PreparedStatement paramPreparedStatement, SQLException paramSQLException) {
    super(paramPooledConnection);
    this.statement = paramPreparedStatement;
    this.exception = paramSQLException;
  }
  
  public PreparedStatement getStatement() { return this.statement; }
  
  public SQLException getSQLException() { return this.exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\StatementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */