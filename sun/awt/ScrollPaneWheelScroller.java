package sun.awt;

import java.awt.Adjustable;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.MouseWheelEvent;
import sun.util.logging.PlatformLogger;

public abstract class ScrollPaneWheelScroller {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.ScrollPaneWheelScroller");
  
  public static void handleWheelScrolling(ScrollPane paramScrollPane, MouseWheelEvent paramMouseWheelEvent) {
    if (log.isLoggable(PlatformLogger.Level.FINER))
      log.finer("x = " + paramMouseWheelEvent.getX() + ", y = " + paramMouseWheelEvent.getY() + ", src is " + paramMouseWheelEvent.getSource()); 
    int i = 0;
    if (paramScrollPane != null && paramMouseWheelEvent.getScrollAmount() != 0) {
      Adjustable adjustable = getAdjustableToScroll(paramScrollPane);
      if (adjustable != null) {
        i = getIncrementFromAdjustable(adjustable, paramMouseWheelEvent);
        if (log.isLoggable(PlatformLogger.Level.FINER))
          log.finer("increment from adjustable(" + adjustable.getClass() + ") : " + i); 
        scrollAdjustable(adjustable, i);
      } 
    } 
  }
  
  public static Adjustable getAdjustableToScroll(ScrollPane paramScrollPane) {
    int i = paramScrollPane.getScrollbarDisplayPolicy();
    if (i == 1 || i == 2) {
      if (log.isLoggable(PlatformLogger.Level.FINER))
        log.finer("using vertical scrolling due to scrollbar policy"); 
      return paramScrollPane.getVAdjustable();
    } 
    Insets insets = paramScrollPane.getInsets();
    int j = paramScrollPane.getVScrollbarWidth();
    if (log.isLoggable(PlatformLogger.Level.FINER)) {
      log.finer("insets: l = " + insets.left + ", r = " + insets.right + ", t = " + insets.top + ", b = " + insets.bottom);
      log.finer("vertScrollWidth = " + j);
    } 
    if (insets.right >= j) {
      if (log.isLoggable(PlatformLogger.Level.FINER))
        log.finer("using vertical scrolling because scrollbar is present"); 
      return paramScrollPane.getVAdjustable();
    } 
    int k = paramScrollPane.getHScrollbarHeight();
    if (insets.bottom >= k) {
      if (log.isLoggable(PlatformLogger.Level.FINER))
        log.finer("using horiz scrolling because scrollbar is present"); 
      return paramScrollPane.getHAdjustable();
    } 
    if (log.isLoggable(PlatformLogger.Level.FINER))
      log.finer("using NO scrollbar becsause neither is present"); 
    return null;
  }
  
  public static int getIncrementFromAdjustable(Adjustable paramAdjustable, MouseWheelEvent paramMouseWheelEvent) {
    if (log.isLoggable(PlatformLogger.Level.FINE) && paramAdjustable == null)
      log.fine("Assertion (adj != null) failed"); 
    int i = 0;
    if (paramMouseWheelEvent.getScrollType() == 0) {
      i = paramMouseWheelEvent.getUnitsToScroll() * paramAdjustable.getUnitIncrement();
    } else if (paramMouseWheelEvent.getScrollType() == 1) {
      i = paramAdjustable.getBlockIncrement() * paramMouseWheelEvent.getWheelRotation();
    } 
    return i;
  }
  
  public static void scrollAdjustable(Adjustable paramAdjustable, int paramInt) {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      if (paramAdjustable == null)
        log.fine("Assertion (adj != null) failed"); 
      if (paramInt == 0)
        log.fine("Assertion (amount != 0) failed"); 
    } 
    int i = paramAdjustable.getValue();
    int j = paramAdjustable.getMaximum() - paramAdjustable.getVisibleAmount();
    if (log.isLoggable(PlatformLogger.Level.FINER))
      log.finer("doScrolling by " + paramInt); 
    if (paramInt > 0 && i < j) {
      if (i + paramInt < j) {
        paramAdjustable.setValue(i + paramInt);
        return;
      } 
      paramAdjustable.setValue(j);
      return;
    } 
    if (paramInt < 0 && i > paramAdjustable.getMinimum()) {
      if (i + paramInt > paramAdjustable.getMinimum()) {
        paramAdjustable.setValue(i + paramInt);
        return;
      } 
      paramAdjustable.setValue(paramAdjustable.getMinimum());
      return;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\ScrollPaneWheelScroller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */