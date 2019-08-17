package javax.sql.rowset;

import java.sql.SQLException;

public class RowSetWarning extends SQLException {
  static final long serialVersionUID = 6678332766434564774L;
  
  public RowSetWarning(String paramString) { super(paramString); }
  
  public RowSetWarning() {}
  
  public RowSetWarning(String paramString1, String paramString2) { super(paramString1, paramString2); }
  
  public RowSetWarning(String paramString1, String paramString2, int paramInt) { super(paramString1, paramString2, paramInt); }
  
  public RowSetWarning getNextWarning() {
    SQLException sQLException = getNextException();
    if (sQLException == null || sQLException instanceof RowSetWarning)
      return (RowSetWarning)sQLException; 
    throw new Error("RowSetWarning chain holds value that is not a RowSetWarning: ");
  }
  
  public void setNextWarning(RowSetWarning paramRowSetWarning) { setNextException(paramRowSetWarning); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\RowSetWarning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */