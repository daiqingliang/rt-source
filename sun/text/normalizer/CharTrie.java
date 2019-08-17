package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CharTrie extends Trie {
  private char m_initialValue_;
  
  private char[] m_data_;
  
  private FriendAgent m_friendAgent_;
  
  public CharTrie(InputStream paramInputStream, Trie.DataManipulate paramDataManipulate) throws IOException {
    super(paramInputStream, paramDataManipulate);
    if (!isCharTrie())
      throw new IllegalArgumentException("Data given does not belong to a char trie."); 
    this.m_friendAgent_ = new FriendAgent();
  }
  
  public CharTrie(int paramInt1, int paramInt2, Trie.DataManipulate paramDataManipulate) {
    super(new char[2080], 512, paramDataManipulate);
    char c2 = 'Ā';
    char c1 = c2;
    if (paramInt2 != paramInt1)
      c1 += ' '; 
    this.m_data_ = new char[c1];
    this.m_dataLength_ = c1;
    this.m_initialValue_ = (char)paramInt1;
    char c3;
    for (c3 = Character.MIN_VALUE; c3 < c2; c3++)
      this.m_data_[c3] = (char)paramInt1; 
    if (paramInt2 != paramInt1) {
      char c4 = (char)(c2 >> '\002');
      c3 = 'ۀ';
      char c = '۠';
      while (c3 < c) {
        this.m_index_[c3] = c4;
        c3++;
      } 
      c = c2 + ' ';
      for (c3 = c2; c3 < c; c3++)
        this.m_data_[c3] = (char)paramInt2; 
    } 
    this.m_friendAgent_ = new FriendAgent();
  }
  
  public void putIndexData(UCharacterProperty paramUCharacterProperty) { paramUCharacterProperty.setIndexData(this.m_friendAgent_); }
  
  public final char getCodePointValue(int paramInt) {
    if (0 <= paramInt && paramInt < 55296) {
      char c = (this.m_index_[paramInt >> 5] << '\002') + (paramInt & 0x1F);
      return this.m_data_[c];
    } 
    int i = getCodePointOffset(paramInt);
    return (i >= 0) ? this.m_data_[i] : this.m_initialValue_;
  }
  
  public final char getLeadValue(char paramChar) { return this.m_data_[getLeadOffset(paramChar)]; }
  
  public final char getSurrogateValue(char paramChar1, char paramChar2) {
    int i = getSurrogateOffset(paramChar1, paramChar2);
    return (i > 0) ? this.m_data_[i] : this.m_initialValue_;
  }
  
  public final char getTrailValue(int paramInt, char paramChar) {
    if (this.m_dataManipulate_ == null)
      throw new NullPointerException("The field DataManipulate in this Trie is null"); 
    int i = this.m_dataManipulate_.getFoldingOffset(paramInt);
    return (i > 0) ? this.m_data_[getRawOffset(i, (char)(paramChar & 0x3FF))] : this.m_initialValue_;
  }
  
  protected final void unserialize(InputStream paramInputStream) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    int i = this.m_dataOffset_ + this.m_dataLength_;
    this.m_index_ = new char[i];
    for (byte b = 0; b < i; b++)
      this.m_index_[b] = dataInputStream.readChar(); 
    this.m_data_ = this.m_index_;
    this.m_initialValue_ = this.m_data_[this.m_dataOffset_];
  }
  
  protected final int getSurrogateOffset(char paramChar1, char paramChar2) {
    if (this.m_dataManipulate_ == null)
      throw new NullPointerException("The field DataManipulate in this Trie is null"); 
    int i = this.m_dataManipulate_.getFoldingOffset(getLeadValue(paramChar1));
    return (i > 0) ? getRawOffset(i, (char)(paramChar2 & 0x3FF)) : -1;
  }
  
  protected final int getValue(int paramInt) { return this.m_data_[paramInt]; }
  
  protected final int getInitialValue() { return this.m_initialValue_; }
  
  public class FriendAgent {
    public char[] getPrivateIndex() { return CharTrie.this.m_index_; }
    
    public char[] getPrivateData() { return CharTrie.this.m_data_; }
    
    public int getPrivateInitialValue() { return CharTrie.this.m_initialValue_; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\CharTrie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */