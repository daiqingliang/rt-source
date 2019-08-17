package sun.security.smartcardio;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardPermission;
import javax.smartcardio.CardTerminal;

final class TerminalImpl extends CardTerminal {
  final long contextId;
  
  final String name;
  
  private CardImpl card;
  
  TerminalImpl(long paramLong, String paramString) {
    this.contextId = paramLong;
    this.name = paramString;
  }
  
  public String getName() { return this.name; }
  
  public Card connect(String paramString) throws CardException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new CardPermission(this.name, "connect")); 
    if (this.card != null) {
      if (this.card.isValid()) {
        String str = this.card.getProtocol();
        if (paramString.equals("*") || paramString.equalsIgnoreCase(str))
          return this.card; 
        throw new CardException("Cannot connect using " + paramString + ", connection already established using " + str);
      } 
      this.card = null;
    } 
    try {
      this.card = new CardImpl(this, paramString);
      return this.card;
    } catch (PCSCException pCSCException) {
      if (pCSCException.code == -2146434967 || pCSCException.code == -2146435060)
        throw new CardNotPresentException("No card present", pCSCException); 
      throw new CardException("connect() failed", pCSCException);
    } 
  }
  
  public boolean isCardPresent() throws CardException {
    try {
      int[] arrayOfInt = PCSC.SCardGetStatusChange(this.contextId, 0L, new int[] { 0 }, new String[] { this.name });
      return ((arrayOfInt[0] & 0x20) != 0);
    } catch (PCSCException pCSCException) {
      throw new CardException("isCardPresent() failed", pCSCException);
    } 
  }
  
  private boolean waitForCard(boolean paramBoolean, long paramLong) throws CardException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("timeout must not be negative"); 
    if (paramLong == 0L)
      paramLong = -1L; 
    int[] arrayOfInt = { 0 };
    String[] arrayOfString = { this.name };
    try {
      arrayOfInt = PCSC.SCardGetStatusChange(this.contextId, 0L, arrayOfInt, arrayOfString);
      boolean bool = ((arrayOfInt[0] & 0x20) != 0) ? 1 : 0;
      if (paramBoolean == bool)
        return true; 
      long l = System.currentTimeMillis() + paramLong;
      while (paramBoolean != bool && paramLong != 0L) {
        if (paramLong != -1L)
          paramLong = Math.max(l - System.currentTimeMillis(), 0L); 
        arrayOfInt = PCSC.SCardGetStatusChange(this.contextId, paramLong, arrayOfInt, arrayOfString);
        bool = ((arrayOfInt[0] & 0x20) != 0) ? 1 : 0;
      } 
      return (paramBoolean == bool);
    } catch (PCSCException pCSCException) {
      if (pCSCException.code == -2146435062)
        return false; 
      throw new CardException("waitForCard() failed", pCSCException);
    } 
  }
  
  public boolean waitForCardPresent(long paramLong) throws CardException { return waitForCard(true, paramLong); }
  
  public boolean waitForCardAbsent(long paramLong) throws CardException { return waitForCard(false, paramLong); }
  
  public String toString() { return "PC/SC terminal " + this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\smartcardio\TerminalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */