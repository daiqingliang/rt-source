package javax.smartcardio;

import java.util.List;

public abstract class CardTerminals {
  public List<CardTerminal> list() throws CardException { return list(State.ALL); }
  
  public abstract List<CardTerminal> list(State paramState) throws CardException;
  
  public CardTerminal getTerminal(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      for (CardTerminal cardTerminal : list()) {
        if (cardTerminal.getName().equals(paramString))
          return cardTerminal; 
      } 
      return null;
    } catch (CardException cardException) {
      return null;
    } 
  }
  
  public void waitForChange() { waitForChange(0L); }
  
  public abstract boolean waitForChange(long paramLong) throws CardException;
  
  public enum State {
    ALL, CARD_PRESENT, CARD_ABSENT, CARD_INSERTION, CARD_REMOVAL;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\CardTerminals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */