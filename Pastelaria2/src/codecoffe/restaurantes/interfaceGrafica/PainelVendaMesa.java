package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import codecoffe.restaurantes.primitivas.ProdutoVenda;
import codecoffe.restaurantes.primitivas.Venda;
import codecoffe.restaurantes.produtos.ProdutosComboBox;
import codecoffe.restaurantes.sockets.CacheAviso;
import codecoffe.restaurantes.sockets.CacheMesaHeader;
import codecoffe.restaurantes.sockets.CacheTodosProdutos;
import codecoffe.restaurantes.sockets.CacheVendaFeita;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.extended.painter.DashedBorderPainter;
import com.alee.extended.painter.TitledBorderPainter;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.*;
import java.io.IOException;
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
	private JLabel labelQuantidade, labelValor, labelTotal, labelRecebido, labelTroco, labelForma, labelCliente;
	private JTabbedPane divisaoPainel;
	private JButton calcular;
	private DefaultTableModel tabela;
	private JTable tabelaPedido;
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
	private WebButton adicionarProduto, finalizarVenda, imprimir, flecha1, flecha2, escolherCliente, deletarCliente;
	private JEditorPane campoRecibo;
	private WebComboBox campoForma;
	private DragPanel painelDropIn, painelDropOut;
	private int mesaID = 0;
	private ImageIcon iconeFinalizar;
	private JCheckBox adicionarDezPorcento;
	private double taxaOpcional;
	private CacheTodosProdutos todosProdutos;
	private CacheAviso aviso;

	@SuppressWarnings("rawtypes")
	private PainelVendaMesa()
	{		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		iconeFinalizar = new ImageIcon(getClass().getClassLoader().getResource("imgs/finalizar.png"));
		divisaoPainel = new JTabbedPane();
		divisaoPainel.setFocusable(false);

		painelProdutos = new JPanel(new MigLayout("align center", "[]15[]15[]15[]15[]15[]15[]"));
		painelProdutos.setMinimumSize(new Dimension(1020, 260));
		painelProdutos.setMaximumSize(new Dimension(1920, 450));

		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.insets = new Insets(5,5,5,5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

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
		campoValor.setPreferredSize(new Dimension(85, 35));
		campoQuantidade = new JTextField("1");
		campoQuantidade.setHorizontalAlignment(SwingConstants.CENTER);
		campoQuantidade.setPreferredSize(new Dimension(40, 35));
		addProduto = new ProdutosComboBox();
		addProduto.setPreferredSize(new Dimension(350, 110));
		addProduto.addActionListener(this);

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

		adicionaisPainel1 = new WebPanel();
		adicionaisPainel1.setLayout(new GridBagLayout());

		adicionaisPainel = new WebPanel();
		adicionaisPainel.setLayout(new GridBagLayout());
		DashedBorderPainter<JComponent> bp4 = new DashedBorderPainter<>(new float[]{ 6f, 10f });
		bp4.setRound(2);
		bp4.setWidth(4);
		bp4.setColor(new Color( 205, 205, 205 ));
		
		painelProdutos.add(addProduto, "cell 1 0, span 4");
		painelProdutos.add(labelQuantidade, "cell 1 1, gapleft 15px");
		painelProdutos.add(campoQuantidade, "cell 2 1");		
		painelProdutos.add(labelValor, "cell 3 1, gapleft 20px");		
		painelProdutos.add(campoValor, "cell 4 1");
		painelProdutos.add(campoComentario, "cell 1 2, gapleft 10px, gaptop 10px, span 4");

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
				if(column == 0 || column == 7)
					return true;

				return false;
			}
		};

		tabela.addColumn("+/-");
		tabela.addColumn("Nome");
		tabela.addColumn("Qntd");
		tabela.addColumn("Pago");
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
		tabelaPedido.getColumnModel().getColumn(1).setMinWidth(180);
		tabelaPedido.getColumnModel().getColumn(1).setMaxWidth(500);
		tabelaPedido.getColumnModel().getColumn(2).setMinWidth(45);
		tabelaPedido.getColumnModel().getColumn(2).setMaxWidth(100);
		tabelaPedido.getColumnModel().getColumn(3).setMinWidth(80);
		tabelaPedido.getColumnModel().getColumn(3).setMaxWidth(200);				
		tabelaPedido.getColumnModel().getColumn(4).setMinWidth(80);
		tabelaPedido.getColumnModel().getColumn(4).setMaxWidth(200);
		tabelaPedido.getColumnModel().getColumn(5).setMinWidth(200);
		tabelaPedido.getColumnModel().getColumn(5).setMaxWidth(700);
		tabelaPedido.getColumnModel().getColumn(6).setMinWidth(200);
		tabelaPedido.getColumnModel().getColumn(6).setMaxWidth(700);
		tabelaPedido.getColumnModel().getColumn(7).setMinWidth(60);
		tabelaPedido.getColumnModel().getColumn(7).setMaxWidth(65);
		tabelaPedido.setRowHeight(30);
		tabelaPedido.getTableHeader().setReorderingAllowed(false);

		tabelaPedido.getColumn("+/-").setCellRenderer(new OpcoesCell());
		tabelaPedido.getColumn("+/-").setCellEditor(new OpcoesCell());		
		tabelaPedido.getColumn("Preço").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Qntd").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Pago").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Nome").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Adicionais").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Comentário").setCellRenderer(new CustomRenderer());
		tabelaPedido.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaPedido.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));	
		tabelaPedido.setPreferredScrollableViewportSize(new Dimension(800, 200));
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
		painelDropIn1.setPainter ( new TitledBorderPainter ( Configuracao.INSTANCE.getTipoNome() ) );
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

		if(Configuracao.INSTANCE.getTipoPrograma() == UtilCoffe.TIPO_MESA)
		{
			divisaoPainel.addTab(Configuracao.INSTANCE.getTipoNome(), 
					new ImageIcon(getClass().getClassLoader().getResource("imgs/mesa_mini.png")), painelProdutos1, "Gerenciar o pedido da mesa.");	
		}
		else
		{
			divisaoPainel.addTab(Configuracao.INSTANCE.getTipoNome(), 
					new ImageIcon(getClass().getClassLoader().getResource("imgs/comanda_24.png")), painelProdutos1, "Gerenciar o pedido da comanda.");		
		}
				
		divisaoPainel.addTab("Pagamento", new ImageIcon(getClass().getClassLoader().getResource("imgs/recibo_mini.png")), painelPagamento, "Pagamento do Pedido.");		
		add(divisaoPainel);

		fiadorIDSalvo = 0;		
		taxaOpcional = 0.0;
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

	private static class VendaMesaSingletonHolder { 
		public static final PainelVendaMesa INSTANCE = new PainelVendaMesa();
	}

	public static PainelVendaMesa getInstance() {
		return VendaMesaSingletonHolder.INSTANCE;
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
			localObjectFlavor = new ActivationDataFlavor(DragPanel.class, DataFlavor.javaJVMLocalObjectMimeType, "DragLabel");
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
				l.setProduto(UtilCoffe.cloneProdutoVenda(src.draggingLabel.getProduto()));
				
				l.setIcon(src.draggingLabel.getIcon());
				l.setText(src.draggingLabel.getText());
				l.setFont(src.draggingLabel.getFont());

				target.add(l);
				target.revalidate();		      
				return true;
			} catch(UnsupportedFlavorException ufe) {
				ufe.printStackTrace();
				new PainelErro(ufe);
			} catch(java.io.IOException ioe) {
				ioe.printStackTrace();
				new PainelErro(ioe);
			}
			return false;
		}
		@Override protected void exportDone(JComponent c, Transferable data, int action) {
			DragPanel src = (DragPanel)c;
			if(action == TransferHandler.MOVE) 
			{				
				src.remove(src.draggingLabel);
				src.revalidate();
				src.repaint();

				double total = 0.0;
				for(int i = 0; i < painelDropOut.getComponentCount(); i++)
				{
					DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
					total += dragL.getProduto().getTotalProduto();
				}

				if(Configuracao.INSTANCE.getDezPorcento())
				{
					taxaOpcional = total * 0.10;
					adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
				}

				if(adicionarDezPorcento.isSelected())
					campoTotal.setText(UtilCoffe.doubleToPreco((total + (total * 0.10))));
				else
					campoTotal.setText(UtilCoffe.doubleToPreco(total));

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
					
					campoComentario.setText("");
					campoValor.setText("");
					adicionarDezPorcento.setSelected(false);	
					campoQuantidade.setText("1");
					campoTotal.setText("0,00");
					campoRecebido.setText("");
					campoTroco.setText("0,00");
					campoForma.setSelectedIndex(0);
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
					adicionarDezPorcento.setText("+ 10% Opcional");
					campoRecibo.setText("### Nenhum produto marcado ###");
					PainelMesas.getInstance().atualizaMesa(mesaID, vendaRapida);
					termina(false);					
				}
				else
				{
					JOptionPane.showMessageDialog(null, aviso.getMensagem(), aviso.getTitulo(), JOptionPane.ERROR_MESSAGE);
				}				
			}
		});
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

		Venda vendaAgora = new Venda();

		for(int i = 0; i < painelDropOut.getComponentCount(); i++)
		{
			DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
			vendaAgora.adicionarProduto(UtilCoffe.cloneProdutoVenda(dragL.getProduto()));
		}

		vendaAgora.calculaTotal();

		for(int i = 0; i < vendaAgora.getQuantidadeProdutos(); i++)
		{
			formataRecibo += (String.format("%-20.20s", vendaAgora.getProduto(i).getReferencia()));
			formataRecibo += (String.format("%4s     ", vendaAgora.getProduto(i).getQuantidade()));

			double totalsub = 0.0;

			for(int j = 0; j < vendaAgora.getProduto(i).getTotalAdicionais(); j++)
			{
				totalsub += vendaAgora.getProduto(i).getAdicional(j).getPreco();
			}

			formataRecibo += (String.format("%7s       ", UtilCoffe.doubleToPreco((vendaAgora.getProduto(i).getPreco() - totalsub))));
			formataRecibo += (String.format("%6s     \n", UtilCoffe.doubleToPreco((vendaAgora.getProduto(i).getPreco() - totalsub) * vendaAgora.getProduto(i).getQuantidade())));

			for(int j = 0; j < vendaAgora.getProduto(i).getTotalAdicionais(); j++)
			{
				formataRecibo += (String.format("%-20.20s", "+" + vendaAgora.getProduto(i).getAdicional(j).getReferencia()));
				formataRecibo += (String.format("%3s     ", vendaAgora.getProduto(i).getQuantidade()));
				formataRecibo += (String.format("%5s    ", UtilCoffe.doubleToPreco(vendaAgora.getProduto(i).getAdicional(j).getPreco())));
				formataRecibo += (String.format("%6s    \n", UtilCoffe.doubleToPreco((vendaAgora.getProduto(i).getAdicional(j).getPreco()*vendaAgora.getProduto(i).getQuantidade()))));
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
		formataRecibo += ("Total                            R$" + UtilCoffe.doubleToPreco(vendaAgora.getTotal()) + "\n");

		if(Configuracao.INSTANCE.getDezPorcento())
		{
			formataRecibo += ("                     ----------------------\n");
			formataRecibo += ("10% Opcional                     R$" + UtilCoffe.doubleToPreco(vendaAgora.getTotal() + taxaOpcional) + "\n");            	  
		}		

		formataRecibo += ("===========================\n");
		formataRecibo += Configuracao.INSTANCE.getMensagemInferior() + "\n";
		formataRecibo += ("       Sistema CodeCoffe V2.0    		  \n");

		campoRecibo.setText(formataRecibo);		
	}	

	private void criarRecibo()
	{
		Venda vendaAgora = new Venda();

		for(int i = 0; i < painelDropOut.getComponentCount(); i++)
		{
			DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
			vendaAgora.adicionarProduto(UtilCoffe.cloneProdutoVenda(dragL.getProduto()));
		}

		vendaAgora.calculaTotal();

		CacheVendaFeita criaImpressao		= new CacheVendaFeita(vendaAgora);
		criaImpressao.total 				= UtilCoffe.doubleToPreco(vendaAgora.getTotal());
		criaImpressao.atendente 			= Usuario.INSTANCE.getNome();
		criaImpressao.fiado_id 				= fiadorIDSalvo;
		criaImpressao.caixa 				= (mesaID+1);
		criaImpressao.delivery 				= "0,00";
		criaImpressao.dezporcento			= UtilCoffe.doubleToPreco(vendaAgora.getTotal() + taxaOpcional);
		criaImpressao.classe				= 1;
		criaImpressao.imprimir 				= true;

		Bartender.INSTANCE.criarImpressao(criaImpressao);		
	}

	private class DragLabel extends JLabel 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ProdutoVenda produto;

		public DragLabel() {
			super();
		}

		public ProdutoVenda getProduto() {
			return produto;
		}

		public void setProduto(ProdutoVenda produto) {
			this.produto = produto;
		}
	}

	private void dragAdicionaProduto(ProdutoVenda p)
	{
		int cacheQuantidade = (p.getQuantidade() - p.getPagos());

		for(int i = 0; i < cacheQuantidade; i++)
		{
			DragLabel dragP = new DragLabel();
			dragP.setProduto(UtilCoffe.cloneProdutoVenda(p));
			dragP.getProduto().setQuantidade(1, 0);
			dragP.getProduto().calcularPreco();

			dragP.setFont(new Font("Verdana", Font.PLAIN, 10));
			dragP.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));

			if(dragP.getProduto().getAdicionaisList().size() > 0)
				dragP.setText(dragP.getProduto().getNome() + " com " + dragP.getProduto().getAllAdicionais() 
						+ " - " + UtilCoffe.doubleToPreco(dragP.getProduto().getTotalProduto()));
			else
				dragP.setText(dragP.getProduto().getNome() + " - " + UtilCoffe.doubleToPreco(dragP.getProduto().getPreco()));

			painelDropIn.add(dragP);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				painelDropIn.revalidate();
				painelDropIn.repaint();
			}
		});
	}

	public void termina(boolean delete)
	{
		CacheMesaHeader mh = new CacheMesaHeader(mesaID , vendaRapida, UtilCoffe.MESA_LIMPAR);
		Bartender.INSTANCE.enviarMesa(mh, 0);
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
					total += dragL.getProduto().getTotalProduto();
				}

				if(Configuracao.INSTANCE.getDezPorcento())
				{
					taxaOpcional = total * 0.10;
					adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
				}				

				if(adicionarDezPorcento.isSelected())
					campoTotal.setText(UtilCoffe.doubleToPreco((total + (total * 0.10))));
				else
					campoTotal.setText(UtilCoffe.doubleToPreco(total));

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						atualizarCampoRecibo();
						painelDropOut.revalidate();
						painelDropOut.repaint();
						painelDropIn.revalidate();
						painelDropIn.repaint();
					}
				});
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
					total += dragL.getProduto().getTotalProduto();
				}

				if(Configuracao.INSTANCE.getDezPorcento())
				{
					taxaOpcional = total * 0.10;
					adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
				}

				if(adicionarDezPorcento.isSelected())
					campoTotal.setText(UtilCoffe.doubleToPreco((total + (total * 0.10))));
				else
					campoTotal.setText(UtilCoffe.doubleToPreco(total));

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						atualizarCampoRecibo();
						painelDropOut.revalidate();
						painelDropOut.repaint();
						painelDropIn.revalidate();
						painelDropIn.repaint();
					}
				});
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
								vendaAgora.adicionarProduto(UtilCoffe.cloneProdutoVenda(dragL.getProduto()));
							}

							Calendar c = Calendar.getInstance();
							Locale locale = new Locale("pt","BR"); 
							GregorianCalendar calendar = new GregorianCalendar(); 
							SimpleDateFormat formatador = new SimpleDateFormat("dd'/'MM'/'yyyy' - 'HH':'mm", locale);

							for(int i = 0; i < vendaAgora.getQuantidadeProdutos(); i++)
							{
								for(int x = 0; x < tabela.getRowCount(); x++)
								{
									String pega1 = tabela.getValueAt(x, 1).toString();	//f
									String pega2 = tabela.getValueAt(x, 5).toString();	//f

									if(pega1.equals(vendaAgora.getProduto(i).getNome()) && pega2.equals(vendaAgora.getProduto(i).getAllAdicionais()))
									{
										vendaRapida.getProduto(x).setPagos(vendaAgora.getProduto(i).getQuantidade());
										tabela.setValueAt(vendaRapida.getProduto(x).getPagos(), x, 3);	//f
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

							if(adicionarDezPorcento.isSelected())
								vendaMesaFeita.dezporcento			= UtilCoffe.doubleToPreco(taxaOpcional);
							else
								vendaMesaFeita.dezporcento			= UtilCoffe.doubleToPreco(0.0);

							vendaMesaFeita.classe				= UtilCoffe.CLASSE_VENDA_MESA;
							Bartender.INSTANCE.enviarVenda(vendaMesaFeita, 0);	// agora aguarda a resposta.
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
				ProdutoVenda produto = new ProdutoVenda(addProduto.getProdutoSelecionado().getNome(), 
						addProduto.getProdutoSelecionado().getReferencia(), 
						addProduto.getProdutoSelecionado().getPreco(), 
						addProduto.getProdutoSelecionado().getIdUnico(), 
						addProduto.getProdutoSelecionado().getCodigo());

				if(!UtilCoffe.vaziu(campoComentario.getText()))
					produto.setComentario(campoComentario.getText());
				
				if(addAdicional.size() > 0)
				{
					for(int x = 0 ; x < addAdicional.size() ; x++)
					{
						produto.adicionrAdc(UtilCoffe.cloneProduto(addAdicional.get(x).getProdutoSelecionado()));
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
						dragAdicionaProduto(produto);

						if(sizeAntes == vendaRapida.getQuantidadeProdutos())
						{
							double total = vendaRapida.getProduto(ultimaIndex).getTotalProduto()*vendaRapida.getProduto(ultimaIndex).getQuantidade();
							tabela.setValueAt(UtilCoffe.doubleToPreco(total), ultimaIndex, 4);
							tabela.setValueAt(vendaRapida.getProduto(ultimaIndex).getQuantidade(), ultimaIndex, 2);						

							CacheMesaHeader mh = new CacheMesaHeader(mesaID, produto, vendaRapida, UtilCoffe.MESA_ATUALIZAR, Integer.parseInt(limpeza), Usuario.INSTANCE.getNome());
							Bartender.INSTANCE.enviarMesa(mh, 0);
						}
						else
						{
							Vector<Serializable> newLinha = new Vector<Serializable>();
							newLinha.add("");
							newLinha.add(produto.getNome());
							newLinha.add(produto.getQuantidade());
							newLinha.add("0");						
							newLinha.add(UtilCoffe.doubleToPreco((produto.getTotalProduto() * Integer.parseInt(limpeza))));
							newLinha.add(produto.getAllAdicionais());
							newLinha.add(produto.getComentario());
							newLinha.add("Deletar");
							tabela.addRow(newLinha);

							CacheMesaHeader mh = new CacheMesaHeader(mesaID, produto, vendaRapida, UtilCoffe.MESA_ADICIONAR, Integer.parseInt(limpeza), Usuario.INSTANCE.getNome());
							Bartender.INSTANCE.enviarMesa(mh, 0);
						}
					}
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						campoValor.setText("");
						campoQuantidade.setText("1");
						campoComentario.setText("");
						addAdicional.clear();
						addRemover.clear();
						adicionaisPainel.removeAll();
						adicionaisPainel.revalidate();
						adicionaisPainel.repaint();
						addProduto.requestFocus();
						PainelMesas.getInstance().atualizaMesa(mesaID, vendaRapida);
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

		public Object getCellEditorValue() 
		{
			if (isPushed) {
				if(vendaRapida.getQuantidadeProdutos() <= 0)
				{
					tabela.setRowCount(0);
					isPushed = false;
					return new String(label);		    		
				}
				if(tabelaPedido.getSelectedRowCount() == 1)
				{
					boolean continua = true;
					String pegaLixo = tabela.getValueAt(tabelaPedido.getSelectedRow(), 2).toString();
					int quantidadeDeletar = Integer.parseInt(pegaLixo);
					int fazendo = PainelCozinha.getInstance().verificaStatusPedido((mesaID+1), vendaRapida.getProduto(tabelaPedido.getSelectedRow()));

					if(fazendo > 0)
					{
						int opcao = JOptionPane.showConfirmDialog(null, "Esses produtos já estão marcados como Fazendo na cozinha."
								+ "\n\nVocê tem certeza que quer deletar?\n\n", "Deletar Produto", JOptionPane.YES_NO_OPTION);
						if(opcao != JOptionPane.YES_OPTION)
							continua = false;
					}

					if(continua)
					{
						for(int i = 0; i < painelDropOut.getComponentCount(); i++)
						{	  
							DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
							if(dragL.getProduto().compareTo(vendaRapida.getProduto(tabelaPedido.getSelectedRow())))
							{
								painelDropOut.remove(i);
								quantidadeDeletar--;
								i = 0;

								if(quantidadeDeletar <= 0)
									break;		
							}
						}

						if(quantidadeDeletar > 0 && painelDropOut.getComponentCount() > 0)
						{
							DragLabel dragL = (DragLabel)painelDropOut.getComponent(0);
							if(dragL.getProduto().compareTo(vendaRapida.getProduto(tabelaPedido.getSelectedRow())))
							{
								painelDropOut.remove(0);
								quantidadeDeletar--;
							}    		  
						}

						if(quantidadeDeletar > 0)
						{
							for(int i = 0; i < painelDropIn.getComponentCount(); i++)
							{ 
								DragLabel dragL = (DragLabel)painelDropIn.getComponent(i);
								if(dragL.getProduto().compareTo(vendaRapida.getProduto(tabelaPedido.getSelectedRow())))
								{
									painelDropIn.remove(i);
									quantidadeDeletar--;
									i = 0;

									if(quantidadeDeletar <= 0)
										break;		
								}
							}		    		  
						}

						if(quantidadeDeletar > 0 && painelDropIn.getComponentCount() > 0)
						{
							DragLabel dragL = (DragLabel)painelDropIn.getComponent(0);
							if(dragL.getProduto().compareTo(vendaRapida.getProduto(tabelaPedido.getSelectedRow())))
							{
								painelDropIn.remove(0);
								quantidadeDeletar--;
							} 	    		  
						}
						
						if(tabelaPedido.getSelectedRow() >= 0 && tabelaPedido.getSelectedRowCount() == 1) 
						{
							ProdutoVenda prod = UtilCoffe.cloneProdutoVenda(vendaRapida.getProduto(tabelaPedido.getSelectedRow()));
							vendaRapida.removerProdutoIndex(tabelaPedido.getSelectedRow());
							vendaRapida.calculaTotal();
							PainelMesas.getInstance().atualizaMesa(mesaID, vendaRapida);

							CacheMesaHeader mh = new CacheMesaHeader(mesaID, prod, vendaRapida, UtilCoffe.MESA_DELETAR, prod.getQuantidade());
							Bartender.INSTANCE.enviarMesa(mh, 0);
						}

						double total = 0.0;
						for(int i = 0; i < painelDropOut.getComponentCount(); i++)
						{
							DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
							total += dragL.getProduto().getTotalProduto();
						}

						if(Configuracao.INSTANCE.getDezPorcento())
						{
							taxaOpcional = total * 0.10;
							adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
						}

						if(adicionarDezPorcento.isSelected())
							campoTotal.setText(UtilCoffe.doubleToPreco((total + (total * 0.10))));
						else
							campoTotal.setText(UtilCoffe.doubleToPreco(total));

						atualizarCampoRecibo();

						if(tabela.getRowCount() == 1)
						{
							SwingUtilities.invokeLater(new Runnable() {  
								public void run() {  
									tabela.setNumRows(0);
									painelDropOut.revalidate();
									painelDropOut.repaint();
									painelDropIn.revalidate();
									painelDropIn.repaint();
								}  
							});   
						}
						else
						{
							SwingUtilities.invokeLater(new Runnable() {  
								public void run() {  
									tabela.removeRow(tabelaPedido.getSelectedRow());
									painelDropOut.revalidate();
									painelDropOut.repaint();
									painelDropIn.revalidate();
									painelDropIn.repaint();
								}  
							});
						}	
					}
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
		mesaID = mesa;
		vendaRapida = v;

		SwingUtilities.invokeLater(new Runnable() {  
			public void run() {
				adicionarDezPorcento.setSelected(false);
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
				fiadorIDSalvo = 0;
				escolherCliente.setText("Escolher");
				painelDropOut.removeAll();
				painelDropOut.revalidate();
				painelDropOut.repaint();
				painelDropIn.removeAll();
				painelDropIn.revalidate();
				painelDropIn.repaint();    		
				campoRecibo.setText("### Nenhum produto marcado ###");
				addProduto.requestFocus();
				taxaOpcional = 0.0;
				adicionarDezPorcento.setText("+ 10% Opcional");

				divisaoPainel.setTitleAt(0, Configuracao.INSTANCE.getTipoNome() + " " + (mesaID+1));
				tabela.setNumRows(0);

				for(int i = 0; i < vendaRapida.getQuantidadeProdutos(); i++)
				{
					dragAdicionaProduto(vendaRapida.getProduto(i));

					Vector<Serializable> linha = new Vector<Serializable>();
					linha.add("");
					linha.add(vendaRapida.getProduto(i).getNome());
					linha.add(vendaRapida.getProduto(i).getQuantidade());
					linha.add(vendaRapida.getProduto(i).getPagos());
					linha.add(UtilCoffe.doubleToPreco((vendaRapida.getProduto(i).getTotalProduto() * vendaRapida.getProduto(i).getQuantidade())));
					linha.add(vendaRapida.getProduto(i).getAllAdicionais());
					linha.add("Deletar");
					tabela.addRow(linha);							
				}		    		  
			}  
		});
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == campoRecebido) {
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

				PainelPrincipal.getInstance().setarEnter(finalizarVenda);
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
			}
			else
			{
				campoTotal.setText(UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(campoTotal.getText()) - taxaOpcional)));
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
						tabela.setValueAt(UtilCoffe.doubleToPreco(total), linha, 4);
						tabela.setValueAt(vendaRapida.getProduto(linha).getQuantidade(), linha, 2);
						
						ProdutoVenda maisUm = UtilCoffe.cloneProdutoVenda(vendaRapida.getProduto(linha));
						maisUm.setQuantidade(1, 0);
						dragAdicionaProduto(maisUm);
						
						PainelMesas.getInstance().atualizaMesa(mesaID, vendaRapida);
						CacheMesaHeader mh = new CacheMesaHeader(mesaID, maisUm, vendaRapida, UtilCoffe.MESA_ATUALIZAR, 1);
						Bartender.INSTANCE.enviarMesa(mh, 0);
					}
					else
					{
						boolean continua = true;
						int fazendo = PainelCozinha.getInstance().verificaStatusPedido((mesaID+1), vendaRapida.getProduto(linha));

						if(fazendo >= vendaRapida.getProduto(linha).getQuantidade())
						{
							int opcao = JOptionPane.showConfirmDialog(null, "Esse produto já está marcado como Fazendo na cozinha."
									+ "\n\nVocê tem certeza que quer deletar?\n\n", "Deletar Produto", JOptionPane.YES_NO_OPTION);
							if(opcao != JOptionPane.YES_OPTION)
								continua = false;
						}

						if(continua)
						{
							boolean deletar_all = true;
							if(vendaRapida.getProduto(linha).getQuantidade() > 1)
							{
								deletar_all = false;
							}

							int quantidadeDeletar = 1;
							for(int i = 0; i < painelDropOut.getComponentCount(); i++)
							{	  
								DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
								if(dragL.getProduto().compareTo(vendaRapida.getProduto(linha)))
								{
									painelDropOut.remove(i);
									quantidadeDeletar = 0;
									break;
								}
							}

							if(quantidadeDeletar > 0 && painelDropOut.getComponentCount() > 0)
							{
								DragLabel dragL = (DragLabel)painelDropOut.getComponent(0);
								if(dragL.getProduto().compareTo(vendaRapida.getProduto(linha)))
								{
									painelDropOut.remove(0);
									quantidadeDeletar = 0;
								}	    		  
							}

							if(quantidadeDeletar > 0)
							{
								for(int i = 0; i < painelDropIn.getComponentCount(); i++)
								{ 
									DragLabel dragL = (DragLabel)painelDropIn.getComponent(i);
									if(dragL.getProduto().compareTo(vendaRapida.getProduto(linha)))
									{
										painelDropIn.remove(i);
										quantidadeDeletar = 0;
										break;
									}
								}		    		  
							}

							if(quantidadeDeletar > 0 && painelDropIn.getComponentCount() > 0)
							{
								DragLabel dragL = (DragLabel)painelDropIn.getComponent(0);
								if(dragL.getProduto().compareTo(vendaRapida.getProduto(linha)))
								{
									painelDropIn.remove(0);
									quantidadeDeletar = 0;
								}	    		  
							}
							
							SwingUtilities.invokeLater(new Runnable() {  
								public void run() {  
									painelDropOut.revalidate();
									painelDropOut.repaint();
									painelDropIn.revalidate();
									painelDropIn.repaint();
								}  
							});  

							if(deletar_all)
							{
								ProdutoVenda pv = UtilCoffe.cloneProdutoVenda(vendaRapida.getProduto(linha));
								vendaRapida.removerProdutoIndex(linha);
								vendaRapida.calculaTotal();
								PainelMesas.getInstance().atualizaMesa(mesaID, vendaRapida);

								CacheMesaHeader mh = new CacheMesaHeader(mesaID, pv, vendaRapida, UtilCoffe.MESA_DELETAR, 1);
								Bartender.INSTANCE.enviarMesa(mh, 0);

								if(tabela.getRowCount() == 1)
								{
									SwingUtilities.invokeLater(new Runnable() {  
										public void run() {  
											tabela.setNumRows(0);
										}  
									});   
								}
								else
								{
									SwingUtilities.invokeLater(new Runnable() {  
										public void run() {  
											tabela.removeRow(linha);
										}  
									});
								}
							}
							else
							{
								ProdutoVenda prod = UtilCoffe.cloneProdutoVenda(vendaRapida.getProduto(linha));
								prod.setQuantidade(1, 0);
								
								vendaRapida.getProduto(linha).setQuantidade(1, 2);
								double total = vendaRapida.getProduto(linha).getTotalProduto()*vendaRapida.getProduto(linha).getQuantidade();
								tabela.setValueAt(UtilCoffe.doubleToPreco(total), linha, 4);
								tabela.setValueAt(vendaRapida.getProduto(linha).getQuantidade(), linha, 2);
								vendaRapida.calculaTotal();
								PainelMesas.getInstance().atualizaMesa(mesaID, vendaRapida);

								CacheMesaHeader mh = new CacheMesaHeader(mesaID, prod, vendaRapida, UtilCoffe.MESA_ATUALIZAR, -1);
								Bartender.INSTANCE.enviarMesa(mh, 0);
							}

							double total = 0.0;
							for(int i = 0; i < painelDropOut.getComponentCount(); i++)
							{
								DragLabel dragL = (DragLabel)painelDropOut.getComponent(i);
								total += dragL.getProduto().getTotalProduto();
							}
							
							if(Configuracao.INSTANCE.getDezPorcento())
							{
								taxaOpcional = total * 0.10;
								adicionarDezPorcento.setText("+ 10% Opcional (R$" + UtilCoffe.doubleToPreco(taxaOpcional) + ")");
							}

							if(adicionarDezPorcento.isSelected())
								campoTotal.setText(UtilCoffe.doubleToPreco((total + (total * 0.10))));
							else
								campoTotal.setText(UtilCoffe.doubleToPreco(total));

							atualizarCampoRecibo();							
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

		public OpcoesCell() {
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
}