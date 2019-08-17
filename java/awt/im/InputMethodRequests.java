package java.awt.im;

import java.awt.Rectangle;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;

public interface InputMethodRequests {
  Rectangle getTextLocation(TextHitInfo paramTextHitInfo);
  
  TextHitInfo getLocationOffset(int paramInt1, int paramInt2);
  
  int getInsertPositionOffset();
  
  AttributedCharacterIterator getCommittedText(int paramInt1, int paramInt2, AttributedCharacterIterator.Attribute[] paramArrayOfAttribute);
  
  int getCommittedTextLength();
  
  AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute);
  
  AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\im\InputMethodRequests.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */