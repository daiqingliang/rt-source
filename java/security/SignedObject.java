package java.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SignedObject implements Serializable {
  private static final long serialVersionUID = 720502720485447167L;
  
  private byte[] content;
  
  private byte[] signature;
  
  private String thealgorithm;
  
  public SignedObject(Serializable paramSerializable, PrivateKey paramPrivateKey, Signature paramSignature) throws IOException, InvalidKeyException, SignatureException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    objectOutputStream.writeObject(paramSerializable);
    objectOutputStream.flush();
    objectOutputStream.close();
    this.content = byteArrayOutputStream.toByteArray();
    byteArrayOutputStream.close();
    sign(paramPrivateKey, paramSignature);
  }
  
  public Object getObject() throws IOException, ClassNotFoundException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.content);
    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
    Object object = objectInputStream.readObject();
    byteArrayInputStream.close();
    objectInputStream.close();
    return object;
  }
  
  public byte[] getSignature() { return (byte[])this.signature.clone(); }
  
  public String getAlgorithm() { return this.thealgorithm; }
  
  public boolean verify(PublicKey paramPublicKey, Signature paramSignature) throws InvalidKeyException, SignatureException {
    paramSignature.initVerify(paramPublicKey);
    paramSignature.update((byte[])this.content.clone());
    return paramSignature.verify((byte[])this.signature.clone());
  }
  
  private void sign(PrivateKey paramPrivateKey, Signature paramSignature) throws InvalidKeyException, SignatureException {
    paramSignature.initSign(paramPrivateKey);
    paramSignature.update((byte[])this.content.clone());
    this.signature = (byte[])paramSignature.sign().clone();
    this.thealgorithm = paramSignature.getAlgorithm();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.content = (byte[])((byte[])getField.get("content", null)).clone();
    this.signature = (byte[])((byte[])getField.get("signature", null)).clone();
    this.thealgorithm = (String)getField.get("thealgorithm", null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\SignedObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */