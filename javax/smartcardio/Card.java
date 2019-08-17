package javax.smartcardio;

public abstract class Card {
  public abstract ATR getATR();
  
  public abstract String getProtocol();
  
  public abstract CardChannel getBasicChannel();
  
  public abstract CardChannel openLogicalChannel();
  
  public abstract void beginExclusive();
  
  public abstract void endExclusive();
  
  public abstract byte[] transmitControlCommand(int paramInt, byte[] paramArrayOfByte) throws CardException;
  
  public abstract void disconnect(boolean paramBoolean) throws CardException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\Card.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */