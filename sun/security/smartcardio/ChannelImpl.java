package sun.security.smartcardio;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.AccessController;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import sun.security.action.GetPropertyAction;

final class ChannelImpl extends CardChannel {
  private final CardImpl card;
  
  private final int channel;
  
  private static final boolean t0GetResponse = getBooleanProperty("sun.security.smartcardio.t0GetResponse", true);
  
  private static final boolean t1GetResponse = getBooleanProperty("sun.security.smartcardio.t1GetResponse", true);
  
  private static final boolean t1StripLe = getBooleanProperty("sun.security.smartcardio.t1StripLe", false);
  
  private static final byte[] B0 = new byte[0];
  
  ChannelImpl(CardImpl paramCardImpl, int paramInt) {
    this.card = paramCardImpl;
    this.channel = paramInt;
  }
  
  void checkClosed() {
    this.card.checkState();
    if (this.isClosed)
      throw new IllegalStateException("Logical channel has been closed"); 
  }
  
  public Card getCard() { return this.card; }
  
  public int getChannelNumber() {
    checkClosed();
    return this.channel;
  }
  
  private static void checkManageChannel(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length < 4)
      throw new IllegalArgumentException("Command APDU must be at least 4 bytes long"); 
    if (paramArrayOfByte[0] >= 0 && paramArrayOfByte[1] == 112)
      throw new IllegalArgumentException("Manage channel command not allowed, use openLogicalChannel()"); 
  }
  
  public ResponseAPDU transmit(CommandAPDU paramCommandAPDU) throws CardException {
    checkClosed();
    this.card.checkExclusive();
    byte[] arrayOfByte1 = paramCommandAPDU.getBytes();
    byte[] arrayOfByte2 = doTransmit(arrayOfByte1);
    return new ResponseAPDU(arrayOfByte2);
  }
  
  public int transmit(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) throws CardException {
    checkClosed();
    this.card.checkExclusive();
    if (paramByteBuffer1 == null || paramByteBuffer2 == null)
      throw new NullPointerException(); 
    if (paramByteBuffer2.isReadOnly())
      throw new ReadOnlyBufferException(); 
    if (paramByteBuffer1 == paramByteBuffer2)
      throw new IllegalArgumentException("command and response must not be the same object"); 
    if (paramByteBuffer2.remaining() < 258)
      throw new IllegalArgumentException("Insufficient space in response buffer"); 
    byte[] arrayOfByte1 = new byte[paramByteBuffer1.remaining()];
    paramByteBuffer1.get(arrayOfByte1);
    byte[] arrayOfByte2 = doTransmit(arrayOfByte1);
    paramByteBuffer2.put(arrayOfByte2);
    return arrayOfByte2.length;
  }
  
  private static boolean getBooleanProperty(String paramString, boolean paramBoolean) {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
    if (str == null)
      return paramBoolean; 
    if (str.equalsIgnoreCase("true"))
      return true; 
    if (str.equalsIgnoreCase("false"))
      return false; 
    throw new IllegalArgumentException(paramString + " must be either 'true' or 'false'");
  }
  
  private byte[] concat(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) {
    int i = paramArrayOfByte1.length;
    if (i == 0 && paramInt == paramArrayOfByte2.length)
      return paramArrayOfByte2; 
    byte[] arrayOfByte = new byte[i + paramInt];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, i);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, i, paramInt);
    return arrayOfByte;
  }
  
  private byte[] doTransmit(byte[] paramArrayOfByte) throws CardException {
    try {
      int j;
      byte[] arrayOfByte;
      checkManageChannel(paramArrayOfByte);
      setChannel(paramArrayOfByte);
      int i = paramArrayOfByte.length;
      boolean bool1 = (this.card.protocol == 1) ? 1 : 0;
      boolean bool2 = (this.card.protocol == 2) ? 1 : 0;
      if (bool1 && i >= 7 && paramArrayOfByte[4] == 0)
        throw new CardException("Extended length forms not supported for T=0"); 
      if ((bool1 || (bool2 && t1StripLe)) && i >= 7) {
        byte b1 = paramArrayOfByte[4] & 0xFF;
        if (b1 != 0) {
          if (i == b1 + 6)
            i--; 
        } else {
          b1 = (paramArrayOfByte[5] & 0xFF) << 8 | paramArrayOfByte[6] & 0xFF;
          if (i == b1 + 9)
            i -= 2; 
        } 
      } 
      boolean bool3 = ((bool1 && t0GetResponse) || (bool2 && t1GetResponse)) ? 1 : 0;
      byte b = 0;
      null = B0;
      while (true) {
        if (++b >= 32)
          throw new CardException("Could not obtain response"); 
        arrayOfByte = PCSC.SCardTransmit(this.card.cardId, this.card.protocol, paramArrayOfByte, 0, i);
        j = arrayOfByte.length;
        if (bool3 && j >= 2) {
          if (j == 2 && arrayOfByte[0] == 108) {
            paramArrayOfByte[i - 1] = arrayOfByte[1];
            continue;
          } 
          if (arrayOfByte[j - 2] == 97) {
            if (j > 2)
              null = concat(null, arrayOfByte, j - 2); 
            paramArrayOfByte[1] = -64;
            paramArrayOfByte[2] = 0;
            paramArrayOfByte[3] = 0;
            paramArrayOfByte[4] = arrayOfByte[j - 1];
            i = 5;
            continue;
          } 
        } 
        break;
      } 
      return concat(null, arrayOfByte, j);
    } catch (PCSCException pCSCException) {
      this.card.handleError(pCSCException);
      throw new CardException(pCSCException);
    } 
  }
  
  private static int getSW(byte[] paramArrayOfByte) throws CardException {
    if (paramArrayOfByte.length < 2)
      throw new CardException("Invalid response length: " + paramArrayOfByte.length); 
    byte b1 = paramArrayOfByte[paramArrayOfByte.length - 2] & 0xFF;
    byte b2 = paramArrayOfByte[paramArrayOfByte.length - 1] & 0xFF;
    return b1 << 8 | b2;
  }
  
  private static boolean isOK(byte[] paramArrayOfByte) throws CardException { return (paramArrayOfByte.length == 2 && getSW(paramArrayOfByte) == 36864); }
  
  private void setChannel(byte[] paramArrayOfByte) {
    byte b = paramArrayOfByte[0];
    if (b < 0)
      return; 
    if ((b & 0xE0) == 32)
      return; 
    if (this.channel <= 3) {
      paramArrayOfByte[0] = (byte)(paramArrayOfByte[0] & 0xBC);
      paramArrayOfByte[0] = (byte)(paramArrayOfByte[0] | this.channel);
    } else if (this.channel <= 19) {
      paramArrayOfByte[0] = (byte)(paramArrayOfByte[0] & 0xB0);
      paramArrayOfByte[0] = (byte)(paramArrayOfByte[0] | 0x40);
      paramArrayOfByte[0] = (byte)(paramArrayOfByte[0] | this.channel - 4);
    } else {
      throw new RuntimeException("Unsupported channel number: " + this.channel);
    } 
  }
  
  public void close() {
    if (getChannelNumber() == 0)
      throw new IllegalStateException("Cannot close basic logical channel"); 
    if (this.isClosed)
      return; 
    this.card.checkExclusive();
    try {
      byte[] arrayOfByte1 = { 0, 112, Byte.MIN_VALUE, 0 };
      arrayOfByte1[3] = (byte)getChannelNumber();
      setChannel(arrayOfByte1);
      byte[] arrayOfByte2 = PCSC.SCardTransmit(this.card.cardId, this.card.protocol, arrayOfByte1, 0, arrayOfByte1.length);
      if (!isOK(arrayOfByte2))
        throw new CardException("close() failed: " + PCSC.toString(arrayOfByte2)); 
    } catch (PCSCException pCSCException) {
      this.card.handleError(pCSCException);
      throw new CardException("Could not close channel", pCSCException);
    } finally {
      this.isClosed = true;
    } 
  }
  
  public String toString() { return "PC/SC channel " + this.channel; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\smartcardio\ChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */