package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class HexDumpEncoder extends CharacterEncoder {
  private int offset;
  
  private int thisLineLength;
  
  private int currentByte;
  
  private byte[] thisLine = new byte[16];
  
  static void hexDigit(PrintStream paramPrintStream, byte paramByte) {
    char c = (char)(paramByte >> 4 & 0xF);
    if (c > '\t') {
      c = (char)(c - '\n' + 'A');
    } else {
      c = (char)(c + '0');
    } 
    paramPrintStream.write(c);
    c = (char)(paramByte & 0xF);
    if (c > '\t') {
      c = (char)(c - '\n' + 'A');
    } else {
      c = (char)(c + '0');
    } 
    paramPrintStream.write(c);
  }
  
  protected int bytesPerAtom() { return 1; }
  
  protected int bytesPerLine() { return 16; }
  
  protected void encodeBufferPrefix(OutputStream paramOutputStream) throws IOException {
    this.offset = 0;
    super.encodeBufferPrefix(paramOutputStream);
  }
  
  protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt) throws IOException {
    hexDigit(this.pStream, (byte)(this.offset >>> 8 & 0xFF));
    hexDigit(this.pStream, (byte)(this.offset & 0xFF));
    this.pStream.print(": ");
    this.currentByte = 0;
    this.thisLineLength = paramInt;
  }
  
  protected void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.thisLine[this.currentByte] = paramArrayOfByte[paramInt1];
    hexDigit(this.pStream, paramArrayOfByte[paramInt1]);
    this.pStream.print(" ");
    this.currentByte++;
    if (this.currentByte == 8)
      this.pStream.print("  "); 
  }
  
  protected void encodeLineSuffix(OutputStream paramOutputStream) throws IOException {
    if (this.thisLineLength < 16)
      for (int i = this.thisLineLength; i < 16; i++) {
        this.pStream.print("   ");
        if (i == 7)
          this.pStream.print("  "); 
      }  
    this.pStream.print(" ");
    for (byte b = 0; b < this.thisLineLength; b++) {
      if (this.thisLine[b] < 32 || this.thisLine[b] > 122) {
        this.pStream.print(".");
      } else {
        this.pStream.write(this.thisLine[b]);
      } 
    } 
    this.pStream.println();
    this.offset += this.thisLineLength;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\HexDumpEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */