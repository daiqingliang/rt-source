package java.sql;

public class SQLIntegrityConstraintViolationException extends SQLNonTransientException {
  private static final long serialVersionUID = 8033405298774849169L;
  
  public SQLIntegrityConstraintViolationException() {}
  
  public SQLIntegrityConstraintViolationException(String paramString) { super(paramString); }
  
  public SQLIntegrityConstraintViolationException(String paramString1, String paramString2) { super(paramString1, paramString2); }
  
  public SQLIntegrityConstraintViolationException(String paramString1, String paramString2, int paramInt) { super(paramString1, paramString2, paramInt); }
  
  public SQLIntegrityConstraintViolationException(Throwable paramThrowable) { super(paramThrowable); }
  
  public SQLIntegrityConstraintViolationException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public SQLIntegrityConstraintViolationException(String paramString1, String paramString2, Throwable paramThrowable) { super(paramString1, paramString2, paramThrowable); }
  
  public SQLIntegrityConstraintViolationException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable) { super(paramString1, paramString2, paramInt, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\SQLIntegrityConstraintViolationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */