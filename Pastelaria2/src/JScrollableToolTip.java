import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class JScrollableToolTip extends JToolTip implements MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private JTextPane textPane;
    
    /** Creates a tool tip. */
    public JScrollableToolTip(final int width, final int height) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        textPane = new JTextPane();
        textPane.setEditable(true);
        textPane.setContentType("text/html");
        
        /*LookAndFeel.installColorsAndFont(textPane, 
                "ToolTip.background",
                "ToolTip.foreground",
                "ToolTip.font");*/
           
        JScrollPane scrollpane = new JScrollPane(textPane);
        scrollpane.setBorder(null);
        scrollpane.getViewport().setOpaque(false);
        add(scrollpane);
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        JComponent comp = getComponent();
        if (comp != null) {
            comp.addMouseWheelListener(this);
        }
    }
 
    @Override
    public void removeNotify() {
        JComponent comp = getComponent();
        if(comp != null) {
            comp.removeMouseWheelListener(this);
        } 
        super.removeNotify();
    }
    
    public void mouseWheelMoved(final MouseWheelEvent e) {
        JComponent comp = getComponent();
        if(comp != null) {
            textPane.dispatchEvent(new MouseWheelEvent(textPane, 
                    e.getID(), e.getWhen(), e.getModifiers(),
                    0, 0, e.getClickCount(), e.isPopupTrigger(),
                    e.getScrollType(), e.getScrollAmount(), e.getWheelRotation()));
        }
    }
 
    @Override
    public void setTipText(final String tipText) {
        String oldValue = this.textPane.getText();
        textPane.setText(tipText);
        textPane.setCaretPosition(0);
        firePropertyChange("tiptext", oldValue, tipText);
    }
 
    @Override
    public String getTipText() {
        return textPane == null ? "" : textPane.getText();
    }
 
    @Override
    protected String paramString() {
        String tipTextString = (textPane.getText() != null ? textPane.getText() : "");
 
        return super.paramString()
                + ",tipText=" + tipTextString;
    }
    /*
    //for testing only:
    public static void main(final String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
            	
        		try {
    		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    		        if ("Nimbus".equals(info.getName())) {
    		        	UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
    		            break;
    		        }
    		    }
    		} catch (Exception e) {
    		    // If Nimbus is not available, you can set the GUI to another look and feel.
    		}
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                JFrame f = new JFrame("JScrollableToolTip");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(300, 200);
                f.setLocationRelativeTo(null);
                ToolTipManager.sharedInstance().setDismissDelay(10000);
                JTable table = new JTable(50, 4) {
         
                    @Override
                    public JToolTip createToolTip() {
                        JScrollableToolTip tip = new JScrollableToolTip(400, 100);
                        tip.setComponent(this);
                        return tip;
                    }
                };
                table.setToolTipText("<html>asdf<b>jai</b></html>");
                f.add(new JScrollPane(table));
                f.setVisible(true);
            }
        });
    }*/
}
