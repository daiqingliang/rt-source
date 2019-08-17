package java.sql;

import java.util.Map;

public class SQLClientInfoException extends SQLException {
  private Map<String, ClientInfoStatus> failedProperties;
  
  private static final long serialVersionUID = -4319604256824655880L;
  
  public SQLClientInfoException() { this.failedProperties = null; }
  
  public SQLClientInfoException(Map<String, ClientInfoStatus> paramMap) { this.failedProperties = paramMap; }
  
  public SQLClientInfoException(Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable) {
    super((paramThrowable != null) ? paramThrowable.toString() : null);
    initCause(paramThrowable);
    this.failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString, Map<String, ClientInfoStatus> paramMap) {
    super(paramString);
    this.failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString, Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable) {
    super(paramString);
    initCause(paramThrowable);
    this.failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, Map<String, ClientInfoStatus> paramMap) {
    super(paramString1, paramString2);
    this.failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable) {
    super(paramString1, paramString2);
    initCause(paramThrowable);
    this.failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, int paramInt, Map<String, ClientInfoStatus> paramMap) {
    super(paramString1, paramString2, paramInt);
    this.failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, int paramInt, Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable) {
    super(paramString1, paramString2, paramInt);
    initCause(paramThrowable);
    this.failedProperties = paramMap;
  }
  
  public Map<String, ClientInfoStatus> getFailedProperties() { return this.failedProperties; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\SQLClientInfoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */