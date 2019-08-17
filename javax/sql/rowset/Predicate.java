package javax.sql.rowset;

import java.sql.SQLException;
import javax.sql.RowSet;

public interface Predicate {
  boolean evaluate(RowSet paramRowSet);
  
  boolean evaluate(Object paramObject, int paramInt) throws SQLException;
  
  boolean evaluate(Object paramObject, String paramString) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\Predicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */