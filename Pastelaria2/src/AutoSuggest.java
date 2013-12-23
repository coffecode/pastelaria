import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.*;

public class AutoSuggest extends JPanel implements ActionListener
{
    private final JTextField tf;
    private final JComboBox combo = new JComboBox();
    private final Vector<String> v = new Vector<String>();
    
    public AutoSuggest()
    {
        super(new BorderLayout());
        combo.setEditable(true);
        combo.addActionListener(this);
        tf = (JTextField) combo.getEditor().getEditorComponent();
        tf.addKeyListener(new KeyAdapter()
        {
        	public void keyTyped(KeyEvent e)
        	{
        		EventQueue.invokeLater(new Runnable()
        		{
        			public void run()
        			{
        				String text = tf.getText();
                        if(text.length()==0)
                        {
                        	combo.hidePopup();
                        	setModel(new DefaultComboBoxModel(v), "");
                        }
                        else
                        {
                        	DefaultComboBoxModel m = getSuggestedModel(v, text);
                        	if(m.getSize()==0 || hide_flag)
                        	{
                        		combo.hidePopup();
                        		hide_flag = false;
                        	}
                        	else
                        	{
                        		setModel(m, text);
                        		combo.showPopup();
                        	}
                        }
                   }
        		});
        	}
        	
        	
        	public void keyPressed(KeyEvent e)
        	{
        		String text = tf.getText();
        		int code = e.getKeyCode();
        		if(code==KeyEvent.VK_ENTER)
        		{
        			if(!v.contains(text))
        			{
        				v.addElement(text);
        				Collections.sort(v);
        				setModel(getSuggestedModel(v, text), text);
        			}
        			
        			hide_flag = true; 
        		}else if(code==KeyEvent.VK_ESCAPE)
        		{
        			hide_flag = true; 
        		}else if(code==KeyEvent.VK_RIGHT)
        		{
        			for(int i=0;i<v.size();i++)
        			{
        				String str = v.elementAt(i);
        				if(str.startsWith(text))
        				{
        					combo.setSelectedIndex(-1);
        					tf.setText(str);
        					return;
        				}
        			}
        		}
            }
        });
        
        ArrayList<String> produtos = new ArrayList<>();
        String nomes;
        
		Query pega = new Query();
		pega.executaQuery("SELECT * FROM produtos WHERE `tipo` = 1");
		
		while(pega.next())
		{
			//double aDouble = Double.parseDouble(pega.getString("preco"));
			
			//nomes = pega.getString("nome") + "   -   " + "R$" + String.format("%.2f", aDouble);
			//nomes.replaceAll(",", ".");
			produtos.add(pega.getString("nome"));
		}
        
        for(int i=0;i<produtos.size();i++)
        {
        	v.addElement(produtos.get(i));
        }
        
        setModel(new DefaultComboBoxModel(v), "");
        JPanel p = new JPanel(new BorderLayout());
        p.add(combo, BorderLayout.NORTH);
        add(p);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setPreferredSize(new Dimension(250, 35));
    }
    	
    private boolean hide_flag = false;
    private void setModel(DefaultComboBoxModel mdl, String str)
    {
		combo.setModel(mdl);
		combo.setSelectedIndex(-1);
		tf.setText(str);   	
    }
    
    private static DefaultComboBoxModel getSuggestedModel(java.util.List<String> list, String text)
    {
    	DefaultComboBoxModel m = new DefaultComboBoxModel();
    	for(String s: list)
    	{
    		if(s.toLowerCase().startsWith(text)) m.addElement(s);
    	}
    	
    	return m;
    }
	@Override
	public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        String produtoNome = (String)cb.getSelectedItem();
        PainelVendaRapida.updateCampo(produtoNome);
	}
}