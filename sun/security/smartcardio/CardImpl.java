package sun.security.smartcardio;

import java.security.AccessController;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardPermission;
import sun.security.action.GetPropertyAction;

final class CardImpl extends Card {
  private final TerminalImpl terminal;
  
  final long cardId;
  
  private final ATR atr;
  
  final int protocol;
  
  private final ChannelImpl basicChannel;
  
  private static final boolean isWindows;
  
  private static byte[] commandOpenChannel;
  
  private static final boolean invertReset;
  
  CardImpl(TerminalImpl paramTerminalImpl, String paramString) throws PCSCException {
    this.terminal = paramTerminalImpl;
    byte b1 = 2;
    if (paramString.equals("*")) {
      b2 = 3;
    } else if (paramString.equalsIgnoreCase("T=0")) {
      b2 = 1;
    } else if (paramString.equalsIgnoreCase("T=1")) {
      b2 = 2;
    } else if (paramString.equalsIgnoreCase("direct")) {
      b2 = isWindows ? 0 : 65536;
      b1 = 3;
    } else {
      throw new IllegalArgumentException("Unsupported protocol " + paramString);
    } 
    this.cardId = PCSC.SCardConnect(paramTerminalImpl.contextId, paramTerminalImpl.name, b1, b2);
    byte[] arrayOfByte1 = new byte[2];
    byte[] arrayOfByte2 = PCSC.SCardStatus(this.cardId, arrayOfByte1);
    this.atr = new ATR(arrayOfByte2);
    this.protocol = arrayOfByte1[1] & 0xFF;
    this.basicChannel = new ChannelImpl(this, 0);
    this.state = State.OK;
  }
  
  void checkState() {
    State state1 = this.state;
    if (state1 == State.DISCONNECTED)
      throw new IllegalStateException("Card has been disconnected"); 
    if (state1 == State.REMOVED)
      throw new IllegalStateException("Card has been removed"); 
  }
  
  boolean isValid() {
    if (this.state != State.OK)
      return false; 
    try {
      PCSC.SCardStatus(this.cardId, new byte[2]);
      return true;
    } catch (PCSCException pCSCException) {
      this.state = State.REMOVED;
      return false;
    } 
  }
  
  private void checkSecurity(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new CardPermission(this.terminal.name, paramString)); 
  }
  
  void handleError(PCSCException paramPCSCException) {
    if (paramPCSCException.code == -2146434967)
      this.state = State.REMOVED; 
  }
  
  public ATR getATR() { return this.atr; }
  
  public String getProtocol() {
    switch (this.protocol) {
      case 1:
        return "T=0";
      case 2:
        return "T=1";
    } 
    return "Unknown protocol " + this.protocol;
  }
  
  public CardChannel getBasicChannel() {
    checkSecurity("getBasicChannel");
    checkState();
    return this.basicChannel;
  }
  
  private static int getSW(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length < 2)
      return -1; 
    byte b1 = paramArrayOfByte[paramArrayOfByte.length - 2] & 0xFF;
    byte b2 = paramArrayOfByte[paramArrayOfByte.length - 1] & 0xFF;
    return b1 << 8 | b2;
  }
  
  public CardChannel openLogicalChannel() {
    checkSecurity("openLogicalChannel");
    checkState();
    checkExclusive();
    try {
      byte[] arrayOfByte = PCSC.SCardTransmit(this.cardId, this.protocol, commandOpenChannel, 0, commandOpenChannel.length);
      if (arrayOfByte.length != 3 || getSW(arrayOfByte) != 36864)
        throw new CardException("openLogicalChannel() failed, card response: " + PCSC.toString(arrayOfByte)); 
      return new ChannelImpl(this, arrayOfByte[0]);
    } catch (PCSCException pCSCException) {
      handleError(pCSCException);
      throw new CardException("openLogicalChannel() failed", pCSCException);
    } 
  }
  
  void checkExclusive() {
    Thread thread = this.exclusiveThread;
    if (thread == null)
      return; 
    if (thread != Thread.currentThread())
      throw new CardException("Exclusive access established by another Thread"); 
  }
  
  public void beginExclusive() {
    checkSecurity("exclusive");
    checkState();
    if (this.exclusiveThread != null)
      throw new CardException("Exclusive access has already been assigned to Thread " + this.exclusiveThread.getName()); 
    try {
      PCSC.SCardBeginTransaction(this.cardId);
    } catch (PCSCException pCSCException) {
      handleError(pCSCException);
      throw new CardException("beginExclusive() failed", pCSCException);
    } 
    this.exclusiveThread = Thread.currentThread();
  }
  
  public void endExclusive() {
    checkState();
    if (this.exclusiveThread != Thread.currentThread())
      throw new IllegalStateException("Exclusive access not assigned to current Thread"); 
    try {
      PCSC.SCardEndTransaction(this.cardId, 0);
    } catch (PCSCException pCSCException) {
      handleError(pCSCException);
      throw new CardException("endExclusive() failed", pCSCException);
    } finally {
      this.exclusiveThread = null;
    } 
  }
  
  public byte[] transmitControlCommand(int paramInt, byte[] paramArrayOfByte) throws CardException {
    checkSecurity("transmitControl");
    checkState();
    checkExclusive();
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    try {
      return PCSC.SCardControl(this.cardId, paramInt, paramArrayOfByte);
    } catch (PCSCException pCSCException) {
      handleError(pCSCException);
      throw new CardException("transmitControlCommand() failed", pCSCException);
    } 
  }
  
  public void disconnect(boolean paramBoolean) throws CardException {
    if (paramBoolean)
      checkSecurity("reset"); 
    if (this.state != State.OK)
      return; 
    checkExclusive();
    if (invertReset)
      paramBoolean = !paramBoolean; 
    try {
      PCSC.SCardDisconnect(this.cardId, paramBoolean ? 1 : 0);
    } catch (PCSCException pCSCException) {
      throw new CardException("disconnect() failed", pCSCException);
    } finally {
      this.state = State.DISCONNECTED;
      this.exclusiveThread = null;
    } 
  }
  
  public String toString() { return "PC/SC card in " + this.terminal.name + ", protocol " + getProtocol() + ", state " + this.state; }
  
  protected void finalize() {
    try {
      if (this.state == State.OK) {
        this.state = State.DISCONNECTED;
        PCSC.SCardDisconnect(this.cardId, 0);
      } 
    } finally {
      super.finalize();
    } 
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(() -> System.getProperty("os.name"));
    isWindows = str.startsWith("Windows");
    commandOpenChannel = new byte[] { 0, 112, 0, 0, 1 };
    invertReset = Boolean.parseBoolean((String)AccessController.doPrivileged(new GetPropertyAction("sun.security.smartcardio.invertCardReset", "false")));
  }
  
  private enum State {
    OK, REMOVED, DISCONNECTED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\smartcardio\CardImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */