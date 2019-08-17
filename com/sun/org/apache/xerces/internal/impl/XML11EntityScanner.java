package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import java.io.IOException;

public class XML11EntityScanner extends XMLEntityScanner {
  public int peekChar() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    return this.fCurrentEntity.isExternal() ? ((c != '\r' && c != '' && c != ' ') ? c : 10) : c;
  }
  
  protected int scanChar(XMLScanner.NameType paramNameType) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
    boolean bool = false;
    if (c == '\n' || ((c == '\r' || c == '' || c == ' ') && (bool = this.fCurrentEntity.isExternal()))) {
      this.fCurrentEntity.lineNumber++;
      this.fCurrentEntity.columnNumber = 1;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = (char)c;
        load(1, false, false);
        i = 0;
      } 
      if (c == '\r' && bool) {
        char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (c1 != '\n' && c1 != '')
          this.fCurrentEntity.position--; 
      } 
      c = '\n';
    } 
    this.fCurrentEntity.columnNumber++;
    if (!this.detectingVersion)
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, this.fCurrentEntity.position - i); 
    return c;
  }
  
  protected String scanNmtoken() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    while (true) {
      char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (XML11Char.isXML11Name(c)) {
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int k = this.fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == this.fCurrentEntity.ch.length) {
            char[] arrayOfChar = new char[this.fCurrentEntity.ch.length << 1];
            System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, k);
            this.fCurrentEntity.ch = arrayOfChar;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, k);
          } 
          i = 0;
          if (load(k, false, false))
            break; 
        } 
        continue;
      } 
      if (XML11Char.isXML11NameHighSurrogate(c)) {
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int k = this.fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == this.fCurrentEntity.ch.length) {
            char[] arrayOfChar = new char[this.fCurrentEntity.ch.length << 1];
            System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, k);
            this.fCurrentEntity.ch = arrayOfChar;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, k);
          } 
          i = 0;
          if (load(k, false, false)) {
            this.fCurrentEntity.startPosition--;
            this.fCurrentEntity.position--;
            break;
          } 
        } 
        char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (!XMLChar.isLowSurrogate(c1) || !XML11Char.isXML11Name(XMLChar.supplemental(c, c1))) {
          this.fCurrentEntity.position--;
          break;
        } 
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int k = this.fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == this.fCurrentEntity.ch.length) {
            char[] arrayOfChar = new char[this.fCurrentEntity.ch.length << 1];
            System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, k);
            this.fCurrentEntity.ch = arrayOfChar;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, k);
          } 
          i = 0;
          if (load(k, false, false))
            break; 
        } 
        continue;
      } 
      break;
    } 
    int j = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += j;
    String str = null;
    if (j > 0)
      str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, j); 
    return str;
  }
  
  protected String scanName(XMLScanner.NameType paramNameType) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    char c = this.fCurrentEntity.ch[i];
    if (XML11Char.isXML11NameStart(c)) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = c;
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.columnNumber++;
          return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
        } 
      } 
    } else if (XML11Char.isXML11NameHighSurrogate(c)) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = c;
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.position--;
          this.fCurrentEntity.startPosition--;
          return null;
        } 
      } 
      char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (!XMLChar.isLowSurrogate(c1) || !XML11Char.isXML11NameStart(XMLChar.supplemental(c, c1))) {
        this.fCurrentEntity.position--;
        return null;
      } 
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(2);
        this.fCurrentEntity.ch[0] = c;
        this.fCurrentEntity.ch[1] = c1;
        i = 0;
        if (load(2, false, false)) {
          this.fCurrentEntity.columnNumber += 2;
          return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
        } 
      } 
    } else {
      return null;
    } 
    int j = 0;
    while (true) {
      c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (XML11Char.isXML11Name(c)) {
        if ((j = checkBeforeLoad(this.fCurrentEntity, i, i)) > 0) {
          i = 0;
          if (load(j, false, false))
            break; 
        } 
        continue;
      } 
      if (XML11Char.isXML11NameHighSurrogate(c)) {
        if ((j = checkBeforeLoad(this.fCurrentEntity, i, i)) > 0) {
          i = 0;
          if (load(j, false, false)) {
            this.fCurrentEntity.position--;
            this.fCurrentEntity.startPosition--;
            break;
          } 
        } 
        char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (!XMLChar.isLowSurrogate(c1) || !XML11Char.isXML11Name(XMLChar.supplemental(c, c1))) {
          this.fCurrentEntity.position--;
          break;
        } 
        if ((j = checkBeforeLoad(this.fCurrentEntity, i, i)) > 0) {
          i = 0;
          if (load(j, false, false))
            break; 
        } 
        continue;
      } 
      break;
    } 
    j = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += j;
    String str = null;
    if (j > 0) {
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, j);
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, j);
      str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, j);
    } 
    return str;
  }
  
  protected String scanNCName() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    char c = this.fCurrentEntity.ch[i];
    if (XML11Char.isXML11NCNameStart(c)) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = c;
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.columnNumber++;
          return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
        } 
      } 
    } else if (XML11Char.isXML11NameHighSurrogate(c)) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = c;
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.position--;
          this.fCurrentEntity.startPosition--;
          return null;
        } 
      } 
      char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (!XMLChar.isLowSurrogate(c1) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(c, c1))) {
        this.fCurrentEntity.position--;
        return null;
      } 
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(2);
        this.fCurrentEntity.ch[0] = c;
        this.fCurrentEntity.ch[1] = c1;
        i = 0;
        if (load(2, false, false)) {
          this.fCurrentEntity.columnNumber += 2;
          return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
        } 
      } 
    } else {
      return null;
    } 
    while (true) {
      c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (XML11Char.isXML11NCName(c)) {
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int k = this.fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == this.fCurrentEntity.ch.length) {
            char[] arrayOfChar = new char[this.fCurrentEntity.ch.length << 1];
            System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, k);
            this.fCurrentEntity.ch = arrayOfChar;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, k);
          } 
          i = 0;
          if (load(k, false, false))
            break; 
        } 
        continue;
      } 
      if (XML11Char.isXML11NameHighSurrogate(c)) {
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int k = this.fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == this.fCurrentEntity.ch.length) {
            char[] arrayOfChar = new char[this.fCurrentEntity.ch.length << 1];
            System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, k);
            this.fCurrentEntity.ch = arrayOfChar;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, k);
          } 
          i = 0;
          if (load(k, false, false)) {
            this.fCurrentEntity.startPosition--;
            this.fCurrentEntity.position--;
            break;
          } 
        } 
        char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (!XMLChar.isLowSurrogate(c1) || !XML11Char.isXML11NCName(XMLChar.supplemental(c, c1))) {
          this.fCurrentEntity.position--;
          break;
        } 
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int k = this.fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == this.fCurrentEntity.ch.length) {
            char[] arrayOfChar = new char[this.fCurrentEntity.ch.length << 1];
            System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, k);
            this.fCurrentEntity.ch = arrayOfChar;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, k);
          } 
          i = 0;
          if (load(k, false, false))
            break; 
        } 
        continue;
      } 
      break;
    } 
    int j = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += j;
    String str = null;
    if (j > 0)
      str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, j); 
    return str;
  }
  
  protected boolean scanQName(QName paramQName, XMLScanner.NameType paramNameType) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    char c = this.fCurrentEntity.ch[i];
    if (XML11Char.isXML11NCNameStart(c)) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = c;
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.columnNumber++;
          String str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
          paramQName.setValues(null, str, str, null);
          checkEntityLimit(paramNameType, this.fCurrentEntity, 0, 1);
          return true;
        } 
      } 
    } else if (XML11Char.isXML11NameHighSurrogate(c)) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = c;
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.startPosition--;
          this.fCurrentEntity.position--;
          return false;
        } 
      } 
      char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (!XMLChar.isLowSurrogate(c1) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(c, c1))) {
        this.fCurrentEntity.position--;
        return false;
      } 
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(2);
        this.fCurrentEntity.ch[0] = c;
        this.fCurrentEntity.ch[1] = c1;
        i = 0;
        if (load(2, false, false)) {
          this.fCurrentEntity.columnNumber += 2;
          String str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
          paramQName.setValues(null, str, str, null);
          checkEntityLimit(paramNameType, this.fCurrentEntity, 0, 2);
          return true;
        } 
      } 
    } else {
      return false;
    } 
    int j = -1;
    int k = 0;
    boolean bool = false;
    while (true) {
      c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (XML11Char.isXML11Name(c)) {
        if (c == ':') {
          if (j != -1)
            break; 
          j = this.fCurrentEntity.position;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, j - i);
        } 
        if ((k = checkBeforeLoad(this.fCurrentEntity, i, j)) > 0) {
          if (j != -1)
            j -= i; 
          i = 0;
          if (load(k, false, false))
            break; 
        } 
        continue;
      } 
      if (XML11Char.isXML11NameHighSurrogate(c)) {
        if ((k = checkBeforeLoad(this.fCurrentEntity, i, j)) > 0) {
          if (j != -1)
            j -= i; 
          i = 0;
          if (load(k, false, false)) {
            bool = true;
            this.fCurrentEntity.startPosition--;
            this.fCurrentEntity.position--;
            break;
          } 
        } 
        char c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (!XMLChar.isLowSurrogate(c1) || !XML11Char.isXML11Name(XMLChar.supplemental(c, c1))) {
          bool = true;
          this.fCurrentEntity.position--;
          break;
        } 
        if ((k = checkBeforeLoad(this.fCurrentEntity, i, j)) > 0) {
          if (j != -1)
            j -= i; 
          i = 0;
          if (load(k, false, false))
            break; 
        } 
        continue;
      } 
      break;
    } 
    k = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += k;
    if (k > 0) {
      String str1 = null;
      String str2 = null;
      String str3 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, k);
      if (j != -1) {
        int m = j - i;
        checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, m);
        str1 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, m);
        int n = k - m - 1;
        int i1 = j + 1;
        if (!XML11Char.isXML11NCNameStart(this.fCurrentEntity.ch[i1]) && (!XML11Char.isXML11NameHighSurrogate(this.fCurrentEntity.ch[i1]) || bool))
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2); 
        checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, j + 1, n);
        str2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, j + 1, n);
      } else {
        str2 = str3;
        checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, k);
      } 
      paramQName.setValues(str1, str2, str3, null);
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, k);
      return true;
    } 
    return false;
  }
  
  protected int scanContent(XMLString paramXMLString) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
      invokeListeners(1);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
      load(1, false, false);
      this.fCurrentEntity.position = 0;
      this.fCurrentEntity.startPosition = 0;
    } 
    int i = this.fCurrentEntity.position;
    byte b = this.fCurrentEntity.ch[i];
    int j = 0;
    boolean bool = false;
    boolean bool1 = this.fCurrentEntity.isExternal();
    if (b == 10 || ((b == 13 || b == 133 || b == 8232) && bool1)) {
      do {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (b == 13 && bool1) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            checkEntityLimit(null, this.fCurrentEntity, i, j);
            i = 0;
            this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
            this.fCurrentEntity.position = j;
            this.fCurrentEntity.startPosition = j;
            if (load(j, false, true)) {
              bool = true;
              break;
            } 
          } 
          char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
          if (c == '\n' || c == '') {
            this.fCurrentEntity.position++;
            i++;
          } else {
            j++;
          } 
        } else if (b == 10 || ((b == 133 || b == 8232) && bool1)) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            checkEntityLimit(null, this.fCurrentEntity, i, j);
            i = 0;
            this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
            this.fCurrentEntity.position = j;
            this.fCurrentEntity.startPosition = j;
            if (load(j, false, true)) {
              bool = true;
              break;
            } 
          } 
        } else {
          this.fCurrentEntity.position--;
          break;
        } 
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      int m;
      for (m = i; m < this.fCurrentEntity.position; m++)
        this.fCurrentEntity.ch[m] = '\n'; 
      m = this.fCurrentEntity.position - i;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
        checkEntityLimit(null, this.fCurrentEntity, i, m);
        paramXMLString.setValues(this.fCurrentEntity.ch, i, m);
        return -1;
      } 
    } 
    if (bool1) {
      while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (!XML11Char.isXML11Content(b) || b == 133 || b == 8232) {
          this.fCurrentEntity.position--;
          break;
        } 
      } 
    } else {
      while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (!XML11Char.isXML11InternalEntityContent(b)) {
          this.fCurrentEntity.position--;
          break;
        } 
      } 
    } 
    int k = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += k - j;
    if (!bool)
      checkEntityLimit(null, this.fCurrentEntity, i, k); 
    paramXMLString.setValues(this.fCurrentEntity.ch, i, k);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
      b = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if ((b == 13 || b == 133 || b == 8232) && bool1)
        b = 10; 
    } else {
      b = -1;
    } 
    return b;
  }
  
  protected int scanLiteral(int paramInt, XMLString paramXMLString, boolean paramBoolean) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
      invokeListeners(1);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
      load(1, false, false);
      this.fCurrentEntity.startPosition = 0;
      this.fCurrentEntity.position = 0;
    } 
    int i = this.fCurrentEntity.position;
    byte b = this.fCurrentEntity.ch[i];
    int j = 0;
    boolean bool = this.fCurrentEntity.isExternal();
    if (b == 10 || ((b == 13 || b == 133 || b == 8232) && bool)) {
      do {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (b == 13 && bool) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            i = 0;
            this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
            this.fCurrentEntity.position = j;
            this.fCurrentEntity.startPosition = j;
            if (load(j, false, true))
              break; 
          } 
          char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
          if (c == '\n' || c == '') {
            this.fCurrentEntity.position++;
            i++;
          } else {
            j++;
          } 
        } else if (b == 10 || ((b == 133 || b == 8232) && bool)) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            i = 0;
            this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
            this.fCurrentEntity.position = j;
            this.fCurrentEntity.startPosition = j;
            if (load(j, false, true))
              break; 
          } 
        } else {
          this.fCurrentEntity.position--;
          break;
        } 
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      int m;
      for (m = i; m < this.fCurrentEntity.position; m++)
        this.fCurrentEntity.ch[m] = '\n'; 
      m = this.fCurrentEntity.position - i;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
        paramXMLString.setValues(this.fCurrentEntity.ch, i, m);
        return -1;
      } 
    } 
    if (bool) {
      while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (b == paramInt || b == 37 || !XML11Char.isXML11Content(b) || b == 133 || b == 8232) {
          this.fCurrentEntity.position--;
          break;
        } 
      } 
    } else {
      while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if ((b == paramInt && !this.fCurrentEntity.literal) || b == 37 || !XML11Char.isXML11InternalEntityContent(b)) {
          this.fCurrentEntity.position--;
          break;
        } 
      } 
    } 
    int k = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += k - j;
    checkEntityLimit(null, this.fCurrentEntity, i, k);
    if (paramBoolean)
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, k); 
    paramXMLString.setValues(this.fCurrentEntity.ch, i, k);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
      b = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (b == paramInt && this.fCurrentEntity.literal)
        byte b1 = -1; 
    } else {
      b = -1;
    } 
    return b;
  }
  
  protected boolean scanData(String paramString, XMLStringBuffer paramXMLStringBuffer) throws IOException {
    boolean bool = false;
    int i = paramString.length();
    char c = paramString.charAt(0);
    boolean bool1 = this.fCurrentEntity.isExternal();
    do {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count)
        load(0, true, false); 
      boolean bool2 = false;
      while (this.fCurrentEntity.position >= this.fCurrentEntity.count - i && !bool2) {
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        bool2 = load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false);
        this.fCurrentEntity.position = 0;
        this.fCurrentEntity.startPosition = 0;
      } 
      if (this.fCurrentEntity.position >= this.fCurrentEntity.count - i) {
        int n = this.fCurrentEntity.count - this.fCurrentEntity.position;
        checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, this.fCurrentEntity.position, n);
        paramXMLStringBuffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, n);
        this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
        this.fCurrentEntity.position = this.fCurrentEntity.count;
        this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
        load(0, true, false);
        return false;
      } 
      int j = this.fCurrentEntity.position;
      char c1 = this.fCurrentEntity.ch[j];
      int k = 0;
      if (c1 == '\n' || ((c1 == '\r' || c1 == '' || c1 == ' ') && bool1)) {
        do {
          c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
          if (c1 == '\r' && bool1) {
            k++;
            this.fCurrentEntity.lineNumber++;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              j = 0;
              this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
              this.fCurrentEntity.position = k;
              this.fCurrentEntity.startPosition = k;
              if (load(k, false, true))
                break; 
            } 
            char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (c2 == '\n' || c2 == '') {
              this.fCurrentEntity.position++;
              j++;
            } else {
              k++;
            } 
          } else if (c1 == '\n' || ((c1 == '' || c1 == ' ') && bool1)) {
            k++;
            this.fCurrentEntity.lineNumber++;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              j = 0;
              this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
              this.fCurrentEntity.position = k;
              this.fCurrentEntity.startPosition = k;
              this.fCurrentEntity.count = k;
              if (load(k, false, true))
                break; 
            } 
          } else {
            this.fCurrentEntity.position--;
            break;
          } 
        } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
        int n;
        for (n = j; n < this.fCurrentEntity.position; n++)
          this.fCurrentEntity.ch[n] = '\n'; 
        n = this.fCurrentEntity.position - j;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
          checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, j, n);
          paramXMLStringBuffer.append(this.fCurrentEntity.ch, j, n);
          return true;
        } 
      } 
      if (bool1) {
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
          c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
          if (c1 == c) {
            int n = this.fCurrentEntity.position - 1;
            for (int i1 = 1; i1 < i; i1++) {
              if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.position -= i1;
                break label116;
              } 
              c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
              if (paramString.charAt(i1) != c1) {
                this.fCurrentEntity.position--;
                break;
              } 
            } 
            if (this.fCurrentEntity.position == n + i) {
              bool = true;
              break;
            } 
            continue;
          } 
          if (c1 == '\n' || c1 == '\r' || c1 == '' || c1 == ' ') {
            this.fCurrentEntity.position--;
            break;
          } 
          if (!XML11Char.isXML11ValidLiteral(c1)) {
            this.fCurrentEntity.position--;
            int n = this.fCurrentEntity.position - j;
            this.fCurrentEntity.columnNumber += n - k;
            checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, j, n);
            paramXMLStringBuffer.append(this.fCurrentEntity.ch, j, n);
            return true;
          } 
        } 
      } else {
        label116: while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
          c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
          if (c1 == c) {
            int n = this.fCurrentEntity.position - 1;
            for (int i1 = 1; i1 < i; i1++) {
              if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.position -= i1;
                break label116;
              } 
              c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
              if (paramString.charAt(i1) != c1) {
                this.fCurrentEntity.position--;
                break;
              } 
            } 
            if (this.fCurrentEntity.position == n + i) {
              bool = true;
              break;
            } 
            continue;
          } 
          if (c1 == '\n') {
            this.fCurrentEntity.position--;
            break;
          } 
          if (!XML11Char.isXML11Valid(c1)) {
            this.fCurrentEntity.position--;
            int n = this.fCurrentEntity.position - j;
            this.fCurrentEntity.columnNumber += n - k;
            checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, j, n);
            paramXMLStringBuffer.append(this.fCurrentEntity.ch, j, n);
            return true;
          } 
        } 
      } 
      int m = this.fCurrentEntity.position - j;
      this.fCurrentEntity.columnNumber += m - k;
      checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, j, m);
      if (bool)
        m -= i; 
      paramXMLStringBuffer.append(this.fCurrentEntity.ch, j, m);
    } while (!bool);
    return !bool;
  }
  
  protected boolean skipChar(int paramInt, XMLScanner.NameType paramNameType) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (c == paramInt) {
      this.fCurrentEntity.position++;
      if (paramInt == 10) {
        this.fCurrentEntity.lineNumber++;
        this.fCurrentEntity.columnNumber = 1;
      } else {
        this.fCurrentEntity.columnNumber++;
      } 
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
      return true;
    } 
    if (paramInt == 10 && (c == ' ' || c == '') && this.fCurrentEntity.isExternal()) {
      this.fCurrentEntity.position++;
      this.fCurrentEntity.lineNumber++;
      this.fCurrentEntity.columnNumber = 1;
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
      return true;
    } 
    if (paramInt == 10 && c == '\r' && this.fCurrentEntity.isExternal()) {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = (char)c;
        load(1, false, false);
      } 
      char c1 = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
      if (c1 == '\n' || c1 == '')
        this.fCurrentEntity.position++; 
      this.fCurrentEntity.lineNumber++;
      this.fCurrentEntity.columnNumber = 1;
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
      return true;
    } 
    return false;
  }
  
  protected boolean skipSpaces() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    if (this.fCurrentEntity == null)
      return false; 
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    int i = this.fCurrentEntity.position - 1;
    if (this.fCurrentEntity.isExternal()) {
      if (XML11Char.isXML11Space(c)) {
        do {
          boolean bool = false;
          if (c == '\n' || c == '\r' || c == '' || c == ' ') {
            this.fCurrentEntity.lineNumber++;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
              invokeListeners(1);
              this.fCurrentEntity.ch[0] = (char)c;
              bool = load(1, true, false);
              if (!bool) {
                this.fCurrentEntity.startPosition = 0;
                this.fCurrentEntity.position = 0;
              } else if (this.fCurrentEntity == null) {
                return true;
              } 
            } 
            if (c == '\r') {
              char c1 = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
              if (c1 != '\n' && c1 != '')
                this.fCurrentEntity.position--; 
            } 
          } else {
            this.fCurrentEntity.columnNumber++;
          } 
          checkEntityLimit(null, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
          i = this.fCurrentEntity.position;
          if (!bool)
            this.fCurrentEntity.position++; 
          if (this.fCurrentEntity.position != this.fCurrentEntity.count)
            continue; 
          load(0, true, true);
          if (this.fCurrentEntity == null)
            return true; 
        } while (XML11Char.isXML11Space(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
        return true;
      } 
    } else if (XMLChar.isSpace(c)) {
      do {
        boolean bool = false;
        if (c == '\n') {
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            invokeListeners(1);
            this.fCurrentEntity.ch[0] = (char)c;
            bool = load(1, true, false);
            if (!bool) {
              this.fCurrentEntity.startPosition = 0;
              this.fCurrentEntity.position = 0;
            } else if (this.fCurrentEntity == null) {
              return true;
            } 
          } 
        } else {
          this.fCurrentEntity.columnNumber++;
        } 
        checkEntityLimit(null, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
        i = this.fCurrentEntity.position;
        if (!bool)
          this.fCurrentEntity.position++; 
        if (this.fCurrentEntity.position != this.fCurrentEntity.count)
          continue; 
        load(0, true, true);
        if (this.fCurrentEntity == null)
          return true; 
      } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
      return true;
    } 
    return false;
  }
  
  protected boolean skipString(String paramString) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = paramString.length();
    int j = this.fCurrentEntity.position;
    for (int k = 0; k < i; k++) {
      char c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
      if (c != paramString.charAt(k)) {
        this.fCurrentEntity.position -= k + 1;
        return false;
      } 
      if (k < i - 1 && this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(0);
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.count - k - 1, this.fCurrentEntity.ch, 0, k + 1);
        if (load(k + 1, false, false)) {
          this.fCurrentEntity.startPosition -= k + 1;
          this.fCurrentEntity.position -= k + 1;
          return false;
        } 
      } 
    } 
    this.fCurrentEntity.columnNumber += i;
    if (!this.detectingVersion)
      checkEntityLimit(null, this.fCurrentEntity, j, i); 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11EntityScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */