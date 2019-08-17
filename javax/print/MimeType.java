package javax.print;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

class MimeType implements Serializable, Cloneable {
  private static final long serialVersionUID = -2785720609362367683L;
  
  private String[] myPieces;
  
  private String myStringValue = null;
  
  private ParameterMapEntrySet myEntrySet = null;
  
  private ParameterMap myParameterMap = null;
  
  private static final int TOKEN_LEXEME = 0;
  
  private static final int QUOTED_STRING_LEXEME = 1;
  
  private static final int TSPECIAL_LEXEME = 2;
  
  private static final int EOF_LEXEME = 3;
  
  private static final int ILLEGAL_LEXEME = 4;
  
  public MimeType(String paramString) { parse(paramString); }
  
  public String getMimeType() { return getStringValue(); }
  
  public String getMediaType() { return this.myPieces[0]; }
  
  public String getMediaSubtype() { return this.myPieces[1]; }
  
  public Map getParameterMap() {
    if (this.myParameterMap == null)
      this.myParameterMap = new ParameterMap(null); 
    return this.myParameterMap;
  }
  
  public String toString() { return getStringValue(); }
  
  public int hashCode() { return getStringValue().hashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof MimeType && getStringValue().equals(((MimeType)paramObject).getStringValue())); }
  
  private String getStringValue() {
    if (this.myStringValue == null) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(this.myPieces[0]);
      stringBuffer.append('/');
      stringBuffer.append(this.myPieces[1]);
      int i = this.myPieces.length;
      for (byte b = 2; b < i; b += 2) {
        stringBuffer.append(';');
        stringBuffer.append(' ');
        stringBuffer.append(this.myPieces[b]);
        stringBuffer.append('=');
        stringBuffer.append(addQuotes(this.myPieces[b + 1]));
      } 
      this.myStringValue = stringBuffer.toString();
    } 
    return this.myStringValue;
  }
  
  private static String toUnicodeLowerCase(String paramString) {
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    for (byte b = 0; b < i; b++)
      arrayOfChar[b] = Character.toLowerCase(paramString.charAt(b)); 
    return new String(arrayOfChar);
  }
  
  private static String removeBackslashes(String paramString) {
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    byte b2 = 0;
    for (byte b1 = 0; b1 < i; b1++) {
      char c = paramString.charAt(b1);
      if (c == '\\')
        c = paramString.charAt(++b1); 
      arrayOfChar[b2++] = c;
    } 
    return new String(arrayOfChar, 0, b2);
  }
  
  private static String addQuotes(String paramString) {
    int i = paramString.length();
    StringBuffer stringBuffer = new StringBuffer(i + 2);
    stringBuffer.append('"');
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c == '"')
        stringBuffer.append('\\'); 
      stringBuffer.append(c);
    } 
    stringBuffer.append('"');
    return stringBuffer.toString();
  }
  
  private void parse(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(paramString);
    Vector vector = new Vector();
    boolean bool1 = false;
    boolean bool2 = false;
    if (lexicalAnalyzer.getLexemeType() == 0) {
      String str = toUnicodeLowerCase(lexicalAnalyzer.getLexeme());
      vector.add(str);
      lexicalAnalyzer.nextLexeme();
      bool1 = str.equals("text");
    } else {
      throw new IllegalArgumentException();
    } 
    if (lexicalAnalyzer.getLexemeType() == 2 && lexicalAnalyzer.getLexemeFirstCharacter() == '/') {
      lexicalAnalyzer.nextLexeme();
    } else {
      throw new IllegalArgumentException();
    } 
    if (lexicalAnalyzer.getLexemeType() == 0) {
      vector.add(toUnicodeLowerCase(lexicalAnalyzer.getLexeme()));
      lexicalAnalyzer.nextLexeme();
    } else {
      throw new IllegalArgumentException();
    } 
    while (lexicalAnalyzer.getLexemeType() == 2 && lexicalAnalyzer.getLexemeFirstCharacter() == ';') {
      lexicalAnalyzer.nextLexeme();
      if (lexicalAnalyzer.getLexemeType() == 0) {
        String str = toUnicodeLowerCase(lexicalAnalyzer.getLexeme());
        vector.add(str);
        lexicalAnalyzer.nextLexeme();
        bool2 = str.equals("charset");
      } else {
        throw new IllegalArgumentException();
      } 
      if (lexicalAnalyzer.getLexemeType() == 2 && lexicalAnalyzer.getLexemeFirstCharacter() == '=') {
        lexicalAnalyzer.nextLexeme();
      } else {
        throw new IllegalArgumentException();
      } 
      if (lexicalAnalyzer.getLexemeType() == 0) {
        String str = lexicalAnalyzer.getLexeme();
        vector.add((bool1 && bool2) ? toUnicodeLowerCase(str) : str);
        lexicalAnalyzer.nextLexeme();
        continue;
      } 
      if (lexicalAnalyzer.getLexemeType() == 1) {
        String str = removeBackslashes(lexicalAnalyzer.getLexeme());
        vector.add((bool1 && bool2) ? toUnicodeLowerCase(str) : str);
        lexicalAnalyzer.nextLexeme();
        continue;
      } 
      throw new IllegalArgumentException();
    } 
    if (lexicalAnalyzer.getLexemeType() != 3)
      throw new IllegalArgumentException(); 
    int i = vector.size();
    this.myPieces = (String[])vector.toArray(new String[i]);
    for (byte b = 4; b < i; b += 2) {
      byte b1;
      for (b1 = 2; b1 < b && this.myPieces[b1].compareTo(this.myPieces[b]) <= 0; b1 += 2);
      while (b1 < b) {
        String str = this.myPieces[b1];
        this.myPieces[b1] = this.myPieces[b];
        this.myPieces[b] = str;
        str = this.myPieces[b1 + 1];
        this.myPieces[b1 + 1] = this.myPieces[b + 1];
        this.myPieces[b + 1] = str;
        b1 += 2;
      } 
    } 
  }
  
  private static class LexicalAnalyzer {
    protected String mySource;
    
    protected int mySourceLength;
    
    protected int myCurrentIndex;
    
    protected int myLexemeType;
    
    protected int myLexemeBeginIndex;
    
    protected int myLexemeEndIndex;
    
    public LexicalAnalyzer(String param1String) {
      this.mySource = param1String;
      this.mySourceLength = param1String.length();
      this.myCurrentIndex = 0;
      nextLexeme();
    }
    
    public int getLexemeType() { return this.myLexemeType; }
    
    public String getLexeme() { return (this.myLexemeBeginIndex >= this.mySourceLength) ? null : this.mySource.substring(this.myLexemeBeginIndex, this.myLexemeEndIndex); }
    
    public char getLexemeFirstCharacter() { return (this.myLexemeBeginIndex >= this.mySourceLength) ? Character.MIN_VALUE : this.mySource.charAt(this.myLexemeBeginIndex); }
    
    public void nextLexeme() {
      byte b = 0;
      byte b1 = 0;
      while (b) {
        char c;
        switch (b) {
          case false:
            if (this.myCurrentIndex >= this.mySourceLength) {
              this.myLexemeType = 3;
              this.myLexemeBeginIndex = this.mySourceLength;
              this.myLexemeEndIndex = this.mySourceLength;
              b = -1;
              continue;
            } 
            if (Character.isWhitespace(c = this.mySource.charAt(this.myCurrentIndex++))) {
              b = 0;
              continue;
            } 
            if (c == '"') {
              this.myLexemeType = 1;
              this.myLexemeBeginIndex = this.myCurrentIndex;
              b = 1;
              continue;
            } 
            if (c == '(') {
              b1++;
              b = 3;
              continue;
            } 
            if (c == '/' || c == ';' || c == '=' || c == ')' || c == '<' || c == '>' || c == '@' || c == ',' || c == ':' || c == '\\' || c == '[' || c == ']' || c == '?') {
              this.myLexemeType = 2;
              this.myLexemeBeginIndex = this.myCurrentIndex - 1;
              this.myLexemeEndIndex = this.myCurrentIndex;
              b = -1;
              continue;
            } 
            this.myLexemeType = 0;
            this.myLexemeBeginIndex = this.myCurrentIndex - 1;
            b = 5;
          case true:
            if (this.myCurrentIndex >= this.mySourceLength) {
              this.myLexemeType = 4;
              this.myLexemeBeginIndex = this.mySourceLength;
              this.myLexemeEndIndex = this.mySourceLength;
              b = -1;
              continue;
            } 
            if ((c = this.mySource.charAt(this.myCurrentIndex++)) == '"') {
              this.myLexemeEndIndex = this.myCurrentIndex - 1;
              b = -1;
              continue;
            } 
            if (c == '\\') {
              b = 2;
              continue;
            } 
            b = 1;
          case true:
            if (this.myCurrentIndex >= this.mySourceLength) {
              this.myLexemeType = 4;
              this.myLexemeBeginIndex = this.mySourceLength;
              this.myLexemeEndIndex = this.mySourceLength;
              b = -1;
              continue;
            } 
            this.myCurrentIndex++;
            b = 1;
          case true:
            if (this.myCurrentIndex >= this.mySourceLength) {
              this.myLexemeType = 4;
              this.myLexemeBeginIndex = this.mySourceLength;
              this.myLexemeEndIndex = this.mySourceLength;
              b = -1;
              continue;
            } 
            if ((c = this.mySource.charAt(this.myCurrentIndex++)) == '(') {
              b1++;
              b = 3;
              continue;
            } 
            if (c == ')') {
              b = (--b1 == 0) ? 0 : 3;
              continue;
            } 
            if (c == '\\') {
              b = 4;
              continue;
            } 
            b = 3;
          case true:
            if (this.myCurrentIndex >= this.mySourceLength) {
              this.myLexemeType = 4;
              this.myLexemeBeginIndex = this.mySourceLength;
              this.myLexemeEndIndex = this.mySourceLength;
              b = -1;
              continue;
            } 
            this.myCurrentIndex++;
            b = 3;
          case true:
            if (this.myCurrentIndex >= this.mySourceLength) {
              this.myLexemeEndIndex = this.myCurrentIndex;
              b = -1;
              continue;
            } 
            if (Character.isWhitespace(c = this.mySource.charAt(this.myCurrentIndex++))) {
              this.myLexemeEndIndex = this.myCurrentIndex - 1;
              b = -1;
              continue;
            } 
            if (c == '"' || c == '(' || c == '/' || c == ';' || c == '=' || c == ')' || c == '<' || c == '>' || c == '@' || c == ',' || c == ':' || c == '\\' || c == '[' || c == ']' || c == '?') {
              this.myLexemeEndIndex = --this.myCurrentIndex;
              b = -1;
              continue;
            } 
            b = 5;
        } 
      } 
    }
  }
  
  private class ParameterMap extends AbstractMap {
    private ParameterMap() {}
    
    public Set entrySet() {
      if (MimeType.this.myEntrySet == null)
        MimeType.this.myEntrySet = new MimeType.ParameterMapEntrySet(MimeType.this, null); 
      return MimeType.this.myEntrySet;
    }
  }
  
  private class ParameterMapEntry implements Map.Entry {
    private int myIndex;
    
    public ParameterMapEntry(int param1Int) { this.myIndex = param1Int; }
    
    public Object getKey() { return MimeType.this.myPieces[this.myIndex]; }
    
    public Object getValue() { return MimeType.this.myPieces[this.myIndex + 1]; }
    
    public Object setValue(Object param1Object) { throw new UnsupportedOperationException(); }
    
    public boolean equals(Object param1Object) { return (param1Object != null && param1Object instanceof Map.Entry && getKey().equals(((Map.Entry)param1Object).getKey()) && getValue().equals(((Map.Entry)param1Object).getValue())); }
    
    public int hashCode() { return getKey().hashCode() ^ getValue().hashCode(); }
  }
  
  private class ParameterMapEntrySet extends AbstractSet {
    private ParameterMapEntrySet() {}
    
    public Iterator iterator() { return new MimeType.ParameterMapEntrySetIterator(MimeType.this, null); }
    
    public int size() { return (MimeType.this.myPieces.length - 2) / 2; }
  }
  
  private class ParameterMapEntrySetIterator implements Iterator {
    private int myIndex = 2;
    
    private ParameterMapEntrySetIterator() {}
    
    public boolean hasNext() { return (this.myIndex < MimeType.this.myPieces.length); }
    
    public Object next() {
      if (hasNext()) {
        MimeType.ParameterMapEntry parameterMapEntry = new MimeType.ParameterMapEntry(MimeType.this, this.myIndex);
        this.myIndex += 2;
        return parameterMapEntry;
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\MimeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */