import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.event.*;
import java.util.ArrayList;

public class PainelVendaRapida extends JPanel implements ActionListener
{
	private JPanel painelTotal, rapidaPainel, adicionaisPainel, adicionaisPainel1;
	private JLabel labelQuantidade, labelProduto, labelValor, labelCodigo;
	private JButton adicionarADC, adicionarProduto;
	
	static private JTextField campoValor = new JTextField(5);
	static private JTextField campoQuantidade = new JTextField("1", 2);
	static private AutoSuggest addProduto = new AutoSuggest();
	
	static private ArrayList<AutoSuggest> addAdicional = new ArrayList<>();
	static private ArrayList<JButton> addRemover = new ArrayList<>();
	
	PainelVendaRapida(boolean refresh)
	{		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(800, 600));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 600));
		
		painelTotal = new JPanel();
		painelTotal.setLayout(new GridBagLayout());
		painelTotal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Venda Rápida"));
		painelTotal.setMinimumSize(new Dimension(800, 250));		// Horizontal , Vertical
		painelTotal.setMaximumSize(new Dimension(800, 250));		
		
		rapidaPainel = new JPanel();
		rapidaPainel.setLayout(new GridBagLayout());
		
		rapidaPainel.setMinimumSize(new Dimension(600, 100));
		rapidaPainel.setMaximumSize(new Dimension(600, 100));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		if(refresh)
		{
			campoValor = new JTextField(5);
			campoQuantidade = new JTextField("1", 2);
			addProduto = new AutoSuggest();
			campoValor = new JTextField(5);
			
			addAdicional = new ArrayList<>();
			addRemover = new ArrayList<>();
		}
		
		labelProduto = new JLabel("Produto:");
		labelValor = new JLabel("Preço:");
		campoValor.setEditable(false);
		
		adicionarADC = new JButton("");
		ImageIcon iconeADC = new ImageIcon("imgs/plus1.png");
		adicionarADC.setIcon(iconeADC);
		adicionarADC.setPreferredSize(new Dimension(25, 22));
		adicionarADC.setBorder(BorderFactory.createEmptyBorder());
		adicionarADC.setContentAreaFilled(false);  		
		adicionarADC.addActionListener(this);
		
		labelQuantidade = new JLabel("Qntd:");
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas
		
		rapidaPainel.add(labelProduto, gbc);
		
		gbc.gridx = 2;	// colunas
		
		rapidaPainel.add(addProduto, gbc);
		
		gbc.gridx = 3;	// colunas
		
		rapidaPainel.add(labelValor, gbc);
		
		gbc.gridx = 4;	// colunas
		
		rapidaPainel.add(campoValor, gbc);
		
		gbc.gridx = 5;	// colunas
		
		rapidaPainel.add(labelQuantidade, gbc);
		
		gbc.gridx = 6;	// colunas
		
		rapidaPainel.add(campoQuantidade, gbc);
		
		gbc.gridx = 7;	// colunas
		
		rapidaPainel.add(adicionarADC, gbc);
		
		adicionaisPainel1 = new JPanel();
		adicionaisPainel1.setLayout(new GridBagLayout());
		adicionaisPainel1.setMinimumSize(new Dimension(360, 120));
		adicionaisPainel1.setMaximumSize(new Dimension(360, 120));	
		
		adicionaisPainel = new JPanel();
		adicionaisPainel.setLayout(new GridBagLayout());
		adicionaisPainel.setMinimumSize(new Dimension(360, 120));
		adicionaisPainel.setMaximumSize(new Dimension(360, 120));
		
		if(addAdicional.size() > 0)
		{
			for(int i = 0; i < addAdicional.size(); i++)
			{
				gbc.gridx = 1;		// coluna
				gbc.gridy = i;	// linha
				
				gbc.gridx++;		// coluna
				adicionaisPainel.add(addAdicional.get(i), gbc);
				
				gbc.gridx++;		// coluna
				adicionaisPainel.add(addRemover.get(i), gbc);
			}
			
			JScrollPane scroll = new JScrollPane(adicionaisPainel);
			scroll.setMinimumSize(new Dimension(360,120));
			scroll.setMaximumSize(new Dimension(360,120));
			scroll.setPreferredSize(new Dimension(360,120));
			scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Adicionais"));
			
			adicionaisPainel1.add(scroll);			
		}
		
		gbc.insets = new Insets(0,0,0,0);  //top padding
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas			
		painelTotal.add(rapidaPainel, gbc);		
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas		
		painelTotal.add(adicionaisPainel1, gbc);
		
		adicionarProduto = new JButton("Adicionar Produto");
		ImageIcon iconePlus = new ImageIcon("imgs/plus2.png");
		adicionarProduto.setIcon(iconePlus);
		adicionarProduto.setPreferredSize(new Dimension(140, 40));
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 3;	// linhas
		
		gbc.insets = new Insets(8,190,8,190);  //top padding
		
		painelTotal.add(adicionarProduto, gbc);
		
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
	public void actionPerformed(ActionEvent e) {
		boolean flag = false;
		
		if(e.getSource() == adicionarADC)
		{
			JButton botao = new JButton();
			ImageIcon iconeRemove = new ImageIcon("imgs/remove.png");
			botao.setIcon(iconeRemove);
			botao.setBorder(BorderFactory.createEmptyBorder());
			botao.setContentAreaFilled(false);
			botao.addActionListener(this);
			
			addAdicional.add(new AutoSuggest());
			addRemover.add(botao);
			MenuPrincipal.AbrirPrincipal(0, false);
		}
		
		if(addRemover.size() > 0)
		{
			for(int i = 0; i < addRemover.size(); i++)
			{
				if(e.getSource() == addRemover.get(i))
				{
					addAdicional.remove(i);
					addRemover.remove(i);
					flag = true;
					break;
				}
			}
		}
		
		if(flag)
			MenuPrincipal.AbrirPrincipal(0, false);
	}
}
