package javax.swing.text.html;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.BitSet;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;

public class FormView extends ComponentView implements ActionListener {
  @Deprecated
  public static final String SUBMIT = new String("Submit Query");
  
  @Deprecated
  public static final String RESET = new String("Reset");
  
  static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
  
  private short maxIsPreferred;
  
  public FormView(Element paramElement) { super(paramElement); }
  
  protected Component createComponent() {
    AttributeSet attributeSet = getElement().getAttributes();
    HTML.Tag tag = (HTML.Tag)attributeSet.getAttribute(StyleConstants.NameAttribute);
    JComponent jComponent = null;
    Object object = attributeSet.getAttribute(StyleConstants.ModelAttribute);
    removeStaleListenerForModel(object);
    if (tag == HTML.Tag.INPUT) {
      jComponent = createInputComponent(attributeSet, object);
    } else if (tag == HTML.Tag.SELECT) {
      if (object instanceof OptionListModel) {
        JList jList = new JList((ListModel)object);
        int i = HTML.getIntegerAttributeValue(attributeSet, HTML.Attribute.SIZE, 1);
        jList.setVisibleRowCount(i);
        jList.setSelectionModel((ListSelectionModel)object);
        jComponent = new JScrollPane(jList);
      } else {
        jComponent = new JComboBox((ComboBoxModel)object);
        this.maxIsPreferred = 3;
      } 
    } else if (tag == HTML.Tag.TEXTAREA) {
      JTextArea jTextArea = new JTextArea((Document)object);
      int i = HTML.getIntegerAttributeValue(attributeSet, HTML.Attribute.ROWS, 1);
      jTextArea.setRows(i);
      int j = HTML.getIntegerAttributeValue(attributeSet, HTML.Attribute.COLS, 20);
      this.maxIsPreferred = 3;
      jTextArea.setColumns(j);
      jComponent = new JScrollPane(jTextArea, 22, 32);
    } 
    if (jComponent != null)
      jComponent.setAlignmentY(1.0F); 
    return jComponent;
  }
  
  private JComponent createInputComponent(AttributeSet paramAttributeSet, Object paramObject) {
    Box box = null;
    String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.TYPE);
    if (str.equals("submit") || str.equals("reset")) {
      String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
      if (str1 == null)
        if (str.equals("submit")) {
          str1 = UIManager.getString("FormView.submitButtonText");
        } else {
          str1 = UIManager.getString("FormView.resetButtonText");
        }  
      JButton jButton = new JButton(str1);
      if (paramObject != null) {
        jButton.setModel((ButtonModel)paramObject);
        jButton.addActionListener(this);
      } 
      box = jButton;
      this.maxIsPreferred = 3;
    } else if (str.equals("image")) {
      JButton jButton;
      String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.SRC);
      try {
        URL uRL1 = ((HTMLDocument)getElement().getDocument()).getBase();
        URL uRL2 = new URL(uRL1, str1);
        ImageIcon imageIcon = new ImageIcon(uRL2);
        jButton = new JButton(imageIcon);
      } catch (MalformedURLException malformedURLException) {
        jButton = new JButton(str1);
      } 
      if (paramObject != null) {
        jButton.setModel((ButtonModel)paramObject);
        jButton.addMouseListener(new MouseEventListener());
      } 
      box = jButton;
      this.maxIsPreferred = 3;
    } else if (str.equals("checkbox")) {
      JCheckBox jCheckBox = new JCheckBox();
      if (paramObject != null)
        ((JCheckBox)jCheckBox).setModel((JToggleButton.ToggleButtonModel)paramObject); 
      this.maxIsPreferred = 3;
    } else if (str.equals("radio")) {
      JRadioButton jRadioButton = new JRadioButton();
      if (paramObject != null)
        ((JRadioButton)jRadioButton).setModel((JToggleButton.ToggleButtonModel)paramObject); 
      this.maxIsPreferred = 3;
    } else if (str.equals("text")) {
      JTextField jTextField2;
      int i = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
      if (i > 0) {
        jTextField2 = new JTextField();
        jTextField2.setColumns(i);
      } else {
        jTextField2 = new JTextField();
        jTextField2.setColumns(20);
      } 
      JTextField jTextField1 = jTextField2;
      if (paramObject != null)
        jTextField2.setDocument((Document)paramObject); 
      jTextField2.addActionListener(this);
      this.maxIsPreferred = 3;
    } else if (str.equals("password")) {
      JPasswordField jPasswordField2 = new JPasswordField();
      JPasswordField jPasswordField1 = jPasswordField2;
      if (paramObject != null)
        jPasswordField2.setDocument((Document)paramObject); 
      int i = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
      jPasswordField2.setColumns((i > 0) ? i : 20);
      jPasswordField2.addActionListener(this);
      this.maxIsPreferred = 3;
    } else if (str.equals("file")) {
      JTextField jTextField = new JTextField();
      if (paramObject != null)
        jTextField.setDocument((Document)paramObject); 
      int i = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
      jTextField.setColumns((i > 0) ? i : 20);
      JButton jButton = new JButton(UIManager.getString("FormView.browseFileButtonText"));
      Box box1 = Box.createHorizontalBox();
      box1.add(jTextField);
      box1.add(Box.createHorizontalStrut(5));
      box1.add(jButton);
      jButton.addActionListener(new BrowseFileAction(paramAttributeSet, (Document)paramObject));
      box = box1;
      this.maxIsPreferred = 3;
    } 
    return box;
  }
  
  private void removeStaleListenerForModel(Object paramObject) {
    if (paramObject instanceof DefaultButtonModel) {
      DefaultButtonModel defaultButtonModel = (DefaultButtonModel)paramObject;
      String str = "javax.swing.AbstractButton$Handler";
      for (ActionListener actionListener : defaultButtonModel.getActionListeners()) {
        if (str.equals(actionListener.getClass().getName()))
          defaultButtonModel.removeActionListener(actionListener); 
      } 
      for (ChangeListener changeListener : defaultButtonModel.getChangeListeners()) {
        if (str.equals(changeListener.getClass().getName()))
          defaultButtonModel.removeChangeListener(changeListener); 
      } 
      for (ItemListener itemListener : defaultButtonModel.getItemListeners()) {
        if (str.equals(itemListener.getClass().getName()))
          defaultButtonModel.removeItemListener(itemListener); 
      } 
    } else if (paramObject instanceof AbstractListModel) {
      AbstractListModel abstractListModel = (AbstractListModel)paramObject;
      String str1 = "javax.swing.plaf.basic.BasicListUI$Handler";
      String str2 = "javax.swing.plaf.basic.BasicComboBoxUI$Handler";
      for (ListDataListener listDataListener : abstractListModel.getListDataListeners()) {
        if (str1.equals(listDataListener.getClass().getName()) || str2.equals(listDataListener.getClass().getName()))
          abstractListModel.removeListDataListener(listDataListener); 
      } 
    } else if (paramObject instanceof AbstractDocument) {
      String str1 = "javax.swing.plaf.basic.BasicTextUI$UpdateHandler";
      String str2 = "javax.swing.text.DefaultCaret$Handler";
      AbstractDocument abstractDocument = (AbstractDocument)paramObject;
      for (DocumentListener documentListener : abstractDocument.getDocumentListeners()) {
        if (str1.equals(documentListener.getClass().getName()) || str2.equals(documentListener.getClass().getName()))
          abstractDocument.removeDocumentListener(documentListener); 
      } 
    } 
  }
  
  public float getMaximumSpan(int paramInt) {
    switch (paramInt) {
      case 0:
        if ((this.maxIsPreferred & true) == 1) {
          super.getMaximumSpan(paramInt);
          return getPreferredSpan(paramInt);
        } 
        return super.getMaximumSpan(paramInt);
      case 1:
        if ((this.maxIsPreferred & 0x2) == 2) {
          super.getMaximumSpan(paramInt);
          return getPreferredSpan(paramInt);
        } 
        return super.getMaximumSpan(paramInt);
    } 
    return super.getMaximumSpan(paramInt);
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    Element element = getElement();
    StringBuilder stringBuilder = new StringBuilder();
    HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
    AttributeSet attributeSet = element.getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.TYPE);
    if (str.equals("submit")) {
      getFormData(stringBuilder);
      submitData(stringBuilder.toString());
    } else if (str.equals("reset")) {
      resetForm();
    } else if (str.equals("text") || str.equals("password")) {
      if (isLastTextOrPasswordField()) {
        getFormData(stringBuilder);
        submitData(stringBuilder.toString());
      } else {
        getComponent().transferFocus();
      } 
    } 
  }
  
  protected void submitData(String paramString) {
    URL uRL2;
    Element element = getFormElement();
    AttributeSet attributeSet = element.getAttributes();
    HTMLDocument hTMLDocument = (HTMLDocument)element.getDocument();
    URL uRL1 = hTMLDocument.getBase();
    String str1 = (String)attributeSet.getAttribute(HTML.Attribute.TARGET);
    if (str1 == null)
      str1 = "_self"; 
    String str2 = (String)attributeSet.getAttribute(HTML.Attribute.METHOD);
    if (str2 == null)
      str2 = "GET"; 
    str2 = str2.toLowerCase();
    boolean bool = str2.equals("post");
    if (bool)
      storePostData(hTMLDocument, str1, paramString); 
    String str3 = (String)attributeSet.getAttribute(HTML.Attribute.ACTION);
    try {
      uRL2 = (str3 == null) ? new URL(uRL1.getProtocol(), uRL1.getHost(), uRL1.getPort(), uRL1.getFile()) : new URL(uRL1, str3);
      if (!bool) {
        String str = paramString.toString();
        uRL2 = new URL(uRL2 + "?" + str);
      } 
    } catch (MalformedURLException malformedURLException) {
      uRL2 = null;
    } 
    final JEditorPane c = (JEditorPane)getContainer();
    HTMLEditorKit hTMLEditorKit = (HTMLEditorKit)jEditorPane.getEditorKit();
    FormSubmitEvent formSubmitEvent1 = null;
    if (!hTMLEditorKit.isAutoFormSubmission() || hTMLDocument.isFrameDocument()) {
      FormSubmitEvent.MethodType methodType = bool ? FormSubmitEvent.MethodType.POST : FormSubmitEvent.MethodType.GET;
      formSubmitEvent1 = new FormSubmitEvent(this, HyperlinkEvent.EventType.ACTIVATED, uRL2, element, str1, methodType, paramString);
    } 
    final FormSubmitEvent fse = formSubmitEvent1;
    final URL url = uRL2;
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (fse != null) {
              c.fireHyperlinkUpdate(fse);
            } else {
              try {
                c.setPage(url);
              } catch (IOException iOException) {
                UIManager.getLookAndFeel().provideErrorFeedback(c);
              } 
            } 
          }
        });
  }
  
  private void storePostData(HTMLDocument paramHTMLDocument, String paramString1, String paramString2) {
    Document document = paramHTMLDocument;
    String str = "javax.swing.JEditorPane.postdata";
    if (paramHTMLDocument.isFrameDocument()) {
      FrameView.FrameEditorPane frameEditorPane = (FrameView.FrameEditorPane)getContainer();
      FrameView frameView = frameEditorPane.getFrameView();
      JEditorPane jEditorPane = frameView.getOutermostJEditorPane();
      if (jEditorPane != null) {
        document = jEditorPane.getDocument();
        str = str + "." + paramString1;
      } 
    } 
    document.putProperty(str, paramString2);
  }
  
  protected void imageSubmit(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    Element element = getElement();
    HTMLDocument hTMLDocument = (HTMLDocument)element.getDocument();
    getFormData(stringBuilder);
    if (stringBuilder.length() > 0)
      stringBuilder.append('&'); 
    stringBuilder.append(paramString);
    submitData(stringBuilder.toString());
  }
  
  private String getImageData(Point paramPoint) {
    String str5;
    String str1 = paramPoint.x + ":" + paramPoint.y;
    int i = str1.indexOf(':');
    String str2 = str1.substring(0, i);
    String str3 = str1.substring(++i);
    String str4 = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
    if (str4 == null || str4.equals("")) {
      str5 = "x=" + str2 + "&y=" + str3;
    } else {
      str4 = URLEncoder.encode(str4);
      str5 = str4 + ".x=" + str2 + "&" + str4 + ".y=" + str3;
    } 
    return str5;
  }
  
  private Element getFormElement() {
    for (Element element = getElement(); element != null; element = element.getParentElement()) {
      if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.FORM)
        return element; 
    } 
    return null;
  }
  
  private void getFormData(StringBuilder paramStringBuilder) {
    Element element = getFormElement();
    if (element != null) {
      ElementIterator elementIterator = new ElementIterator(element);
      Element element1;
      while ((element1 = elementIterator.next()) != null) {
        if (isControl(element1)) {
          String str = (String)element1.getAttributes().getAttribute(HTML.Attribute.TYPE);
          if ((str == null || !str.equals("submit") || element1 == getElement()) && (str == null || !str.equals("image")))
            loadElementDataIntoBuffer(element1, paramStringBuilder); 
        } 
      } 
    } 
  }
  
  private void loadElementDataIntoBuffer(Element paramElement, StringBuilder paramStringBuilder) {
    AttributeSet attributeSet = paramElement.getAttributes();
    String str1 = (String)attributeSet.getAttribute(HTML.Attribute.NAME);
    if (str1 == null)
      return; 
    String str2 = null;
    HTML.Tag tag = (HTML.Tag)paramElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
    if (tag == HTML.Tag.INPUT) {
      str2 = getInputElementData(attributeSet);
    } else if (tag == HTML.Tag.TEXTAREA) {
      str2 = getTextAreaData(attributeSet);
    } else if (tag == HTML.Tag.SELECT) {
      loadSelectData(attributeSet, paramStringBuilder);
    } 
    if (str1 != null && str2 != null)
      appendBuffer(paramStringBuilder, str1, str2); 
  }
  
  private String getInputElementData(AttributeSet paramAttributeSet) {
    Object object = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.TYPE);
    String str2 = null;
    if (str1.equals("text") || str1.equals("password")) {
      Document document = (Document)object;
      try {
        str2 = document.getText(0, document.getLength());
      } catch (BadLocationException badLocationException) {
        str2 = null;
      } 
    } else if (str1.equals("submit") || str1.equals("hidden")) {
      str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
      if (str2 == null)
        str2 = ""; 
    } else if (str1.equals("radio") || str1.equals("checkbox")) {
      ButtonModel buttonModel = (ButtonModel)object;
      if (buttonModel.isSelected()) {
        str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
        if (str2 == null)
          str2 = "on"; 
      } 
    } else if (str1.equals("file")) {
      String str;
      Document document = (Document)object;
      try {
        str = document.getText(0, document.getLength());
      } catch (BadLocationException badLocationException) {
        str = null;
      } 
      if (str != null && str.length() > 0)
        str2 = str; 
    } 
    return str2;
  }
  
  private String getTextAreaData(AttributeSet paramAttributeSet) {
    Document document = (Document)paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    try {
      return document.getText(0, document.getLength());
    } catch (BadLocationException badLocationException) {
      return null;
    } 
  }
  
  private void loadSelectData(AttributeSet paramAttributeSet, StringBuilder paramStringBuilder) {
    String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.NAME);
    if (str == null)
      return; 
    Object object = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    if (object instanceof OptionListModel) {
      OptionListModel optionListModel = (OptionListModel)object;
      for (byte b = 0; b < optionListModel.getSize(); b++) {
        if (optionListModel.isSelectedIndex(b)) {
          Option option = (Option)optionListModel.getElementAt(b);
          appendBuffer(paramStringBuilder, str, option.getValue());
        } 
      } 
    } else if (object instanceof ComboBoxModel) {
      ComboBoxModel comboBoxModel = (ComboBoxModel)object;
      Option option = (Option)comboBoxModel.getSelectedItem();
      if (option != null)
        appendBuffer(paramStringBuilder, str, option.getValue()); 
    } 
  }
  
  private void appendBuffer(StringBuilder paramStringBuilder, String paramString1, String paramString2) {
    if (paramStringBuilder.length() > 0)
      paramStringBuilder.append('&'); 
    String str1 = URLEncoder.encode(paramString1);
    paramStringBuilder.append(str1);
    paramStringBuilder.append('=');
    String str2 = URLEncoder.encode(paramString2);
    paramStringBuilder.append(str2);
  }
  
  private boolean isControl(Element paramElement) { return paramElement.isLeaf(); }
  
  boolean isLastTextOrPasswordField() {
    Element element1 = getFormElement();
    Element element2 = getElement();
    if (element1 != null) {
      ElementIterator elementIterator = new ElementIterator(element1);
      boolean bool = false;
      Element element;
      while ((element = elementIterator.next()) != null) {
        if (element == element2) {
          bool = true;
          continue;
        } 
        if (bool && isControl(element)) {
          AttributeSet attributeSet = element.getAttributes();
          if (HTMLDocument.matchNameAttribute(attributeSet, HTML.Tag.INPUT)) {
            String str = (String)attributeSet.getAttribute(HTML.Attribute.TYPE);
            if ("text".equals(str) || "password".equals(str))
              return false; 
          } 
        } 
      } 
    } 
    return true;
  }
  
  void resetForm() {
    Element element = getFormElement();
    if (element != null) {
      ElementIterator elementIterator = new ElementIterator(element);
      Element element1;
      while ((element1 = elementIterator.next()) != null) {
        if (isControl(element1)) {
          AttributeSet attributeSet = element1.getAttributes();
          Object object = attributeSet.getAttribute(StyleConstants.ModelAttribute);
          if (object instanceof TextAreaDocument) {
            TextAreaDocument textAreaDocument = (TextAreaDocument)object;
            textAreaDocument.reset();
            continue;
          } 
          if (object instanceof PlainDocument)
            try {
              PlainDocument plainDocument = (PlainDocument)object;
              plainDocument.remove(0, plainDocument.getLength());
              if (HTMLDocument.matchNameAttribute(attributeSet, HTML.Tag.INPUT)) {
                String str = (String)attributeSet.getAttribute(HTML.Attribute.VALUE);
                if (str != null)
                  plainDocument.insertString(0, str, null); 
              } 
              continue;
            } catch (BadLocationException badLocationException) {
              continue;
            }  
          if (object instanceof OptionListModel) {
            OptionListModel optionListModel = (OptionListModel)object;
            int i = optionListModel.getSize();
            for (byte b1 = 0; b1 < i; b1++)
              optionListModel.removeIndexInterval(b1, b1); 
            BitSet bitSet = optionListModel.getInitialSelection();
            for (byte b2 = 0; b2 < bitSet.size(); b2++) {
              if (bitSet.get(b2))
                optionListModel.addSelectionInterval(b2, b2); 
            } 
            continue;
          } 
          if (object instanceof OptionComboBoxModel) {
            OptionComboBoxModel optionComboBoxModel = (OptionComboBoxModel)object;
            Option option = optionComboBoxModel.getInitialSelection();
            if (option != null)
              optionComboBoxModel.setSelectedItem(option); 
            continue;
          } 
          if (object instanceof JToggleButton.ToggleButtonModel) {
            boolean bool = ((String)attributeSet.getAttribute(HTML.Attribute.CHECKED) != null);
            JToggleButton.ToggleButtonModel toggleButtonModel = (JToggleButton.ToggleButtonModel)object;
            toggleButtonModel.setSelected(bool);
          } 
        } 
      } 
    } 
  }
  
  private class BrowseFileAction implements ActionListener {
    private AttributeSet attrs;
    
    private Document model;
    
    BrowseFileAction(AttributeSet param1AttributeSet, Document param1Document) {
      this.attrs = param1AttributeSet;
      this.model = param1Document;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JFileChooser jFileChooser = new JFileChooser();
      jFileChooser.setMultiSelectionEnabled(false);
      if (jFileChooser.showOpenDialog(FormView.this.getContainer()) == 0) {
        File file = jFileChooser.getSelectedFile();
        if (file != null)
          try {
            if (this.model.getLength() > 0)
              this.model.remove(0, this.model.getLength()); 
            this.model.insertString(0, file.getPath(), null);
          } catch (BadLocationException badLocationException) {} 
      } 
    }
  }
  
  protected class MouseEventListener extends MouseAdapter {
    public void mouseReleased(MouseEvent param1MouseEvent) {
      String str = FormView.this.getImageData(param1MouseEvent.getPoint());
      FormView.this.imageSubmit(str);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\FormView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */