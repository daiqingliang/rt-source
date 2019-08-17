package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BuiltInEncodingAlgorithm implements EncodingAlgorithm {
  protected static final Pattern SPACE_PATTERN = Pattern.compile("\\s");
  
  public abstract int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException;
  
  public abstract int getOctetLengthFromPrimitiveLength(int paramInt) throws EncodingAlgorithmException;
  
  public abstract void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
  
  public void matchWhiteSpaceDelimnatedWords(CharBuffer paramCharBuffer, WordListener paramWordListener) {
    Matcher matcher = SPACE_PATTERN.matcher(paramCharBuffer);
    int i = 0;
    int j = 0;
    while (matcher.find()) {
      j = matcher.start();
      if (j != i)
        paramWordListener.word(i, j); 
      i = matcher.end();
    } 
    if (i != paramCharBuffer.length())
      paramWordListener.word(i, paramCharBuffer.length()); 
  }
  
  public StringBuilder removeWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    int j;
    for (j = 0; j < paramInt2; j++) {
      if (Character.isWhitespace(paramArrayOfChar[j + paramInt1])) {
        if (i < j)
          stringBuilder.append(paramArrayOfChar, i + paramInt1, j - i); 
        i = j + 1;
      } 
    } 
    if (i < j)
      stringBuilder.append(paramArrayOfChar, i + paramInt1, j - i); 
    return stringBuilder;
  }
  
  public static interface WordListener {
    void word(int param1Int1, int param1Int2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\BuiltInEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */