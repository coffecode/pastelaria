package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import codecoffe.restaurantes.primitivas.Adicionais;
import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.primitivas.Venda;
import codecoffe.restaurantes.sockets.CacheAviso;
import codecoffe.restaurantes.sockets.CacheMesaHeader;
import codecoffe.restaurantes.sockets.CacheTodosProdutos;
import codecoffe.restaurantes.sockets.CacheVendaFeita;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;
import codecoffe.restaurantes.utilitarios.VisualizarRecibo;

import com.alee.extended.painter.DashedBorderPainter;
import com.alee.extended.painter.TitledBorderPainter;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

public class PainelVendaMesa extends JPanel implements ActionListener, FocusListener, ItemListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pedidoPainel, painelProdutos, painelProdutos1, painelPagamento;
	private JLabel labelQuantidade, labelProduto, labelValor, labelTotal, labelRecebido, labelTroco, labelForma, labelCliente;
	private JTabbedPane divisaoPainel;
	private JButton calcular;
	private DefaultTableModel tabela;
	private JTable tabelaPedido;
	private JTextField campoTotal, campoRecebido, campoTroco;
	private JTextField campoValor;
	private JTextField campoQuantidade;
	private VendaMesaProdutoCampo addProduto;
	private ArrayList<VendaMesaAdicionaisCampo> addAdicional;
	private ArrayList<JButton> addRemover;
	private Venda vendaRapida;
	private int fiadorIDSalvo;
	private WebPanel adicionaisPainel, adicionaisPainel1;
	private WebButton adicionarADC, adicionarProduto, finalizarVenda, imprimir, flecha1, flecha2, escolherCliente, deletarCliente;
	private JEditorPane campoRecibo;
	private WebComboBox campoForma;
	private DragPanel painelDropIn, painelDropOut;
	private int mesaID = 0;
	private ImageIcon iconeFinalizar;
	private JCheckBox adicionarDezPorcento;
	private double taxaOpcional;
	private CacheTodosProdutos todosProdutos;
	
	@SuppressWarnings("rawtypes")
	private PainelVendaMesa()
	{		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		iconeFinalizar = new ImageIcon(getClass().getClassLoader().getResource("imgs/finalizar.png"));
		divisaoPainel = new JTabbedPane();
		
		painelProdutos = new JPanel();
		painelProdutos.setLayout(new GridBagLayout());
		painelProdutos.setMinimumSize(new Dimension(975, 280));
		painelProdutos.setMaximumSize(new Dimension(1920, 380)); 
		
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.insets = new Insets(5,5,5,5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		labelProduto = new JLabel("Produto:");
		labelProduto.setFont(new Font("Helvetica", Font.BOLD, 16));
		labelValor = new JLabel(" Preço:");
		labelValor.setFont(new Font("Helvetica", Font.BOLD, 16));
		
		labelQuantidade = new JLabel("Qntd:");
		labelQuantidade.setFont(new Font("Helvetica", Font.BOLD, 16));
		
		campoValor = new JTextField(5);
		campoQuantidade = new JTextField("1", 2);
		addAdicional = new ArrayList<>();
		addRemover = new ArrayList<>();
		vendaRapida = new Venda();		
		
		campoValor = new JTextField("");
		campoValor.setEditable(false);
		campoValor.setHorizontalAlignment(SwingConstants.CENTER);
		campoValor.setPreferredSize(new Dimension(80, 35));
		campoQuantidade = new JTextField("1");
		campoQuantidade.setHorizontalAlignment(SwingConstants.CENTER);
		campoQuantidade.setPreferredSize(new Dimension(40, 35));
		addProduto = new VendaMesaProdutoCampo(new CacheTodosProdutos());
		
		adicionarADC = new WebButton("Adicionais");
		adicionarADC.setHorizontalTextPosition(AbstractButton.CENTER);
		adicionarADC.setVerticalTextPosition(AbstractButton.BOTTOM);
		adicionarADC.setFont(new Font("Verdana", Font.PLAIN, 10));
		adicionarADC.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/plus1.png")));
		adicionarADC.setPreferredSize(new Dimension(50, 50));
		adicionarADC.setUndecorated(true);
		adicionarADC.addActionListener(this);
		
		flecha1 = new WebButton("");
		flecha1.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/arrow1.png")));
		flecha1.setToolTipText("Move todos para mesa.");
		flecha1.setPreferredSize(new Dimension(50, 50));
		flecha1.setUndecorated(true);
		flecha1.addActionListener(this);
		
		flecha2 = new WebButton("");
		flecha2.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/arrow2.png")));
		flecha2.setToolTipText("Move todos para pagando.");
		flecha2.setPreferredSize(new Dimension(50, 50));
		flecha2.setUndecorated(true);
		flecha2.addActionListener(this);		
		
		adicionarProduto = new WebButton("Adicionar Produto");
		adicionarProduto.setFont(new Font("Helvetica", Font.PLAIN, 12));
		adicionarProduto.setRolloverShine(true);
		adicionarProduto.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/plus2.png")));
		adicionarProduto.setPreferredSize(new Dimension(120, 50));
		adicionarProduto.addActionListener(this);		
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas		
		painelProdutos.add(labelProduto, gbc);
		
		gbc.gridx = 2;	// colunas	
		painelProdutos.add(addProduto, gbc);
		
		adicionaisPainel1 = new WebPanel();
		adicionaisPainel1.setLayout(new GridBagLayout());
		
		adicionaisPainel = new WebPanel();
		adicionaisPainel.setLayout(new GridBagLayout());
		
        DashedBorderPainter bp4 = new DashedBorderPainter ( new float[]{ 3f, 3f } );
        bp4.setRound ( 12 );
        bp4.setWidth ( 2 );
        bp4.setColor ( new Color ( 205, 205, 205 ) );		
		
		WebScrollPane scroll = new WebScrollPane(adicionaisPainel, false);
		scroll.setMinimumSize(new Dimension(280, 100));
		scroll.setMaximumSize(new Dimension(280, 100));
		scroll.setPreferredSize(new Dimension(280, 100));
		adicionaisPainel1.add(scroll);
		
		adicionaisPainel1.setPainter(bp4);		
		
		gbc.gridx = 2;	// colunas
		gbc.gridy = 2;	// linhas		
		painelProdutos.add(adicionaisPainel1, gbc);
		
		gbc.gridx = 1;	// colunas	
		painelProdutos.add(adicionarADC, gbc);
		
		gbc.insets = new Insets(5,30,5,5);
		gbc.gridx = 3;	// colunas
		gbc.gridy = 1;	// linhas		
		painelProdutos.add(labelQuantidade, gbc);
		
		gbc.insets = new Insets(5,5,5,5);
		gbc.gridx = 4;	// colunas
		painelProdutos.add(campoQuantidade, gbc);		
		
		gbc.gridx = 5;	// colunas
		painelProdutos.add(labelValor, gbc);		
		
		gbc.gridx = 6;	// colunas
		painelProdutos.add(campoValor, gbc);
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 2;	// linhas
		gbc.gridwidth = 3;
		gbc.insets = new Insets(5,5,5,35);
		painelProdutos.add(adicionarProduto, gbc);
		
		pedidoPainel = new JPanel(new BorderLayout());
		pedidoPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pedido"));		
		
		tabela = new DefaultTableModel() {

		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 5)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("Nome");
		tabela.addColumn("Qntd");
		tabela.addColumn("Pago");
		tabela.addColumn("Preço");
		tabela.addColumn("Adicionais");
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
		
		tabelaPedido.setModel(tabela);
		tabelaPedido.getColumnModel().getColumn(0).setMinWidth(205);
		tabelaPedido.getColumnModel().getColumn(0).setMaxWidth(500);
		tabelaPedido.getColumnModel().getColumn(1).setMinWidth(45);
		tabelaPedido.getColumnModel().getColumn(1).setMaxWidth(100);
		tabelaPedido.getColumnModel().getColumn(2).setMinWidth(45);
		tabelaPedido.getColumnModel().getColumn(2).setMaxWidth(100);
		tabelaPedido.getColumnModel().getColumn(3).setMinWidth(80);
		tabelaPedido.getColumnModel().getColumn(3).setMaxWidth(200);				
		tabelaPedido.getColumnModel().getColumn(4).setMinWidth(380);
		tabelaPedido.getColumnModel().getColumn(4).setMaxWidth(1400);
		tabelaPedido.getColumnModel().getColumn(5).setMinWidth(60);
		tabelaPedido.getColumnModel().getColumn(5).setMaxWidth(65);
		tabelaPedido.setRowHeight(25);
		tabelaPedido.getTableHeader().setReorderingAllowed(false);
		
		tabelaPedido.getColumn("Preço").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Qntd").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Pago").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Nome").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Adicionais").setCellRenderer(new CustomRenderer());
		
		tabelaPedido.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaPedido.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));	
		tabelaPedido.setPreferredScrollableViewportSize(new Dimension(800, 200));
		WebScrollPane scrolltabela = new WebScrollPane(tabelaPedido, true);	
		pedidoPainel.add(scrolltabela, BorderLayout.CENTER);
		
		painelProdutos1 = new JPanel();
		painelProdutos1.setLayout(new BoxLayout(painelProdutos1, BoxLayout.Y_AXIS));	
		
		painelProdutos1.add(painelProdutos);
		painelProdutos1.add(pedidoPainel);
		
		painelPagamento = new JPanel();
		painelPagamento.setLayout(new GridBagLayout());
		gbc.insets = new Insets(5,5,5,5);
		
		adicionarDezPorcento = new JCheckBox("+ 10% Opcional");
		adicionarDezPorcento.setPreferredSize(new Dimension(140, 30));
		adicionarDezPorcento.setFont(new Font("Helvetica", Font.BOLD, 16));
		adicionarDezPorcento.addItemListener(this);
		adicionarDezPorcento.setSelected(false);
		
		if(!Configuracao.INSTANCE.getDezPorcento())
			adicionarDezPorcento.setEnabled(false);

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
		campoTotal.setPreferredSize(new Dimension(90, 30));
		campoTotal.setEnabled(false);
		
		labelRecebido = new JLabel("Recebido:");
		labelRecebido.setFont(new Font("Helvetica", Font.BOLD, 16));
		campoRecebido = new JTextField("");
		campoRecebido.setHorizontalAlignment(SwingConstants.CENTER);
		campoRecebido.setPreferredSize(new Dimension(90, 30));
		campoRecebido.setEditable(true);
		campoRecebido.addFocusListener(this);
	
        ImageIcon iconeCalcular = new ImageIcon(getClass().getClassLoader().getResource("imgs/calcular.png"));
        calcular = new JButton(iconeCalcular);
        calcular.addActionListener(this);
        calcular.setBorder(BorderFactory.createEmptyBorder());
        calcular.setContentAreaFilled(false);
		
		labelTroco = new JLabel("Troco:");
		labelTroco.setFont(new Font("Helvetica", Font.BOLD, 16));
		campoTroco = new JTextField("0,00");
		campoTroco.setHorizontalAlignment(SwingConstants.CENTER);
		campoTroco.setPreferredSize(new Dimension(90, 30));
		campoTroco.setEnabled(false);
		
		labelForma = new JLabel("Pagamento:");
		labelForma.setFont(new Font("Helvetica", Font.BOLD, 16));
		
		String[] tiposPagamento = {"Dinheiro", "Ticket Refeição", "Cartão de Crédito", "Fiado" };
		campoForma = new WebComboBox(tiposPagamento);
		campoForma.setSelectedIndex(0);
		campoForma.setPreferredSize(new Dimension(140, 40));
		
		finalizarVenda = new WebButton("Concluir Venda");
		finalizarVenda.setRolloverShine(true);
		finalizarVenda.setFont(new Font("Helvetica", Font.BOLD, 16));
		finalizarVenda.setPreferredSize(new Dimension(150, 50));
		finalizarVenda.setIcon(iconeFinalizar);	
		finalizarVenda.addActionListener(this);
		
		painelDropIn = new DragPanel();
		painelDropIn.tipo = 1;
		painelDropIn.setLayout(new BoxLayout(painelDropIn, BoxLayout.Y_AXIS));
		painelDropIn.setMinimumSize(new Dimension(290, 120));
		painelDropIn.setMaximumSize(new Dimension(290, 120));	
		
		WebScrollPane scrollDropIn = new WebScrollPane(painelDropIn, false);
		scrollDropIn.setMinimumSize(new Dimension(290,120));
		scrollDropIn.setMaximumSize(new Dimension(290,120));
		scrollDropIn.setPreferredSize(new Dimension(290,120));
		
		WebPanel painelDropIn1 = new WebPanel();
		painelDropIn1.setPainter ( new TitledBorderPainter ( "Mesa" ) );
		painelDropIn1.add(scrollDropIn);
		
	    MouseListener handler = new Handler();
	    painelDropIn.addMouseListener(handler);
	    LabelTransferHandler th = new LabelTransferHandler();
	    painelDropIn.setTransferHandler(th); 

		gbc.gridheight = 2;
		gbc.gridwidth = 3;
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridx = 6;	// colunas
		gbc.gridy = 1;	// linhas	
		painelPagamento.add(painelDropIn1, gbc);	
		
		painelDropOut = new DragPanel();
		painelDropOut.tipo = 2;
		painelDropOut.setLayout(new BoxLayout(painelDropOut, BoxLayout.Y_AXIS));
		painelDropOut.setMinimumSize(new Dimension(290, 120));
		painelDropOut.setMaximumSize(new Dimension(290, 120));
		WebScrollPane scrollDropOut = new WebScrollPane(painelDropOut, false);
		scrollDropOut.setMinimumSize(new Dimension(290,120));
		scrollDropOut.setMaximumSize(new Dimension(290,120));
		scrollDropOut.setPreferredSize(new Dimension(290,120));		
		WebPanel painelDropOut1 = new WebPanel();
		painelDropOut1.setPainter ( new TitledBorderPainter ( "Pagando" ) );
		painelDropOut1.add(scrollDropOut);		
		
		painelDropOut.addMouseListener(handler);
		painelDropOut.setTransferHandler(th);		
		
		gbc.insets = new Insets(3,3,3,3);  //top padding
		gbc.gridheight = 2;
		gbc.gridwidth = 2;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas				
		painelPagamento.add(painelDropOut1, gbc);
		
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(30,30,3,0);  //top padding
		gbc.gridx = 3;	// colunas
		gbc.gridy = 1;	// linhas			
		painelPagamento.add(flecha2, gbc);		
		
		gbc.insets = new Insets(3,30,3,0);  //top padding
		gbc.gridx = 3;	// colunas
		gbc.gridy = 2;	// linhas			
		painelPagamento.add(flecha1, gbc);
		
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		
		gbc.insets = new Insets(3,3,3,3);  //top padding
		gbc.gridx = 1;	// colunas
		gbc.gridy = 3;	// linhas			
		painelPagamento.add(labelForma, gbc);
		
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridx = 2;	// colunas
		gbc.gridy = 3;	// linhas			
		painelPagamento.add(campoForma, gbc);
		
		gbc.insets = new Insets(3,3,3,3);  //top padding
		gbc.gridwidth = 2;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 5;	// linhas					
		painelPagamento.add(adicionarDezPorcento, gbc);	
		
		gbc.gridwidth = 1;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 6;	// linhas			
		painelPagamento.add(labelTotal, gbc);	
		
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridx = 2;	// colunas
		painelPagamento.add(campoTotal, gbc);
		
		gbc.insets = new Insets(3,3,3,3);  //top padding
		gbc.gridx = 1;	// colunas
		gbc.gridy = 7;	// linhas			
		painelPagamento.add(labelRecebido, gbc);
		
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridx = 2;	// colunas
		painelPagamento.add(campoRecebido, gbc);
		
		gbc.insets = new Insets(3,3,3,3);  //top padding
		gbc.gridx = 3;	// colunas
		painelPagamento.add(calcular, gbc);		
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 8;	// linhas			
		painelPagamento.add(labelTroco, gbc);
		
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridx = 2;	// colunas
		painelPagamento.add(campoTroco, gbc);		
		
		gbc.insets = new Insets(3,3,3,3);  //top padding
		gbc.gridwidth = 2;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 9;	// linhas			
		painelPagamento.add(finalizarVenda, gbc);
		
		WebPanel reciboPainel = new WebPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		reciboPainel.setPreferredSize(new Dimension(230, 140));
		
        DashedBorderPainter bp5 = new DashedBorderPainter ( new float[]{ 3f, 3f } );
        //bp5.setRound(14);
        bp5.setWidth(2);
        bp5.setColor(new Color( 205, 205, 205 ));
        reciboPainel.setPainter(bp5);			
		
		campoRecibo = new JEditorPane();
		campoRecibo.setPreferredSize(new Dimension(220, 120));
		campoRecibo.setFont(new Font("Verdana", Font.PLAIN, 8));
		campoRecibo.setEditable(false);
		campoRecibo.setText("### Nenhum produto marcado ###");
		
		JScrollPane scrollrecibo = new JScrollPane(campoRecibo);
		scrollrecibo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollrecibo.setPreferredSize(new Dimension(220, 120));
		reciboPainel.add(scrollrecibo);
		
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridheight = 1;
		gbc.gridwidth = 1;		
		gbc.gridx = 6;	// colunas
		gbc.gridy = 3;	// linhas		
		painelPagamento.add(labelCliente, gbc);
		
		gbc.insets = new Insets(3,20,3,3);  //top padding
		gbc.gridx = 7;	// colunas
		gbc.gridy = 3;	// linhas			
		painelPagamento.add(escolherCliente, gbc);
		
		gbc.insets = new Insets(3,3,3,3);  //top padding
		gbc.gridx = 8;	// colunas
		gbc.gridy = 3;	// linhas			
		painelPagamento.add(deletarCliente, gbc);		
		
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridheight = 4;
		gbc.gridwidth = 3;
		gbc.gridy = 5;	// linhas			
		gbc.gridx = 6;	// colunas
		painelPagamento.add(reciboPainel, gbc);
		
		imprimir = new WebButton("Imprimir");
		imprimir.setPreferredSize(new Dimension(170, 50));
		imprimir.setRolloverShine(true);
		imprimir.setFont(new Font("Helvetica", Font.BOLD, 16));
		imprimir.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/imprimir.png")));
		imprimir.addActionListener(this);
		
		gbc.insets = new Insets(3,30,3,3);  //top padding
		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		gbc.gridy = 9;	// linhas			
		gbc.gridx = 6;	// colunas
		painelPagamento.add(imprimir, gbc);		
		
		divisaoPainel.addTab("Mesa", new ImageIcon(getClass().getClassLoader().getResource("imgs/mesa_mini.png")), painelProdutos1, "Gerenciar o pedido da mesa.");		
		divisaoPainel.addTab("Pagamento", new ImageIcon(getClass().getClassLoader().getResource("imgs/recibo_mini.png")), painelPagamento, "Pagamento do Pedido.");		
		add(divisaoPainel);
		
		fiadorIDSalvo = 0;		
		taxaOpcional = 0.0;
		
		todosProdutos = new CacheTodosProdutos();
	}
	
	private static class VendaMesaSingletonHolder { 
		public static final PainelVendaMesa INSTANCE = new PainelVendaMesa();
	}
 
	public static PainelVendaMesa getInstance() {
		return VendaMesaSingletonHolder.INSTANCE;
	}	
	
	public void atualizaProdutos(CacheTodosProdutos tp)
	{
		todosProdutos = tp;
		addProduto.atualizaProdutosCampo(tp);
		
		for(int i = 0; i < addAdicional.size(); i++)
			addAdicional.get(i).atualizaProdutosCampo(tp);
	}	
	
	class DragPanel extends JPanel {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public DragPanel() {
		    super();
		  }
		  public DragLabel draggingLabel;
		  public int tipo;
	}
	
	class Handler extends MouseAdapter {
		  @Override public void mousePressed(MouseEvent e) {
		    DragPanel p = (DragPanel)e.getSource();
		    Component c = SwingUtilities.getDeepestComponentAt(p, e.getX(), e.getY());
		    if(c!=null && c instanceof DragLabel) {
		      p.draggingLabel = (DragLabel)c;
		      p.getTransferHandler().exportAsDrag(p, e, TransferHandler.MOVE);
		    }
		  }
		}
		class LabelTransferHandler extends TransferHandler {
		  /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		private final DataFlavor localObjectFlavor;
		  private final DragLabel label = new DragLabel() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public boolean contains(int x, int y) {
		      return false;
		    }
		  };
		  private final JWindow window = new JWindow();
		  public LabelTransferHandler() {
		    localObjectFlavor = new ActivationDataFlavor(
		    DragPanel.class, DataFlavor.javaJVMLocalObjectMimeType, "DragLabel");
		    window.add(label);
		    window.setAlwaysOnTop(true);
		    window.setBackground(new Color(0,true));
		    DragSource.getDefaultDragSource().addDragSourceMotionListener(
		    new DragSourceMotionListener() {
		      @Override public void dragMouseMoved(DragSourceDragEvent dsde) {
		        Point pt = dsde.getLocation();
		        pt.translate(5, 5); // offset
		        window.setLocation(pt);
		      }
		    });
		  }
		  @Override protected Transferable createTransferable(JComponent c) {
		    DragPanel p = (DragPanel)c;
		    DragLabel l = p.draggingLabel;
		    String text = l.getText();
		    final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
		    if(text==null) return dh;
		    final StringSelection ss = new StringSelection(text+"\n");
		    return new Transferable() {
		      @Override public DataFlavor[] getTransferDataFlavors() {
		        ArrayList<DataFlavor> list = new ArrayList<>();
		        for(DataFlavor f:ss.getTransferDataFlavors()) {
		          list.add(f);
		        }
		        for(DataFlavor f:dh.getTransferDataFlavors()) {
		          list.add(f);
		        }
		        return list.toArray(dh.getTransferDataFlavors());
		      }
		      public boolean isDataFlavorSupported(DataFlavor flavor) {
		        for (DataFlavor f: getTransferDataFlavors()) {
		          if (flavor.equals(f)) {
		            return true;
		          }
		        }
		        return false;
		      }
		      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		        if(flavor.equals(localObjectFlavor)) {
		          return dh.getTransferData(flavor);
		        } else {
		          return ss.getTransferData(flavor);
		        }
		      }
		    };
		  }
		  @Override public boolean canImport(TransferSupport support) {
		    if(!support.isDrop()) {
		      return false;
		    }
		    return true;
		  }
		  @Override public int getSourceActions(JComponent c) {
		    DragPanel p = (DragPanel)c;
		    label.setIcon(p.draggingLabel.getIcon());
		    label.setText(p.draggingLabel.getText());
		    label.setFont(p.draggingLabel.getFont());
		    label.setNome(p.draggingLabel.getNome());
		    label.setAdicionais(p.draggingLabel.getAdicionais());
		    label.setPreco(p.draggingLabel.getPreco());
		    label.setId(p.draggingLabel.getId());		    
		    window.pack();
		    Point pt = p.draggingLabel.getLocation();
		    SwingUtilities.convertPointToScreen(pt, p);
		    window.setLocation(pt);
		    window.setVisible(true);
		    return MOVE;
		  }
		  @Override public boolean importData(TransferSupport support) {
		    if(!canImport(support)) return false;
		    DragPanel target = (DragPanel)support.getComponent();
		    try {
		      DragPanel src = (DragPanel)support.getTransferable().getTransferData(localObjectFlavor);
		      DragLabel l = new DragLabel();
		      l.setIcon(src.draggingLabel.getIcon());
		      l.setText(src.draggingLabel.getText());
		      l.setFont(src.draggingLabel.getFont());
		      l.setNome(src.draggingLabel.getNome());
		      l.setAdicionais(src.draggingLabel.getAdicionais());
		      l.setPreco(src.draggingLabel.getPreco());
		      l.setId(src.draggingLabel.getId());
		      
		      target.add(l);
		      target.revalidate();		      
		      return true;
		    } catch(UnsupportedFlavorException ufe) {
		      //ufe.printStackTrace();
		      JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + ufe.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		    } catch(java.io.IOException ioe) {
		      //ioe.printStackTrace();
		    	JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + ioe.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		    }
		    return false;
		  }
		  @Override protected void exportDone(JComponent c, Transferable data, int action) {
		    DragPanel src = (DragPanel)c;
		    if(action == TransferHandler.MOVE) {
		      src.remove(src.draggingLabel);
		      src.revalidate();
		      src.repaint();

		      double total = 0.0;
		      for(int i = 0; i < painelDropOut.getComponentCount(); i++)
		      {
		    	  DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
		    	  total += dragL.getPreco();
		      }
		      
		      String pegaPreco;
		      
		      if(adicionarDezPorcento.isSelected())
		      {
		    	  taxaOpcional = total * 0.10;
		    	  pegaPreco = String.format("%.2f", (total + (total * 0.10)));
		    	  adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
		      }
		      else
		      {
		    	  taxaOpcional = 0.0;
		    	  pegaPreco = String.format("%.2f", total);
		      }
		      
	    	  pegaPreco.replaceAll(".", ",");
	    	  campoTotal.setText(pegaPreco);
	    	  atualizarCampoRecibo();
		    }
		    src.draggingLabel = null;
		    window.setVisible(false);
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
	        	if(column != 4 && column != 0)
	        	{
	        		setHorizontalAlignment( JLabel.CENTER );
	        	}
	        	
	        	c.setForeground(new Color(72, 61, 139));
	        	return c;
	        }
	        else
	        {
	        	if(column != 4 && column != 0)
	        	{
	        		setHorizontalAlignment( JLabel.CENTER );
	        	}
	        	
	        	c.setForeground(Color.BLACK);
	        	return c;
	        }
	    }
	}
	
	public void receberAviso(CacheAviso aviso)
	{
		if(!Configuracao.INSTANCE.getReciboFim())
			JOptionPane.showMessageDialog(null, aviso.getMensagem(), aviso.getTitulo(), JOptionPane.INFORMATION_MESSAGE);
		else
		{
			int opcao = JOptionPane.showConfirmDialog(null, aviso.getMensagem() + "\n\nDeseja imprimir o recibo?", "Venda #" + aviso.getTitulo(), JOptionPane.YES_NO_OPTION);
			if(opcao == JOptionPane.YES_OPTION)
			{
				criarRecibo();
				VisualizarRecibo.imprimirRecibo();
			}			
		}		
		
		campoValor.setText("");
		adicionarDezPorcento.setSelected(false);	
		campoQuantidade.setText("1");
		campoTotal.setText("0,00");
		campoRecebido.setText("");
		campoTroco.setText("0,00");
		campoForma.setSelectedIndex(0);
		addProduto.zeraString();
		addAdicional.clear();
		addRemover.clear();			
		adicionaisPainel.removeAll();
		adicionaisPainel.revalidate();
		adicionaisPainel.repaint();
		fiadorIDSalvo = 0;
		escolherCliente.setText("Escolher");
		painelDropOut.removeAll();
		painelDropOut.revalidate();
		painelDropOut.repaint();
		taxaOpcional = 0.0;			    		
		campoRecibo.setText("### Nenhum produto marcado ###");
		termina(false);		
	}

	public void updateCampo()
	{
		if(addProduto.getSelecionado() != null)
		{
			double aDouble = 0;
			
			for(int i = 0; i < todosProdutos.getProdutos().size(); i++)
			{
				if(addProduto.getSelecionado().equals(todosProdutos.getProdutos().get(i).getNome()))
				{
					aDouble += todosProdutos.getProdutos().get(i).getPreco();
					break;
				}
			}
			
			for(int i = 0; i < addAdicional.size() ; i++)
			{
				if(addAdicional.get(i).getSelecionado() != null)
				{					
					for(int x = 0; x < todosProdutos.getAdicionais().size(); x++)
					{
						if(addAdicional.get(i).getSelecionado().equals(todosProdutos.getAdicionais().get(x).getNome()))
						{
							aDouble += todosProdutos.getAdicionais().get(x).getPreco();
							break;
						}
					}		
				}
			}
			
			campoValor.setText(UtilCoffe.doubleToPreco(aDouble));
		}
	}
	
	private void atualizarCampoRecibo()
	{
  	  	String formataRecibo = "";
  	  	String pegaPreco = "";
      
      	formataRecibo += ("===========================\n");
      	formataRecibo += (String.format("              %s              \n", Configuracao.INSTANCE.getRestaurante()));
      	formataRecibo += ("===========================\n");
      	formataRecibo += ("********* NAO TEM VALOR FISCAL ********\n");
      	formataRecibo += ("===========================\n");		                	
      	formataRecibo += ("PRODUTO              QTDE  VALOR UN.  VALOR\n");
      
		Venda vendaAgora = new Venda();
		
		for(int i = 0; i < painelDropOut.getComponentCount(); i++)
		{
			DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
			Produto p = new Produto();
			
			p.setNome(dragL.getNome());
			p.setPreco(dragL.getPreco());
			
			for(int x = 0; x < vendaRapida.getProduto(dragL.getId()).getTotalAdicionais(); x++)
			{
				Adicionais adc = new Adicionais();
				adc.nomeAdicional = vendaRapida.getProduto(dragL.getId()).getAdicional(x).nomeAdicional;
				adc.precoAdicional = vendaRapida.getProduto(dragL.getId()).getAdicional(x).precoAdicional;
				p.adicionrAdc(adc);
			}
			
			vendaAgora.adicionarProduto(p);
		}
      
		for(int i = 0; i < vendaAgora.getQuantidadeProdutos(); i++)
		{
			formataRecibo += (String.format("%-20.20s", vendaAgora.getProduto(i).getNome()));
			formataRecibo += (String.format("%4s     ", vendaAgora.getProduto(i).getQuantidade()));
			
			double totalsub = 0.0;
			
			for(int j = 0; j < vendaAgora.getProduto(i).getTotalAdicionais(); j++)
			{
				totalsub += vendaAgora.getProduto(i).getAdicional(j).precoAdicional;
			}
			
			pegaPreco = String.format("%.2f", (vendaAgora.getProduto(i).getPreco() - totalsub));
			pegaPreco.replaceAll(",", ".");							
			
			formataRecibo += (String.format("%7s       ", pegaPreco));
			
			pegaPreco = String.format("%.2f", ((vendaAgora.getProduto(i).getPreco() - totalsub) * vendaAgora.getProduto(i).getQuantidade()));
			pegaPreco.replaceAll(",", ".");							
			
			formataRecibo += (String.format("%6s     \n", pegaPreco));
			
			for(int j = 0; j < vendaAgora.getProduto(i).getTotalAdicionais(); j++)
			{
				formataRecibo += (String.format("%-20.20s", "+" + vendaAgora.getProduto(i).getAdicional(j).nomeAdicional));
				formataRecibo += (String.format("%3s     ", vendaAgora.getProduto(i).getQuantidade()));
				
				pegaPreco = String.format("%.2f", vendaAgora.getProduto(i).getAdicional(j).precoAdicional);
				pegaPreco.replaceAll(",", ".");							
				
				formataRecibo += (String.format("%5s    ", pegaPreco));
				
				pegaPreco = String.format("%.2f", (vendaAgora.getProduto(i).getAdicional(j).precoAdicional*vendaAgora.getProduto(i).getQuantidade()));
				pegaPreco.replaceAll(",", ".");							
				
				formataRecibo += (String.format("%6s    \n", pegaPreco));
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
        
		formataRecibo += ("===========================\n");
		formataRecibo += ("                     -------------------\n");
		formataRecibo += ("Total                            R$" + campoTotal.getText() + "\n");
		
        if(Configuracao.INSTANCE.getDezPorcento())
        {
        	formataRecibo += ("                     ----------------------\n");
        	formataRecibo += ("10% Opcional                     R$" + UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) + taxaOpcional)) + "\n");            	  
        }		
		
		formataRecibo += ("===========================\n");
		formataRecibo += ("       OBRIGADO E VOLTE SEMPRE!	          \n");
		formataRecibo += ("       POWERED BY CodeCoffe V1.0    		  \n");
		
		campoRecibo.setText(formataRecibo);		
	}	
	
	private boolean criarRecibo()
	{
	      try{
	          File arquivo = new File("codecoffe/recibo.txt");
              FileWriter arquivoTxt = new FileWriter(arquivo, false);
              PrintWriter linhasTxt = new PrintWriter(arquivoTxt);
              
              String pegaPreco = "";
              linhasTxt.println("===========================================");
              linhasTxt.println(String.format("              %s              ", Configuracao.INSTANCE.getRestaurante()));
              linhasTxt.println("===========================================");
              linhasTxt.println("*********** NAO TEM VALOR FISCAL **********");
              linhasTxt.println("===========================================");
              linhasTxt.println("PRODUTO              QTDE  VALOR UN.  VALOR");
              
              Venda vendaAgora = new Venda();
    		
    		for(int i = 0; i < painelDropOut.getComponentCount(); i++)
    		{
    			DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
    			Produto p = new Produto();
    			
    			p.setNome(dragL.getNome());
    			p.setPreco(dragL.getPreco());
    			
    			for(int x = 0; x < vendaRapida.getProduto(dragL.getId()).getTotalAdicionais(); x++)
    			{
    				Adicionais adc = new Adicionais();
    				adc.nomeAdicional = vendaRapida.getProduto(dragL.getId()).getAdicional(x).nomeAdicional;
    				adc.precoAdicional = vendaRapida.getProduto(dragL.getId()).getAdicional(x).precoAdicional;
    				p.adicionrAdc(adc);
    			}
    			
    			vendaAgora.adicionarProduto(p);
    		}              
              
				for(int i = 0; i < vendaAgora.getQuantidadeProdutos(); i++)
				{
					linhasTxt.print(String.format("%-20.20s", vendaAgora.getProduto(i).getNome()));
					linhasTxt.print(String.format("%3s     ", vendaAgora.getProduto(i).getQuantidade()));
					
					double totalsub = 0.0;
					
					for(int j = 0; j < vendaAgora.getProduto(i).getTotalAdicionais(); j++)
					{
						totalsub += vendaAgora.getProduto(i).getAdicional(j).precoAdicional;
					}					
											
					pegaPreco = String.format("%.2f", (vendaAgora.getProduto(i).getPreco() - totalsub));
					pegaPreco.replaceAll(",", ".");							
					
					linhasTxt.print(String.format("%5s    ", pegaPreco));
					
					pegaPreco = String.format("%.2f", ((vendaAgora.getProduto(i).getPreco() - totalsub)*vendaAgora.getProduto(i).getQuantidade()));
					pegaPreco.replaceAll(",", ".");							
					
					linhasTxt.print(String.format("%6s    ", pegaPreco));
					linhasTxt.println();
					
					for(int j = 0; j < vendaAgora.getProduto(i).getTotalAdicionais(); j++)
					{
						linhasTxt.print(String.format("%-20.20s", "+" + vendaAgora.getProduto(i).getAdicional(j).nomeAdicional));
						linhasTxt.print(String.format("%3s     ", vendaAgora.getProduto(i).getQuantidade()));
						
						pegaPreco = String.format("%.2f", vendaAgora.getProduto(i).getAdicional(j).precoAdicional);
						pegaPreco.replaceAll(",", ".");							
						
						linhasTxt.print(String.format("%5s    ", pegaPreco));
						
						pegaPreco = String.format("%.2f", (vendaAgora.getProduto(i).getAdicional(j).precoAdicional*vendaAgora.getProduto(i).getQuantidade()));
						pegaPreco.replaceAll(",", ".");							
						
						linhasTxt.print(String.format("%6s    ", pegaPreco));
						linhasTxt.println();
					}
				}            
              
              linhasTxt.println("===========================================");
              linhasTxt.println("   INFORMACOES PARA FECHAMENTO DE CONTA    ");
              linhasTxt.println("===========================================");
              
              linhasTxt.print(String.format("%-18.18s", "Atendido por: "));
              linhasTxt.println(Usuario.INSTANCE.getNome());
              
              Locale locale = new Locale("pt","BR"); 
              GregorianCalendar calendar = new GregorianCalendar(); 
              SimpleDateFormat formatador = new SimpleDateFormat("EEE, dd'/'MM'/'yyyy' - 'HH':'mm", locale);		                
              
              linhasTxt.print(String.format("%-18.18s", "Data: "));
              linhasTxt.println(formatador.format(calendar.getTime()));
	            
              linhasTxt.println("===========================================");
              linhasTxt.println("                     ----------------------");
              linhasTxt.println("Total                            R$" + campoTotal.getText());
              
              if(Configuracao.INSTANCE.getDezPorcento())
              {
                  linhasTxt.println("                     ----------------------");
                  linhasTxt.println("10% Opcional                     R$" + UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) + taxaOpcional)));            	  
              }
      
              linhasTxt.println("===========================================");
              linhasTxt.println("       OBRIGADO E VOLTE SEMPRE!	          ");
              linhasTxt.println("       POWERED BY CodeCoffe V1.0    		  ");
              
              int i = 0;
              while(i < 10){
                  i++;
                  linhasTxt.println();
              }
              
              arquivoTxt.close();
              linhasTxt.close();
              return true;
	      }
	      catch(IOException error)
	      {
	    	  JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + error.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
	          return false;
	      }			
	}
	
	private class DragLabel extends JLabel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String nome;
		private String adicionais;
		private double preco = 0.0;
		private int id;
		
		public DragLabel() {
	        super();
	    }
		
		public void setNome(String texto)
		{
			this.nome = texto;
		}
		
		public void setAdicionais(String texto)
		{
			this.adicionais = texto;
		}
		
		public void setPreco(double valor)
		{
			this.preco = valor;
		}
		
		public void setId(int id)
		{
			this.id = id;
		}
		
		public String getNome()
		{
			return this.nome;
		}
		
		public String getAdicionais()
		{
			return this.adicionais;
		}
		
		public double getPreco()
		{
			return this.preco;
		}
		
		public int getId()
		{
			return this.id;
		}
	}
	
	private void dragAdicionaProduto(Produto p, int id)
	{
		DragLabel dragP = new DragLabel();
		dragP.setNome(p.getNome());
		dragP.setId(id);
		
		String pegaAdicionais = "";
		double pegaPreco = 0.0;
		if(p.getTotalAdicionais() > 0)
		{
			for(int x = 0; x < p.getTotalAdicionais() ; x++)
			{
				pegaAdicionais += p.getAdicional(x).nomeAdicional;
				pegaPreco += p.getAdicional(x).precoAdicional;
				
				if(x != (p.getTotalAdicionais()-1))
					pegaAdicionais += ", ";
			}
		}
		
		dragP.setPreco((p.getPreco() + pegaPreco));
		
		String pegaPreco2 = String.format("%.2f", dragP.getPreco());
		pegaPreco2.replaceAll(",", ".");		
		
		dragP.setAdicionais(pegaAdicionais);
		dragP.setFont(new Font("Verdana", Font.PLAIN, 10));
		dragP.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));
		if("".equals(dragP.getAdicionais()))
			dragP.setText(dragP.getNome() + " - " + pegaPreco2);
		else
			dragP.setText(dragP.getNome() + " com " + dragP.getAdicionais() + " - " + pegaPreco2);
		
		painelDropIn.add(dragP);
		painelDropIn.revalidate();
		painelDropIn.repaint();
	}
	
	public void termina(boolean delete)
	{
		System.out.println("termina chegou!");
		
		CacheMesaHeader mh = new CacheMesaHeader(mesaID , vendaRapida, UtilCoffe.MESA_LIMPAR);
		Bartender.INSTANCE.enviarMesa(mh);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean flag = false;
		
		if(e.getSource() == imprimir)
		{
			criarRecibo();
			VisualizarRecibo.imprimirRecibo();
		}
		else if(e.getSource() == deletarCliente)
		{
			escolherCliente.setText("Escolher");
			fiadorIDSalvo = 0;
		}			
		else if(e.getSource() == escolherCliente)
		{
			MenuPrincipal.getInstance().AbrirPrincipal(5);
			PainelClientes.getInstance().setCallBack(mesaID+1);
		}		
		else if(e.getSource() == flecha1)
		{
			if(painelDropOut.getComponentCount() > 0)
			{
				while(painelDropOut.getComponentCount() > 0)
				{
					DragLabel dragL = (DragLabel) painelDropOut.getComponent(0);
					painelDropOut.remove(0);
					painelDropIn.add(dragL);
				}
				
			      double total = 0.0;
			      for(int i = 0; i < painelDropOut.getComponentCount(); i++)
			      {
			    	  DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
			    	  total += dragL.getPreco();
			      }
			      
			      String pegaPreco;
			      if(adicionarDezPorcento.isSelected())
			      {
			    	  taxaOpcional = total * 0.10;
			    	  pegaPreco = String.format("%.2f", (total + (total * 0.10)));
			    	  adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
			      }
			      else
			      {
			    	  taxaOpcional = 0.0;
			    	  pegaPreco = String.format("%.2f", total);
			      }      
			      
		    	  pegaPreco.replaceAll(".", ",");
		    	  campoTotal.setText(pegaPreco);
		    	  atualizarCampoRecibo();				
				
		    	  painelDropOut.revalidate();
		    	  painelDropOut.repaint();
		    	  painelDropIn.revalidate();
		    	  painelDropIn.repaint();
			}
		}
		else if(e.getSource() == flecha2)
		{
			if(painelDropIn.getComponentCount() > 0)
			{
				while(painelDropIn.getComponentCount() > 0)
				{
					DragLabel dragL = (DragLabel) painelDropIn.getComponent(0);
					painelDropIn.remove(0);
					painelDropOut.add(dragL);
				}
				
			      double total = 0.0;
			      for(int i = 0; i < painelDropOut.getComponentCount(); i++)
			      {
			    	  DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
			    	  total += dragL.getPreco();
			      }
			      
			      String pegaPreco;
			      if(adicionarDezPorcento.isSelected())
			      {
			    	  taxaOpcional = total * 0.10;
			    	  pegaPreco = String.format("%.2f", (total + (total * 0.10)));
			    	  adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
			      }
			      else
			      {
			    	  taxaOpcional = 0.0;
			    	  pegaPreco = String.format("%.2f", total);
			      }
			      
		    	  pegaPreco.replaceAll(".", ",");
		    	  campoTotal.setText(pegaPreco);
		    	  atualizarCampoRecibo();				
				
		    	  painelDropOut.revalidate();
		    	  painelDropOut.repaint();
		    	  painelDropIn.revalidate();
		    	  painelDropIn.repaint();
			}
		}
		else if(e.getSource() == finalizarVenda)
		{	
				if(painelDropOut.getComponentCount() > 0)
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
								Venda vendaAgora = new Venda();
								
								for(int i = 0; i < painelDropOut.getComponentCount(); i++)
								{
									DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
									Produto p = new Produto();
									
									p.setNome(dragL.getNome());
									p.setPreco(dragL.getPreco());
									
									for(int x = 0; x < vendaRapida.getProduto(dragL.getId()).getTotalAdicionais(); x++)
									{
										Adicionais adc = new Adicionais();
										adc.nomeAdicional = vendaRapida.getProduto(dragL.getId()).getAdicional(x).nomeAdicional;
										adc.precoAdicional = vendaRapida.getProduto(dragL.getId()).getAdicional(x).precoAdicional;
										p.adicionrAdc(adc);
									}
									
									vendaAgora.adicionarProduto(p);
								}
								
								Calendar c = Calendar.getInstance();
								Locale locale = new Locale("pt","BR"); 
								GregorianCalendar calendar = new GregorianCalendar(); 
								SimpleDateFormat formatador = new SimpleDateFormat("dd'/'MM'/'yyyy' - 'HH':'mm", locale);
								
								for(int i = 0; i < vendaAgora.getQuantidadeProdutos(); i++)
								{
									for(int x = 0; x < tabela.getRowCount(); x++)
									{
										String pega1 = tabela.getValueAt(x, 0).toString();
										String pega2 = tabela.getValueAt(x, 4).toString();
										
										if(pega1.equals(vendaAgora.getProduto(i).getNome()) && pega2.equals(vendaAgora.getProduto(i).getAllAdicionais()))
										{
											System.out.println("passo xD");
											vendaRapida.getProduto(x).setPagos(vendaAgora.getProduto(i).getQuantidade());
											break;
										}
									}
								}
								
								CacheMesaHeader mesaAgora			= new CacheMesaHeader(mesaID, vendaRapida, UtilCoffe.MESA_ATUALIZAR2);
								CacheVendaFeita vendaMesaFeita		= new CacheVendaFeita(vendaAgora, vendaRapida, mesaAgora);
								vendaMesaFeita.total 				= campoTotal.getText();
								vendaMesaFeita.atendente 			= Usuario.INSTANCE.getNome();
								vendaMesaFeita.ano 					= c.get(Calendar.YEAR);
								vendaMesaFeita.mes 					= c.get(Calendar.MONTH);
								vendaMesaFeita.dia_mes 				= c.get(Calendar.DAY_OF_MONTH);
								vendaMesaFeita.dia_semana 			= c.get(Calendar.DAY_OF_WEEK);
								vendaMesaFeita.horario 				= formatador.format(calendar.getTime());
								vendaMesaFeita.forma_pagamento 		= campoForma.getSelectedItem().toString();
								vendaMesaFeita.valor_pago 			= campoRecebido.getText();	
								vendaMesaFeita.troco 				= campoTroco.getText();
								vendaMesaFeita.fiado_id 			= fiadorIDSalvo;
								vendaMesaFeita.caixa 				= (mesaID+1);
								vendaMesaFeita.delivery 			= "0,00";
								vendaMesaFeita.dezporcento			= UtilCoffe.doubleToPreco(taxaOpcional);
								vendaMesaFeita.classe				= UtilCoffe.VENDA_MESA;
								
								Bartender.INSTANCE.enviarVenda(vendaMesaFeita);	// agora aguarda a resposta.
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
				
				MenuPrincipal.getInstance().setarEnter(finalizarVenda);
			}
			
			finalizarVenda.requestFocus();
		}
		else if(e.getSource() == adicionarProduto)
		{
			String nomeProduto = addProduto.getSelecionado();
			
			if(nomeProduto == null)
			{
				JOptionPane.showMessageDialog(null, "Você precisa selecionar um produto antes!");
			}
			else
			{
				Produto p = new Produto();
				
				for(int i = 0; i < todosProdutos.getProdutos().size(); i++)
				{
					if(nomeProduto.equals(todosProdutos.getProdutos().get(i).getNome()))
					{
						p.setNome(nomeProduto);
						p.setPreco(todosProdutos.getProdutos().get(i).getPreco());
						if(addAdicional.size() > 0)
						{
							for(int x = 0 ; x < addAdicional.size() ; x++)
							{
								String nomeAdicional = addAdicional.get(x).getSelecionado();
								
								for(int z = 0; z < todosProdutos.getAdicionais().size(); z++)
								{
									if(nomeAdicional.equals(todosProdutos.getAdicionais().get(z).getNome()))
									{
										Adicionais adcional = new Adicionais();
										adcional.nomeAdicional = nomeAdicional;
										adcional.precoAdicional = todosProdutos.getAdicionais().get(z).getPreco();
										p.adicionrAdc(adcional);										
										break;
									}
								}
							}							
						}
						break;
					}
				}
				
				int qntdProduto = 0;
				String limpeza = campoQuantidade.getText().replaceAll("[^0-9]+","");
				
				if(!"".equals(limpeza.trim()))
				{
      				if(Integer.parseInt(limpeza) > 0)
    					for(int i = 0; i < Integer.parseInt(limpeza) ; i++)
    					{
    						int ret = vendaRapida.adicionarProduto(p); 
    						dragAdicionaProduto(p, ret);
    						qntdProduto++;
    					}					
				}
				
				boolean new_flag = false;
				String pegaAdicionais = "";
				if(p.getTotalAdicionais() > 0)
				{
					for(int x = 0; x < p.getTotalAdicionais() ; x++)
					{
						pegaAdicionais += p.getAdicional(x).nomeAdicional;
						
						if(x != (p.getTotalAdicionais()-1))
							pegaAdicionais += ", ";
					}
				}
				
				Pedido ped = new Pedido(p, Usuario.INSTANCE.getNome(), "", (mesaID+1), (vendaRapida.getQuantidadeProdutos()-1));
				Bartender.INSTANCE.enviarPedido(ped);
				
				for(int row = 0; row < tabela.getRowCount(); row++)
				{
					String cmpNome = tabela.getValueAt(row, 0).toString();
					String cmpAdc = tabela.getValueAt(row, 4).toString();
					
					if(cmpNome.equals(p.getNome()) && cmpAdc.equals(pegaAdicionais))
					{
						CacheMesaHeader mh = new CacheMesaHeader(mesaID, p, vendaRapida, UtilCoffe.MESA_ATUALIZAR, qntdProduto);
						Bartender.INSTANCE.enviarMesa(mh);
						new_flag = true;
						break;
					}
				}				
				
				if(!new_flag)
				{
					CacheMesaHeader mh = new CacheMesaHeader(mesaID, p, vendaRapida, UtilCoffe.MESA_ADICIONAR, qntdProduto);
					Bartender.INSTANCE.enviarMesa(mh);				
				}
				/*
				campoValor.setText("");
				campoQuantidade.setText("1");
				addProduto.zeraString();
				
				addAdicional.clear();
				addRemover.clear();
				
				adicionaisPainel.removeAll();
				adicionaisPainel.revalidate();
				adicionaisPainel.repaint();
				VendaMesaProdutoCampo.setFocus();
				
				PainelMesas.atualizaMesa(mesaID, vendaRapida);*/
			}
		}
		else if(e.getSource() == adicionarADC)
		{
			JButton botao = new JButton();
			botao.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/remove.png")));
			botao.setBorder(BorderFactory.createEmptyBorder());
			botao.setContentAreaFilled(false);
			botao.addActionListener(this);
			
			addAdicional.add(new VendaMesaAdicionaisCampo(todosProdutos));
			addRemover.add(botao);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;	// coluna
			gbc.gridy = addAdicional.size();	// linha
			
			adicionaisPainel.add(addAdicional.get(addAdicional.size()-1), gbc);
			
			gbc.gridx = 2;		// coluna
			adicionaisPainel.add(addRemover.get(addAdicional.size()-1), gbc);
			
			adicionaisPainel.revalidate();
			adicionaisPainel.repaint();
			addAdicional.get(addAdicional.size()-1).setFocus();
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
					flag = true;
					break;
				}
			}
		}
		
		if(flag)
		{
			double aDouble = 0;
			
			for(int i = 0; i < todosProdutos.getProdutos().size(); i++)
			{
				if(addProduto.getSelecionado().equals(todosProdutos.getProdutos().get(i).getNome()))
				{
					aDouble += todosProdutos.getProdutos().get(i).getPreco();
					break;
				}
			}
			
			for(int i = 0; i < addAdicional.size() ; i++)
			{
				if(addAdicional.get(i).getSelecionado() != null)
				{					
					for(int x = 0; x < todosProdutos.getAdicionais().size(); x++)
					{
						if(addAdicional.get(i).getSelecionado().equals(todosProdutos.getAdicionais().get(x).getNome()))
						{
							aDouble += todosProdutos.getAdicionais().get(x).getPreco();
							break;
						}
					}		
				}
			}
			
			campoValor.setText(UtilCoffe.doubleToPreco(aDouble));
			adicionaisPainel.revalidate();
			adicionaisPainel.repaint();			
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
		    	  String pegaLixo = tabela.getValueAt(tabelaPedido.getSelectedRow(), 1).toString();
		    	  int quantidadeDeletar = Integer.parseInt(pegaLixo);
		    	  
		    	  for(int i = 0; i < painelDropOut.getComponentCount(); i++)
		    	  {	  
		    		  DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
		    		  if(dragL.getNome().equals(vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getNome()))
		    		  {
		    			  String pegaAdc = "";
		    			  for(int x = 0; x < vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais(); x++)
		    			  {
		    				  pegaAdc += vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getAdicional(x).nomeAdicional;
		    				  
		    				  if(x != (vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais()-1))
		    				  {
		    					  pegaAdc += ", ";
		    				  }
		    			  }
		    			  
		    			  if(dragL.getAdicionais().equals(pegaAdc))
		    			  {
		    				  painelDropOut.remove(i);
		    				  quantidadeDeletar--;
		    				  i = 0;
		    				  
			    			  if(quantidadeDeletar <= 0)
			    				  break;		  
		    			  }
		    		  }
		    	  }
		    	  
		    	  if(quantidadeDeletar > 0 && painelDropOut.getComponentCount() > 0)
		    	  {
		    		  DragLabel dragL = (DragLabel)painelDropOut.getComponent(0);
		    		  if(dragL.getNome().equals(vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getNome()))
		    		  {
		    			  String pegaAdc = "";
		    			  for(int x = 0; x < vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais(); x++)
		    			  {
		    				  pegaAdc += vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getAdicional(x).nomeAdicional;
		    				  
		    				  if(x != (vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais()-1))
		    				  {
		    					  pegaAdc += ", ";
		    				  }		    				  
		    			  }
		    			  
		    			  if(dragL.getAdicionais().equals(pegaAdc))
		    			  {
		    				  painelDropOut.remove(0);
		    				  quantidadeDeletar--;	  
		    			  }
		    		  }		    		  
		    	  }
		    	  
		    	  if(quantidadeDeletar > 0)
		    	  {
			    	  for(int i = 0; i < painelDropIn.getComponentCount(); i++)
			    	  { 
			    		  DragLabel dragL = (DragLabel)painelDropIn.getComponent(i);
			    		  if(dragL.getNome().equals(vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getNome()))
			    		  {
			    			  String pegaAdc = "";
			    			  for(int x = 0; x < vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais(); x++)
			    			  {
			    				  pegaAdc += vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getAdicional(x).nomeAdicional;
			    				  
			    				  if(x != (vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais()-1))
			    				  {
			    					  pegaAdc += ", ";
			    				  }			    				  
			    			  }
			    			  
			    			  if(dragL.getAdicionais().equals(pegaAdc))
			    			  {
			    				  painelDropIn.remove(i);
			    				  quantidadeDeletar--;
			    				  i = 0;
			    				  
				    			  if(quantidadeDeletar <= 0)
				    				  break;		  
			    			  }
			    		  }
			    	  }		    		  
		    	  }
		    	  
		    	  if(quantidadeDeletar > 0 && painelDropIn.getComponentCount() > 0)
		    	  {
		    		  DragLabel dragL = (DragLabel)painelDropIn.getComponent(0);
		    		  if(dragL.getNome().equals(vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getNome()))
		    		  {
		    			  String pegaAdc = "";
		    			  for(int x = 0; x < vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais(); x++)
		    			  {
		    				  pegaAdc += vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getAdicional(x).nomeAdicional;
		    				  
		    				  if(x != (vendaRapida.getProduto(tabelaPedido.getSelectedRow()).getTotalAdicionais()-1))
		    				  {
		    					  pegaAdc += ", ";
		    				  }		    				  
		    			  }
		    			  
		    			  if(dragL.getAdicionais().equals(pegaAdc))
		    			  {
		    				  painelDropIn.remove(0);
		    				  quantidadeDeletar--;	  
		    			  }
		    		  }		    		  
		    	  }		    	  

		    	  painelDropOut.revalidate();
		    	  painelDropOut.repaint();
		    	  painelDropIn.revalidate();
		    	  painelDropIn.repaint();
			     
			      Produto prod = new Produto();
			      
			     if(tabelaPedido.getSelectedRow() >= 0 && tabelaPedido.getSelectedRowCount() == 1) 
			     {
			    	 prod = vendaRapida.getProduto(tabelaPedido.getSelectedRow());
			    	 vendaRapida.removerProdutoIndex(tabelaPedido.getSelectedRow());
			    	 vendaRapida.calculaTotal();
			    	 
			    	 CacheMesaHeader mh = new CacheMesaHeader(mesaID, prod, vendaRapida, UtilCoffe.MESA_DELETAR, 0);
			    	 Bartender.INSTANCE.enviarMesa(mh);
			     }
		    	  
			      /*double total = 0.0;
			      for(int i = 0; i < painelDropOut.getComponentCount(); i++)
			      {
			    	  DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
			    	  total += dragL.getPreco();
			      }
			      
			      String pegaPreco;
			      if(adicionarDezPorcento.isSelected())
			      {
			    	  taxaOpcional = total * 0.10;
			    	  pegaPreco = String.format("%.2f", (total + (total * 0.10)));
			    	  adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
			      }
			      else
			      {
			    	  taxaOpcional = 0.0;
			    	  pegaPreco = String.format("%.2f", total);
			      }
			      
		    	  pegaPreco.replaceAll(".", ",");
		    	  campoTotal.setText(pegaPreco);
		    	  atualizarCampoRecibo();
		    	  
			      SwingUtilities.invokeLater(new Runnable() {  
			    	  public void run() {  
			    		  tabela.removeRow(tabelaPedido.getSelectedRow());
			    	  }  
			      });
			      
			      termina(true);	*/		      
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
		
		public void setarMesa(int mesa, Venda v)
		{			
			campoValor.setText("");
			campoQuantidade.setText("1");
			campoTotal.setText("0,00");
			campoRecebido.setText("");
			campoTroco.setText("0,00");
			campoForma.setSelectedIndex(0);
			addProduto.zeraString();
			addAdicional.clear();
			addRemover.clear();			
			adicionaisPainel.removeAll();
			adicionaisPainel.revalidate();
			adicionaisPainel.repaint();
			fiadorIDSalvo = 0;
        	escolherCliente.setText("Escolher");
    		painelDropOut.removeAll();
    		painelDropOut.revalidate();
    		painelDropOut.repaint();
    		painelDropIn.removeAll();
    		painelDropIn.revalidate();
    		painelDropIn.repaint();    		
    		campoRecibo.setText("### Nenhum produto marcado ###");
    		addProduto.setFocus();
			
			mesaID = mesa;
			divisaoPainel.setTitleAt(0, "Mesa " + (mesaID+1));
			vendaRapida = v;
			
		      SwingUtilities.invokeLater(new Runnable() {  
		    	  public void run() {  
		    		  tabela.setNumRows(0);
		    		  
		  			for(int i = 0; i < vendaRapida.getQuantidadeProdutos(); i++)
					{
						for(int x = 0; x < (vendaRapida.getProduto(i).getQuantidade()-vendaRapida.getProduto(i).getPagos()) ; x++)
						{
							dragAdicionaProduto(vendaRapida.getProduto(i), i);
						}
						
						Vector<Serializable> linha = new Vector<Serializable>();
						linha.add(vendaRapida.getProduto(i).getNome());
						linha.add(vendaRapida.getProduto(i).getQuantidade());
						linha.add(vendaRapida.getProduto(i).getPagos());
						String pegaPreco = String.format("%.2f", (vendaRapida.getProduto(i).getTotalProduto() * vendaRapida.getProduto(i).getQuantidade()));
						pegaPreco.replaceAll(",", ".");
						linha.add(pegaPreco);
						linha.add(vendaRapida.getProduto(i).getAllAdicionais());
						linha.add("Deletar");
						tabela.addRow(linha);							
					}		    		  
		    	  }  
		      });
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
					
					MenuPrincipal.getInstance().setarEnter(finalizarVenda);
				}			
			}
		}
		
		public void setFiado(String fiador, int fiadoID)
		{
			if(fiadoID > 0)
			{
				fiadorIDSalvo = fiadoID;
				escolherCliente.setText(fiador);
			}					
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getItemSelectable() == adicionarDezPorcento)
			{
				if(adicionarDezPorcento.isSelected())
				{
					taxaOpcional = (UtilCoffe.precoToDouble(campoTotal.getText()) * 0.10);
					campoTotal.setText(UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) + taxaOpcional)));
					adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
					atualizarCampoRecibo();
				}
				else
				{
					adicionarDezPorcento.setText("+ 10% Opcional");
					campoTotal.setText(UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) - taxaOpcional)));
					taxaOpcional = 0.0;
					atualizarCampoRecibo();
				}
			}
		}
		
		class VendaMesaProdutoCampo extends JPanel implements ActionListener
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private JComboBox<String> combo;
			private JTextField tf;
		    private Vector<String> v;
		    
		    public VendaMesaProdutoCampo(CacheTodosProdutos produtos)
		    {
		        super(new BorderLayout());
		        combo = new JComboBox<String>();
		        tf = (JTextField) combo.getEditor().getEditorComponent();
		        v = new Vector<String>();
		        combo.setEditable(true);
		        combo.addActionListener(this);
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
		                        	setModel(new DefaultComboBoxModel<String>(v), "");
		                        }
		                        else
		                        {
		                        	DefaultComboBoxModel<String> m = getSuggestedModel(v, text);
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
		        				//v.addElement(text);
		        				//Collections.sort(v);
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
		        
				v.removeAllElements();
				
				for(int i = 0; i < produtos.getProdutos().size(); i++)
				{
					v.addElement(produtos.getProdutos().get(i).getNome());
				}
				
				//setModel(new DefaultComboBoxModel<String>(v), "");
		        JPanel p = new JPanel(new BorderLayout());
		        p.setPreferredSize(new Dimension(300, 50));
		        combo.setPreferredSize(new Dimension(300, 40));
		        p.add(combo, BorderLayout.NORTH);
		        add(p);
		        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		        setPreferredSize(new Dimension(300, 50));
		    }
		    	
		    private boolean hide_flag = false;
		    private void setModel(DefaultComboBoxModel<String> mdl, String str)
		    {
				combo.setModel(mdl);
				combo.setSelectedIndex(-1);
				tf.setText(str);   	
		    }
		    
		    private DefaultComboBoxModel<String> getSuggestedModel(java.util.List<String> list, String text)
		    {
		    	DefaultComboBoxModel<String> m = new DefaultComboBoxModel<String>();
		    	for(String s: list)
		    	{
		    		if(s.toLowerCase().contains(text)) m.addElement(s);
		    	}
		    	
		    	return m;
		    }
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == combo)
				{
					updateCampo(); 
				}
			}
			
			public void zeraString()
			{
				combo.setSelectedIndex(-1);
				tf.setText("");   
			}	
			
			public String getSelecionado()
			{
				if(combo.getSelectedIndex() == -1)
					return null;
				
				return combo.getSelectedItem().toString();
			}
			
			public void setFocus()
			{
				combo.requestFocus();
			}
			
			public void atualizaProdutosCampo(CacheTodosProdutos produtos)
			{
				v.removeAllElements();
				
				for(int i = 0; i < produtos.getProdutos().size(); i++)
				{
					v.addElement(produtos.getProdutos().get(i).getNome());
				}
				
				setModel(new DefaultComboBoxModel<String>(v), "");
			}
		}
		
		class VendaMesaAdicionaisCampo extends JPanel implements ActionListener
		{
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private JComboBox<String> combo;
			private JTextField tf;
		    private Vector<String> v;
		    
		    public VendaMesaAdicionaisCampo(CacheTodosProdutos produtos)
		    {
		        super(new BorderLayout());
		        combo = new JComboBox<String>();
		        v = new Vector<String>();
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
		                        	setModel(new DefaultComboBoxModel<String>(v), "");
		                        }
		                        else
		                        {
		                        	DefaultComboBoxModel<String> m = getSuggestedModel(v, text);
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
		        				//v.addElement(text);
		        				//Collections.sort(v);
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
		        
				v.removeAllElements();
				
				for(int i = 0; i < produtos.getAdicionais().size(); i++)
				{
					v.addElement(produtos.getAdicionais().get(i).getNome());
				}
		        
		        setModel(new DefaultComboBoxModel<String>(v), "");
		        JPanel p = new JPanel(new BorderLayout());
		        p.setPreferredSize(new Dimension(220, 45));
		        combo.setPreferredSize(new Dimension(220, 35));
		        p.add(combo, BorderLayout.NORTH);
		        add(p);
		        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		        setPreferredSize(new Dimension(220, 45));
		    }
		    	
		    private boolean hide_flag = false;
		    private void setModel(DefaultComboBoxModel<String> mdl, String str)
		    {
				combo.setModel(mdl);
				combo.setSelectedIndex(-1);
				tf.setText(str);   	
		    }
		    
		    private DefaultComboBoxModel<String> getSuggestedModel(java.util.List<String> list, String text)
		    {
		    	DefaultComboBoxModel<String> m = new DefaultComboBoxModel<String>();
		    	for(String s: list)
		    	{
		    		if(s.toLowerCase().contains(text)) m.addElement(s);
		    	}
		    	
		    	return m;
		    }
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == combo)
				{
					updateCampo();
				}
			}
			
			public String getSelecionado()
			{
				if(combo.getSelectedIndex() == -1)
					return null;		
				
				return combo.getSelectedItem().toString();
			}
			
			public void setFocus()
			{
				combo.requestFocus();
			}
			
			public void atualizaProdutosCampo(CacheTodosProdutos produtos)
			{
				v.removeAllElements();
				
				for(int i = 0; i < produtos.getAdicionais().size(); i++)
				{
					v.addElement(produtos.getAdicionais().get(i).getNome());
				}
				
				setModel(new DefaultComboBoxModel<String>(v), "");
			}
		}		
}