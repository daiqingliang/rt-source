package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.font.TextHitInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class InputMethodEvent extends AWTEvent {
  private static final long serialVersionUID = 4727190874778922661L;
  
  public static final int INPUT_METHOD_FIRST = 1100;
  
  public static final int INPUT_METHOD_TEXT_CHANGED = 1100;
  
  public static final int CARET_POSITION_CHANGED = 1101;
  
  public static final int INPUT_METHOD_LAST = 1101;
  
  long when;
  
  private AttributedCharacterIterator text;
  
  private int committedCharacterCount;
  
  private TextHitInfo caret;
  
  private TextHitInfo visiblePosition;
  
  public InputMethodEvent(Component paramComponent, int paramInt1, long paramLong, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2) {
    super(paramComponent, paramInt1);
    if (paramInt1 < 1100 || paramInt1 > 1101)
      throw new IllegalArgumentException("id outside of valid range"); 
    if (paramInt1 == 1101 && paramAttributedCharacterIterator != null)
      throw new IllegalArgumentException("text must be null for CARET_POSITION_CHANGED"); 
    this.when = paramLong;
    this.text = paramAttributedCharacterIterator;
    int i = 0;
    if (paramAttributedCharacterIterator != null)
      i = paramAttributedCharacterIterator.getEndIndex() - paramAttributedCharacterIterator.getBeginIndex(); 
    if (paramInt2 < 0 || paramInt2 > i)
      throw new IllegalArgumentException("committedCharacterCount outside of valid range"); 
    this.committedCharacterCount = paramInt2;
    this.caret = paramTextHitInfo1;
    this.visiblePosition = paramTextHitInfo2;
  }
  
  public InputMethodEvent(Component paramComponent, int paramInt1, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2) { this(paramComponent, paramInt1, getMostRecentEventTimeForSource(paramComponent), paramAttributedCharacterIterator, paramInt2, paramTextHitInfo1, paramTextHitInfo2); }
  
  public InputMethodEvent(Component paramComponent, int paramInt, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2) { this(paramComponent, paramInt, getMostRecentEventTimeForSource(paramComponent), null, 0, paramTextHitInfo1, paramTextHitInfo2); }
  
  public AttributedCharacterIterator getText() { return this.text; }
  
  public int getCommittedCharacterCount() { return this.committedCharacterCount; }
  
  public TextHitInfo getCaret() { return this.caret; }
  
  public TextHitInfo getVisiblePosition() { return this.visiblePosition; }
  
  public void consume() { this.consumed = true; }
  
  public boolean isConsumed() { return this.consumed; }
  
  public long getWhen() { return this.when; }
  
  public String paramString() {
    String str5;
    String str4;
    String str2;
    String str1;
    switch (this.id) {
      case 1100:
        str1 = "INPUT_METHOD_TEXT_CHANGED";
        break;
      case 1101:
        str1 = "CARET_POSITION_CHANGED";
        break;
      default:
        str1 = "unknown type";
        break;
    } 
    if (this.text == null) {
      str2 = "no text";
    } else {
      StringBuilder stringBuilder = new StringBuilder("\"");
      int i = this.committedCharacterCount;
      char c;
      for (c = this.text.first(); i-- > 0; c = this.text.next())
        stringBuilder.append(c); 
      stringBuilder.append("\" + \"");
      while (c != Character.MAX_VALUE) {
        stringBuilder.append(c);
        c = this.text.next();
      } 
      stringBuilder.append("\"");
      str2 = stringBuilder.toString();
    } 
    String str3 = this.committedCharacterCount + " characters committed";
    if (this.caret == null) {
      str4 = "no caret";
    } else {
      str4 = "caret: " + this.caret.toString();
    } 
    if (this.visiblePosition == null) {
      str5 = "no visible position";
    } else {
      str5 = "visible position: " + this.visiblePosition.toString();
    } 
    return str1 + ", " + str2 + ", " + str3 + ", " + str4 + ", " + str5;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    if (this.when == 0L)
      this.when = EventQueue.getMostRecentEventTime(); 
  }
  
  private static long getMostRecentEventTimeForSource(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("null source"); 
    AppContext appContext = SunToolkit.targetToAppContext(paramObject);
    EventQueue eventQueue = SunToolkit.getSystemEventQueueImplPP(appContext);
    return AWTAccessor.getEventQueueAccessor().getMostRecentEventTime(eventQueue);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\InputMethodEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */