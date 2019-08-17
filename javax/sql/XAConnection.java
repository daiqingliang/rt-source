package javax.sql;

import java.sql.SQLException;
import javax.transaction.xa.XAResource;

public interface XAConnection extends PooledConnection {
  XAResource getXAResource() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\XAConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */