package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Trie {
  protected static final int LEAD_INDEX_OFFSET_ = 320;
  
  protected static final int INDEX_STAGE_1_SHIFT_ = 5;
  
  protected static final int INDEX_STAGE_2_SHIFT_ = 2;
  
  protected static final int DATA_BLOCK_LENGTH = 32;
  
  protected static final int INDEX_STAGE_3_MASK_ = 31;
  
  protected static final int SURROGATE_BLOCK_BITS = 5;
  
  protected static final int SURROGATE_BLOCK_COUNT = 32;
  
  protected static final int BMP_INDEX_LENGTH = 2048;
  
  protected static final int SURROGATE_MASK_ = 1023;
  
  protected char[] m_index_;
  
  protected DataManipulate m_dataManipulate_;
  
  protected int m_dataOffset_;
  
  protected int m_dataLength_;
  
  protected static final int HEADER_OPTIONS_LATIN1_IS_LINEAR_MASK_ = 512;
  
  protected static final int HEADER_SIGNATURE_ = 1416784229;
  
  private static final int HEADER_OPTIONS_SHIFT_MASK_ = 15;
  
  protected static final int HEADER_OPTIONS_INDEX_SHIFT_ = 4;
  
  protected static final int HEADER_OPTIONS_DATA_IS_32_BIT_ = 256;
  
  private boolean m_isLatin1Linear_;
  
  private int m_options_;
  
  protected Trie(InputStream paramInputStream, DataManipulate paramDataManipulate) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    int i = dataInputStream.readInt();
    this.m_options_ = dataInputStream.readInt();
    if (!checkHeader(i))
      throw new IllegalArgumentException("ICU data file error: Trie header authentication failed, please check if you have the most updated ICU data file"); 
    if (paramDataManipulate != null) {
      this.m_dataManipulate_ = paramDataManipulate;
    } else {
      this.m_dataManipulate_ = new DefaultGetFoldingOffset(null);
    } 
    this.m_isLatin1Linear_ = ((this.m_options_ & 0x200) != 0);
    this.m_dataOffset_ = dataInputStream.readInt();
    this.m_dataLength_ = dataInputStream.readInt();
    unserialize(paramInputStream);
  }
  
  protected Trie(char[] paramArrayOfChar, int paramInt, DataManipulate paramDataManipulate) {
    this.m_options_ = paramInt;
    if (paramDataManipulate != null) {
      this.m_dataManipulate_ = paramDataManipulate;
    } else {
      this.m_dataManipulate_ = new DefaultGetFoldingOffset(null);
    } 
    this.m_isLatin1Linear_ = ((this.m_options_ & 0x200) != 0);
    this.m_index_ = paramArrayOfChar;
    this.m_dataOffset_ = this.m_index_.length;
  }
  
  protected abstract int getSurrogateOffset(char paramChar1, char paramChar2);
  
  protected abstract int getValue(int paramInt);
  
  protected abstract int getInitialValue();
  
  protected final int getRawOffset(int paramInt, char paramChar) { return (this.m_index_[paramInt + (paramChar >> '\005')] << '\002') + (paramChar & 0x1F); }
  
  protected final int getBMPOffset(char paramChar) { return (paramChar >= '?' && paramChar <= '?') ? getRawOffset(320, paramChar) : getRawOffset(0, paramChar); }
  
  protected final int getLeadOffset(char paramChar) { return getRawOffset(0, paramChar); }
  
  protected final int getCodePointOffset(int paramInt) { return (paramInt < 0) ? -1 : ((paramInt < 55296) ? getRawOffset(0, (char)paramInt) : ((paramInt < 65536) ? getBMPOffset((char)paramInt) : ((paramInt <= 1114111) ? getSurrogateOffset(UTF16.getLeadSurrogate(paramInt), (char)(paramInt & 0x3FF)) : -1))); }
  
  protected void unserialize(InputStream paramInputStream) throws IOException {
    this.m_index_ = new char[this.m_dataOffset_];
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    for (byte b = 0; b < this.m_dataOffset_; b++)
      this.m_index_[b] = dataInputStream.readChar(); 
  }
  
  protected final boolean isIntTrie() { return ((this.m_options_ & 0x100) != 0); }
  
  protected final boolean isCharTrie() { return ((this.m_options_ & 0x100) == 0); }
  
  private final boolean checkHeader(int paramInt) { return (paramInt != 1416784229) ? false : (!((this.m_options_ & 0xF) != 5 || (this.m_options_ >> 4 & 0xF) != 2)); }
  
  public static interface DataManipulate {
    int getFoldingOffset(int param1Int);
  }
  
  private static class DefaultGetFoldingOffset implements DataManipulate {
    private DefaultGetFoldingOffset() {}
    
    public int getFoldingOffset(int param1Int) { return param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\Trie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */