package sun.security.jgss.spnego;

import java.io.IOException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NegTokenTarg extends SpNegoToken {
  private int negResult = 0;
  
  private Oid supportedMech = null;
  
  private byte[] responseToken = null;
  
  private byte[] mechListMIC = null;
  
  NegTokenTarg(int paramInt, Oid paramOid, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    super(1);
    this.negResult = paramInt;
    this.supportedMech = paramOid;
    this.responseToken = paramArrayOfByte1;
    this.mechListMIC = paramArrayOfByte2;
  }
  
  public NegTokenTarg(byte[] paramArrayOfByte) throws GSSException {
    super(1);
    parseToken(paramArrayOfByte);
  }
  
  final byte[] encode() throws GSSException {
    try {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      DerOutputStream derOutputStream2 = new DerOutputStream();
      derOutputStream2.putEnumerated(this.negResult);
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
      if (this.supportedMech != null) {
        DerOutputStream derOutputStream = new DerOutputStream();
        byte[] arrayOfByte = this.supportedMech.getDER();
        derOutputStream.write(arrayOfByte);
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream);
      } 
      if (this.responseToken != null) {
        DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOctetString(this.responseToken);
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream);
      } 
      if (this.mechListMIC != null) {
        if (DEBUG)
          System.out.println("SpNegoToken NegTokenTarg: sending MechListMIC"); 
        DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOctetString(this.mechListMIC);
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), derOutputStream);
      } else if (GSSUtil.useMSInterop() && this.responseToken != null) {
        if (DEBUG)
          System.out.println("SpNegoToken NegTokenTarg: sending additional token for MS Interop"); 
        DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOctetString(this.responseToken);
        derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), derOutputStream);
      } 
      DerOutputStream derOutputStream3 = new DerOutputStream();
      derOutputStream3.write((byte)48, derOutputStream1);
      return derOutputStream3.toByteArray();
    } catch (IOException iOException) {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + iOException.getMessage());
    } 
  }
  
  private void parseToken(byte[] paramArrayOfByte) throws GSSException {
    try {
      DerValue derValue1 = new DerValue(paramArrayOfByte);
      if (!derValue1.isContextSpecific((byte)1))
        throw new IOException("SPNEGO NegoTokenTarg : did not have the right token type"); 
      DerValue derValue2 = derValue1.data.getDerValue();
      if (derValue2.tag != 48)
        throw new IOException("SPNEGO NegoTokenTarg : did not have the Sequence tag"); 
      int i = -1;
      while (derValue2.data.available() > 0) {
        DerValue derValue = derValue2.data.getDerValue();
        if (derValue.isContextSpecific((byte)0)) {
          i = checkNextField(i, 0);
          this.negResult = derValue.data.getEnumerated();
          if (DEBUG)
            System.out.println("SpNegoToken NegTokenTarg: negotiated result = " + getNegoResultString(this.negResult)); 
          continue;
        } 
        if (derValue.isContextSpecific((byte)1)) {
          i = checkNextField(i, 1);
          ObjectIdentifier objectIdentifier = derValue.data.getOID();
          this.supportedMech = new Oid(objectIdentifier.toString());
          if (DEBUG)
            System.out.println("SpNegoToken NegTokenTarg: supported mechanism = " + this.supportedMech); 
          continue;
        } 
        if (derValue.isContextSpecific((byte)2)) {
          i = checkNextField(i, 2);
          this.responseToken = derValue.data.getOctetString();
          continue;
        } 
        if (derValue.isContextSpecific((byte)3)) {
          i = checkNextField(i, 3);
          if (!GSSUtil.useMSInterop()) {
            this.mechListMIC = derValue.data.getOctetString();
            if (DEBUG)
              System.out.println("SpNegoToken NegTokenTarg: MechListMIC Token = " + getHexBytes(this.mechListMIC)); 
          } 
        } 
      } 
    } catch (IOException iOException) {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + iOException.getMessage());
    } 
  }
  
  int getNegotiatedResult() { return this.negResult; }
  
  public Oid getSupportedMech() { return this.supportedMech; }
  
  byte[] getResponseToken() throws GSSException { return this.responseToken; }
  
  byte[] getMechListMIC() throws GSSException { return this.mechListMIC; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spnego\NegTokenTarg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */