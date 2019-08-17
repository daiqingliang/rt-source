package sun.applet;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

final class TextFrame extends Frame {
  private static AppletMessageHandler amh = new AppletMessageHandler("textframe");
  
  TextFrame(int paramInt1, int paramInt2, String paramString1, String paramString2) {
    setTitle(paramString1);
    TextArea textArea = new TextArea(20, 60);
    textArea.setText(paramString2);
    textArea.setEditable(false);
    add("Center", textArea);
    Panel panel = new Panel();
    add("South", panel);
    Button button = new Button(amh.getMessage("button.dismiss", "Dismiss"));
    panel.add(button);
    class ActionEventListener implements ActionListener {
      public void actionPerformed(ActionEvent param1ActionEvent) { TextFrame.this.dispose(); }
    };
    button.addActionListener(new ActionEventListener());
    pack();
    move(paramInt1, paramInt2);
    setVisible(true);
    WindowAdapter windowAdapter = new WindowAdapter() {
        public void windowClosing(WindowEvent param1WindowEvent) { TextFrame.this.dispose(); }
      };
    addWindowListener(windowAdapter);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\TextFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */