package javax.swing.text.rtf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

abstract class RTFParser extends AbstractFilter {
  public int level = 0;
  
  private int state = 0;
  
  private StringBuffer currentCharacters = new StringBuffer();
  
  private String pendingKeyword = null;
  
  private int pendingCharacter;
  
  private long binaryBytesLeft;
  
  ByteArrayOutputStream binaryBuf;
  
  private boolean[] savedSpecials;
  
  protected PrintStream warnings;
  
  private final int S_text = 0;
  
  private final int S_backslashed = 1;
  
  private final int S_token = 2;
  
  private final int S_parameter = 3;
  
  private final int S_aftertick = 4;
  
  private final int S_aftertickc = 5;
  
  private final int S_inblob = 6;
  
  static final boolean[] rtfSpecialsTable = (boolean[])noSpecialsTable.clone();
  
  public abstract boolean handleKeyword(String paramString);
  
  public abstract boolean handleKeyword(String paramString, int paramInt);
  
  public abstract void handleText(String paramString);
  
  public void handleText(char paramChar) { handleText(String.valueOf(paramChar)); }
  
  public abstract void handleBinaryBlob(byte[] paramArrayOfByte);
  
  public abstract void begingroup();
  
  public abstract void endgroup();
  
  public void writeSpecial(int paramInt) throws IOException { write((char)paramInt); }
  
  protected void warning(String paramString) {
    if (this.warnings != null)
      this.warnings.println(paramString); 
  }
  
  public void write(String paramString) {
    if (this.state != 0) {
      byte b = 0;
      int i = paramString.length();
      while (b < i && this.state != 0) {
        write(paramString.charAt(b));
        b++;
      } 
      if (b >= i)
        return; 
      paramString = paramString.substring(b);
    } 
    if (this.currentCharacters.length() > 0) {
      this.currentCharacters.append(paramString);
    } else {
      handleText(paramString);
    } 
  }
  
  public void write(char paramChar) {
    int i;
    boolean bool;
    switch (this.state) {
      case 0:
        if (paramChar == '\n' || paramChar == '\r')
          break; 
        if (paramChar == '{') {
          if (this.currentCharacters.length() > 0) {
            handleText(this.currentCharacters.toString());
            this.currentCharacters = new StringBuffer();
          } 
          this.level++;
          begingroup();
          break;
        } 
        if (paramChar == '}') {
          if (this.currentCharacters.length() > 0) {
            handleText(this.currentCharacters.toString());
            this.currentCharacters = new StringBuffer();
          } 
          if (this.level == 0)
            throw new IOException("Too many close-groups in RTF text"); 
          endgroup();
          this.level--;
          break;
        } 
        if (paramChar == '\\') {
          if (this.currentCharacters.length() > 0) {
            handleText(this.currentCharacters.toString());
            this.currentCharacters = new StringBuffer();
          } 
          this.state = 1;
          break;
        } 
        this.currentCharacters.append(paramChar);
        break;
      case 1:
        if (paramChar == '\'') {
          this.state = 4;
          break;
        } 
        if (!Character.isLetter(paramChar)) {
          char[] arrayOfChar = new char[1];
          arrayOfChar[0] = paramChar;
          if (!handleKeyword(new String(arrayOfChar)))
            warning("Unknown keyword: " + arrayOfChar + " (" + (byte)paramChar + ")"); 
          this.state = 0;
          this.pendingKeyword = null;
          break;
        } 
        this.state = 2;
      case 2:
        if (Character.isLetter(paramChar)) {
          this.currentCharacters.append(paramChar);
          break;
        } 
        this.pendingKeyword = this.currentCharacters.toString();
        this.currentCharacters = new StringBuffer();
        if (Character.isDigit(paramChar) || paramChar == '-') {
          this.state = 3;
          this.currentCharacters.append(paramChar);
          break;
        } 
        bool = handleKeyword(this.pendingKeyword);
        if (!bool)
          warning("Unknown keyword: " + this.pendingKeyword); 
        this.pendingKeyword = null;
        this.state = 0;
        if (!Character.isWhitespace(paramChar))
          write(paramChar); 
        break;
      case 3:
        if (Character.isDigit(paramChar)) {
          this.currentCharacters.append(paramChar);
          break;
        } 
        if (this.pendingKeyword.equals("bin")) {
          long l = Long.parseLong(this.currentCharacters.toString());
          this.pendingKeyword = null;
          this.state = 6;
          this.binaryBytesLeft = l;
          if (this.binaryBytesLeft > 2147483647L) {
            this.binaryBuf = new ByteArrayOutputStream(2147483647);
          } else {
            this.binaryBuf = new ByteArrayOutputStream((int)this.binaryBytesLeft);
          } 
          this.savedSpecials = this.specialsTable;
          this.specialsTable = allSpecialsTable;
          break;
        } 
        i = Integer.parseInt(this.currentCharacters.toString());
        bool = handleKeyword(this.pendingKeyword, i);
        if (!bool)
          warning("Unknown keyword: " + this.pendingKeyword + " (param " + this.currentCharacters + ")"); 
        this.pendingKeyword = null;
        this.currentCharacters = new StringBuffer();
        this.state = 0;
        if (!Character.isWhitespace(paramChar))
          write(paramChar); 
        break;
      case 4:
        if (Character.digit(paramChar, 16) == -1) {
          this.state = 0;
          break;
        } 
        this.pendingCharacter = Character.digit(paramChar, 16);
        this.state = 5;
        break;
      case 5:
        this.state = 0;
        if (Character.digit(paramChar, 16) != -1) {
          this.pendingCharacter = this.pendingCharacter * 16 + Character.digit(paramChar, 16);
          paramChar = this.translationTable[this.pendingCharacter];
          if (paramChar != '\000')
            handleText(paramChar); 
        } 
        break;
      case 6:
        this.binaryBuf.write(paramChar);
        this.binaryBytesLeft--;
        if (this.binaryBytesLeft == 0L) {
          this.state = 0;
          this.specialsTable = this.savedSpecials;
          this.savedSpecials = null;
          handleBinaryBlob(this.binaryBuf.toByteArray());
          this.binaryBuf = null;
        } 
        break;
    } 
  }
  
  public void flush() {
    super.flush();
    if (this.state == 0 && this.currentCharacters.length() > 0) {
      handleText(this.currentCharacters.toString());
      this.currentCharacters = new StringBuffer();
    } 
  }
  
  public void close() {
    flush();
    if (this.state != 0 || this.level > 0) {
      warning("Truncated RTF file.");
      while (this.level > 0) {
        endgroup();
        this.level--;
      } 
    } 
    super.close();
  }
  
  static  {
    rtfSpecialsTable[10] = true;
    rtfSpecialsTable[13] = true;
    rtfSpecialsTable[123] = true;
    rtfSpecialsTable[125] = true;
    rtfSpecialsTable[92] = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\RTFParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */