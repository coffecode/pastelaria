package codecoffe.restaurantes.interfaceGrafica;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.panel.WebPanel;


public class PainelErro 
{
	public PainelErro(Exception e)
	{
        JOptionPane.showMessageDialog(null, getConteudo(e), "CodeCoffe Restaurantes " + UtilCoffe.VERSAO, JOptionPane.OK_CANCEL_OPTION);		
	}
	
	private JPanel getConteudo(Exception e) 
	{
        WebPanel panel = new WebPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 240));
        panel.setUndecorated(true);
        
        StringWriter sw = new StringWriter();  
        e.printStackTrace(new PrintWriter(sw));
        JTextArea ta = new JTextArea(e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
        
        ta.append(sw.toString());
        
        ta.setBackground(Color.WHITE);
        
        JScrollPane scrollErro = new JScrollPane(ta);
        scrollErro.setPreferredSize(new Dimension(300, 140));
        
        JLabel lab = new JLabel("<html>Parece que ocorreu um erro no programa.<br>"
        		+ "Copie todo o código a baixo e envia para:<br>"
        		+ "<b>contato@codecoffe.com.br</b><br><br>"
        		+ "Se possível escreva detalhadamente como ocorreu o erro e informe que sua versão é a " + UtilCoffe.VERSAO + ".<br></html>");
        lab.setPreferredSize(new Dimension(300, 100));
        
        panel.add(lab, BorderLayout.NORTH);
        panel.add(scrollErro, BorderLayout.CENTER);
        return panel;
    }	
}
