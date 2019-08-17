package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HTMLSerializer extends BaseMarkupSerializer {
  private boolean _xhtml;
  
  public static final String XHTMLNamespace = "http://www.w3.org/1999/xhtml";
  
  private String fUserXHTMLNamespace = null;
  
  protected HTMLSerializer(boolean paramBoolean, OutputFormat paramOutputFormat) {
    super(paramOutputFormat);
    this._xhtml = paramBoolean;
  }
  
  public HTMLSerializer() { this(false, new OutputFormat("html", "ISO-8859-1", false)); }
  
  public HTMLSerializer(OutputFormat paramOutputFormat) { this(false, (paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("html", "ISO-8859-1", false)); }
  
  public HTMLSerializer(Writer paramWriter, OutputFormat paramOutputFormat) {
    this(false, (paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("html", "ISO-8859-1", false));
    setOutputCharStream(paramWriter);
  }
  
  public HTMLSerializer(OutputStream paramOutputStream, OutputFormat paramOutputFormat) {
    this(false, (paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("html", "ISO-8859-1", false));
    setOutputByteStream(paramOutputStream);
  }
  
  public void setOutputFormat(OutputFormat paramOutputFormat) { super.setOutputFormat((paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("html", "ISO-8859-1", false)); }
  
  public void setXHTMLNamespace(String paramString) { this.fUserXHTMLNamespace = paramString; }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    boolean bool = false;
    try {
      String str;
      if (this._printer == null)
        throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null)); 
      ElementState elementState = getElementState();
      if (isDocumentState()) {
        if (!this._started)
          startDocument((paramString2 == null || paramString2.length() == 0) ? paramString3 : paramString2); 
      } else {
        if (elementState.empty)
          this._printer.printText('>'); 
        if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement))
          this._printer.breakLine(); 
      } 
      boolean bool1 = elementState.preserveSpace;
      boolean bool2 = (paramString1 != null && paramString1.length() != 0) ? 1 : 0;
      if (paramString3 == null || paramString3.length() == 0) {
        paramString3 = paramString2;
        if (bool2) {
          String str1 = getPrefix(paramString1);
          if (str1 != null && str1.length() != 0)
            paramString3 = str1 + ":" + paramString2; 
        } 
        bool = true;
      } 
      if (!bool2) {
        str = paramString3;
      } else if (paramString1.equals("http://www.w3.org/1999/xhtml") || (this.fUserXHTMLNamespace != null && this.fUserXHTMLNamespace.equals(paramString1))) {
        str = paramString2;
      } else {
        str = null;
      } 
      this._printer.printText('<');
      if (this._xhtml) {
        this._printer.printText(paramString3.toLowerCase(Locale.ENGLISH));
      } else {
        this._printer.printText(paramString3);
      } 
      this._printer.indent();
      if (paramAttributes != null)
        for (byte b = 0; b < paramAttributes.getLength(); b++) {
          this._printer.printSpace();
          String str1 = paramAttributes.getQName(b).toLowerCase(Locale.ENGLISH);
          String str2 = paramAttributes.getValue(b);
          if (this._xhtml || bool2) {
            if (str2 == null) {
              this._printer.printText(str1);
              this._printer.printText("=\"\"");
            } else {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              printEscaped(str2);
              this._printer.printText('"');
            } 
          } else {
            if (str2 == null)
              str2 = ""; 
            if (!this._format.getPreserveEmptyAttributes() && str2.length() == 0) {
              this._printer.printText(str1);
            } else if (HTMLdtd.isURI(paramString3, str1)) {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              this._printer.printText(escapeURI(str2));
              this._printer.printText('"');
            } else if (HTMLdtd.isBoolean(paramString3, str1)) {
              this._printer.printText(str1);
            } else {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              printEscaped(str2);
              this._printer.printText('"');
            } 
          } 
        }  
      if (str != null && HTMLdtd.isPreserveSpace(str))
        bool1 = true; 
      if (bool)
        for (Map.Entry entry : this._prefixes.entrySet()) {
          this._printer.printSpace();
          String str2 = (String)entry.getKey();
          String str1 = (String)entry.getValue();
          if (str1.length() == 0) {
            this._printer.printText("xmlns=\"");
            printEscaped(str2);
            this._printer.printText('"');
            continue;
          } 
          this._printer.printText("xmlns:");
          this._printer.printText(str1);
          this._printer.printText("=\"");
          printEscaped(str2);
          this._printer.printText('"');
        }  
      elementState = enterElementState(paramString1, paramString2, paramString3, bool1);
      if (str != null && (str.equalsIgnoreCase("A") || str.equalsIgnoreCase("TD"))) {
        elementState.empty = false;
        this._printer.printText('>');
      } 
      if (str != null && (paramString3.equalsIgnoreCase("SCRIPT") || paramString3.equalsIgnoreCase("STYLE")))
        if (this._xhtml) {
          elementState.doCData = true;
        } else {
          elementState.unescaped = true;
        }  
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      endElementIO(paramString1, paramString2, paramString3);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElementIO(String paramString1, String paramString2, String paramString3) throws SAXException {
    String str;
    this._printer.unindent();
    ElementState elementState = getElementState();
    if (elementState.namespaceURI == null || elementState.namespaceURI.length() == 0) {
      str = elementState.rawName;
    } else if (elementState.namespaceURI.equals("http://www.w3.org/1999/xhtml") || (this.fUserXHTMLNamespace != null && this.fUserXHTMLNamespace.equals(elementState.namespaceURI))) {
      str = elementState.localName;
    } else {
      str = null;
    } 
    if (this._xhtml) {
      if (elementState.empty) {
        this._printer.printText(" />");
      } else {
        if (elementState.inCData)
          this._printer.printText("]]>"); 
        this._printer.printText("</");
        this._printer.printText(elementState.rawName.toLowerCase(Locale.ENGLISH));
        this._printer.printText('>');
      } 
    } else {
      if (elementState.empty)
        this._printer.printText('>'); 
      if (str == null || !HTMLdtd.isOnlyOpening(str)) {
        if (this._indenting && !elementState.preserveSpace && elementState.afterElement)
          this._printer.breakLine(); 
        if (elementState.inCData)
          this._printer.printText("]]>"); 
        this._printer.printText("</");
        this._printer.printText(elementState.rawName);
        this._printer.printText('>');
      } 
    } 
    elementState = leaveElementState();
    if (str == null || (!str.equalsIgnoreCase("A") && !str.equalsIgnoreCase("TD")))
      elementState.afterElement = true; 
    elementState.empty = false;
    if (isDocumentState())
      this._printer.flush(); 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      ElementState elementState = content();
      elementState.doCData = false;
      super.characters(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startElement(String paramString, AttributeList paramAttributeList) throws SAXException {
    try {
      if (this._printer == null)
        throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null)); 
      ElementState elementState = getElementState();
      if (isDocumentState()) {
        if (!this._started)
          startDocument(paramString); 
      } else {
        if (elementState.empty)
          this._printer.printText('>'); 
        if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement))
          this._printer.breakLine(); 
      } 
      boolean bool = elementState.preserveSpace;
      this._printer.printText('<');
      if (this._xhtml) {
        this._printer.printText(paramString.toLowerCase(Locale.ENGLISH));
      } else {
        this._printer.printText(paramString);
      } 
      this._printer.indent();
      if (paramAttributeList != null)
        for (byte b = 0; b < paramAttributeList.getLength(); b++) {
          this._printer.printSpace();
          String str1 = paramAttributeList.getName(b).toLowerCase(Locale.ENGLISH);
          String str2 = paramAttributeList.getValue(b);
          if (this._xhtml) {
            if (str2 == null) {
              this._printer.printText(str1);
              this._printer.printText("=\"\"");
            } else {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              printEscaped(str2);
              this._printer.printText('"');
            } 
          } else {
            if (str2 == null)
              str2 = ""; 
            if (!this._format.getPreserveEmptyAttributes() && str2.length() == 0) {
              this._printer.printText(str1);
            } else if (HTMLdtd.isURI(paramString, str1)) {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              this._printer.printText(escapeURI(str2));
              this._printer.printText('"');
            } else if (HTMLdtd.isBoolean(paramString, str1)) {
              this._printer.printText(str1);
            } else {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              printEscaped(str2);
              this._printer.printText('"');
            } 
          } 
        }  
      if (HTMLdtd.isPreserveSpace(paramString))
        bool = true; 
      elementState = enterElementState(null, null, paramString, bool);
      if (paramString.equalsIgnoreCase("A") || paramString.equalsIgnoreCase("TD")) {
        elementState.empty = false;
        this._printer.printText('>');
      } 
      if (paramString.equalsIgnoreCase("SCRIPT") || paramString.equalsIgnoreCase("STYLE"))
        if (this._xhtml) {
          elementState.doCData = true;
        } else {
          elementState.unescaped = true;
        }  
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElement(String paramString) { endElement(null, null, paramString); }
  
  protected void startDocument(String paramString) {
    this._printer.leaveDTD();
    if (!this._started) {
      if (this._docTypePublicId == null && this._docTypeSystemId == null)
        if (this._xhtml) {
          this._docTypePublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
          this._docTypeSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
        } else {
          this._docTypePublicId = "-//W3C//DTD HTML 4.01//EN";
          this._docTypeSystemId = "http://www.w3.org/TR/html4/strict.dtd";
        }  
      if (!this._format.getOmitDocumentType())
        if (this._docTypePublicId != null && (!this._xhtml || this._docTypeSystemId != null)) {
          if (this._xhtml) {
            this._printer.printText("<!DOCTYPE html PUBLIC ");
          } else {
            this._printer.printText("<!DOCTYPE HTML PUBLIC ");
          } 
          printDoctypeURL(this._docTypePublicId);
          if (this._docTypeSystemId != null) {
            if (this._indenting) {
              this._printer.breakLine();
              this._printer.printText("                      ");
            } else {
              this._printer.printText(' ');
            } 
            printDoctypeURL(this._docTypeSystemId);
          } 
          this._printer.printText('>');
          this._printer.breakLine();
        } else if (this._docTypeSystemId != null) {
          if (this._xhtml) {
            this._printer.printText("<!DOCTYPE html SYSTEM ");
          } else {
            this._printer.printText("<!DOCTYPE HTML SYSTEM ");
          } 
          printDoctypeURL(this._docTypeSystemId);
          this._printer.printText('>');
          this._printer.breakLine();
        }  
    } 
    this._started = true;
    serializePreRoot();
  }
  
  protected void serializeElement(Element paramElement) throws IOException {
    String str = paramElement.getTagName();
    ElementState elementState = getElementState();
    if (isDocumentState()) {
      if (!this._started)
        startDocument(str); 
    } else {
      if (elementState.empty)
        this._printer.printText('>'); 
      if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement))
        this._printer.breakLine(); 
    } 
    boolean bool = elementState.preserveSpace;
    this._printer.printText('<');
    if (this._xhtml) {
      this._printer.printText(str.toLowerCase(Locale.ENGLISH));
    } else {
      this._printer.printText(str);
    } 
    this._printer.indent();
    NamedNodeMap namedNodeMap = paramElement.getAttributes();
    if (namedNodeMap != null)
      for (byte b = 0; b < namedNodeMap.getLength(); b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str1 = attr.getName().toLowerCase(Locale.ENGLISH);
        String str2 = attr.getValue();
        if (attr.getSpecified()) {
          this._printer.printSpace();
          if (this._xhtml) {
            if (str2 == null) {
              this._printer.printText(str1);
              this._printer.printText("=\"\"");
            } else {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              printEscaped(str2);
              this._printer.printText('"');
            } 
          } else {
            if (str2 == null)
              str2 = ""; 
            if (!this._format.getPreserveEmptyAttributes() && str2.length() == 0) {
              this._printer.printText(str1);
            } else if (HTMLdtd.isURI(str, str1)) {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              this._printer.printText(escapeURI(str2));
              this._printer.printText('"');
            } else if (HTMLdtd.isBoolean(str, str1)) {
              this._printer.printText(str1);
            } else {
              this._printer.printText(str1);
              this._printer.printText("=\"");
              printEscaped(str2);
              this._printer.printText('"');
            } 
          } 
        } 
      }  
    if (HTMLdtd.isPreserveSpace(str))
      bool = true; 
    if (paramElement.hasChildNodes() || !HTMLdtd.isEmptyTag(str)) {
      elementState = enterElementState(null, null, str, bool);
      if (str.equalsIgnoreCase("A") || str.equalsIgnoreCase("TD")) {
        elementState.empty = false;
        this._printer.printText('>');
      } 
      if (str.equalsIgnoreCase("SCRIPT") || str.equalsIgnoreCase("STYLE"))
        if (this._xhtml) {
          elementState.doCData = true;
        } else {
          elementState.unescaped = true;
        }  
      for (Node node = paramElement.getFirstChild(); node != null; node = node.getNextSibling())
        serializeNode(node); 
      endElementIO(null, null, str);
    } else {
      this._printer.unindent();
      if (this._xhtml) {
        this._printer.printText(" />");
      } else {
        this._printer.printText('>');
      } 
      elementState.afterElement = true;
      elementState.empty = false;
      if (isDocumentState())
        this._printer.flush(); 
    } 
  }
  
  protected void characters(String paramString) {
    ElementState elementState = content();
    super.characters(paramString);
  }
  
  protected String getEntityRef(int paramInt) { return HTMLdtd.fromChar(paramInt); }
  
  protected String escapeURI(String paramString) {
    int i = paramString.indexOf("\"");
    return (i >= 0) ? paramString.substring(0, i) : paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\HTMLSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */