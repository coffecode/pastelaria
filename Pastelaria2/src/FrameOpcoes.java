import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class FrameOpcoes extends JFrame implements ItemListener
{
	private JLabel labelNumeroMesas, labelRestaurante, labelCreditos, labelEmail, labelImagem;
	private JTextField campoNumeroMesas, campoRestaurante;
	private JCheckBox checkDez, checkRecibo;
	
	public FrameOpcoes()
	{
		setTitle("Opções do Sistema");
		JPanel opcaoPainel = new JPanel();
		opcaoPainel.setLayout(null);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				atualizaCampos();
				dispose();
			}
		});
		
		Query pega = new Query();
		pega.executaQuery("SELECT * FROM opcoes");
		
		if(pega.next())
		{
			labelRestaurante = new JLabel("Restaurante:");
			labelRestaurante.setFont(new Font("Helvetica", Font.BOLD, 15));
			labelRestaurante.setBounds(15,20,250,30); // Coluna, Linha, Largura, Altura!
			opcaoPainel.add(labelRestaurante);
			
			campoRestaurante = new JTextField();
			campoRestaurante.setToolTipText("É necessário reiniciar o programa para fazer efeito.");
			campoRestaurante.setBounds(164,20,250,30);
			opcaoPainel.add(campoRestaurante);		
			
			labelNumeroMesas = new JLabel("Número de Mesas:");
			labelNumeroMesas.setFont(new Font("Helvetica", Font.BOLD, 15));
			labelNumeroMesas.setBounds(15,60,250,30); // Coluna, Linha, Largura, Altura!
			opcaoPainel.add(labelNumeroMesas);
			
			campoNumeroMesas = new JTextField();
			campoNumeroMesas.setToolTipText("É necessário reiniciar o programa para fazer efeito.");
			campoNumeroMesas.setHorizontalAlignment(SwingConstants.CENTER);
			campoNumeroMesas.setBounds(164,60,35,30);
			opcaoPainel.add(campoNumeroMesas);
			
			checkDez = new JCheckBox("10% opcional nas mesas");
			checkDez.setFont(new Font("Helvetica", Font.BOLD, 15));
			checkDez.addItemListener(this);
			checkDez.setBounds(10,130,300,30);
			checkDez.setEnabled(false);
			opcaoPainel.add(checkDez);
			
			checkRecibo = new JCheckBox("Imprimir recibo no fim da venda");
			checkRecibo.setFont(new Font("Helvetica", Font.BOLD, 15));
			checkRecibo.addItemListener(this);
			checkRecibo.setBounds(10,160,300,30);
			checkRecibo.setEnabled(false);
			opcaoPainel.add(checkRecibo);
			
			labelImagem = new JLabel(new ImageIcon(getClass().getResource("imgs/opcoes_full.png")));
			labelImagem.setBounds(310,95,128,128);
			labelImagem.setEnabled(false);
			opcaoPainel.add(labelImagem);		
			
			JPanel creditosPainel = new JPanel(new FlowLayout(FlowLayout.CENTER, 45, 20));
			creditosPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "CodeCoffe - Restaurantes v1.00"));
			creditosPainel.setBounds(5,240,480,130);
			
			labelCreditos = new JLabel("Sistema desenvolvido por André Alves & Fernando Ferreira.");
			labelCreditos.setFont(new Font("Verdana", Font.PLAIN, 12));
			labelCreditos.setBounds(15,20,400,30); // Coluna, Linha, Largura, Altura!
			creditosPainel.add(labelCreditos);
			
			labelEmail = new JLabel("contato@codecoffe.com.br");
			labelEmail.setFont(new Font("Verdana", Font.ITALIC, 12));
			labelEmail.setBounds(15,20,400,30); // Coluna, Linha, Largura, Altura!
			creditosPainel.add(labelEmail);
			
			opcaoPainel.add(creditosPainel);
			add(opcaoPainel);
			
			setSize(500, 400);							// Largura, Altura
			setLocationRelativeTo(null);				// Abre no centro da tela
			
			campoRestaurante.setText(pega.getString("restaurante"));
			campoNumeroMesas.setText("" + pega.getInt("mesas"));
			
			if(pega.getInt("dezporcento") == 1)
				checkDez.setSelected(true);
			else
				checkDez.setSelected(false);
			
			if(pega.getInt("recibofim") == 1)
				checkRecibo.setSelected(true);
			else
				checkRecibo.setSelected(false);			
			
			setIconImage(new ImageIcon(getClass().getResource("imgs/icone_programa.png")).getImage());
			setResizable(false);
			setVisible(true);			
		}
		
		ActionMap actionMap = opcaoPainel.getActionMap();
		actionMap.put("sair", new EscSair());
		opcaoPainel.setActionMap(actionMap);
		
		InputMap imap = opcaoPainel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke("ESCAPE"), "sair");			
		
		pega.fechaConexao();
	}
	
	private class EscSair extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			atualizaCampos();
			dispose();
		}
	}	
	
	public void atualizaCampos()
	{
		if(!"".equals(campoRestaurante.getText().trim()))
		{
			Query envia = new Query();
			envia.executaUpdate("UPDATE opcoes SET restaurante = '" + campoRestaurante.getText() + "'");
			envia.fechaConexao();
		}
		
		String limpeza = campoNumeroMesas.getText().replaceAll("[^0-9]+","");
		
		if(!"".equals(limpeza.trim()))
		{
			Query envia = new Query();
			envia.executaUpdate("UPDATE opcoes SET mesas = " + limpeza);
			envia.fechaConexao();
		}		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getItemSelectable() == checkDez)
		{
			if(checkDez.isSelected())
			{
				Query envia = new Query();
				envia.executaUpdate("UPDATE opcoes SET dezporcento = 1");
				envia.fechaConexao();				
			}
			else
			{
				Query envia = new Query();
				envia.executaUpdate("UPDATE opcoes SET dezporcento = 0");
				envia.fechaConexao();					
			}
		}
		
		if(e.getItemSelectable() == checkRecibo)
		{
			if(checkRecibo.isSelected())
			{
				Query envia = new Query();
				envia.executaUpdate("UPDATE opcoes SET recibofim = 1");
				envia.fechaConexao();				
			}
			else
			{
				Query envia = new Query();
				envia.executaUpdate("UPDATE opcoes SET recibofim = 0");
				envia.fechaConexao();					
			}
		}		
	}
}
