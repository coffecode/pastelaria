import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.event.*;

public class PainelVendaRapida extends JPanel implements MouseListener
{
	private JPanel painelTotal, rapidaPainel;
	private JLabel total, quantidade, labelProduto, labelValor, labelCodigo;
	static private JTextField campoValor;
	private AutoSuggest addProduto;
	
	PainelVendaRapida()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Venda Rápida"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(800, 480));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 480));
		
		painelTotal = new JPanel();
		painelTotal.setLayout(new BoxLayout(painelTotal, BoxLayout.X_AXIS));
		painelTotal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "painelTotal"));
		
		rapidaPainel = new JPanel();
		rapidaPainel.setLayout(new GridBagLayout());
		rapidaPainel.setMinimumSize(new Dimension(800, 200));
		rapidaPainel.setMaximumSize(new Dimension(800, 200));
		rapidaPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "rapidaPainel"));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		
		labelProduto = new JLabel("Produto:");
		addProduto = new AutoSuggest();
		
		labelValor = new JLabel("Preço:");
		campoValor = new JTextField(5);
		campoValor.setEditable(false);
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas
		
		rapidaPainel.add(labelProduto, gbc);
		
		gbc.gridx = 2;	// colunas
		
		rapidaPainel.add(addProduto, gbc);
		
		gbc.gridx = 3;	// colunas
		
		rapidaPainel.add(labelValor, gbc);
		
		gbc.gridx = 4;	// colunas
		
		rapidaPainel.add(campoValor, gbc);
		
		painelTotal.add(rapidaPainel);
		add(painelTotal);
	}
	
	static public void updateCampo(String valor)
	{
		Query pega = new Query();
		pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + valor + "';");
		
		if(pega.next())
		{
			String pegaPreco;
			double aDouble = Double.parseDouble(pega.getString("preco"));
			pegaPreco = String.format("%.2f", aDouble);
			pegaPreco.replaceAll(",", ".");			
			campoValor.setText(pegaPreco);
		}
		
		pega.fechaConexao();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e) {
		PainelLegenda.AtualizaLegenda("Desenvolvido por CodeCoffe (C) - 2013");
	}
}
