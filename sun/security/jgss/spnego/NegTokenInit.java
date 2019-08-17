package sun.security.jgss.spnego;

import java.io.IOException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NegTokenInit extends SpNegoToken {
  private byte[] mechTypes = null;
  
  private Oid[] mechTypeList = null;
  
  private BitArray reqFlags = null;
  
  private byte[] mechToken = null;
  
  private byte[] mechListMIC = null;
  
  NegTokenInit(byte[] paramArrayOfByte1, BitArray paramBitArray, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    super(0);
    this.mechTypes = paramArrayOfByte1;
    this.reqFlags = paramBitArray;
    this.mechToken = paramArrayOfByte2;
    this.mechListMIC = paramArrayOfByte3;
  }
  
  public NegTokenInit(byte[] paramArrayOfByte) throws GSSException {
    super(0);
    parseToken(paramArrayOfByte);
  }
  
  final byte[] encode() throws GSSException {
    try {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      if (this.mechTypes != null)
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), this.mechTypes); 
      if (this.reqFlags != null) {
        DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putUnalignedBitString(this.reqFlags);
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream);
      } 
      if (this.mechToken != null) {
        DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOctetString(this.mechToken);
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream);
      } 
      if (this.mechListMIC != null) {
        if (DEBUG)
          System.out.println("SpNegoToken NegTokenInit: sending MechListMIC"); 
        DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOctetString(this.mechListMIC);
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), derOutputStream);
      } 
      DerOutputStream derOutputStream2 = new DerOutputStream();
      derOutputStream2.write((byte)48, derOutputStream1);
      return derOutputStream2.toByteArray();
    } catch (IOException iOException) {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + iOException.getMessage());
    } 
  }
  
  private void parseToken(byte[] paramArrayOfByte) throws GSSException {
    try {
      DerValue derValue1 = new DerValue(paramArrayOfByte);
      if (!derValue1.isContextSpecific((byte)0))
        throw new IOException("SPNEGO NegoTokenInit : did not have right token type"); 
      DerValue derValue2 = derValue1.data.getDerValue();
      if (derValue2.tag != 48)
        throw new IOException("SPNEGO NegoTokenInit : did not have the Sequence tag"); 
      int i = -1;
      while (derValue2.data.available() > 0) {
        DerValue derValue = derValue2.data.getDerValue();
        if (derValue.isContextSpecific((byte)0)) {
          i = checkNextField(i, 0);
          DerInputStream derInputStream = derValue.data;
          this.mechTypes = derInputStream.toByteArray();
          DerValue[] arrayOfDerValue = derInputStream.getSequence(0);
          this.mechTypeList = new Oid[arrayOfDerValue.length];
          ObjectIdentifier objectIdentifier = null;
          for (byte b = 0; b < arrayOfDerValue.length; b++) {
            objectIdentifier = arrayOfDerValue[b].getOID();
            if (DEBUG)
              System.out.println("SpNegoToken NegTokenInit: reading Mechanism Oid = " + objectIdentifier); 
            this.mechTypeList[b] = new Oid(objectIdentifier.toString());
          } 
          continue;
        } 
        if (derValue.isContextSpecific((byte)1)) {
          i = checkNextField(i, 1);
          continue;
        } 
        if (derValue.isContextSpecific((byte)2)) {
          i = checkNextField(i, 2);
          if (DEBUG)
            System.out.println("SpNegoToken NegTokenInit: reading Mech Token"); 
          this.mechToken = derValue.data.getOctetString();
          continue;
        } 
        if (derValue.isContextSpecific((byte)3)) {
          i = checkNextField(i, 3);
          if (!GSSUtil.useMSInterop()) {
            this.mechListMIC = derValue.data.getOctetString();
            if (DEBUG)
              System.out.println("SpNegoToken NegTokenInit: MechListMIC Token = " + getHexBytes(this.mechListMIC)); 
          } 
        } 
      } 
    } catch (IOException iOException) {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + iOException.getMessage());
    } 
  }
  
  byte[] getMechTypes() throws GSSException { return this.mechTypes; }
  
  public Oid[] getMechTypeList() { return this.mechTypeList; }
  
  BitArray getReqFlags() { return this.reqFlags; }
  
  public byte[] getMechToken() throws GSSException { return this.mechToken; }
  
  byte[] getMechListMIC() throws GSSException { return this.mechListMIC; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spnego\NegTokenInit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */