package javax.swing.text.html.parser;

import java.io.IOException;
import java.io.Reader;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class DocumentParser extends Parser {
  private int inbody;
  
  private int intitle;
  
  private int inhead;
  
  private int instyle;
  
  private int inscript;
  
  private boolean seentitle;
  
  private HTMLEditorKit.ParserCallback callback = null;
  
  private boolean ignoreCharSet = false;
  
  private static final boolean debugFlag = false;
  
  public DocumentParser(DTD paramDTD) { super(paramDTD); }
  
  public void parse(Reader paramReader, HTMLEditorKit.ParserCallback paramParserCallback, boolean paramBoolean) throws IOException {
    this.ignoreCharSet = paramBoolean;
    this.callback = paramParserCallback;
    parse(paramReader);
    paramParserCallback.handleEndOfLineString(getEndOfLineString());
  }
  
  protected void handleStartTag(TagElement paramTagElement) {
    Element element = paramTagElement.getElement();
    if (element == this.dtd.body) {
      this.inbody++;
    } else if (element != this.dtd.html) {
      if (element == this.dtd.head) {
        this.inhead++;
      } else if (element == this.dtd.title) {
        this.intitle++;
      } else if (element == this.dtd.style) {
        this.instyle++;
      } else if (element == this.dtd.script) {
        this.inscript++;
      } 
    } 
    if (paramTagElement.fictional()) {
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
      simpleAttributeSet.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
      this.callback.handleStartTag(paramTagElement.getHTMLTag(), simpleAttributeSet, getBlockStartPosition());
    } else {
      this.callback.handleStartTag(paramTagElement.getHTMLTag(), getAttributes(), getBlockStartPosition());
      flushAttributes();
    } 
  }
  
  protected void handleComment(char[] paramArrayOfChar) { this.callback.handleComment(paramArrayOfChar, getBlockStartPosition()); }
  
  protected void handleEmptyTag(TagElement paramTagElement) {
    Element element = paramTagElement.getElement();
    if (element == this.dtd.meta && !this.ignoreCharSet) {
      SimpleAttributeSet simpleAttributeSet = getAttributes();
      if (simpleAttributeSet != null) {
        String str = (String)simpleAttributeSet.getAttribute(HTML.Attribute.CONTENT);
        if (str != null)
          if ("content-type".equalsIgnoreCase((String)simpleAttributeSet.getAttribute(HTML.Attribute.HTTPEQUIV))) {
            if (!str.equalsIgnoreCase("text/html") && !str.equalsIgnoreCase("text/plain"))
              throw new ChangedCharSetException(str, false); 
          } else if ("charset".equalsIgnoreCase((String)simpleAttributeSet.getAttribute(HTML.Attribute.HTTPEQUIV))) {
            throw new ChangedCharSetException(str, true);
          }  
      } 
    } 
    if (this.inbody != 0 || element == this.dtd.meta || element == this.dtd.base || element == this.dtd.isindex || element == this.dtd.style || element == this.dtd.link)
      if (paramTagElement.fictional()) {
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        simpleAttributeSet.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
        this.callback.handleSimpleTag(paramTagElement.getHTMLTag(), simpleAttributeSet, getBlockStartPosition());
      } else {
        this.callback.handleSimpleTag(paramTagElement.getHTMLTag(), getAttributes(), getBlockStartPosition());
        flushAttributes();
      }  
  }
  
  protected void handleEndTag(TagElement paramTagElement) {
    Element element = paramTagElement.getElement();
    if (element == this.dtd.body) {
      this.inbody--;
    } else if (element == this.dtd.title) {
      this.intitle--;
      this.seentitle = true;
    } else if (element == this.dtd.head) {
      this.inhead--;
    } else if (element == this.dtd.style) {
      this.instyle--;
    } else if (element == this.dtd.script) {
      this.inscript--;
    } 
    this.callback.handleEndTag(paramTagElement.getHTMLTag(), getBlockStartPosition());
  }
  
  protected void handleText(char[] paramArrayOfChar) {
    if (paramArrayOfChar != null) {
      if (this.inscript != 0) {
        this.callback.handleComment(paramArrayOfChar, getBlockStartPosition());
        return;
      } 
      if (this.inbody != 0 || this.instyle != 0 || (this.intitle != 0 && !this.seentitle))
        this.callback.handleText(paramArrayOfChar, getBlockStartPosition()); 
    } 
  }
  
  protected void handleError(int paramInt, String paramString) { this.callback.handleError(paramString, getCurrentPos()); }
  
  private void debug(String paramString) { System.out.println(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\DocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */