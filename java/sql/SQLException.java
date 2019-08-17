package java.sql;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class SQLException extends Exception implements Iterable<Throwable> {
  private String SQLState;
  
  private int vendorCode;
  
  private static final AtomicReferenceFieldUpdater<SQLException, SQLException> nextUpdater = AtomicReferenceFieldUpdater.newUpdater(SQLException.class, SQLException.class, "next");
  
  private static final long serialVersionUID = 2135244094396331484L;
  
  public SQLException(String paramString1, String paramString2, int paramInt) {
    super(paramString1);
    this.SQLState = paramString2;
    this.vendorCode = paramInt;
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
      DriverManager.println("SQLState(" + paramString2 + ") vendor code(" + paramInt + ")");
      printStackTrace(DriverManager.getLogWriter());
    } 
  }
  
  public SQLException(String paramString1, String paramString2) {
    super(paramString1);
    this.SQLState = paramString2;
    this.vendorCode = 0;
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
      printStackTrace(DriverManager.getLogWriter());
      DriverManager.println("SQLException: SQLState(" + paramString2 + ")");
    } 
  }
  
  public SQLException(String paramString) {
    super(paramString);
    this.SQLState = null;
    this.vendorCode = 0;
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null)
      printStackTrace(DriverManager.getLogWriter()); 
  }
  
  public SQLException() {
    this.SQLState = null;
    this.vendorCode = 0;
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null)
      printStackTrace(DriverManager.getLogWriter()); 
  }
  
  public SQLException(Throwable paramThrowable) {
    super(paramThrowable);
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null)
      printStackTrace(DriverManager.getLogWriter()); 
  }
  
  public SQLException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null)
      printStackTrace(DriverManager.getLogWriter()); 
  }
  
  public SQLException(String paramString1, String paramString2, Throwable paramThrowable) {
    super(paramString1, paramThrowable);
    this.SQLState = paramString2;
    this.vendorCode = 0;
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
      printStackTrace(DriverManager.getLogWriter());
      DriverManager.println("SQLState(" + this.SQLState + ")");
    } 
  }
  
  public SQLException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable) {
    super(paramString1, paramThrowable);
    this.SQLState = paramString2;
    this.vendorCode = paramInt;
    if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
      DriverManager.println("SQLState(" + this.SQLState + ") vendor code(" + paramInt + ")");
      printStackTrace(DriverManager.getLogWriter());
    } 
  }
  
  public String getSQLState() { return this.SQLState; }
  
  public int getErrorCode() { return this.vendorCode; }
  
  public SQLException getNextException() { return this.next; }
  
  public void setNextException(SQLException paramSQLException) {
    for (SQLException sQLException = this;; sQLException = sQLException.next) {
      SQLException sQLException1 = sQLException.next;
      if (sQLException1 != null) {
        sQLException = sQLException1;
        continue;
      } 
      if (nextUpdater.compareAndSet(sQLException, null, paramSQLException))
        return; 
    } 
  }
  
  public Iterator<Throwable> iterator() { return new Iterator<Throwable>() {
        SQLException firstException = SQLException.this;
        
        SQLException nextException = this.firstException.getNextException();
        
        Throwable cause = this.firstException.getCause();
        
        public boolean hasNext() { return (this.firstException != null || this.nextException != null || this.cause != null); }
        
        public Throwable next() {
          SQLException sQLException = null;
          if (this.firstException != null) {
            sQLException = this.firstException;
            this.firstException = null;
          } else if (this.cause != null) {
            Throwable throwable = this.cause;
            this.cause = this.cause.getCause();
          } else if (this.nextException != null) {
            sQLException = this.nextException;
            this.cause = this.nextException.getCause();
            this.nextException = this.nextException.getNextException();
          } else {
            throw new NoSuchElementException();
          } 
          return sQLException;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\SQLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */