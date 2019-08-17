package javax.sql.rowset;

import java.sql.SQLException;

public interface FilteredRowSet extends WebRowSet {
  void setFilter(Predicate paramPredicate) throws SQLException;
  
  Predicate getFilter();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\FilteredRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */