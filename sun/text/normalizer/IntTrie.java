package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IntTrie extends Trie {
  private int m_initialValue_;
  
  private int[] m_data_;
  
  public IntTrie(InputStream paramInputStream, Trie.DataManipulate paramDataManipulate) throws IOException {
    super(paramInputStream, paramDataManipulate);
    if (!isIntTrie())
      throw new IllegalArgumentException("Data given does not belong to a int trie."); 
  }
  
  public final int getCodePointValue(int paramInt) {
    int i = getCodePointOffset(paramInt);
    return (i >= 0) ? this.m_data_[i] : this.m_initialValue_;
  }
  
  public final int getLeadValue(char paramChar) { return this.m_data_[getLeadOffset(paramChar)]; }
  
  public final int getTrailValue(int paramInt, char paramChar) {
    if (this.m_dataManipulate_ == null)
      throw new NullPointerException("The field DataManipulate in this Trie is null"); 
    int i = this.m_dataManipulate_.getFoldingOffset(paramInt);
    return (i > 0) ? this.m_data_[getRawOffset(i, (char)(paramChar & 0x3FF))] : this.m_initialValue_;
  }
  
  protected final void unserialize(InputStream paramInputStream) throws IOException {
    super.unserialize(paramInputStream);
    this.m_data_ = new int[this.m_dataLength_];
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    for (byte b = 0; b < this.m_dataLength_; b++)
      this.m_data_[b] = dataInputStream.readInt(); 
    this.m_initialValue_ = this.m_data_[0];
  }
  
  protected final int getSurrogateOffset(char paramChar1, char paramChar2) {
    if (this.m_dataManipulate_ == null)
      throw new NullPointerException("The field DataManipulate in this Trie is null"); 
    int i = this.m_dataManipulate_.getFoldingOffset(getLeadValue(paramChar1));
    return (i > 0) ? getRawOffset(i, (char)(paramChar2 & 0x3FF)) : -1;
  }
  
  protected final int getValue(int paramInt) { return this.m_data_[paramInt]; }
  
  protected final int getInitialValue() { return this.m_initialValue_; }
  
  IntTrie(char[] paramArrayOfChar, int[] paramArrayOfInt, int paramInt1, int paramInt2, Trie.DataManipulate paramDataManipulate) {
    super(paramArrayOfChar, paramInt2, paramDataManipulate);
    this.m_data_ = paramArrayOfInt;
    this.m_dataLength_ = this.m_data_.length;
    this.m_initialValue_ = paramInt1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\IntTrie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */