package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class UBiDiProps {
  private static UBiDiProps gBdp = null;
  
  private static UBiDiProps gBdpDummy = null;
  
  private int[] indexes;
  
  private int[] mirrors;
  
  private byte[] jgArray;
  
  private CharTrie trie;
  
  private static final String DATA_FILE_NAME = "/sun/text/resources/ubidi.icu";
  
  private static final byte[] FMT = { 66, 105, 68, 105 };
  
  private static final int IX_INDEX_TOP = 0;
  
  private static final int IX_MIRROR_LENGTH = 3;
  
  private static final int IX_JG_START = 4;
  
  private static final int IX_JG_LIMIT = 5;
  
  private static final int IX_TOP = 16;
  
  private static final int CLASS_MASK = 31;
  
  public UBiDiProps() throws IOException {
    InputStream inputStream = ICUData.getStream("/sun/text/resources/ubidi.icu");
    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 4096);
    readData(bufferedInputStream);
    bufferedInputStream.close();
    inputStream.close();
  }
  
  private void readData(InputStream paramInputStream) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    ICUBinary.readHeader(dataInputStream, FMT, new IsAcceptable(null));
    int i = dataInputStream.readInt();
    if (i < 0)
      throw new IOException("indexes[0] too small in /sun/text/resources/ubidi.icu"); 
    this.indexes = new int[i];
    this.indexes[0] = i;
    byte b;
    for (b = 1; b < i; b++)
      this.indexes[b] = dataInputStream.readInt(); 
    this.trie = new CharTrie(dataInputStream, null);
    i = this.indexes[3];
    if (i > 0) {
      this.mirrors = new int[i];
      for (b = 0; b < i; b++)
        this.mirrors[b] = dataInputStream.readInt(); 
    } 
    i = this.indexes[5] - this.indexes[4];
    this.jgArray = new byte[i];
    for (b = 0; b < i; b++)
      this.jgArray[b] = dataInputStream.readByte(); 
  }
  
  public static final UBiDiProps getSingleton() throws IOException {
    if (gBdp == null)
      gBdp = new UBiDiProps(); 
    return gBdp;
  }
  
  private UBiDiProps(boolean paramBoolean) {
    this.indexes = new int[16];
    this.indexes[0] = 16;
    this.trie = new CharTrie(0, 0, null);
  }
  
  public static final UBiDiProps getDummy() throws IOException {
    if (gBdpDummy == null)
      gBdpDummy = new UBiDiProps(true); 
    return gBdpDummy;
  }
  
  public final int getClass(int paramInt) { return getClassFromProps(this.trie.getCodePointValue(paramInt)); }
  
  private static final int getClassFromProps(int paramInt) { return paramInt & 0x1F; }
  
  private final class IsAcceptable implements ICUBinary.Authenticate {
    private IsAcceptable() {}
    
    public boolean isDataVersionAcceptable(byte[] param1ArrayOfByte) { return (param1ArrayOfByte[0] == 1 && param1ArrayOfByte[2] == 5 && param1ArrayOfByte[3] == 2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\UBiDiProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */