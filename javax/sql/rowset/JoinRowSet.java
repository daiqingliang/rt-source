package javax.sql.rowset;

import java.sql.SQLException;
import java.util.Collection;
import javax.sql.RowSet;

public interface JoinRowSet extends WebRowSet {
  public static final int CROSS_JOIN = 0;
  
  public static final int INNER_JOIN = 1;
  
  public static final int LEFT_OUTER_JOIN = 2;
  
  public static final int RIGHT_OUTER_JOIN = 3;
  
  public static final int FULL_JOIN = 4;
  
  void addRowSet(Joinable paramJoinable) throws SQLException;
  
  void addRowSet(RowSet paramRowSet, int paramInt) throws SQLException;
  
  void addRowSet(RowSet paramRowSet, String paramString) throws SQLException;
  
  void addRowSet(RowSet[] paramArrayOfRowSet, int[] paramArrayOfInt) throws SQLException;
  
  void addRowSet(RowSet[] paramArrayOfRowSet, String[] paramArrayOfString) throws SQLException;
  
  Collection<?> getRowSets() throws SQLException;
  
  String[] getRowSetNames() throws SQLException;
  
  CachedRowSet toCachedRowSet() throws SQLException;
  
  boolean supportsCrossJoin();
  
  boolean supportsInnerJoin();
  
  boolean supportsLeftOuterJoin();
  
  boolean supportsRightOuterJoin();
  
  boolean supportsFullJoin();
  
  void setJoinType(int paramInt) throws SQLException;
  
  String getWhereClause() throws SQLException;
  
  int getJoinType() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\JoinRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */