package sun.applet;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;

class AppletPropsErrorDialog extends Dialog {
  public AppletPropsErrorDialog(Frame paramFrame, String paramString1, String paramString2, String paramString3) {
    super(paramFrame, paramString1, true);
    Panel panel = new Panel();
    add("Center", new Label(paramString2));
    panel.add(new Button(paramString3));
    add("South", panel);
    pack();
    Dimension dimension = size();
    Rectangle rectangle = paramFrame.bounds();
    move(rectangle.x + (rectangle.width - dimension.width) / 2, rectangle.y + (rectangle.height - dimension.height) / 2);
  }
  
  public boolean action(Event paramEvent, Object paramObject) {
    hide();
    dispose();
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletPropsErrorDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */