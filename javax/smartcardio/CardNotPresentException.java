package javax.smartcardio;

public class CardNotPresentException extends CardException {
  private static final long serialVersionUID = 1346879911706545215L;
  
  public CardNotPresentException(String paramString) { super(paramString); }
  
  public CardNotPresentException(Throwable paramThrowable) { super(paramThrowable); }
  
  public CardNotPresentException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\CardNotPresentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */