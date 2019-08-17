package sun.security.smartcardio;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;

final class PCSCTerminals extends CardTerminals {
  private static long contextId;
  
  private Map<String, ReaderState> stateMap;
  
  private static final Map<String, Reference<TerminalImpl>> terminals = new HashMap();
  
  static void initContext() {
    if (contextId == 0L)
      contextId = PCSC.SCardEstablishContext(0); 
  }
  
  private static TerminalImpl implGetTerminal(String paramString) {
    Reference reference = (Reference)terminals.get(paramString);
    TerminalImpl terminalImpl = (reference != null) ? (TerminalImpl)reference.get() : null;
    if (terminalImpl != null)
      return terminalImpl; 
    terminalImpl = new TerminalImpl(contextId, paramString);
    terminals.put(paramString, new WeakReference(terminalImpl));
    return terminalImpl;
  }
  
  public List<CardTerminal> list(CardTerminals.State paramState) throws CardException {
    if (paramState == null)
      throw new NullPointerException(); 
    try {
      String[] arrayOfString = PCSC.SCardListReaders(contextId);
      ArrayList arrayList = new ArrayList(arrayOfString.length);
      if (this.stateMap == null)
        if (paramState == CardTerminals.State.CARD_INSERTION) {
          paramState = CardTerminals.State.CARD_PRESENT;
        } else if (paramState == CardTerminals.State.CARD_REMOVAL) {
          paramState = CardTerminals.State.CARD_ABSENT;
        }  
      for (String str : arrayOfString) {
        ReaderState readerState;
        TerminalImpl terminalImpl = implGetTerminal(str);
        switch (paramState) {
          case ALL:
            arrayList.add(terminalImpl);
            break;
          case CARD_PRESENT:
            if (terminalImpl.isCardPresent())
              arrayList.add(terminalImpl); 
            break;
          case CARD_ABSENT:
            if (!terminalImpl.isCardPresent())
              arrayList.add(terminalImpl); 
            break;
          case CARD_INSERTION:
            readerState = (ReaderState)this.stateMap.get(str);
            if (readerState != null && readerState.isInsertion())
              arrayList.add(terminalImpl); 
            break;
          case CARD_REMOVAL:
            readerState = (ReaderState)this.stateMap.get(str);
            if (readerState != null && readerState.isRemoval())
              arrayList.add(terminalImpl); 
            break;
          default:
            throw new CardException("Unknown state: " + paramState);
        } 
      } 
      return Collections.unmodifiableList(arrayList);
    } catch (PCSCException pCSCException) {
      throw new CardException("list() failed", pCSCException);
    } 
  }
  
  public boolean waitForChange(long paramLong) throws CardException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("Timeout must not be negative: " + paramLong); 
    if (this.stateMap == null) {
      this.stateMap = new HashMap();
      waitForChange(0L);
    } 
    if (paramLong == 0L)
      paramLong = -1L; 
    try {
      String[] arrayOfString = PCSC.SCardListReaders(contextId);
      int i = arrayOfString.length;
      if (i == 0)
        throw new IllegalStateException("No terminals available"); 
      int[] arrayOfInt = new int[i];
      ReaderState[] arrayOfReaderState = new ReaderState[i];
      byte b;
      for (b = 0; b < arrayOfString.length; b++) {
        String str = arrayOfString[b];
        ReaderState readerState = (ReaderState)this.stateMap.get(str);
        if (readerState == null)
          readerState = new ReaderState(); 
        arrayOfReaderState[b] = readerState;
        arrayOfInt[b] = readerState.get();
      } 
      arrayOfInt = PCSC.SCardGetStatusChange(contextId, paramLong, arrayOfInt, arrayOfString);
      this.stateMap.clear();
      for (b = 0; b < i; b++) {
        ReaderState readerState = arrayOfReaderState[b];
        readerState.update(arrayOfInt[b]);
        this.stateMap.put(arrayOfString[b], readerState);
      } 
      return true;
    } catch (PCSCException pCSCException) {
      if (pCSCException.code == -2146435062)
        return false; 
      throw new CardException("waitForChange() failed", pCSCException);
    } 
  }
  
  static List<CardTerminal> waitForCards(List<? extends CardTerminal> paramList, long paramLong, boolean paramBoolean) throws CardException {
    long l;
    if (paramLong == 0L) {
      paramLong = -1L;
      l = -1L;
    } else {
      l = 0L;
    } 
    String[] arrayOfString = new String[paramList.size()];
    byte b = 0;
    for (CardTerminal cardTerminal : paramList) {
      if (!(cardTerminal instanceof TerminalImpl))
        throw new IllegalArgumentException("Invalid terminal type: " + cardTerminal.getClass().getName()); 
      TerminalImpl terminalImpl = (TerminalImpl)cardTerminal;
      arrayOfString[b++] = terminalImpl.name;
    } 
    int[] arrayOfInt = new int[arrayOfString.length];
    Arrays.fill(arrayOfInt, 0);
    try {
      ArrayList arrayList;
      do {
        arrayOfInt = PCSC.SCardGetStatusChange(contextId, l, arrayOfInt, arrayOfString);
        l = paramLong;
        arrayList = null;
        for (b = 0; b < arrayOfString.length; b++) {
          boolean bool = ((arrayOfInt[b] & 0x20) != 0) ? 1 : 0;
          if (bool == paramBoolean) {
            if (arrayList == null)
              arrayList = new ArrayList(); 
            arrayList.add(implGetTerminal(arrayOfString[b]));
          } 
        } 
      } while (arrayList == null);
      return Collections.unmodifiableList(arrayList);
    } catch (PCSCException pCSCException) {
      if (pCSCException.code == -2146435062)
        return Collections.emptyList(); 
      throw new CardException("waitForCard() failed", pCSCException);
    } 
  }
  
  private static class ReaderState {
    private int current = 0;
    
    private int previous = 0;
    
    int get() { return this.current; }
    
    void update(int param1Int) {
      this.previous = this.current;
      this.current = param1Int;
    }
    
    boolean isInsertion() { return (!present(this.previous) && present(this.current)); }
    
    boolean isRemoval() { return (present(this.previous) && !present(this.current)); }
    
    static boolean present(int param1Int) { return ((param1Int & 0x20) != 0); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\smartcardio\PCSCTerminals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */