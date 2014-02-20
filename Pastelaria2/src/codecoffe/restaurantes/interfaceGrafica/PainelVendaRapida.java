package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.primitivas.ProdutoVenda;
import codecoffe.restaurantes.primitivas.Venda;
import codecoffe.restaurantes.produtos.ProdutosComboBox;
import codecoffe.restaurantes.sockets.CacheAviso;
import codecoffe.restaurantes.sockets.CacheTodosProdutos;
import codecoffe.restaurantes.sockets.CacheVendaFeita;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.extended.painter.DashedBorderPainter;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;

import java.awt.event.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

public class PainelVendaRapida extends JPanel implements ActionListener, FocusListener, ItemListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pedidoPainel, painelProdutos, painelProdutos1, painelPagamento;
	private JLabel labelQuantidade, labelValor, labelTotal, labelRecebido, labelTroco, labelForma, labelCliente;
	private JTabbedPane divisaoPainel;
	private DefaultTableModel tabela;
	private JTable tabelaPedido;
	private JCheckBox campoEntrega;
	private WebTextField campoComentario;
	private JTextField campoTotal, campoRecebido, campoTroco;
	private JTextField campoValor;
	private JTextField campoQuantidade;
	private ProdutosComboBox addProduto;
	private ArrayList<ProdutosComboBox> addAdicional;
	private ArrayList<JButton> addRemover;
	private Venda vendaRapida;
	private int fiadorIDSalvo;
	private WebPanel adicionaisPainel, adicionaisPainel1;
	private WebButton adicionarProduto, finalizarVenda, imprimir, escolherCliente, deletarCliente, calcular;
	private JEditorPane campoRecibo;
	private WebComboBox campoForma;
	private ImageIcon iconeFinalizar;
	private double taxaEntrega;
	private CacheTodosProdutos todosProdutos;
	private String fiadorTelefone, fiadorEndereco, fiadorNumero, fiadorComplemento;
	private CacheAviso aviso;

	private PainelVendaRapida()
	{		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	
		iconeFinalizar = new ImageIcon(getClass().getClassLoader().getResource("imgs/finalizar.png"));
		divisaoPainel = new JTabbedPane();
		divisaoPainel.setFocusable(false);

		painelProdutos = new JPanel(new MigLayout("align center", "[]15[]15[]15[]15[]15[]15[]"));
		painelProdutos.setMinimumSize(new Dimension(1020, 340));
		painelProdutos.setMaximumSize(new Dimension(1920, 450));
		
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.insets = new Insets(5,5,5,5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		labelValor = new JLabel(" Preço:");
		labelValor.setFont(new Font("Helvetica", Font.BOLD, 16));
		labelQuantidade = new JLabel("Qntd:");
		labelQuantidade.setFont(new Font("Helvetica", Font.BOLD, 16));

		campoValor = new JTextField(6);
		campoQuantidade = new JTextField("1", 2);
		addAdicional = new ArrayList<>();
		addRemover = new ArrayList<>();
		vendaRapida = new Venda();		

		campoValor = new JTextField();
		campoValor.setEditable(false);
		campoValor.setHorizontalAlignment(SwingConstants.CENTER);
		campoValor.setPreferredSize(new Dimension(85, 35));
		campoQuantidade = new JTextField("1");
		campoQuantidade.setHorizontalAlignment(SwingConstants.CENTER);
		campoQuantidade.setPreferredSize(new Dimension(40, 35));
		addProduto = new ProdutosComboBox();
		addProduto.setPreferredSize(new Dimension(350, 110));
		addProduto.addActionListener(this);
		
		campoComentario = new WebTextField();
		campoComentario.setMargin(5, 5, 5, 5);
		campoComentario.setInputPrompt("Comentário p/ Cozinha");
		campoComentario.setPreferredSize(new Dimension(290, 35));

		adicionarProduto = new WebButton("Adicionar Produto");
		adicionarProduto.setFont(new Font("Helvetica", Font.BOLD, 14));
		adicionarProduto.setRolloverShine(true);
		adicionarProduto.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/produtos_add.png")));
		adicionarProduto.setHorizontalTextPosition(AbstractButton.CENTER);
		adicionarProduto.setVerticalTextPosition(AbstractButton.BOTTOM);
		adicionarProduto.setPreferredSize(new Dimension(150, 90));
		adicionarProduto.addActionListener(this);		

		painelProdutos.add(addProduto, "cell 1 0, span 4");
		painelProdutos.add(labelQuantidade, "cell 1 1, gapleft 15px");
		painelProdutos.add(campoQuantidade, "cell 2 1");		
		painelProdutos.add(labelValor, "cell 3 1, gapleft 20px");		
		painelProdutos.add(campoValor, "cell 4 1");
		painelProdutos.add(campoComentario, "cell 1 2, gapleft 10px, gaptop 10px, span 4");

		adicionaisPainel1 = new WebPanel();
		adicionaisPainel1.setLayout(new GridBagLayout());
		adicionaisPainel = new WebPanel();
		
		adicionaisPainel.setLayout(new GridBagLayout());
		DashedBorderPainter<JComponent> bp4 = new DashedBorderPainter<>(new float[]{ 6f, 10f });
		bp4.setRound(2);
		bp4.setWidth(4);
		bp4.setColor(new Color( 205, 205, 205 ));

		WebScrollPane scroll = new WebScrollPane(adicionaisPainel, false);
		scroll.setMinimumSize(new Dimension(260, 180));
		scroll.setMaximumSize(new Dimension(260, 180));
		scroll.setPreferredSize(new Dimension(260, 180));
		adicionaisPainel1.add(scroll);
		adicionaisPainel1.setPainter(bp4);
		TooltipManager.addTooltip(adicionaisPainel, "Adicionais", TooltipWay.up, 500);
		adicionaisPainel.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				JButton botao = new JButton();
				botao.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/remove.png")));
				botao.setBorder(BorderFactory.createEmptyBorder());
				botao.setContentAreaFilled(false);
				botao.addActionListener(getInstance());

				for(int x = 0; x < todosProdutos.getCategorias().size(); x++)
				{
					if(todosProdutos.getCategorias().get(x).getIdCategoria() == 1)
					{
						ProdutosComboBox adcCombo = new ProdutosComboBox(todosProdutos.getCategorias().get(x));
						adcCombo.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								updateCampo();
							}
						});
						addAdicional.add(adcCombo);
						addRemover.add(botao);
						break;
					}
				}

				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 1;	// coluna
				gbc.gridy = addAdicional.size();	// linha

				adicionaisPainel.add(addAdicional.get(addAdicional.size()-1), gbc);

				gbc.gridx = 2;		// coluna
				adicionaisPainel.add(addRemover.get(addAdicional.size()-1), gbc);

				adicionaisPainel.revalidate();
				adicionaisPainel.repaint();
				addAdicional.get(addAdicional.size()-1).requestFocus();
				updateCampo();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {  
					public void run() {
						DashedBorderPainter<JComponent> bp4 = new DashedBorderPainter<>(new float[]{ 6f, 10f });
						bp4.setRound(2);
						bp4.setWidth(4);
						bp4.setColor(new Color(140, 140, 140));
						adicionaisPainel1.setPainter(bp4);
						adicionaisPainel1.revalidate();
						adicionaisPainel1.repaint();
					}
				});
			}

			@Override
			public void mouseExited(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {  
					public void run() {
						DashedBorderPainter<JComponent> bp4 = new DashedBorderPainter<>(new float[]{ 6f, 10f });
						bp4.setRound(2);
						bp4.setWidth(4);
						bp4.setColor(new Color(205, 205, 205));
						adicionaisPainel1.setPainter(bp4);
						adicionaisPainel1.revalidate();
						adicionaisPainel1.repaint();
					}
				});
			}
		});
		
		painelProdutos.add(adicionaisPainel1, "cell 5 0, gaptop 20px, span 1 5");
		painelProdutos.add(adicionarProduto, "cell 6 0, aligny center, gapleft 40px, span 1 5");

		pedidoPainel = new JPanel(new BorderLayout());
		pedidoPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pedido"));		

		tabela = new DefaultTableModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if(column == 0 || column == 6)
					return true;

				return false;
			}
		};

		tabela.addColumn("+/-");
		tabela.addColumn("Nome");
		tabela.addColumn("Qntd");
		tabela.addColumn("Preço");
		tabela.addColumn("Adicionais");
		tabela.addColumn("Comentário");
		tabela.addColumn("Deletar");

		tabelaPedido = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Color alternate = new Color(206, 220, 249);

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component stamp = super.prepareRenderer(renderer, row, column);
				if (row % 2 == 0)
					stamp.setBackground(alternate);
				else
					stamp.setBackground(this.getBackground());     	
				return stamp;
			}				
		};
		
		tabelaPedido.setFocusable(false);
		tabelaPedido.setModel(tabela);
		tabelaPedido.getColumnModel().getColumn(0).setMinWidth(70);
		tabelaPedido.getColumnModel().getColumn(0).setMaxWidth(70);
		tabelaPedido.getColumnModel().getColumn(1).setMinWidth(205);
		tabelaPedido.getColumnModel().getColumn(1).setMaxWidth(500);
		tabelaPedido.getColumnModel().getColumn(2).setMinWidth(45);
		tabelaPedido.getColumnModel().getColumn(2).setMaxWidth(100);
		tabelaPedido.getColumnModel().getColumn(3).setMinWidth(80);
		tabelaPedido.getColumnModel().getColumn(3).setMaxWidth(200);				
		tabelaPedido.getColumnModel().getColumn(4).setMinWidth(200);
		tabelaPedido.getColumnModel().getColumn(4).setMaxWidth(700);
		tabelaPedido.getColumnModel().getColumn(5).setMinWidth(200);
		tabelaPedido.getColumnModel().getColumn(5).setMaxWidth(700);
		tabelaPedido.getColumnModel().getColumn(6).setMinWidth(60);
		tabelaPedido.getColumnModel().getColumn(6).setMaxWidth(65);
		tabelaPedido.setRowHeight(30);
		tabelaPedido.getTableHeader().setReorderingAllowed(false);

		tabelaPedido.getColumn("+/-").setCellRenderer(new OpcoesCell());
		tabelaPedido.getColumn("+/-").setCellEditor(new OpcoesCell());
		tabelaPedido.getColumn("Preço").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Qntd").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Nome").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Adicionais").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Comentário").setCellRenderer(new CustomRenderer());

		tabelaPedido.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaPedido.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));	
		tabelaPedido.setPreferredScrollableViewportSize(new Dimension(800, 150));
		WebScrollPane scrolltabela = new WebScrollPane(tabelaPedido, true);
		scrolltabela.setFocusable(false);
		scrolltabela.getViewport().setBackground(new Color(237, 237, 237));
		pedidoPainel.add(scrolltabela, BorderLayout.CENTER);

		painelProdutos1 = new JPanel();
		painelProdutos1.setLayout(new BoxLayout(painelProdutos1, BoxLayout.Y_AXIS));	

		painelProdutos1.add(painelProdutos);
		painelProdutos1.add(pedidoPainel);

		painelPagamento = new JPanel();
		painelPagamento.setLayout(new GridBagLayout());

		labelCliente = new JLabel("Cliente:");
		labelCliente.setFont(new Font("Helvetica", Font.BOLD, 16));

		escolherCliente = new WebButton("Escolher");
		escolherCliente.setRolloverShine(true);
		escolherCliente.setPreferredSize(new Dimension(170, 40));
		escolherCliente.setHorizontalTextPosition(AbstractButton.LEFT);
		escolherCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/report_user_mini.png")));	
		escolherCliente.addActionListener(this);

		deletarCliente = new WebButton("");
		deletarCliente.setUndecorated(true);
		deletarCliente.setPreferredSize(new Dimension(20, 20));
		deletarCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/delete.png")));	
		deletarCliente.addActionListener(this);		

		labelTotal = new JLabel("Total:");
		labelTotal.setFont(new Font("Helvetica", Font.BOLD, 16));
		campoTotal = new JTextField("0,00");
		campoTotal.setHorizontalAlignment(SwingConstants.CENTER);
		campoTotal.setPreferredSize(new Dimension(90, 35));
		campoTotal.setEnabled(false);

		labelRecebido = new JLabel("Recebido:");
		labelRecebido.setFont(new Font("Helvetica", Font.BOLD, 16));
		campoRecebido = new JTextField("");
		campoRecebido.setHorizontalAlignment(SwingConstants.CENTER);
		campoRecebido.setPreferredSize(new Dimension(110, 35));
		campoRecebido.setEditable(true);
		campoRecebido.addFocusListener(this);

		ImageIcon iconeCalcular = new ImageIcon(getClass().getClassLoader().getResource("imgs/calcular.png"));
		calcular = new WebButton(iconeCalcular);
		calcular.addActionListener(this);
		calcular.setUndecorated(true);
		calcular.setBorder(BorderFactory.createEmptyBorder());
		calcular.setContentAreaFilled(false);

		labelTroco = new JLabel("Troco:");
		labelTroco.setFont(new Font("Helvetica", Font.BOLD, 16));
		campoTroco = new JTextField("0,00");
		campoTroco.setHorizontalAlignment(SwingConstants.CENTER);
		campoTroco.setPreferredSize(new Dimension(90, 35));
		campoTroco.setEnabled(false);

		labelForma = new JLabel("Pagamento:");
		labelForma.setFont(new Font("Helvetica", Font.BOLD, 16));

		campoEntrega = new JCheckBox("Delivery (+ " + UtilCoffe.doubleToPreco( Configuracao.INSTANCE.getTaxaEntrega()) + ")");
		campoEntrega.setPreferredSize(new Dimension(140, 30));
		campoEntrega.setFont(new Font("Helvetica", Font.BOLD, 16));
		campoEntrega.addItemListener(this);
		campoEntrega.setSelected(false);

		String[] tiposPagamento = {"Dinheiro", "Ticket Refeição", "Cartão de Crédito", "Fiado" };
		campoForma = new WebComboBox(tiposPagamento);
		campoForma.setSelectedIndex(0);
		campoForma.setPreferredSize(new Dimension(170, 40));

		finalizarVenda = new WebButton("Concluir Venda");
		finalizarVenda.setRolloverShine(true);
		finalizarVenda.setFont(new Font("Helvetica", Font.BOLD, 16));
		finalizarVenda.setPreferredSize(new Dimension(150, 60));
		finalizarVenda.setIcon(iconeFinalizar);	
		finalizarVenda.addActionListener(this);

		gbc.insets = new Insets(5,5,5,5);  //top padding
		gbc.gridwidth = 2;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas			
		painelPagamento.add(campoEntrega, gbc);

		gbc.insets = new Insets(5,5,5,5);  //top padding
		gbc.gridwidth = 1;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas			
		painelPagamento.add(labelForma, gbc);

		gbc.gridx = 1;	// colunas
		gbc.gridy = 3;	// linhas		
		painelPagamento.add(labelCliente, gbc);		

		gbc.gridwidth = 2;
		gbc.insets = new Insets(5,30,5,5);  //top padding
		gbc.gridx = 2;	// colunas
		gbc.gridy = 2;	// linhas			
		painelPagamento.add(campoForma, gbc);

		gbc.insets = new Insets(5,30,5,5);  //top padding
		gbc.gridx = 2;	// colunas
		gbc.gridy = 3;	// linhas			
		painelPagamento.add(escolherCliente, gbc);

		gbc.insets = new Insets(5,5,5,5);  //top padding
		gbc.gridwidth = 1;
		gbc.gridx = 4;	// colunas
		gbc.gridy = 3;	// linhas			
		painelPagamento.add(deletarCliente, gbc);

		gbc.insets = new Insets(5,5,5,5);  //top padding
		gbc.gridx = 1;	// colunas
		gbc.gridy = 4;	// linhas			
		painelPagamento.add(labelTotal, gbc);	

		gbc.insets = new Insets(5,30,5,5);  //top padding
		gbc.gridx = 2;	// colunas
		painelPagamento.add(campoTotal, gbc);

		gbc.insets = new Insets(5,5,5,5);  //top padding
		gbc.gridx = 1;	// colunas
		gbc.gridy = 5;	// linhas			
		painelPagamento.add(labelRecebido, gbc);

		gbc.insets = new Insets(5,30,5,5);  //top padding
		gbc.gridx = 2;	// colunas
		painelPagamento.add(campoRecebido, gbc);

		gbc.insets = new Insets(5,5,5,5);  //top padding
		gbc.gridx = 3;	// colunas
		painelPagamento.add(calcular, gbc);

		gbc.gridx = 1;	// colunas
		gbc.gridy = 6;	// linhas			
		painelPagamento.add(labelTroco, gbc);

		gbc.insets = new Insets(5,30,5,5);  //top padding
		gbc.gridx = 2;	// colunas
		painelPagamento.add(campoTroco, gbc);		

		gbc.insets = new Insets(5,5,5,5);  //top padding
		gbc.gridwidth = 2;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 8;	// linhas			
		painelPagamento.add(finalizarVenda, gbc);

		JPanel reciboPainel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		reciboPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Recibo"));
		reciboPainel.setPreferredSize(new Dimension(240, 320));

		campoRecibo = new JEditorPane();
		campoRecibo.setPreferredSize(new Dimension(230, 280));
		campoRecibo.setFont(new Font("Verdana", Font.PLAIN, 8));
		campoRecibo.setEditable(false);
		campoRecibo.setText("### Nenhum produto marcado ###");

		JScrollPane scrollrecibo = new JScrollPane(campoRecibo);
		scrollrecibo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollrecibo.setPreferredSize(new Dimension(230, 280));
		reciboPainel.add(scrollrecibo);

		gbc.insets = new Insets(0,50,0,15);  //top padding
		gbc.gridheight = 7;
		gbc.gridwidth = 2;
		gbc.gridy = 1;	// linhas			
		gbc.gridx = 6;	// colunas
		painelPagamento.add(reciboPainel, gbc);

		imprimir = new WebButton("Imprimir");
		imprimir.setPreferredSize(new Dimension(170, 60));
		imprimir.setRolloverShine(true);
		imprimir.setFont(new Font("Helvetica", Font.BOLD, 16));
		imprimir.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/imprimir.png")));
		imprimir.addActionListener(this);

		gbc.insets = new Insets(10,50,0,15);  //top padding
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.gridy = 8;	// linhas			
		gbc.gridx = 6;	// colunas
		painelPagamento.add(imprimir, gbc);

		divisaoPainel.addTab("Venda Rápida", new ImageIcon(getClass().getClassLoader().getResource("imgs/vrapida_mini.png")), painelProdutos1, "Gerenciar o Pedido.");		
		divisaoPainel.addTab("Pagamento", new ImageIcon(getClass().getClassLoader().getResource("imgs/recibo_mini.png")), painelPagamento, "Pagamento do Pedido.");		
		add(divisaoPainel);

		fiadorIDSalvo = 0;
		taxaEntrega = 0.0;
		todosProdutos = new CacheTodosProdutos();
		
		ArrayList<Component> ordem = new ArrayList<Component>();
		ordem.add(addProduto.getEditorTextField());
		ordem.add(campoQuantidade);
		ordem.add(campoComentario);
		ordem.add(adicionarProduto);
		
		FocusTraversal ordemFocus = new FocusTraversal(ordem);
		setFocusCycleRoot(true);
		setFocusTraversalPolicy(ordemFocus);
	}

	private static class VendaRapidaSingletonHolder { 
		public static final PainelVendaRapida INSTANCE = new PainelVendaRapida();
	}

	public static PainelVendaRapida getInstance() {
		return VendaRapidaSingletonHolder.INSTANCE;
	}	

	public void atualizaProdutos(CacheTodosProdutos tp)
	{
		todosProdutos = tp;
		addProduto.atualizaProdutosCombo(tp.getCategorias());

		for(int x = 0; x < tp.getCategorias().size(); x++)
		{
			if(tp.getCategorias().get(x).getIdCategoria() == 1)
			{
				for(int i = 0; i < addAdicional.size(); i++)
					addAdicional.get(i).atualizaAdicionaisCombo(tp.getCategorias().get(x));

				break;
			}
		}
	}

	class OpcoesCellComponent extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private WebButton maisProduto, menosProduto;
		private int linha;

		public OpcoesCellComponent()
		{
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));

			ActionListener al = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getSource() == maisProduto)
					{
						vendaRapida.getProduto(linha).setQuantidade(1, 1);
						double total = vendaRapida.getProduto(linha).getTotalProduto()*vendaRapida.getProduto(linha).getQuantidade();
						tabela.setValueAt(UtilCoffe.doubleToPreco(total), linha, 3);
						tabela.setValueAt(vendaRapida.getProduto(linha).getQuantidade(), linha, 2);
						vendaRapida.calculaTotal();
						campoTotal.setText(UtilCoffe.doubleToPreco((vendaRapida.getTotal() + taxaEntrega)));						
						atualizarCampoRecibo();
					}
					else
					{
						if(vendaRapida.getProduto(linha).getQuantidade() > 1)
						{
							vendaRapida.getProduto(linha).setQuantidade(1, 2);
							double total = vendaRapida.getProduto(linha).getTotalProduto()*vendaRapida.getProduto(linha).getQuantidade();
							tabela.setValueAt(UtilCoffe.doubleToPreco(total), linha, 3);
							tabela.setValueAt(vendaRapida.getProduto(linha).getQuantidade(), linha, 2);
							vendaRapida.calculaTotal();
							campoTotal.setText(UtilCoffe.doubleToPreco((vendaRapida.getTotal() + taxaEntrega)));						
							atualizarCampoRecibo();							
						}
						else
						{
							SwingUtilities.invokeLater(new Runnable() {  
								public void run() {
									if(tabela.getRowCount() > linha)
									{
										vendaRapida.removerProdutoIndex(linha);
										vendaRapida.calculaTotal();
										atualizarCampoRecibo();
										campoTotal.setText(UtilCoffe.doubleToPreco((vendaRapida.getTotal() + taxaEntrega)));									
										tabela.removeRow(linha);
									}
								}  
							});
						}
					}
				}
			};

			maisProduto = new WebButton(new ImageIcon(getClass().getClassLoader().getResource("imgs/plus2.png")));
			maisProduto.setUndecorated(true);
			maisProduto.setPreferredSize(new Dimension(28, 24));
			maisProduto.addActionListener(al);

			menosProduto = new WebButton(new ImageIcon(getClass().getClassLoader().getResource("imgs/remove.png")));
			menosProduto.setUndecorated(true);
			menosProduto.setPreferredSize(new Dimension(28, 24));
			menosProduto.addActionListener(al);

			add(maisProduto);
			add(menosProduto);
		}

		public void setLinha(int li)
		{
			linha = li;
		}
	}

	class OpcoesCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		OpcoesCellComponent cellOpcoes;
		Color alternate = new Color(206, 220, 249);

		public OpcoesCell()
		{
			cellOpcoes = new OpcoesCellComponent();
		}	

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			atualizaPainel(row);
			return cellOpcoes;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			atualizaPainel(row);
			return cellOpcoes;
		}

		public void atualizaPainel(int row)
		{
			if(row % 2 == 0)
				cellOpcoes.setBackground(alternate);
			else
				cellOpcoes.setBackground(Color.WHITE);

			cellOpcoes.setLinha(row);
		}
	}

	class CustomRenderer extends DefaultTableCellRenderer 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(isSelected)
			{
				setHorizontalAlignment( JLabel.CENTER );
				c.setForeground(new Color(72, 61, 139));
				return c;
			}
			else
			{
				setHorizontalAlignment( JLabel.CENTER );
				c.setForeground(Color.BLACK);
				return c;
			}
		}
	}

	public void setaFocusAdd() {
		addProduto.requestFocus();
	}

	public void updateCampo()
	{
		if(addProduto.getProdutoSelecionado() != null)
		{
			double aDouble = addProduto.getProdutoSelecionado().getPreco();

			for(int i = 0; i < addAdicional.size() ; i++)
			{
				if(addAdicional.get(i).getProdutoSelecionado() != null)
				{
					aDouble += addAdicional.get(i).getProdutoSelecionado().getPreco();	
				}
			}

			campoValor.setText(UtilCoffe.doubleToPreco(aDouble));
		}
	}

	private void atualizarCampoRecibo()
	{
		String formataRecibo = "";
		formataRecibo += ("===========================\n");
		formataRecibo += Configuracao.INSTANCE.getMensagemSuperior() + "\n";
		formataRecibo += ("===========================\n");
		formataRecibo += ("********* NAO TEM VALOR FISCAL ********\n");
		formataRecibo += ("===========================\n");		                	

		formataRecibo += ("PRODUTO              QTDE  VALOR UN.  VALOR\n");

		for(int i = 0; i < vendaRapida.getQuantidadeProdutos(); i++)
		{
			formataRecibo += (String.format("%-20.20s", vendaRapida.getProduto(i).getReferencia()));
			formataRecibo += (String.format("%4s     ", vendaRapida.getProduto(i).getQuantidade()));
			formataRecibo += (String.format("%7s       ", UtilCoffe.doubleToPreco(vendaRapida.getProduto(i).getPreco())));
			formataRecibo += (String.format("%6s     \n", UtilCoffe.doubleToPreco((vendaRapida.getProduto(i).getPreco()*vendaRapida.getProduto(i).getQuantidade()))));

			for(int j = 0; j < vendaRapida.getProduto(i).getTotalAdicionais(); j++)
			{
				formataRecibo += (String.format("%-20.20s", "+" + vendaRapida.getProduto(i).getAdicional(j).getReferencia()));
				formataRecibo += (String.format("%3s     ", vendaRapida.getProduto(i).getQuantidade()));
				formataRecibo += (String.format("%5s    ", UtilCoffe.doubleToPreco(vendaRapida.getProduto(i).getAdicional(j).getPreco())));
				formataRecibo += (String.format("%6s    \n", UtilCoffe.doubleToPreco(vendaRapida.getProduto(i).getAdicional(j).getPreco()*vendaRapida.getProduto(i).getQuantidade())));
			}
		}            

		formataRecibo += ("===========================\n");
		formataRecibo += ("INFORMACOES PARA FECHAMENTO DE CONTA    \n");
		formataRecibo += ("===========================\n");

		formataRecibo += (String.format("%-18.18s", "Atendido por: "));
		formataRecibo += (Usuario.INSTANCE.getNome() + "\n");

		Locale locale = new Locale("pt","BR"); 
		GregorianCalendar calendar = new GregorianCalendar(); 
		SimpleDateFormat formatador = new SimpleDateFormat("EEE, dd'/'MM'/'yyyy' - 'HH':'mm", locale);		                

		formataRecibo += (String.format("%-18.18s", "Data: "));
		formataRecibo += (formatador.format(calendar.getTime()) + "\n");

		if(taxaEntrega > 0 && fiadorIDSalvo > 0)
		{
			formataRecibo += "\n" + escolherCliente.getText() + " - TEL: " + fiadorTelefone + "\n";
			formataRecibo += (fiadorEndereco + " - " + fiadorNumero + "\n");
			formataRecibo += (fiadorComplemento + "\n");	
		}

		formataRecibo += ("===========================\n");

		if(taxaEntrega > 0)
			formataRecibo += ("Taxa de Entrega                  R$" + UtilCoffe.doubleToPreco(taxaEntrega) + "\n");

		formataRecibo += ("                     -------------------\n");
		formataRecibo += ("Total                            R$" + campoTotal.getText() + "\n");
		formataRecibo += ("===========================\n");
		formataRecibo += Configuracao.INSTANCE.getMensagemInferior() + "\n";
		formataRecibo += ("       Sistema CodeCoffe V2.0    		  \n");

		campoRecibo.setText(formataRecibo);		
	}	

	private void criarRecibo()
	{
		CacheVendaFeita criaImpressao		= new CacheVendaFeita(vendaRapida);
		criaImpressao.total 				= campoTotal.getText();
		criaImpressao.atendente 			= Usuario.INSTANCE.getNome();
		criaImpressao.fiado_id 				= fiadorIDSalvo;
		criaImpressao.caixa 				= 0;
		criaImpressao.delivery 				= UtilCoffe.doubleToPreco(taxaEntrega);
		criaImpressao.dezporcento			= "0,00";
		criaImpressao.classe				= 2;
		criaImpressao.imprimir 				= true;

		Bartender.INSTANCE.criarImpressao(criaImpressao);
	}

	public void receberAviso(CacheAviso cacheAviso)
	{
		aviso = cacheAviso;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(aviso.getTipo() == 1)
				{
					if(!Configuracao.INSTANCE.getReciboFim())
						JOptionPane.showMessageDialog(null, aviso.getMensagem(), aviso.getTitulo(), JOptionPane.INFORMATION_MESSAGE);
					else
					{
						int opcao = JOptionPane.showConfirmDialog(null, aviso.getMensagem() + "\n\nDeseja imprimir o recibo?", "Venda #" + aviso.getTitulo(), JOptionPane.YES_NO_OPTION);
						if(opcao == JOptionPane.YES_OPTION)
						{
							criarRecibo();
						}			
					}

					for(int i = 0; i < vendaRapida.getQuantidadeProdutos(); i++)
					{
						Pedido ped = new Pedido(vendaRapida.getProduto(i), Usuario.INSTANCE.getNome(), 0);
						Bartender.INSTANCE.enviarPedido(ped);
					}			

					vendaRapida.clear();
					campoEntrega.setSelected(false);
					campoValor.setText("");
					campoQuantidade.setText("1");
					campoTotal.setText("0,00");
					campoRecebido.setText("");
					campoTroco.setText("0,00");
					campoForma.setSelectedIndex(0);
					campoComentario.setText("");
					addAdicional.clear();
					addRemover.clear();			
					adicionaisPainel.removeAll();
					adicionaisPainel.revalidate();
					adicionaisPainel.repaint();
					tabela.setNumRows(0);
					fiadorIDSalvo = 0;
					escolherCliente.setText("Escolher");
					campoRecibo.setText("### Nenhum produto marcado ###");			
				}
				else
				{
					JOptionPane.showMessageDialog(null, aviso.getMensagem(), aviso.getTitulo(), JOptionPane.ERROR_MESSAGE);
				}				
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == imprimir)
		{
			criarRecibo();
		}
		else if(e.getSource() == addProduto)
		{
			updateCampo();
		}
		else if(e.getSource() == deletarCliente)
		{
			escolherCliente.setText("Escolher");
			fiadorIDSalvo = 0;
		}
		else if(e.getSource() == escolherCliente)
		{
			PainelPrincipal.getInstance().AbrirPrincipal(5);
			PainelClientes.getInstance().setCallBack(0);
		}
		else if(e.getSource() == finalizarVenda)
		{			
			if(vendaRapida.getQuantidadeProdutos() > 0)
			{
				if("".equals(campoRecebido.getText().trim()))
					campoRecebido.setText("0,00");					

				String limpeza1 = campoRecebido.getText().replaceAll("[^0-9.,]+","");
				limpeza1 = limpeza1.replaceAll(",", ".");
				String limpeza2 = campoTotal.getText().replaceAll("[^0-9.,]+","");
				limpeza2 = limpeza2.replaceAll(",", ".");

				if(campoForma.getSelectedItem() == "Fiado" && (Double.parseDouble(limpeza1) >= Double.parseDouble(limpeza2)))
				{
					JOptionPane.showMessageDialog(null, "A quantia recebida não pode ser maior ou igual o total para fiado!");
				}
				else if("".equals(campoRecebido.getText().trim()) && campoForma.getSelectedItem() != "Fiado")
				{
					JOptionPane.showMessageDialog(null, "É necessário preencher o campo recebido caso a venda não seja fiada!", "Erro", JOptionPane.INFORMATION_MESSAGE);
				}				
				else
				{
					if(campoForma.getSelectedItem() != "Fiado" || (campoForma.getSelectedItem() == "Fiado" && !escolherCliente.getText().equals("Escolher") && fiadorIDSalvo != 0))
					{						
						if(campoForma.getSelectedItem() != "Fiado" && Double.parseDouble(campoRecebido.getText().replaceAll(",",".")) < Double.parseDouble(campoTotal.getText().replaceAll(",",".")))
							campoRecebido.setText(campoTotal.getText());

						String confirmacao = "";

						if(campoForma.getSelectedItem() == "Fiado")
						{				
							String divida = UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) - UtilCoffe.precoToDouble(campoRecebido.getText())));						
							confirmacao = "Valor Total: " + campoTotal.getText() + "\n" + 
									"Valor Pago: " + campoRecebido.getText() + 
									"\n\nForma de Pagamento: " + campoForma.getSelectedItem() + "\n\n" +
									"Será adicionado a dívida de R$" + divida + " na conta de " + escolherCliente.getText() + ".\n" + "\nConfirmar ?";							
						}
						else
						{
							confirmacao = "Valor Total: " + campoTotal.getText() + "\n" + 
									"Valor Pago: " + campoRecebido.getText() + "\n(Troco: " + campoTroco.getText() + ")\n\n" + 
									"Forma de Pagamento: " + campoForma.getSelectedItem() + "\n\n" +
									"Confirmar ?";							
						}

						int opcao = JOptionPane.showConfirmDialog(null, confirmacao, "Confirmar Venda", JOptionPane.YES_NO_OPTION);			

						if(opcao == JOptionPane.YES_OPTION)
						{
							Calendar c = Calendar.getInstance();
							Locale locale = new Locale("pt","BR"); 
							GregorianCalendar calendar = new GregorianCalendar(); 
							SimpleDateFormat formatador = new SimpleDateFormat("dd'/'MM'/'yyyy' - 'HH':'mm",locale);				

							CacheVendaFeita vendaRapidaFeita= new CacheVendaFeita(vendaRapida);
							vendaRapidaFeita.total 				= campoTotal.getText();
							vendaRapidaFeita.atendente 			= Usuario.INSTANCE.getNome();
							vendaRapidaFeita.ano 				= c.get(Calendar.YEAR);
							vendaRapidaFeita.mes 				= c.get(Calendar.MONTH);
							vendaRapidaFeita.dia_mes 			= c.get(Calendar.DAY_OF_MONTH);
							vendaRapidaFeita.dia_semana 		= c.get(Calendar.DAY_OF_WEEK);
							vendaRapidaFeita.horario 			= formatador.format(calendar.getTime());
							vendaRapidaFeita.forma_pagamento 	= campoForma.getSelectedItem().toString();
							vendaRapidaFeita.valor_pago 		= campoRecebido.getText();	
							vendaRapidaFeita.troco 				= campoTroco.getText();
							vendaRapidaFeita.fiado_id 			= fiadorIDSalvo;
							vendaRapidaFeita.caixa 				= 0;
							vendaRapidaFeita.delivery 			= UtilCoffe.doubleToPreco(taxaEntrega);
							vendaRapidaFeita.dezporcento		= "0,00";
							vendaRapidaFeita.classe				= UtilCoffe.CLASSE_VENDA_RAPIDA;

							Bartender.INSTANCE.enviarVenda(vendaRapidaFeita, 0);	// agora aguarda a resposta.
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Escolha um cliente antes!");
					}
				}
			}
		}
		else if(e.getSource() == calcular)
		{
			String limpeza = campoRecebido.getText().replaceAll("[^0-9.,]+","");

			if(!"".equals(limpeza.trim()))
			{
				double pegaTotal = Double.parseDouble(campoTotal.getText().replaceAll(",", "."));
				double pegaRecebido = Double.parseDouble(limpeza.replaceAll(",", "."));

				if(((pegaTotal - pegaRecebido)*-1) <= 0)
				{
					campoTroco.setText("0,00");
				}
				else
				{
					String resultado = String.format("%.2f", (pegaTotal - pegaRecebido)*-1);
					resultado.replaceAll(",", ".");
					campoTroco.setText(resultado);					
				}

				PainelPrincipal.getInstance().setarEnter(finalizarVenda);
			}

			finalizarVenda.requestFocus();
		}
		else if(e.getSource() == adicionarProduto)
		{
			campoComentario.setText(campoComentario.getText().replaceAll("'", ""));
			
			if(addProduto.getProdutoSelecionado() == null)
			{
				JOptionPane.showMessageDialog(null, "Você precisa selecionar um produto antes!");
			}
			else if(campoComentario.getText().length() > 100)
			{
				JOptionPane.showMessageDialog(null, "Campo comentário pode ter no máximo 100 caracteres!");
			}
			else
			{
				Produto p = addProduto.getProdutoSelecionado();
				ProdutoVenda produto = new ProdutoVenda(p.getNome(), p.getReferencia(), p.getPreco(), p.getIdUnico(), p.getCodigo());
				
				if(!UtilCoffe.vaziu(campoComentario.getText()))
					produto.setComentario(campoComentario.getText());

				if(addAdicional.size() > 0)
				{
					for(int x = 0 ; x < addAdicional.size() ; x++)
					{
						if(addAdicional.get(x).getProdutoSelecionado() != null)
						{
							produto.adicionrAdc(UtilCoffe.cloneProduto(addAdicional.get(x).getProdutoSelecionado()));
						}
					}
				}

				String limpeza = UtilCoffe.limpaNumero(campoQuantidade.getText());
				if(!UtilCoffe.vaziu(limpeza) && limpeza.length() < 6)
				{
					int sizeAntes = vendaRapida.getQuantidadeProdutos();
					int ultimaIndex = 0;

					if(Integer.parseInt(limpeza) > 0)
					{
						produto.setQuantidade(Integer.parseInt(limpeza), 0);
						ultimaIndex = vendaRapida.adicionarProduto(produto);

						if(sizeAntes == vendaRapida.getQuantidadeProdutos())
						{
							String pegaQntd = tabela.getValueAt(ultimaIndex, 2).toString();
							tabela.setValueAt("" + (Integer.parseInt(pegaQntd) + Integer.parseInt(limpeza)), ultimaIndex, 2);
							tabela.setValueAt(UtilCoffe.doubleToPreco((produto.getTotalProduto() * (Integer.parseInt(limpeza) 
									+ Integer.parseInt(pegaQntd)))), ultimaIndex, 3);
						}
						else
						{
							Vector<Serializable> linha = new Vector<Serializable>();
							linha.add("");
							linha.add(produto.getNome());
							linha.add(produto.getQuantidade());
							linha.add(UtilCoffe.doubleToPreco((produto.getTotalProduto()*Integer.parseInt(limpeza))));
							linha.add(produto.getAllAdicionais());
							linha.add(produto.getComentario());
							linha.add("Deletar");
							tabela.addRow(linha);	
						}
					}
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						vendaRapida.calculaTotal();
						campoTotal.setText(UtilCoffe.doubleToPreco((vendaRapida.getTotal() + taxaEntrega)));
						campoValor.setText("");
						campoQuantidade.setText("1");
						campoComentario.setText("");
						addAdicional.clear();
						addRemover.clear();

						atualizarCampoRecibo();

						adicionaisPainel.removeAll();
						adicionaisPainel.revalidate();
						adicionaisPainel.repaint();
						addProduto.requestFocus();
					}
				});
			}
		}
		if(addRemover.size() > 0)
		{
			for(int i = 0; i < addRemover.size(); i++)
			{
				if(e.getSource() == addRemover.get(i))
				{
					adicionaisPainel.remove(addAdicional.get(i));
					adicionaisPainel.remove(addRemover.get(i));
					addAdicional.remove(i);
					addRemover.remove(i);
					double novoValor = 0.0;
					
					if(addProduto != null)
					{
						if(addProduto.getProdutoSelecionado() != null)
						{
							novoValor += addProduto.getProdutoSelecionado().getPreco();
							
							for(int x = 0; x < addAdicional.size() ; x++)
							{
								if(addAdicional.get(x).getProdutoSelecionado() != null)
								{
									novoValor += addAdicional.get(x).getProdutoSelecionado().getPreco();
								}
							}
							
							campoValor.setText(UtilCoffe.doubleToPreco(novoValor));
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									adicionaisPainel.revalidate();
									adicionaisPainel.repaint();	
								}
							});	
						}
					}
					
					break;
				}
			}
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/delete.png")));

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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			label = (value == null) ? "" : value.toString();
			button.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/delete.png")));
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				if(vendaRapida.getQuantidadeProdutos() <= 0)
				{
					tabela.setRowCount(0);
					isPushed = false;
					return new String(label);		    		
				}		    	
				if(tabelaPedido.getSelectedRowCount() == 1)
				{
					vendaRapida.removerProdutoIndex(tabelaPedido.getSelectedRow());
					vendaRapida.calculaTotal();
					atualizarCampoRecibo();

					String pegaPreco;
					pegaPreco = String.format("%.2f", (vendaRapida.getTotal() + taxaEntrega));		    	

					pegaPreco.replaceAll(".", ",");	
					campoTotal.setText(pegaPreco);

					SwingUtilities.invokeLater(new Runnable() {  
						public void run() {  
							tabela.removeRow(tabelaPedido.getSelectedRow());
						}  
					});
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
			campoRecebido.setText("");
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == campoRecebido)
		{
			String limpeza = campoRecebido.getText().replaceAll("[^0-9.,]+","");

			if(!"".equals(limpeza.trim()))
			{
				double pegaTotal = Double.parseDouble(campoTotal.getText().replaceAll(",", "."));
				double pegaRecebido = Double.parseDouble(limpeza.replaceAll(",", "."));

				if(((pegaTotal - pegaRecebido)*-1) <= 0)
				{
					campoTroco.setText("0,00");
				}
				else
				{
					String resultado = String.format("%.2f", (pegaTotal - pegaRecebido)*-1);
					resultado.replaceAll(",", ".");
					campoTroco.setText(resultado);					
				}

				PainelPrincipal.getInstance().setarEnter(finalizarVenda);
			}			
		}
	}

	public void setFiado(String fiador, int fiadoID, String telefone, String endereco, String numero, String complemento)
	{
		if(fiadoID > 0)
		{
			fiadorIDSalvo 		= fiadoID;
			fiadorTelefone 		= telefone;
			fiadorEndereco 		= endereco;
			fiadorNumero 		= numero;
			fiadorComplemento 	= complemento;
			escolherCliente.setText(fiador);
		}					
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getItemSelectable() == campoEntrega)
		{
			if(campoEntrega.isSelected())
			{
				taxaEntrega = Configuracao.INSTANCE.getTaxaEntrega();
				campoTotal.setText(UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) + taxaEntrega)));
				atualizarCampoRecibo();
			}
			else
			{
				campoTotal.setText(UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) - taxaEntrega)));
				taxaEntrega = 0.0;
				atualizarCampoRecibo();
			}
		}
	}	
}