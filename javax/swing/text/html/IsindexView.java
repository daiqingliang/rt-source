package javax.swing.text.html;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;

class IsindexView extends ComponentView implements ActionListener {
  JTextField textField;
  
  public IsindexView(Element paramElement) { super(paramElement); }
  
  public Component createComponent() {
    AttributeSet attributeSet = getElement().getAttributes();
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.setBackground(null);
    String str = (String)attributeSet.getAttribute(HTML.Attribute.PROMPT);
    if (str == null)
      str = UIManager.getString("IsindexView.prompt"); 
    JLabel jLabel = new JLabel(str);
    this.textField = new JTextField();
    this.textField.addActionListener(this);
    jPanel.add(jLabel, "West");
    jPanel.add(this.textField, "Center");
    jPanel.setAlignmentY(1.0F);
    jPanel.setOpaque(false);
    return jPanel;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    String str1 = this.textField.getText();
    if (str1 != null)
      str1 = URLEncoder.encode(str1); 
    AttributeSet attributeSet = getElement().getAttributes();
    HTMLDocument hTMLDocument = (HTMLDocument)getElement().getDocument();
    String str2 = (String)attributeSet.getAttribute(HTML.Attribute.ACTION);
    if (str2 == null)
      str2 = hTMLDocument.getBase().toString(); 
    try {
      URL uRL = new URL(str2 + "?" + str1);
      JEditorPane jEditorPane = (JEditorPane)getContainer();
      jEditorPane.setPage(uRL);
    } catch (MalformedURLException malformedURLException) {
    
    } catch (IOException iOException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\IsindexView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */