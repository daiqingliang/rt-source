package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class UCharacterPropertyReader implements ICUBinary.Authenticate {
  private static final int INDEX_SIZE_ = 16;
  
  private DataInputStream m_dataInputStream_;
  
  private int m_propertyOffset_;
  
  private int m_exceptionOffset_;
  
  private int m_caseOffset_;
  
  private int m_additionalOffset_;
  
  private int m_additionalVectorsOffset_;
  
  private int m_additionalColumnsCount_;
  
  private int m_reservedOffset_;
  
  private byte[] m_unicodeVersion_;
  
  private static final byte[] DATA_FORMAT_ID_ = { 85, 80, 114, 111 };
  
  private static final byte[] DATA_FORMAT_VERSION_ = { 5, 0, 5, 2 };
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfByte) { return (paramArrayOfByte[0] == DATA_FORMAT_VERSION_[0] && paramArrayOfByte[2] == DATA_FORMAT_VERSION_[2] && paramArrayOfByte[3] == DATA_FORMAT_VERSION_[3]); }
  
  protected UCharacterPropertyReader(InputStream paramInputStream) throws IOException {
    this.m_unicodeVersion_ = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID_, this);
    this.m_dataInputStream_ = new DataInputStream(paramInputStream);
  }
  
  protected void read(UCharacterProperty paramUCharacterProperty) throws IOException {
    byte b = 16;
    this.m_propertyOffset_ = this.m_dataInputStream_.readInt();
    b--;
    this.m_exceptionOffset_ = this.m_dataInputStream_.readInt();
    b--;
    this.m_caseOffset_ = this.m_dataInputStream_.readInt();
    b--;
    this.m_additionalOffset_ = this.m_dataInputStream_.readInt();
    b--;
    this.m_additionalVectorsOffset_ = this.m_dataInputStream_.readInt();
    b--;
    this.m_additionalColumnsCount_ = this.m_dataInputStream_.readInt();
    b--;
    this.m_reservedOffset_ = this.m_dataInputStream_.readInt();
    b--;
    this.m_dataInputStream_.skipBytes(12);
    b -= 3;
    paramUCharacterProperty.m_maxBlockScriptValue_ = this.m_dataInputStream_.readInt();
    b--;
    paramUCharacterProperty.m_maxJTGValue_ = this.m_dataInputStream_.readInt();
    this.m_dataInputStream_.skipBytes(--b << 2);
    paramUCharacterProperty.m_trie_ = new CharTrie(this.m_dataInputStream_, null);
    int i = this.m_exceptionOffset_ - this.m_propertyOffset_;
    this.m_dataInputStream_.skipBytes(i * 4);
    i = this.m_caseOffset_ - this.m_exceptionOffset_;
    this.m_dataInputStream_.skipBytes(i * 4);
    i = this.m_additionalOffset_ - this.m_caseOffset_ << 1;
    this.m_dataInputStream_.skipBytes(i * 2);
    if (this.m_additionalColumnsCount_ > 0) {
      paramUCharacterProperty.m_additionalTrie_ = new CharTrie(this.m_dataInputStream_, null);
      i = this.m_reservedOffset_ - this.m_additionalVectorsOffset_;
      paramUCharacterProperty.m_additionalVectors_ = new int[i];
      for (byte b1 = 0; b1 < i; b1++)
        paramUCharacterProperty.m_additionalVectors_[b1] = this.m_dataInputStream_.readInt(); 
    } 
    this.m_dataInputStream_.close();
    paramUCharacterProperty.m_additionalColumnsCount_ = this.m_additionalColumnsCount_;
    paramUCharacterProperty.m_unicodeVersion_ = VersionInfo.getInstance(this.m_unicodeVersion_[0], this.m_unicodeVersion_[1], this.m_unicodeVersion_[2], this.m_unicodeVersion_[3]);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\UCharacterPropertyReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */