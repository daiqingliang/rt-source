package sun.net.idn;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.ParseException;
import sun.text.Normalizer;
import sun.text.normalizer.CharTrie;
import sun.text.normalizer.NormalizerImpl;
import sun.text.normalizer.Trie;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UCharacterIterator;
import sun.text.normalizer.UTF16;
import sun.text.normalizer.VersionInfo;

public final class StringPrep {
  public static final int DEFAULT = 0;
  
  public static final int ALLOW_UNASSIGNED = 1;
  
  private static final int UNASSIGNED = 0;
  
  private static final int MAP = 1;
  
  private static final int PROHIBITED = 2;
  
  private static final int DELETE = 3;
  
  private static final int TYPE_LIMIT = 4;
  
  private static final int NORMALIZATION_ON = 1;
  
  private static final int CHECK_BIDI_ON = 2;
  
  private static final int TYPE_THRESHOLD = 65520;
  
  private static final int MAX_INDEX_VALUE = 16319;
  
  private static final int MAX_INDEX_TOP_LENGTH = 3;
  
  private static final int INDEX_TRIE_SIZE = 0;
  
  private static final int INDEX_MAPPING_DATA_SIZE = 1;
  
  private static final int NORM_CORRECTNS_LAST_UNI_VERSION = 2;
  
  private static final int ONE_UCHAR_MAPPING_INDEX_START = 3;
  
  private static final int TWO_UCHARS_MAPPING_INDEX_START = 4;
  
  private static final int THREE_UCHARS_MAPPING_INDEX_START = 5;
  
  private static final int FOUR_UCHARS_MAPPING_INDEX_START = 6;
  
  private static final int OPTIONS = 7;
  
  private static final int INDEX_TOP = 16;
  
  private static final int DATA_BUFFER_SIZE = 25000;
  
  private StringPrepTrieImpl sprepTrieImpl;
  
  private int[] indexes;
  
  private char[] mappingData;
  
  private byte[] formatVersion;
  
  private VersionInfo sprepUniVer;
  
  private VersionInfo normCorrVer;
  
  private boolean doNFKC;
  
  private boolean checkBiDi;
  
  private char getCodePointValue(int paramInt) { return this.sprepTrieImpl.sprepTrie.getCodePointValue(paramInt); }
  
  private static VersionInfo getVersionInfo(int paramInt) {
    int i = paramInt & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt >> 16 & 0xFF;
    int m = paramInt >> 24 & 0xFF;
    return VersionInfo.getInstance(m, k, j, i);
  }
  
  private static VersionInfo getVersionInfo(byte[] paramArrayOfByte) { return (paramArrayOfByte.length != 4) ? null : VersionInfo.getInstance(paramArrayOfByte[0], paramArrayOfByte[1], paramArrayOfByte[2], paramArrayOfByte[3]); }
  
  public StringPrep(InputStream paramInputStream) throws IOException {
    BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream, 25000);
    StringPrepDataReader stringPrepDataReader = new StringPrepDataReader(bufferedInputStream);
    this.indexes = stringPrepDataReader.readIndexes(16);
    byte[] arrayOfByte = new byte[this.indexes[0]];
    this.mappingData = new char[this.indexes[1] / 2];
    stringPrepDataReader.read(arrayOfByte, this.mappingData);
    this.sprepTrieImpl.sprepTrie = new CharTrie(new ByteArrayInputStream(arrayOfByte), this.sprepTrieImpl);
    this.formatVersion = stringPrepDataReader.getDataFormatVersion();
    this.doNFKC = ((this.indexes[7] & true) > 0);
    this.checkBiDi = ((this.indexes[7] & 0x2) > 0);
    this.sprepUniVer = getVersionInfo(stringPrepDataReader.getUnicodeVersion());
    this.normCorrVer = getVersionInfo(this.indexes[2]);
    VersionInfo versionInfo = NormalizerImpl.getUnicodeVersion();
    if (versionInfo.compareTo(this.sprepUniVer) < 0 && versionInfo.compareTo(this.normCorrVer) < 0 && (this.indexes[7] & true) > 0)
      throw new IOException("Normalization Correction version not supported"); 
    bufferedInputStream.close();
  }
  
  private static final void getValues(char paramChar, Values paramValues) {
    paramValues.reset();
    if (paramChar == '\000') {
      paramValues.type = 4;
    } else if (paramChar >= '￰') {
      paramValues.type = paramChar - '￰';
    } else {
      paramValues.type = 1;
      if ((paramChar & 0x2) > '\000') {
        paramValues.isIndex = true;
        paramValues.value = paramChar >> '\002';
      } else {
        paramValues.isIndex = false;
        paramValues.value = paramChar << '\020' >> '\020';
        paramValues.value >>= 2;
      } 
      if (paramChar >> '\002' == '㾿') {
        paramValues.type = 3;
        paramValues.isIndex = false;
        paramValues.value = 0;
      } 
    } 
  }
  
  private StringBuffer map(UCharacterIterator paramUCharacterIterator, int paramInt) throws ParseException {
    Values values = new Values(null);
    char c = Character.MIN_VALUE;
    int i = -1;
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool = ((paramInt & true) > 0) ? 1 : 0;
    while ((i = paramUCharacterIterator.nextCodePoint()) != -1) {
      c = getCodePointValue(i);
      getValues(c, values);
      if (values.type == 0 && !bool)
        throw new ParseException("An unassigned code point was found in the input " + paramUCharacterIterator.getText(), paramUCharacterIterator.getIndex()); 
      if (values.type == 1) {
        if (values.isIndex) {
          char c1;
          int j = values.value;
          if (j >= this.indexes[3] && j < this.indexes[4]) {
            c1 = '\001';
          } else if (j >= this.indexes[4] && j < this.indexes[5]) {
            c1 = '\002';
          } else if (j >= this.indexes[5] && j < this.indexes[6]) {
            c1 = '\003';
          } else {
            c1 = this.mappingData[j++];
          } 
          stringBuffer.append(this.mappingData, j, c1);
          continue;
        } 
        i -= values.value;
      } else if (values.type == 3) {
        continue;
      } 
      UTF16.append(stringBuffer, i);
    } 
    return stringBuffer;
  }
  
  private StringBuffer normalize(StringBuffer paramStringBuffer) { return new StringBuffer(Normalizer.normalize(paramStringBuffer.toString(), Normalizer.Form.NFKC, 262432)); }
  
  public StringBuffer prepare(UCharacterIterator paramUCharacterIterator, int paramInt) throws ParseException {
    StringBuffer stringBuffer1 = map(paramUCharacterIterator, paramInt);
    StringBuffer stringBuffer2 = stringBuffer1;
    if (this.doNFKC)
      stringBuffer2 = normalize(stringBuffer1); 
    UCharacterIterator uCharacterIterator = UCharacterIterator.getInstance(stringBuffer2);
    Values values = new Values(null);
    int j = 19;
    int k = 19;
    int m = -1;
    int n = -1;
    boolean bool1 = false;
    boolean bool2 = false;
    int i;
    while ((i = uCharacterIterator.nextCodePoint()) != -1) {
      char c = getCodePointValue(i);
      getValues(c, values);
      if (values.type == 2)
        throw new ParseException("A prohibited code point was found in the input" + uCharacterIterator.getText(), values.value); 
      j = UCharacter.getDirection(i);
      if (k == 19)
        k = j; 
      if (j == 0) {
        bool2 = true;
        n = uCharacterIterator.getIndex() - 1;
      } 
      if (j == 1 || j == 13) {
        bool1 = true;
        m = uCharacterIterator.getIndex() - 1;
      } 
    } 
    if (this.checkBiDi == true) {
      if (bool2 == true && bool1 == true)
        throw new ParseException("The input does not conform to the rules for BiDi code points." + uCharacterIterator.getText(), (m > n) ? m : n); 
      if (bool1 == true && ((k != 1 && k != 13) || (j != 1 && j != 13)))
        throw new ParseException("The input does not conform to the rules for BiDi code points." + uCharacterIterator.getText(), (m > n) ? m : n); 
    } 
    return stringBuffer2;
  }
  
  private static final class StringPrepTrieImpl implements Trie.DataManipulate {
    private CharTrie sprepTrie = null;
    
    private StringPrepTrieImpl() {}
    
    public int getFoldingOffset(int param1Int) { return param1Int; }
  }
  
  private static final class Values {
    boolean isIndex;
    
    int value;
    
    int type;
    
    private Values() {}
    
    public void reset() {
      this.isIndex = false;
      this.value = 0;
      this.type = -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\idn\StringPrep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */