import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.event.*;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;

public class PainelVenda extends JPanel implements ActionListener, FocusListener, TableModelListener
{
	private JPanel painelTotal, rapidaPainel, adicionaisPainel, adicionaisPainel1, pedidoPainel, pagamentoPainel;
	private JLabel labelQuantidade, labelProduto, labelValor, labelCodigo, labelTotal, labelRecebido, labelTroco, labelForma, labelFaltando, labelPagar;
	private static JLabel labelFiado1;
	private static JLabel labelFiado2;
	private JButton adicionarADC, adicionarProduto, calcular;
	private static JButton finalizarVenda;
	private DefaultTableModel tabela;
	private JComboBox campoForma;
	private JTable tabelaPedido;
	static private JTextField campoTotal, campoRecebido, campoTroco, campoFaltando, campoPagar;
	static private JTextField campoValor = new JTextField(5);
	static private JTextField campoQuantidade = new JTextField("1", 2);
	static private VendaRapidaProdutoCampo addProduto = new VendaRapidaProdutoCampo();
	static private ArrayList<VendaRapidaAdicionaisCampo> addAdicional = new ArrayList<>();
	static private ArrayList<JButton> addRemover = new ArrayList<>();
	static private Venda vendaRapida = new Venda();
	private static int fiadorIDSalvo;
	static private int numeroMesa;
	
	private int flag1;
	
	private Timer timer;
	private static boolean fiadoConcluido = false;
	
	PainelVenda(boolean refresh, int numero)
	{		
		flag1 = 0 ;
		numero  -= 4;
		numeroMesa = numero;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(800, 600));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 600));
		
		painelTotal = new JPanel();
		painelTotal.setLayout(new BoxLayout(painelTotal, BoxLayout.X_AXIS));
		painelTotal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Mesa "+ ( numero) ));
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
			labelFiado1 = new JLabel("");
			labelFiado2 = new JLabel("");
			
			campoValor = new JTextField(5);
			campoQuantidade = new JTextField("1", 2);
			addProduto = new VendaRapidaProdutoCampo();
			campoValor = new JTextField(5);
			
			addAdicional = new ArrayList<>();
			addRemover = new ArrayList<>();					
			
			vendaRapida = new Venda();
			campoTotal = new JTextField("0,00", 4);
			
			campoFaltando = new JTextField();
			campoPagar = new JTextField();
			
			
	        timer = new Timer();
	        timer.schedule(new AtualizaFocusInicial(), 700); // em milisegundos				
		}
		vendaRapida = new Venda();
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
		
		adicionarProduto = new JButton("Adicionar");
		ImageIcon iconePlus = new ImageIcon("imgs/plus2.png");
		adicionarProduto.setIcon(iconePlus);
		adicionarProduto.setPreferredSize(new Dimension(60, 40));
		adicionarProduto.addActionListener(this);
		
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.gridy = 2;	// linhas
		gbc.gridx = 6;	// colunas
		gbc.gridwidth = 5;
		
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
		}
		
		JScrollPane scroll = new JScrollPane(adicionaisPainel);
		scroll.setMinimumSize(new Dimension(360,120));
		scroll.setMaximumSize(new Dimension(360,120));
		scroll.setPreferredSize(new Dimension(360,120));
		scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Adicionais"));
		
		adicionaisPainel1.add(scroll);			
		
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
		       if(column == 6 || column == 4)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("Nome");
		tabela.addColumn("Qntd pago");
		tabela.addColumn("Qntd");
		tabela.addColumn("Preço/Uni");
		tabela.addColumn("Pagar");
		tabela.addColumn("Adicionais");
		tabela.addColumn("Deletar");
		
		ArrayList<Integer> pago = new ArrayList<>();
		
		Query pega = new Query();
		pega.executaQuery("SELECT * FROM produtosMesa WHERE mesa_id = "+ numeroMesa +";");		
		
		while(pega.next())
		{
			String nomeProduto = pega.getString("produto");
			String adicionais = pega.getString("adicionais");
			String[] nome = adicionais.split(", ");
			pago.add(pega.getInt("qntdPago"));
			int qntd = pega.getInt("qntd");
			Produto p = new Produto();
			
			Query pega1 = new Query();
			pega1.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nomeProduto + "';");
			
			if(pega1.next())
			{
				double precoProduto = Double.parseDouble(pega1.getString("preco").replaceAll(",", "."));
				
				p.setNome(nomeProduto);
				p.setPreco(precoProduto);
				
				if(nome.length > 0)
				{
					for(int i = 0 ; i < nome.length ; i++)
					{
						Query pega2 = new Query();
						pega2.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nome[i] + "' AND `tipo` = 2;");
						
						if(pega2.next())
						{
							double pAdicional = Double.parseDouble(pega2.getString("preco").replaceAll(",", "."));
							
							Adicionais adcional = new Adicionais();
							adcional.nomeAdicional = nome[i];
							adcional.precoAdicional = pAdicional;
							
							p.adicionrAdc(adcional);
						}
						pega2.fechaConexao();
					}
				}
				if(qntd > 0)
					for(int i = 0; i < qntd ; i++)
						vendaRapida.adicionarProduto(p);
				
				
				
			}	
			pega1.fechaConexao();
		}
		pega.fechaConexao();
		vendaRapida.calculaTotal();
		String pegaPreco;
		pegaPreco = String.format("%.2f", vendaRapida.getTotal());		    	
    	  
		pegaPreco.replaceAll(".", ",");	
		campoTotal.setText(pegaPreco);
		pega.fechaConexao();
		
		
		
		
		if(vendaRapida.getQuantidadeProdutos() > 0)
		{
			for(int i = 0; i < vendaRapida.getQuantidadeProdutos() ; i++)
			{
				Vector<Serializable> linha = new Vector<Serializable>();
				
				linha.add(vendaRapida.getProduto(i).getNome());
				linha.add(pago.get(i));
				linha.add(vendaRapida.getProduto(i).getQuantidade());
				

				pegaPreco = String.format("%.2f", (vendaRapida.getProduto(i).getTotalProduto() ));
				pegaPreco.replaceAll(",", ".");
				
				linha.add(pegaPreco);
				linha.add(0);
				
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
		
		//Pegar o valor já pago e valor a pagar
		
		//double pegaValorPago = 0;
		double pegaValorFaltando = 0;
		//double pegaValorTotal = 0;
		for(int i=0; i< tabela.getRowCount(); i++)
		{
			int qntdPago =(int) tabela.getValueAt(i, 1);
			int qntdFaltando = ((int) tabela.getValueAt(i, 2)) - qntdPago;
			
			pegaPreco = (String) tabela.getValueAt(i, 3);
	    	pegaPreco = pegaPreco.replace(",", ".");
	    	double precoP =Double.parseDouble(pegaPreco.replaceAll(",", "."));
	    	
	    	
	    	//pegaValorPago = +qntdPago * precoP;
	    	pegaValorFaltando += qntdFaltando * precoP;
	    	//pegaValorTotal += (qntdFaltando + qntdPago) * precoP;
			
		}
		
		
		pegaPreco = String.format("%.2f", pegaValorFaltando);		    	  
		pegaPreco.replaceAll(".", ",");	
		campoFaltando.setText(pegaPreco);
		
		
		tabelaPedido = new JTable() {
		    Color alternate = new Color(141, 182, 205);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 6)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }				
		};
		
		tabelaPedido.setModel(tabela);
		
		tabelaPedido.getColumnModel().getColumn(0).setMinWidth(150); // 765 =205 + 45 + 80 + 380 + 55
		tabelaPedido.getColumnModel().getColumn(0).setMaxWidth(150); //10 + 10 + 20 + 45 + 15 
		
		tabelaPedido.getColumnModel().getColumn(1).setMinWidth(70);
		tabelaPedido.getColumnModel().getColumn(1).setMaxWidth(70);
		
		tabelaPedido.getColumnModel().getColumn(2).setMinWidth(45);
		tabelaPedido.getColumnModel().getColumn(2).setMaxWidth(45);
		
		tabelaPedido.getColumnModel().getColumn(3).setMinWidth(80);
		tabelaPedido.getColumnModel().getColumn(3).setMaxWidth(80);
		
		
		tabelaPedido.getColumnModel().getColumn(4).setMinWidth(60);
		tabelaPedido.getColumnModel().getColumn(4).setMaxWidth(60);				
		
		tabelaPedido.getColumnModel().getColumn(5).setMinWidth(305);
		tabelaPedido.getColumnModel().getColumn(5).setMaxWidth(305);
		
		tabelaPedido.getColumnModel().getColumn(6).setMinWidth(55);
		tabelaPedido.getColumnModel().getColumn(6).setMaxWidth(55);			
		
		tabelaPedido.setRowHeight(24);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );//
		
		
		
		
		
		tabelaPedido.getColumn("Qntd pago").setCellRenderer(centraliza);
		tabelaPedido.getColumn("Qntd").setCellRenderer(centraliza);
		tabelaPedido.getColumn("Preço/Uni").setCellRenderer(centraliza);
		tabelaPedido.getColumn("Deletar").setCellRenderer(centraliza);
		tabelaPedido.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaPedido.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));		
		
		tabelaPedido.setPreferredScrollableViewportSize(new Dimension(765, 120));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaPedido);
		pedidoPainel.add(scrolltabela);
		
		pagamentoPainel = new JPanel();
		pagamentoPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pagamento"));
		
		pagamentoPainel.setLayout(new GridBagLayout());
		pagamentoPainel.setMinimumSize(new Dimension(800, 260));
		pagamentoPainel.setMaximumSize(new Dimension(800, 260));
		
		labelTotal = new JLabel("Total:");
		campoTotal.setEditable(false);
		
		labelFaltando = new JLabel("Faltando:");
		campoFaltando.setEditable(false);
		
		labelPagar = new JLabel("Valor a pagar:");
		campoPagar = new JTextField("0,00", 4);
		campoPagar.setEditable(false);
		
		
		labelRecebido = new JLabel("Recebido:");
		campoRecebido = new JTextField("0,00", 4);
		campoRecebido.setEditable(true);
		campoRecebido.addFocusListener(this);
	
        ImageIcon iconeCalcular = new ImageIcon("imgs/calcular.png");
        calcular = new JButton(iconeCalcular);
        calcular.addActionListener(this);
        calcular.setBorder(BorderFactory.createEmptyBorder());
        calcular.setContentAreaFilled(false);
		
		labelTroco = new JLabel("Troco:");
		campoTroco = new JTextField("0,00", 4);
		campoTroco.setEditable(false);
		
		labelForma = new JLabel("Forma de Pagamento:");
		
		String[] tiposPagamento = {"Dinheiro", "Ticket Refeição", "Cartão de Crédito", "Fiado" };
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
		
		gbc.gridx = 0;	// colunas
		gbc.gridy = 1;	// linhas			
		pagamentoPainel.add(labelTotal, gbc);	
		
		gbc.gridx = 1;	// colunas
		pagamentoPainel.add(campoTotal, gbc);
		
		gbc.gridx = 0;	// colunas
		gbc.gridy = 2;	// linhas			
		pagamentoPainel.add(labelFaltando, gbc);	
		
		gbc.gridx = 1;	// colunas
		pagamentoPainel.add(campoFaltando, gbc);
		
		gbc.gridx = 2;	// colunas
		gbc.gridy = 1;	// linhas			
		pagamentoPainel.add(labelPagar, gbc);	
		
		gbc.gridx = 3;	// colunas
		pagamentoPainel.add(campoPagar, gbc);
		
		gbc.gridx = 2;	// colunas
		gbc.gridy = 2;	// linhas			
		pagamentoPainel.add(labelRecebido, gbc);
		
		gbc.gridx = 3;	// colunas
		pagamentoPainel.add(campoRecebido, gbc);
		
		gbc.gridx = 4;	// colunas
		pagamentoPainel.add(calcular, gbc);		
		
		gbc.gridx = 2;	// colunas
		gbc.gridy = 3;	// linhas			
		pagamentoPainel.add(labelTroco, gbc);
		
		gbc.gridx = 3;	// colunas
		pagamentoPainel.add(campoTroco, gbc);
		
		gbc.insets = new Insets(0,50,0,15);  //top padding
		
		gbc.gridx = 5;	// colunas
		gbc.gridy = 1;	// linhas			
		pagamentoPainel.add(labelForma, gbc);
		
		gbc.gridx = 5;	// colunas
		gbc.gridy = 2;	// linhas		
		pagamentoPainel.add(labelFiado1, gbc);		
		
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		gbc.gridx = 6;	// colunas
		gbc.gridy = 1;	// linhas			
		pagamentoPainel.add(campoForma, gbc);
		
		gbc.gridx = 6;	// colunas
		gbc.gridy = 2;	// linhas			
		pagamentoPainel.add(labelFiado2, gbc);			
		
		gbc.gridx = 6;	// colunas
		gbc.gridy = 3;	// linhas			
		pagamentoPainel.add(finalizarVenda, gbc);
		
		fiadorIDSalvo = 0;
		
		add(painelTotal);
		add(pedidoPainel);
		add(pagamentoPainel);	
		tabelaPedido.getModel().addTableModelListener(this);
		this.terminar();
	}
	
    class AtualizaFocusInicial extends TimerTask {

        @Override
        public void run() {
            addProduto.setFocus();
            timer.cancel();
        }
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
				aDouble += Double.parseDouble(pega.getString("preco").replaceAll(",", "."));
			
			for(int i = 0; i < addAdicional.size() ; i++)
			{
				if(addAdicional.get(i).getSelecionado() != null)
				{
					pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + addAdicional.get(i).getSelecionado() + "';");
					
					if(pega.next())
						aDouble += Double.parseDouble(pega.getString("preco").replaceAll(",", "."));					
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
			if(campoForma.getSelectedItem() != "Fiado")
			{
				String confirmacao = "Valor Total: " + campoTotal.getText() + "\n" + 
				"Valor Pago: " + campoRecebido.getText() + "\n(Troco: " + campoTroco.getText() + ")\n\n" + 
				"Forma de Pagamento: " + campoForma.getSelectedItem() + "\n\n" +
				"Confirmar ?";
				
				int opcao = JOptionPane.showConfirmDialog(null, confirmacao, "Confirmar Venda", JOptionPane.YES_NO_OPTION);
				int venda_id = 0;				
				
				if(opcao == JOptionPane.YES_OPTION)
				{
					Calendar c = Calendar.getInstance();
					Locale locale = new Locale("pt","BR"); 
					GregorianCalendar calendar = new GregorianCalendar(); 
					SimpleDateFormat formatador = new SimpleDateFormat("dd'/'MM'/'yyyy' - 'HH':'mm",locale); 
					
					c.get(Calendar.DAY_OF_WEEK);
					
					String formatacao;
					Query envia = new Query();
					formatacao = "INSERT INTO vendas(total, atendente, ano, mes, dia_mes, dia_semana, horario, forma_pagamento, valor_pago, troco, fiado_id) VALUES('"
					+ campoPagar.getText() +
					"', '" + MenuLogin.logado +
					"', " + c.get(Calendar.YEAR) + ", "
					+ c.get(Calendar.MONTH) + ", "
					+ c.get(Calendar.DAY_OF_MONTH) + ", "
					+ c.get(Calendar.DAY_OF_WEEK) +
					", '" + formatador.format(calendar.getTime()) + "', '" + campoForma.getSelectedItem() + "', '" + campoRecebido.getText() + "', '" + campoTroco.getText() + "', 0);";
					
					envia.executaUpdate(formatacao);
					
					Query pega = new Query();
					pega.executaQuery("SELECT vendas_id FROM vendas ORDER BY vendas_id DESC");
					
					if(pega.next())
					{
						venda_id = pega.getInt("vendas_id");
						String pegaPreco = "";
						
						
						for(int i=0; i<tabelaPedido.getRowCount(); i++)
						{
							int qntd = Integer.parseInt(tabelaPedido.getValueAt(i, 4).toString());
							if((qntd)>0)
							{
								pegaPreco = String.format("%.2f", (vendaRapida.getProduto(i).getTotalProduto() * qntd));
								pegaPreco.replaceAll(",", ".");						
								
								formatacao = "INSERT INTO vendas_produtos(id_link, nome_produto, adicionais_produto, preco_produto, quantidade_produto) VALUES('"
										+ venda_id +
										"', '" + vendaRapida.getProduto(i).getNome() +
										"', '" + vendaRapida.getProduto(i).getAllAdicionais() + "', '" + pegaPreco + "', '" + qntd + "');";
										
										envia.executaUpdate(formatacao);
										
								int pago = ((int)tabelaPedido.getValueAt(i, 1))+ qntd;
								Query ajusta = new Query();
								formatacao = "UPDATE produtosMesa SET qntdPago = " + pago + " WHERE produto = '"+vendaRapida.getProduto(i).getNome()+"' AND adicionais ='"+vendaRapida.getProduto(i).getAllAdicionais()+"' ;";
								ajusta.executaUpdate(formatacao);
								ajusta.fechaConexao();
							}
						}
						
					}
					
					
					pega.fechaConexao();
					envia.fechaConexao();
					this.terminar();
					JOptionPane.showMessageDialog(null, "A venda foi computada com sucesso!", "Venda #" + venda_id, JOptionPane.INFORMATION_MESSAGE);
					MenuPrincipal.AbrirPrincipal(numeroMesa+4, true);	
				}
			}
			else
			{
				if(fiadoConcluido)
				{
					String formata;
					formata = campoPagar.getText();
					formata = formata.replaceAll(",",".");	
					double pTotal = Double.parseDouble(formata.replaceAll(",", "."));
					formata = campoRecebido.getText();
					formata = formata.replaceAll(",",".");							
					double pPago = Double.parseDouble(formata);					
					double totalFiado = (pTotal - pPago);
					String divida;
					divida = String.format("%.2f", totalFiado);
					
					String confirmacao = "Valor Total: " + campoPagar.getText() + "\n" + 
					"Valor Pago: " + campoRecebido.getText() + 
					"\n\nForma de Pagamento: " + campoForma.getSelectedItem() + "\n\n" +
					"Ser� adicionado a d�vida de R$" + divida + " na conta de " + labelFiado2.getText() + ".\n" + "\nConfirmar ?";
					
					int opcao = JOptionPane.showConfirmDialog(null, confirmacao, "Confirmar Venda", JOptionPane.YES_NO_OPTION);
					int venda_id = 0;
					
					if(opcao == JOptionPane.YES_OPTION)
					{
						Calendar c = Calendar.getInstance();
						Locale locale = new Locale("pt","BR"); 
						GregorianCalendar calendar = new GregorianCalendar(); 
						SimpleDateFormat formatador = new SimpleDateFormat("dd'/'MM'/'yyyy' - 'HH':'mm",locale); 
						
						c.get(Calendar.DAY_OF_WEEK);
						
						String formatacao;
						Query envia = new Query();
						formatacao = "INSERT INTO vendas(total, atendente, ano, mes, dia_mes, dia_semana, horario, forma_pagamento, valor_pago, troco, fiado_id) VALUES('"
						+ campoPagar.getText() +
						"', '" + MenuLogin.logado +
						"', " + c.get(Calendar.YEAR) + ", "
						+ c.get(Calendar.MONTH) + ", "
						+ c.get(Calendar.DAY_OF_MONTH) + ", "
						+ c.get(Calendar.DAY_OF_WEEK) +
						", '" + formatador.format(calendar.getTime()) + "', '" + campoForma.getSelectedItem() + "', '" + campoRecebido.getText() + "', '" + campoTroco.getText() + 
						"', " + fiadorIDSalvo + ");";
						
						envia.executaUpdate(formatacao);
						
						Query pega = new Query();
						pega.executaQuery("SELECT vendas_id FROM vendas ORDER BY vendas_id DESC");
						
						if(pega.next())
						{
							venda_id = pega.getInt("vendas_id");
							String pegaPreco = "";
							
							for(int i=0; i<tabelaPedido.getRowCount(); i++)
							{
								int qntd = Integer.parseInt(tabelaPedido.getValueAt(i, 4).toString());
								if((qntd)>0)
								{
									pegaPreco = String.format("%.2f", (vendaRapida.getProduto(i).getTotalProduto() * qntd));
									pegaPreco.replaceAll(",", ".");						
									
									formatacao = "INSERT INTO vendas_produtos(id_link, nome_produto, adicionais_produto, preco_produto, quantidade_produto) VALUES('"
											+ venda_id +
											"', '" + vendaRapida.getProduto(i).getNome() +
											"', '" + vendaRapida.getProduto(i).getAllAdicionais() + "', '" + pegaPreco + "', '" + qntd + "');";
											
											envia.executaUpdate(formatacao);
											
									int pago = ((int)tabelaPedido.getValueAt(i, 1))+ qntd;
									Query ajusta = new Query();
									formatacao = "UPDATE produtosMesa SET qntdPago = " + pago + " WHERE produto = '"+vendaRapida.getProduto(i).getNome()+"' AND adicionais ='"+vendaRapida.getProduto(i).getAllAdicionais()+"' ;";
									ajusta.executaUpdate(formatacao);
									ajusta.fechaConexao();
								}
							}
						}
						
						
						pega.fechaConexao();
						envia.fechaConexao();
						JOptionPane.showMessageDialog(null, "A venda foi computada com sucesso!", "Venda #" + venda_id, JOptionPane.INFORMATION_MESSAGE);
						MenuPrincipal.AbrirPrincipal(numeroMesa+4, true);
					}					
				}
				else
				{
					CadastrarFiado f = new CadastrarFiado();
					f.setCallBack(1);
					f.setVisible(true);					
				}
			}
		}
		
		if(e.getSource() == calcular)
		{
			String limpeza = campoRecebido.getText().replaceAll("[^0-9.,]+","");
			
			if(!"".equals(limpeza.trim()))
			{
				double pegaTotal = Double.parseDouble(campoPagar.getText().replaceAll(",", "."));
				double pegaRecebido = Double.parseDouble(limpeza.replaceAll(",", "."));
				
				String resultado = String.format("%.2f", (pegaTotal - pegaRecebido)*-1);
				resultado.replaceAll(",", ".");
				
				campoTroco.setText(resultado);
				
				MenuPrincipal.setarEnter(finalizarVenda);
			}
			
			finalizarVenda.requestFocus();
		}
		
		if(e.getSource() == adicionarProduto)
		{
			String nomeProduto = addProduto.getSelecionado();
			String nomeP = nomeProduto;
			String nomeA = "";
			int quantidade = 0;
			if(nomeProduto == null)
			{
				JOptionPane.showMessageDialog(null, "Você precisa selecionar um produto antes!");
			}
			else
			{
				Produto p = new Produto();
				
				Query pega = new Query();
				pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nomeProduto + "';");
				
				if(pega.next())
				{
					
					double precoProduto = Double.parseDouble(pega.getString("preco").replaceAll(",", "."));
					
					p.setNome(nomeProduto);
					p.setPreco(precoProduto);
					
					if(addAdicional.size() > 0)
					{
						for(int i = 0 ; i < addAdicional.size() ; i++)
						{
							String nomeAdicional = addAdicional.get(i).getSelecionado();
							nomeA += nomeAdicional;
							nomeA += ", ";
							
							pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nomeAdicional + "' AND `tipo` = 2;");
							
							if(pega.next())
							{
								double pAdicional = Double.parseDouble(pega.getString("preco").replaceAll(",", "."));
								
								Adicionais adcional = new Adicionais();
								adcional.nomeAdicional = nomeAdicional;
								adcional.precoAdicional = pAdicional;
								
								p.adicionrAdc(adcional);
							}
						}
					}
				}
				pega.fechaConexao();
				if(Integer.parseInt(campoQuantidade.getText()) > 0)
					for(int i = 0; i < Integer.parseInt(campoQuantidade.getText()) ; i++)
						vendaRapida.adicionarProduto(p);
				Query teste = new Query();
				teste.executaQuery("SELECT * FROM produtosMesa WHERE mesa_id = "+ numeroMesa +" AND produto = '"+nomeP+"' AND adicionais = '"+ nomeA +"'  ; ");
				if(teste.next()){
				
					

					int qntd = teste.getInt("qntd");
					qntd += Integer.parseInt(campoQuantidade.getText());
					
					teste.executaUpdate("UPDATE produtosMesa SET qntd = " + qntd + " WHERE produto = '"+nomeP+"' AND adicionais = '"+ nomeA+"' AND mesa_id  = "+numeroMesa+";");
					
				}else{
					quantidade= Integer.parseInt(campoQuantidade.getText());
					String formatacao;
					Query envia = new Query();
					formatacao = "INSERT INTO produtosMesa(mesa_id, produto, adicionais, qntd, qntdPago ) VALUES("+ numeroMesa +",'"
					+ nomeP +
					"', '"+ nomeA +"', "+quantidade+", 0);";
					
					envia.executaUpdate(formatacao);
					envia.fechaConexao();
				}
				
				teste.fechaConexao();
				
				
				vendaRapida.calculaTotal();
				String pegaPreco;
				pegaPreco = String.format("%.2f", vendaRapida.getTotal());		    	
		    	  
				pegaPreco.replaceAll(".", ",");	
				campoTotal.setText(pegaPreco);
				pega.fechaConexao();
				
				campoValor = new JTextField(5);
				campoQuantidade = new JTextField("1", 2);
				addProduto = new VendaRapidaProdutoCampo();
				campoValor = new JTextField(5);
				
				addAdicional = new ArrayList<>();
				addRemover = new ArrayList<>();				
				
				MenuPrincipal.AbrirPrincipal(numeroMesa+4, false);
				addProduto.setFocus();
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
	    		
	    		fiadoConcluido = false;
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
			MenuPrincipal.AbrirPrincipal(numeroMesa + 4, false);
			addAdicional.get(addAdicional.size()-1).setFocus();
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
				aDouble += Double.parseDouble(pega.getString("preco").replaceAll(",", "."));
			
			for(int i = 0; i < addAdicional.size() ; i++)
			{
				if(addAdicional.get(i).getSelecionado() != null)
				{
					pega.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + addAdicional.get(i).getSelecionado() + "';");
					
					if(pega.next())
						aDouble += Double.parseDouble(pega.getString("preco").replaceAll(",", "."));					
				}
			}
			
			pegaPreco = String.format("%.2f", aDouble);
			pegaPreco.replaceAll(",", ".");			
			pega.fechaConexao();
			campoValor.setText(pegaPreco);
			MenuPrincipal.AbrirPrincipal(numeroMesa+4, false);
		}
	}
	
	@SuppressWarnings("serial")
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

		@SuppressWarnings("serial")
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
		    	  String nome = (String) tabelaPedido.getValueAt(tabelaPedido.getSelectedRow(), 0);
		    	  String adicionais = (String) tabelaPedido.getValueAt(tabelaPedido.getSelectedRow(),5 );
		    	  Query pega = new Query();
		    	  String formatacao = "DELETE  FROM produtosMesa WHERE mesa_id = "+ numeroMesa +" AND produto ='"+ nome +"' AND adicionais='"+adicionais+"'; ";
		    	  pega.executaUpdate(formatacao);
		    	  
		    	  pega.fechaConexao();
		    	  
		    	  MenuPrincipal.AbrirPrincipal(numeroMesa+4, true);
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

		@Override
		public void focusGained(FocusEvent e) {
			if(e.getSource() == campoRecebido)
			{
				campoRecebido.setText("");
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if(e.getSource() == campoRecebido)
			{
				String limpeza = campoRecebido.getText().replaceAll("[^0-9.,]+","");
				
				if(!"".equals(limpeza.trim()))
				{
					double pegaTotal = Double.parseDouble(campoPagar.getText().replaceAll(",", "."));
					double pegaRecebido = Double.parseDouble(limpeza.replaceAll(",", "."));
					
					String resultado = String.format("%.2f", (pegaTotal - pegaRecebido)*-1);
					resultado.replaceAll(",", ".");
					
					campoTroco.setText(resultado);
					
					MenuPrincipal.setarEnter(finalizarVenda);
				}			
			}
		}
		
		static public void setFiado(String fiador, int fiadoID)
		{
			if(fiadoID > 0)
			{
				fiadorIDSalvo = fiadoID;
				labelFiado1.setText("Fiado na conta de:");
				labelFiado2.setText(fiador);
				labelFiado2.setForeground(Color.BLUE);
				
	        	finalizarVenda.setText("Concluir Venda");
	    		ImageIcon iconeFinalizar = new ImageIcon("imgs/finalizar.png");
	    		finalizarVenda.setIcon(iconeFinalizar);	
	    		
	    		fiadoConcluido = true;
			}					
		}
	public void tableChanged(TableModelEvent e) {
				if(flag1 == 0){
					
			        final int row = e.getFirstRow();
			        int column = e.getColumn();
			        if((TableModel)e.getSource() == tabela)
			        {
			        	if(column == 4)
			        	{
			        		String pega =(String)tabelaPedido.getValueAt(row, column);
			        		String limpeza = pega.replaceAll("[^0-9f]+","");
			        		
			        		if("".equals(limpeza.trim())){
			        			flag1 =1;
			        			tabelaPedido.setValueAt(""+0, row, column);
			        		}else{
	
			        			
			        			int valor = Integer.parseInt(limpeza);
				        		
	
	
			        			if((valor> ((int)tabelaPedido.getValueAt(row, 2)) - ((int)tabelaPedido.getValueAt(row, 1))) || valor ==0){
			        				System.out.println("ENTREI2  "+ valor);
			        				flag1 =1;
			        				tabelaPedido.setValueAt(""+0, row, column);
			        			}else{
			        				System.out.println("ENTREI3  "+ valor);
			        				flag1 =1;
			        				tabelaPedido.setValueAt(""+valor, row, column);
	
			        			}
			        			
			        			
			        		}
			        		
			        
			        	}
			        }
				}else {
					flag1 =0;
					this.calcularPagar();
				}
		        
	}
	
	private void calcularPagar(){
		double valor = 0;
		for(int i = 0 ; i < tabelaPedido.getRowCount(); i++){
			String pegaPreco = (String) tabela.getValueAt(i, 3);
	    	pegaPreco = pegaPreco.replace(",", ".");
	    	double precoP =Double.parseDouble(pegaPreco.replaceAll(",", "."));
	    	
	    	pegaPreco =  tabelaPedido.getValueAt(i, 4).toString();
	    	
	    	System.out.println(pegaPreco);
	    	double qntd =  Double.parseDouble(pegaPreco.replaceAll(",", "."));
	    	
	    	valor += qntd * precoP;
		}
		 campoPagar.setText(""+valor);
	}
	
	private void terminar(){
		int flag = 1;
		for(int i =0; i< tabelaPedido.getRowCount(); i++)
		{
			
			int v1 = Integer.parseInt(tabelaPedido.getValueAt(i, 1).toString());
			int v2 = Integer.parseInt(tabelaPedido.getValueAt(i, 2).toString());
			
			flag =0;
			if(v1!=v2){
				flag =1;
				break;
			}
		}
		if(flag == 0){
			Query apaga = new Query();
			apaga.executaUpdate("DELETE from produtosMesa WHERE  mesa_id ="+numeroMesa+";");
			apaga.fechaConexao();
			MenuPrincipal.AbrirPrincipal(numeroMesa + 4, true);
			
		}
	}
}

