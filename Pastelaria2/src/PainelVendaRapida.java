import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class PainelVendaRapida extends JPanel implements ActionListener
{
	private JPanel painelTotal, rapidaPainel, adicionaisPainel, adicionaisPainel1, pedidoPainel, pagamentoPainel;
	private JLabel labelQuantidade, labelProduto, labelValor, labelCodigo, labelTotal, labelRecebido, labelTroco, labelForma;
	private JButton adicionarADC, adicionarProduto, calcular, finalizarVenda;
	private DefaultTableModel tabela;
	private JComboBox campoForma;
	private JTable tabelaPedido;
	static private JTextField campoTotal, campoRecebido, campoTroco;
	
	static private JTextField campoValor = new JTextField(5);
	static private JTextField campoQuantidade = new JTextField("1", 2);
	static private VendaRapidaProdutoCampo addProduto = new VendaRapidaProdutoCampo();
	
	static private ArrayList<VendaRapidaAdicionaisCampo> addAdicional = new ArrayList<>();
	static private ArrayList<JButton> addRemover = new ArrayList<>();
	
	static private Venda vendaRapida = new Venda();
	
	PainelVendaRapida(boolean refresh)
	{		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(800, 600));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 600));
		
		painelTotal = new JPanel();
		painelTotal.setLayout(new BoxLayout(painelTotal, BoxLayout.X_AXIS));
		painelTotal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Venda R�pida"));
		painelTotal.setMinimumSize(new Dimension(800, 150));		// Horizontal , Vertical
		painelTotal.setMaximumSize(new Dimension(800, 150));		
		
		rapidaPainel = new JPanel();
		rapidaPainel.setLayout(new GridBagLayout());
		
		rapidaPainel.setMinimumSize(new Dimension(400, 100));
		rapidaPainel.setMaximumSize(new Dimension(400, 100));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		if(refresh)
		{
			campoValor = new JTextField(5);
			campoQuantidade = new JTextField("1", 2);
			addProduto = new VendaRapidaProdutoCampo();
			campoValor = new JTextField(5);
			
			addAdicional = new ArrayList<>();
			addRemover = new ArrayList<>();
			
			vendaRapida = new Venda();
			
			campoTotal = new JTextField("0,00", 4);
		}
		
		labelProduto = new JLabel("Produto:");
		labelValor = new JLabel("Pre�o:");
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
		gbc.gridwidth = 5;
		
		rapidaPainel.add(addProduto, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas
		
		rapidaPainel.add(labelValor, gbc);
		
		gbc.gridx = 3;	// colunas
		
		rapidaPainel.add(campoValor, gbc);
		
		gbc.gridx = 4;	// colunas
		
		rapidaPainel.add(labelQuantidade, gbc);
		
		gbc.gridx = 5;	// colunas
		
		rapidaPainel.add(campoQuantidade, gbc);
		
		gbc.gridy = 1;	// linhas
		gbc.gridx = 8;	// colunas
		
		gbc.insets = new Insets(0,0,0,0);  //top padding
		
		rapidaPainel.add(adicionarADC, gbc);
		
		adicionarProduto = new JButton("Adicionar Produto");
		ImageIcon iconePlus = new ImageIcon("imgs/plus2.png");
		adicionarProduto.setIcon(iconePlus);
		adicionarProduto.setPreferredSize(new Dimension(60, 40));
		adicionarProduto.addActionListener(this);
		
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.gridy = 2;	// linhas
		gbc.gridx = 6;	// colunas
		gbc.gridwidth = 3;
		
		rapidaPainel.add(adicionarProduto, gbc);
		
		gbc.gridwidth = 1;
		
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
		
		gbc.ipady = 0;
		
		gbc.insets = new Insets(0,0,0,0);  //top padding
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas			
		painelTotal.add(rapidaPainel);		
		
		gbc.gridx = 2;	// colunas
		gbc.gridy = 1;	// linhas		
		painelTotal.add(adicionaisPainel1);
		
		pedidoPainel = new JPanel();
		pedidoPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pedido"));
		pedidoPainel.setMinimumSize(new Dimension(800, 178));		// Horizontal , Vertical
		pedidoPainel.setMaximumSize(new Dimension(800, 178));			
		
		tabela = new DefaultTableModel() {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 4)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("Nome");
		tabela.addColumn("Qntd");
		tabela.addColumn("Pre�o");
		tabela.addColumn("Adicionais");
		tabela.addColumn("Deletar");
		
		
		if(vendaRapida.getQuantidadeProdutos() > 0)
		{
			for(int i = 0; i < vendaRapida.getQuantidadeProdutos() ; i++)
			{
				Vector<Serializable> linha = new Vector<Serializable>();
				
				linha.add(vendaRapida.getProduto(i).getNome());
				linha.add(vendaRapida.getProduto(i).getQuantidade());
				
				String pegaPreco;
				pegaPreco = String.format("%.2f", (vendaRapida.getProduto(i).getTotalProduto() * vendaRapida.getProduto(i).getQuantidade()));
				pegaPreco.replaceAll(",", ".");
				
				linha.add(pegaPreco);
				
				String pegaAdicionais = "";
				
				if(vendaRapida.getProduto(i).getTotalAdicionais() > 0)
				{
					for(int x = 0; x < vendaRapida.getProduto(i).getTotalAdicionais() ; x++)
					{
						pegaAdicionais += vendaRapida.getProduto(i).getAdicional(x).nomeAdicional;
						pegaAdicionais += ", ";
					}
				}
				
				linha.add(pegaAdicionais);
				linha.add("Deletar");
				tabela.addRow(linha);				
			}
		}
		
		tabelaPedido = new JTable() {
		    Color alternate = new Color(141, 182, 205);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 4)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }				
		};
		
		tabelaPedido.setModel(tabela);
		
		tabelaPedido.getColumnModel().getColumn(0).setMinWidth(205);
		tabelaPedido.getColumnModel().getColumn(0).setMaxWidth(205);
		
		tabelaPedido.getColumnModel().getColumn(1).setMinWidth(45);
		tabelaPedido.getColumnModel().getColumn(1).setMaxWidth(45);
		
		tabelaPedido.getColumnModel().getColumn(2).setMinWidth(80);
		tabelaPedido.getColumnModel().getColumn(2).setMaxWidth(80);				
		
		tabelaPedido.getColumnModel().getColumn(3).setMinWidth(385);
		tabelaPedido.getColumnModel().getColumn(3).setMaxWidth(385);
		
		tabelaPedido.getColumnModel().getColumn(4).setMinWidth(50);
		tabelaPedido.getColumnModel().getColumn(4).setMaxWidth(50);			
		
		tabelaPedido.setRowHeight(24);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );
		
		tabelaPedido.getColumn("Pre�o").setCellRenderer(centraliza);
		tabelaPedido.getColumn("Qntd").setCellRenderer(centraliza);
		tabelaPedido.getColumn("Deletar").setCellRenderer(centraliza);
		tabelaPedido.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaPedido.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));		
		
		tabelaPedido.setPreferredScrollableViewportSize(new Dimension(764, 120));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaPedido);
		pedidoPainel.add(scrolltabela);
		
		pagamentoPainel = new JPanel();
		pagamentoPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pagamento"));
		
		pagamentoPainel.setLayout(new GridBagLayout());
		pagamentoPainel.setMinimumSize(new Dimension(800, 260));
		pagamentoPainel.setMaximumSize(new Dimension(800, 260));
		
		
		labelTotal = new JLabel("Total:");
		campoTotal.setEditable(false);
		
		labelRecebido = new JLabel("Recebido:");
		campoRecebido = new JTextField("0,00", 4);
		campoRecebido.setEditable(true);
		
        ImageIcon iconeCalcular = new ImageIcon("imgs/calcular.png");
        calcular = new JButton(iconeCalcular);
        calcular.addActionListener(this);
        calcular.setBorder(BorderFactory.createEmptyBorder());
        calcular.setContentAreaFilled(false);
		
		labelTroco = new JLabel("Troco:");
		campoTroco = new JTextField("0,00", 4);
		campoTroco.setEditable(false);
		
		labelForma = new JLabel("Forma de Pagamento:");
		
		String[] tiposPagamento = {"Dinheiro", "Ticket Refei��o", "Cart�o de Cr�dito", "Fiado" };
		campoForma = new JComboBox(tiposPagamento);
		campoForma.setSelectedIndex(0);
		campoForma.setPreferredSize(new Dimension(150, 30));
		campoForma.addActionListener(this);
		
		finalizarVenda = new JButton("Concluir Venda");
		finalizarVenda.setPreferredSize(new Dimension(100, 35));
		ImageIcon iconeFinalizar = new ImageIcon("imgs/finalizar.png");
		finalizarVenda.setIcon(iconeFinalizar);	
		finalizarVenda.addActionListener(this);
		
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas			
		pagamentoPainel.add(labelTotal, gbc);	
		
		gbc.gridx = 2;	// colunas
		pagamentoPainel.add(campoTotal, gbc);
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas			
		pagamentoPainel.add(labelRecebido, gbc);
		
		gbc.gridx = 2;	// colunas
		pagamentoPainel.add(campoRecebido, gbc);
		
		gbc.gridx = 3;	// colunas
		pagamentoPainel.add(calcular, gbc);		
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 3;	// linhas			
		pagamentoPainel.add(labelTroco, gbc);
		
		gbc.gridx = 2;	// colunas
		pagamentoPainel.add(campoTroco, gbc);
		
		gbc.insets = new Insets(0,50,0,15);  //top padding
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 1;	// linhas			
		pagamentoPainel.add(labelForma, gbc);
		
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.gridx = 5;	// colunas
		gbc.gridy = 1;	// linhas			
		pagamentoPainel.add(campoForma, gbc);
		
		gbc.gridx = 5;	// colunas
		gbc.gridy = 3;	// linhas			
		pagamentoPainel.add(finalizarVenda, gbc);			
		
		add(painelTotal);
		add(pedidoPainel);
		add(pagamentoPainel);
	}
	
	static public void updateCampo()
	{
		if(addProduto.getSelecionado() != null)
		{
			String pegaPreco;
			double aDouble = 0;
			
			Query pega = new Query();
			pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + addProduto.getSelecionado() + "';");
			
			if(pega.next())
				aDouble += Double.parseDouble(pega.getString("preco"));
			
			for(int i = 0; i < addAdicional.size() ; i++)
			{
				if(addAdicional.get(i).getSelecionado() != null)
				{
					pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + addAdicional.get(i).getSelecionado() + "';");
					
					if(pega.next())
						aDouble += Double.parseDouble(pega.getString("preco"));					
				}
			}
			
			pegaPreco = String.format("%.2f", aDouble);
			pegaPreco.replaceAll(",", ".");			
			
			campoValor.setText(pegaPreco);
			pega.fechaConexao();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean flag = false;
		
		if(e.getSource() == finalizarVenda)
		{
			
		}
		
		if(e.getSource() == calcular)
		{
			double pegaTotal = Double.parseDouble(campoTotal.getText().replaceAll(",", "."));
			double pegaRecebido = Double.parseDouble(campoRecebido.getText().replaceAll(",", "."));
			
			String resultado = String.format("%.2f", (pegaTotal - pegaRecebido)*-1);
			resultado.replaceAll(",", ".");
			
			campoTroco.setText(resultado);			
		}
		
		if(e.getSource() == adicionarProduto)
		{
			String nomeProduto = addProduto.getSelecionado();
			
			if(nomeProduto == null)
			{
				JOptionPane.showMessageDialog(null, "Voc� precisa selecionar um produto antes!");
			}
			else
			{
				Produto p = new Produto();
				
				Query pega = new Query();
				pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nomeProduto + "';");
				
				if(pega.next())
				{
					double precoProduto = Double.parseDouble(pega.getString("preco"));
					
					p.setNome(nomeProduto);
					p.setPreco(precoProduto);
					
					if(addAdicional.size() > 0)
					{
						for(int i = 0 ; i < addAdicional.size() ; i++)
						{
							String nomeAdicional = addAdicional.get(i).getSelecionado();
							pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nomeAdicional + "' AND `tipo` = 2;");
							
							if(pega.next())
							{
								double pAdicional = Double.parseDouble(pega.getString("preco"));
								
								Adicionais adcional = new Adicionais();
								adcional.nomeAdicional = nomeAdicional;
								adcional.precoAdicional = pAdicional;
								
								p.adicionrAdc(adcional);
							}
						}
					}
				}
				
				if(Integer.parseInt(campoQuantidade.getText()) > 0)
					for(int i = 0; i < Integer.parseInt(campoQuantidade.getText()) ; i++)
						vendaRapida.adicionarProduto(p);
				
				vendaRapida.calculaTotal();
				String pegaPreco;
				pegaPreco = String.format("%.2f", vendaRapida.getTotal());		    	
		    	  
				pegaPreco.replaceAll(".", ",");	
				campoTotal.setText(pegaPreco);	
				
				pega.fechaConexao();
				MenuPrincipal.AbrirPrincipal(0, false);
			}
		}
		
		if(e.getSource() == campoForma)
		{
	        JComboBox cb = (JComboBox)e.getSource();
	        String forma = (String)cb.getSelectedItem();
	        
	        if(forma == "Fiado")
	        {
	        	finalizarVenda.setText("Prosseguir");
	    		ImageIcon iconeFinalizar = new ImageIcon("imgs/fiado24.png");
	    		finalizarVenda.setIcon(iconeFinalizar);   		
	        }
	        else
	        {
	        	finalizarVenda.setText("Concluir Venda");
	    		ImageIcon iconeFinalizar = new ImageIcon("imgs/finalizar.png");
	    		finalizarVenda.setIcon(iconeFinalizar);		    		
	        }
		}
		
		if(e.getSource() == adicionarADC)
		{
			JButton botao = new JButton();
			ImageIcon iconeRemove = new ImageIcon("imgs/remove.png");
			botao.setIcon(iconeRemove);
			botao.setBorder(BorderFactory.createEmptyBorder());
			botao.setContentAreaFilled(false);
			botao.addActionListener(this);
			
			addAdicional.add(new VendaRapidaAdicionaisCampo());
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
		{
			String pegaPreco;
			double aDouble = 0;
			
			Query pega = new Query();
			pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + addProduto.getSelecionado() + "';");
			
			if(pega.next())
				aDouble += Double.parseDouble(pega.getString("preco"));
			
			for(int i = 0; i < addAdicional.size() ; i++)
			{
				if(addAdicional.get(i).getSelecionado() != null)
				{
					pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + addAdicional.get(i).getSelecionado() + "';");
					
					if(pega.next())
						aDouble += Double.parseDouble(pega.getString("preco"));					
				}
			}
			
			pegaPreco = String.format("%.2f", aDouble);
			pegaPreco.replaceAll(",", ".");			
			
			campoValor.setText(pegaPreco);
			MenuPrincipal.AbrirPrincipal(0, false);
		}
	}
	
	class ButtonRenderer extends JButton implements TableCellRenderer {

		  public ButtonRenderer() {
		    setOpaque(true);
		  }

		  public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {
			  
			  setIcon(new ImageIcon("imgs/delete.png"));
			  
		    if (isSelected) {
		    		setForeground(table.getSelectionForeground());
		    		setBackground(table.getSelectionBackground());
		    } else {
			      setForeground(table.getSelectionForeground());
			      setBackground(table.getSelectionBackground());
		    }
		    return this;
		  }
		}

		/**
		 * @version 1.0 11/09/98
		 */

		class ButtonEditor extends DefaultCellEditor {
		  protected JButton button;

		  private String label;

		  private boolean isPushed;

		  public ButtonEditor(JCheckBox checkBox) {
		    super(checkBox);
		    button = new JButton();
		    button.setOpaque(true);
		    button.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        fireEditingStopped();
		      }
		    });
		  }

		  public Component getTableCellEditorComponent(JTable table, Object value,
		      boolean isSelected, int row, int column) {
			  if (isSelected) {
				  button.setForeground(table.getSelectionForeground());
				  button.setBackground(table.getSelectionBackground());
		    } else {
		    	button.setForeground(table.getForeground());
		    	button.setBackground(table.getBackground());
		    }
		    label = (value == null) ? "" : value.toString();
		    button.setIcon(new ImageIcon("imgs/delete.png"));
		    isPushed = true;
		    return button;
		  }

		  public Object getCellEditorValue() {
		    if (isPushed) {
		      if(tabelaPedido.getSelectedRowCount() == 1)
		      {
		    	  vendaRapida.removerProdutoIndex(tabelaPedido.getSelectedRow());
		    	  vendaRapida.calculaTotal();
		    	  
		    	  String pegaPreco;
		    	  pegaPreco = String.format("%.2f", vendaRapida.getTotal());		    	
		    	  
		    	  pegaPreco.replaceAll(".", ",");	
		    	  campoTotal.setText(pegaPreco);
		    	  
		    	  MenuPrincipal.AbrirPrincipal(0, false);
		       }
		    }
		    isPushed = false;
		    return new String(label);
		  }

		  public boolean stopCellEditing() {
		    isPushed = false;
		    return super.stopCellEditing();
		  }

		  protected void fireEditingStopped() {
		    super.fireEditingStopped();
		  }
		}	
}
