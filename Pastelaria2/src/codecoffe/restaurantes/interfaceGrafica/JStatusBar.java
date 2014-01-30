package codecoffe.restaurantes.interfaceGrafica;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JStatusBar extends JPanel {

    private static final long serialVersionUID = 1L;
    protected JPanel leftPanel;
    protected JPanel rightPanel;
    private boolean firstTime;

    public JStatusBar() {
        createPartControl();
    }

    protected void createPartControl() {    
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(getWidth(), 40));
        firstTime = true;

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 2));
        leftPanel.setOpaque(false);
        add(leftPanel, BorderLayout.WEST);

        rightPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 2));
        rightPanel.setOpaque(false);
        add(rightPanel, BorderLayout.EAST);
    }

    public void addLeftComponent(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
        panel.setOpaque(false);
        
        if(!firstTime)
        	panel.add(new SeparatorPanel(Color.GRAY, Color.WHITE));
        
        firstTime = false;        
        
        panel.add(component);
        leftPanel.add(panel);
    }

    public void addRightComponent(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
        panel.setOpaque(false);
        panel.add(new SeparatorPanel(Color.GRAY, Color.WHITE));
        panel.add(component);
        rightPanel.add(panel);
    }
}