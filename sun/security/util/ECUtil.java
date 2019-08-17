package sun.security.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

public class ECUtil {
  public static ECPoint decodePoint(byte[] paramArrayOfByte, EllipticCurve paramEllipticCurve) throws IOException {
    if (paramArrayOfByte.length == 0 || paramArrayOfByte[0] != 4)
      throw new IOException("Only uncompressed point format supported"); 
    int i = (paramArrayOfByte.length - 1) / 2;
    if (i != paramEllipticCurve.getField().getFieldSize() + 7 >> 3)
      throw new IOException("Point does not match field size"); 
    byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfByte, 1, 1 + i);
    byte[] arrayOfByte2 = Arrays.copyOfRange(paramArrayOfByte, i + 1, i + 1 + i);
    return new ECPoint(new BigInteger(1, arrayOfByte1), new BigInteger(1, arrayOfByte2));
  }
  
  public static byte[] encodePoint(ECPoint paramECPoint, EllipticCurve paramEllipticCurve) {
    int i = paramEllipticCurve.getField().getFieldSize() + 7 >> 3;
    byte[] arrayOfByte1 = trimZeroes(paramECPoint.getAffineX().toByteArray());
    byte[] arrayOfByte2 = trimZeroes(paramECPoint.getAffineY().toByteArray());
    if (arrayOfByte1.length > i || arrayOfByte2.length > i)
      throw new RuntimeException("Point coordinates do not match field size"); 
    byte[] arrayOfByte3 = new byte[1 + (i << 1)];
    arrayOfByte3[0] = 4;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i - arrayOfByte1.length + 1, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length, arrayOfByte2.length);
    return arrayOfByte3;
  }
  
  public static byte[] trimZeroes(byte[] paramArrayOfByte) {
    byte b;
    for (b = 0; b < paramArrayOfByte.length - 1 && paramArrayOfByte[b] == 0; b++);
    return (b == 0) ? paramArrayOfByte : Arrays.copyOfRange(paramArrayOfByte, b, paramArrayOfByte.length);
  }
  
  private static AlgorithmParameters getECParameters(Provider paramProvider) {
    try {
      return (paramProvider != null) ? AlgorithmParameters.getInstance("EC", paramProvider) : AlgorithmParameters.getInstance("EC");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException);
    } 
  }
  
  public static byte[] encodeECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(paramECParameterSpec);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new RuntimeException("Not a known named curve: " + paramECParameterSpec);
    } 
    try {
      return algorithmParameters.getEncoded();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(paramECParameterSpec);
      return (ECParameterSpec)algorithmParameters.getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, byte[] paramArrayOfByte) throws IOException {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    algorithmParameters.init(paramArrayOfByte);
    try {
      return (ECParameterSpec)algorithmParameters.getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, String paramString) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(new ECGenParameterSpec(paramString));
      return (ECParameterSpec)algorithmParameters.getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, int paramInt) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(new ECKeySizeParameterSpec(paramInt));
      return (ECParameterSpec)algorithmParameters.getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static String getCurveName(Provider paramProvider, ECParameterSpec paramECParameterSpec) {
    ECGenParameterSpec eCGenParameterSpec;
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(paramECParameterSpec);
      eCGenParameterSpec = (ECGenParameterSpec)algorithmParameters.getParameterSpec(ECGenParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
    return (eCGenParameterSpec == null) ? null : eCGenParameterSpec.getName();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ECUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */