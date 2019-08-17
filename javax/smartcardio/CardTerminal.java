package javax.smartcardio;

public abstract class CardTerminal {
  public abstract String getName();
  
  public abstract Card connect(String paramString) throws CardException;
  
  public abstract boolean isCardPresent() throws CardException;
  
  public abstract boolean waitForCardPresent(long paramLong) throws CardException;
  
  public abstract boolean waitForCardAbsent(long paramLong) throws CardException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\CardTerminal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */