import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;

public class VisualizarRecibo extends JFrame implements ActionListener
{
	private JEditorPane campoRecibo;
	private JButton imprimir;
	
	public VisualizarRecibo()
	{	
		setTitle("Recibo");
		JPanel reciboPainel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		reciboPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pré-Visualização"));
		reciboPainel.setPreferredSize(new Dimension(331, 320));
		
		campoRecibo = new JEditorPane();
		campoRecibo.setPreferredSize(new Dimension(315, 280));
		campoRecibo.setFont(new Font("Verdana", Font.PLAIN, 8));
		campoRecibo.setEditable(false);
		
		File file = new File("codecoffe/recibo.txt");
		
		try
		{
			campoRecibo.setPage(file.toURI().toURL());
		}
		catch (IOException e)
		{
		    System.err.println("Attempted to read a bad file ");
		    e.printStackTrace();
		}		
		
		JScrollPane scrollrecibo = new JScrollPane(campoRecibo);
		scrollrecibo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrollrecibo.setPreferredSize(new Dimension(315, 280));
		
		reciboPainel.add(scrollrecibo);			
		add(reciboPainel);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		});
		
		imprimir = new JButton("Imprimir");
		imprimir.setPreferredSize(new Dimension(170, 60));
		imprimir.setFont(new Font("Helvetica", Font.BOLD, 16));
		ImageIcon iconeFuncionarios = new ImageIcon(getClass().getResource("imgs/imprimir.png"));
		imprimir.setIcon(iconeFuncionarios);
		imprimir.addActionListener(this);
		reciboPainel.add(imprimir);			
		
		setSize(355, 420);							// Largura, Altura
		setLocationRelativeTo(null);				// Abre no centro da tela
		
		ActionMap actionMap = reciboPainel.getActionMap();
		actionMap.put("sair", new EscSair());
		reciboPainel.setActionMap(actionMap);
		
		InputMap imap = reciboPainel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke("ESCAPE"), "sair");		
		
		setIconImage(new ImageIcon(getClass().getResource("imgs/icone_programa.png")).getImage());
		setResizable(false);
		setVisible(true);		
	}
	
	private class EscSair extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
	
	static public void imprimirRecibo()
	{
	       try {
	            java.io.InputStream is = new FileInputStream("codecoffe/recibo.txt");
	            Scanner sc = new Scanner(is);
	            FileOutputStream fs = new FileOutputStream("LPT1:");
	            PrintStream ps = new PrintStream(fs);

	            while(sc.hasNextLine()){
	                String linhas = sc.nextLine();
	                ps.println(linhas);
	            }
	            fs.close();
	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + ex.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
	        }			
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == imprimir)
		{
			imprimirRecibo();
		}
	}
}