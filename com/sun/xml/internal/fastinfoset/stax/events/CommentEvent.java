package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.Comment;

public class CommentEvent extends EventBase implements Comment {
  private String _text;
  
  public CommentEvent() { super(5); }
  
  public CommentEvent(String paramString) {
    this();
    this._text = paramString;
  }
  
  public String toString() { return "<!--" + this._text + "-->"; }
  
  public String getText() { return this._text; }
  
  public void setText(String paramString) { this._text = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\CommentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */