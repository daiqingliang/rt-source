package sun.java2d.pipe;

public interface AATileGenerator {
  int getTileWidth();
  
  int getTileHeight();
  
  int getTypicalAlpha();
  
  void nextTile();
  
  void getAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  void dispose();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\AATileGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */