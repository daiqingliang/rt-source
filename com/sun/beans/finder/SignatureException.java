package com.sun.beans.finder;

final class SignatureException extends RuntimeException {
  SignatureException(Throwable paramThrowable) { super(paramThrowable); }
  
  NoSuchMethodException toNoSuchMethodException(String paramString) {
    Throwable throwable = getCause();
    if (throwable instanceof NoSuchMethodException)
      return (NoSuchMethodException)throwable; 
    NoSuchMethodException noSuchMethodException = new NoSuchMethodException(paramString);
    noSuchMethodException.initCause(throwable);
    return noSuchMethodException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\SignatureException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */