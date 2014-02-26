package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Clientes;
import codecoffe.restaurantes.sockets.CacheAviso;
import codecoffe.restaurantes.sockets.CacheClientes;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;
import codecoffe.restaurantes.utilitarios.buscaCEP;

import com.alee.extended.image.WebImage;
import com.alee.extended.painter.DashedBorderPainter;
import com.alee.laf.button.WebButton;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;

import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

public class PainelClientes extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JPanel painelClientes,verClientes;
	private JLabel labelNome, labelApelido, labelTelefone, labelCPF, labelCEP, labelEndereco, labelNumero, labelBairro, labelComplemento, labelDivida, labelLoad;
	private JTextField campoNome, campoApelido, campoTelefone, campoCPF, campoEndereco, campoNumero, campoBairro, campoComplemento;
	private WebTextField campoBusca, campoCEP;
	private JTabbedPane divisaoPainel;
	private DefaultListModel<ClienteModel> modeloLista;
	private WebButton bSalvarCliente, bNovoCliente, bDeletarCliente, bDivida, bVenda;
	private JTable tabelaUltimasVendas;
	private DefaultTableModel tabela;
	private WebList jlist;
	private JScrollPane scrolltabela;
	private boolean flag_aciona;
	private int callBack = 0;
	private int callBackVisualiza = 0;
	private CacheClientes todosClientes;
	private Clientes clienteSelecionado;
	private ArrayList<VisualizarVenda> vv;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PainelClientes()
	{
		vv = new ArrayList<VisualizarVenda>();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		divisaoPainel = new JTabbedPane();			
		painelClientes = new JPanel();
		painelClientes.setLayout(new MigLayout("fill", "[]20[]10[]10[]20[]"));
		todosClientes = new CacheClientes();
		clienteSelecionado = null;
		
		flag_aciona = true;
		labelNome = new JLabel("Nome:");
		labelApelido = new JLabel("Apelido:");
		labelTelefone = new JLabel("Tel:");
		labelCPF = new JLabel("CPF:");
		labelCEP = new JLabel("CEP:");
		labelEndereco = new JLabel("Endereço:");
		labelNumero = new JLabel("Número:");
		labelBairro = new JLabel("Bairro:");
		labelComplemento = new JLabel("Complemento:");
		labelDivida = new JLabel("Dívida:");
		
		campoBusca = new WebTextField("");
		campoBusca.setToolTipText("Digite qualquer informação do cliente.");
		campoBusca.setInputPrompt("Buscar clientes...");
		campoBusca.setMargin(5, 5, 5, 5);
		campoBusca.setTrailingComponent(new WebImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/buscar.png"))));
		campoBusca.addKeyListener(new KeyAdapter()
        {
        	public void keyPressed(KeyEvent e)
        	{
        		int code = e.getKeyCode();
        		if(code==KeyEvent.VK_ENTER)
        		{
        			if(!UtilCoffe.vaziu(campoBusca.getText()))
        				buscarCliente(campoBusca.getText());
        			else
        				buscarCliente("allclients");
        		}
            }
        });		
		
		campoNome = new JTextField("");
		campoNome.setBorder(BorderFactory.createCompoundBorder(campoNome.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		campoApelido = new JTextField("");
		campoApelido.setBorder(BorderFactory.createCompoundBorder(campoApelido.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		campoTelefone = new JTextField("");
		campoTelefone.setBorder(BorderFactory.createCompoundBorder(campoTelefone.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	
		campoCPF = new JTextField("");
		campoCPF.setBorder(BorderFactory.createCompoundBorder(campoCPF.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));	
		
		campoCEP = new WebTextField("");
		campoCEP.setMargin(5, 5, 5, 5);
		campoCEP.setBorder(BorderFactory.createCompoundBorder(campoCEP.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		campoCEP.setTrailingComponent(new WebImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/buscar.png"))));
		campoCEP.addKeyListener(new KeyAdapter()
        {
        	public void keyPressed(KeyEvent e)
        	{
        		int code = e.getKeyCode();
        		if(code==KeyEvent.VK_ENTER)
        		{
        			if(!UtilCoffe.vaziu(campoCEP.getText()))
        			{
        				SwingUtilities.invokeLater(new Runnable() {
        					@Override
        					public void run() {
                				labelLoad.setVisible(true);
        					}
        				});
        				
        				buscarCEP buscar = new buscarCEP();
        				Thread iniciaBuscaCep = new Thread(buscar);
        				iniciaBuscaCep.start();	
        			}
        			else {
        				JOptionPane.showMessageDialog(null, "Digite o CEP, exemplo: 13040050");
        			}
        		}
            }
        });				
		
		campoEndereco = new JTextField("");
		campoEndereco.setBorder(BorderFactory.createCompoundBorder(campoEndereco.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		campoNumero = new JTextField("");
		campoNumero.setBorder(BorderFactory.createCompoundBorder(campoNumero.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		campoBairro = new JTextField("");
		campoBairro.setBorder(BorderFactory.createCompoundBorder(campoBairro.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		campoComplemento = new JTextField("");
		campoComplemento.setBorder(BorderFactory.createCompoundBorder(campoComplemento.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		verClientes = new JPanel();
		verClientes.setLayout(new BoxLayout(verClientes, BoxLayout.Y_AXIS));
		
		modeloLista = new DefaultListModel<ClienteModel>();		
		jlist = new WebList(modeloLista);
		jlist.setBackground(new Color(205, 205, 205));
		
		jlist.setCellRenderer(new DefaultListCellRenderer() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		        label.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/usuario.png")));
		        
		        ClienteModel cm = (ClienteModel) value;
		        label.setText(cm.nome);
		        
		        if(index % 2 == 0)
		        {
		        	label.setBackground(new Color(206, 220, 249));
		        	label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(205, 205, 205)));
		        }
		        else
		        {
		        	label.setBackground(Color.WHITE);
		        	label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,new Color(205, 205, 205)));
		        }
		        
		        
		        if(isSelected)
		        {
		        	label.setForeground(Color.BLACK);
		        	label.setFont(new Font("Verdana", Font.BOLD, 11));
		        	label.setPreferredSize(new Dimension(1900, 30));
		        }
		        else
		        {
		        	label.setForeground(Color.BLACK);
		        	label.setFont(new Font("Verdana", Font.PLAIN, 11));
		        	label.setPreferredSize(new Dimension(1900, 30));
		        }
		        
		        return label;
		    }
		});
		
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent le) {
				int idx = jlist.getSelectedIndex();
		        if (idx != -1)
		        {
		        	if(flag_aciona)
		        	{
		        		ClienteModel cLista = modeloLista.getElementAt(idx);
		        		receberCliente(todosClientes.getClienteID(cLista.idUnico));
		        		flag_aciona = false;
		        	}
		        	else
		        	{
		        		flag_aciona = true;
		        	}
		        }
		    }
		});
		
		verClientes.add(jlist);
		verClientes.setFocusable(false);
		JScrollPane scrollClientes = new JScrollPane(verClientes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		WebPanel verClientes1 = new WebPanel();
		verClientes1.setFocusable(false);
		scrollClientes.setFocusable(false);
		
        DashedBorderPainter bp4 = new DashedBorderPainter(new float[]{3f, 3f});
        bp4.setWidth(2);
        bp4.setColor(new Color(205, 205, 205));
        verClientes1.setPainter(bp4);
		verClientes1.add(scrollClientes);
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			painelClientes.add(campoBusca, "cell 0 0, grow, w 30%, h 2%, hmax 40px");	
			painelClientes.add(verClientes1, "cell 0 1, span 1 6, grow, w 30%");
			painelClientes.add(labelNome, "cell 1 0");	
			painelClientes.add(campoNome, "cell 2 0, grow, pushx, h 2%, hmax 40px");	
			painelClientes.add(labelApelido, "cell 3 0");
			painelClientes.add(campoApelido, "cell 4 0, grow, pushx, h 2%, hmax 40px");
			painelClientes.add(labelCPF, "cell 1 1");
			painelClientes.add(campoCPF, "cell 2 1, grow, h 2%, hmax 40px");
			painelClientes.add(labelTelefone, "cell 3 1");
			painelClientes.add(campoTelefone, "cell 4 1, grow, h 2%, hmax 40px");
			painelClientes.add(labelCEP, "cell 1 2");
			painelClientes.add(campoCEP, "cell 2 2, grow, h 2%, hmax 40px");
			
			labelLoad = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/loadcep.gif")));
			labelLoad.setVisible(false);
			
			painelClientes.add(labelLoad, "cell 3 2, align left");
			painelClientes.add(labelEndereco, "cell 1 3");	
			painelClientes.add(campoEndereco, "cell 2 3, span 3 1, grow, pushx, h 2%, hmax 40px");			
			painelClientes.add(labelNumero, "cell 1 4");
			painelClientes.add(campoNumero, "cell 2 4, grow, pushx, h 2%, hmax 40px");	
			painelClientes.add(labelBairro, "cell 3 4");
			painelClientes.add(campoBairro, "cell 4 4, grow, pushx, h 2%, hmax 40px");				
			painelClientes.add(labelComplemento, "cell 1 5");
			painelClientes.add(campoComplemento, "cell 2 5, span 3 1, grow, pushx, h 2%, hmax 40px");	
		}
		else
		{
			painelClientes.add(campoBusca, "cell 0 0, grow, w 30%, h 2%, hmax 40px");	
			painelClientes.add(verClientes1, "cell 0 1, span 1 6, grow, w 30%");
			painelClientes.add(labelNome, "cell 1 1");	
			painelClientes.add(campoNome, "cell 2 1, grow, pushx, h 2%, hmax 40px");	
			painelClientes.add(labelApelido, "cell 3 1");
			painelClientes.add(campoApelido, "cell 4 1, grow, pushx, h 2%, hmax 40px");
			painelClientes.add(labelCPF, "cell 1 2");
			painelClientes.add(campoCPF, "cell 2 2, grow, h 2%, hmax 40px");
			painelClientes.add(labelTelefone, "cell 3 2");
			painelClientes.add(campoTelefone, "cell 4 2, grow, h 2%, hmax 40px");
			painelClientes.add(labelCEP, "cell 1 3");
			painelClientes.add(campoCEP, "cell 2 3, grow, h 2%, hmax 40px");
			
			labelLoad = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/loadcep.gif")));
			labelLoad.setVisible(false);
			
			painelClientes.add(labelLoad, "cell 3 3, align left");
			painelClientes.add(labelEndereco, "cell 1 4");	
			painelClientes.add(campoEndereco, "cell 2 4, span 3 1, grow, pushx, h 2%, hmax 40px");			
			painelClientes.add(labelNumero, "cell 1 5");
			painelClientes.add(campoNumero, "cell 2 5, grow, pushx, h 2%, hmax 40px");	
			painelClientes.add(labelBairro, "cell 3 5");
			painelClientes.add(campoBairro, "cell 4 5, grow, pushx, h 2%, hmax 40px");				
			painelClientes.add(labelComplemento, "cell 1 6");
			painelClientes.add(campoComplemento, "cell 2 6, span 3 1, grow, pushx, h 2%, hmax 40px");			
		}
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			tabela = new DefaultTableModel() {
				private static final long serialVersionUID = 1L;

				@Override
			    public boolean isCellEditable(int row, int column) {
			       return false;
			    }
			};
			
			tabela.addColumn("ID");
			tabela.addColumn("Data");
			tabela.addColumn("Total");
			tabela.addColumn("Status");
			tabela.addColumn("Atendente");
			
			tabelaUltimasVendas = new JTable();
			tabelaUltimasVendas.setFocusable(false);
			tabelaUltimasVendas.setModel(tabela);
			tabelaUltimasVendas.getColumnModel().getColumn(0).setMinWidth(0);
			tabelaUltimasVendas.getColumnModel().getColumn(0).setMaxWidth(0);
			tabelaUltimasVendas.getColumnModel().getColumn(1).setMinWidth(150);
			tabelaUltimasVendas.getColumnModel().getColumn(1).setMaxWidth(600);
			tabelaUltimasVendas.getColumnModel().getColumn(2).setMinWidth(150);
			tabelaUltimasVendas.getColumnModel().getColumn(2).setMaxWidth(600);		
			tabelaUltimasVendas.getColumnModel().getColumn(3).setMinWidth(150);
			tabelaUltimasVendas.getColumnModel().getColumn(3).setMaxWidth(600);		
			tabelaUltimasVendas.setRowHeight(25);
			tabelaUltimasVendas.getTableHeader().setReorderingAllowed(false);
			tabelaUltimasVendas.setPreferredScrollableViewportSize(new Dimension(350, 90));
			tabelaUltimasVendas.setDefaultRenderer(Object.class, new TabelaVendasRenderer());
			
			scrolltabela = new JScrollPane(tabelaUltimasVendas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrolltabela.getViewport().setBackground(new Color(237, 237, 237));
			scrolltabela.setFocusable(false);
			painelClientes.add(scrolltabela, "cell 1 6, span 4 1, grow, h 34%, hmax 600px");			
		}
		else
		{
			tabela = new DefaultTableModel();
			scrolltabela = new JScrollPane();			
		}
		
		bVenda = new WebButton("Venda");
		bVenda.setRolloverShine(true);
		bVenda.setPreferredSize(new Dimension(110, 35));
		bVenda.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/plus2.png")));
		bVenda.addActionListener(this);
		painelClientes.add(bVenda, "cell 1 7");
		
		bDivida = new WebButton("0,00");
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			painelClientes.add(labelDivida, "cell 3 7");
			bDivida.setRolloverShine(true);
			bDivida.setPreferredSize(new Dimension(110, 35));
			bDivida.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/fiados1.png")));
			bDivida.addActionListener(this);
			painelClientes.add(bDivida, "cell 4 7");				
		}	
		
		JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		
		bSalvarCliente = new WebButton("");
		bSalvarCliente.setToolTipText("Salvar cliente.");
		bSalvarCliente.setPreferredSize(new Dimension(32, 32));
		bSalvarCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/salvar.png")));
		bSalvarCliente.setUndecorated(true);
		bSalvarCliente.addActionListener(this);
		
		bNovoCliente = new WebButton("");
		bNovoCliente.setToolTipText("Novo cliente.");
		bNovoCliente.setPreferredSize(new Dimension(32, 32));
		bNovoCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/newcliente.png")));
		bNovoCliente.setUndecorated(true);
		bNovoCliente.addActionListener(this);
		
		bDeletarCliente = new WebButton("");
		bDeletarCliente.setToolTipText("Deletar cliente.");
		bDeletarCliente.setPreferredSize(new Dimension(32, 32));
		bDeletarCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/deletecliente.png")));
		bDeletarCliente.setUndecorated(true);
		bDeletarCliente.addActionListener(this);
		
		painelBotoes.add(bSalvarCliente);
		painelBotoes.add(bNovoCliente);
		painelBotoes.add(bDeletarCliente);
		painelClientes.add(painelBotoes, "cell 0 7, align center");
		
		divisaoPainel.addTab("Clientes", new ImageIcon(getClass().getClassLoader().getResource("imgs/report_user_mini.png")), painelClientes, "Gerenciar Clientes.");			
		add(divisaoPainel);
		setarAtivado(false);
		callBack = 0;
		
		ArrayList<Component> ordem = new ArrayList<Component>();
		ordem.add(campoBusca);
		ordem.add(campoNome);
		ordem.add(campoApelido);
		ordem.add(campoCPF);
		ordem.add(campoTelefone);
		ordem.add(campoCEP);
		ordem.add(campoEndereco);
		ordem.add(campoNumero);
		ordem.add(campoBairro);
		ordem.add(campoComplemento);
		FocusTraversal ordemFocus = new FocusTraversal(ordem);
		setFocusCycleRoot(true);
		setFocusTraversalPolicy(ordemFocus);
	}
	
	private static class ClientesSingletonHolder { 
		public static final PainelClientes INSTANCE = new PainelClientes();
	}
 
	public static PainelClientes getInstance() {
		return ClientesSingletonHolder.INSTANCE;
	}
	
	public void atualizarClientes()
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					modeloLista.clear();
					todosClientes.getListaClientes().clear();

					Query pega = new Query();
					pega.executaQuery("SELECT * FROM fiados ORDER BY nome");

					while(pega.next())
					{
						Clientes cliente = new Clientes(pega.getInt("fiador_id"), pega.getString("nome"), 
						pega.getString("apelido"), pega.getString("telefone"), pega.getString("endereco"), 
						pega.getString("bairro"), pega.getString("complemento"), pega.getString("cpf"), 
						pega.getString("cep"), pega.getString("numero"));
						
						todosClientes.getListaClientes().add(cliente);
						modeloLista.addElement(new ClienteModel(pega.getString("nome"), pega.getInt("fiador_id")));
					}
					
					pega.fechaConexao();
					clienteSelecionado = null;
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
					new PainelErro(e1);
					System.exit(0);
				}		
			}
		});	
	}
	
	public void atualizarClientes(CacheClientes cc)
	{
		todosClientes = cc;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				modeloLista.clear();
				for(int i = 0; i < todosClientes.getListaClientes().size(); i++)
				{
					modeloLista.addElement(new ClienteModel(todosClientes.getListaClientes().get(i).getNome(),
							todosClientes.getListaClientes().get(i).getIdUnico()));
				}
				
				clienteSelecionado = null;	
			}
		});
	}
	
	public CacheClientes getTodosClientes()
	{
		return todosClientes;
	}
	
	public void setCallBack(int menu)
	{
		callBack = menu;
		callBackVisualiza = 0;
	}
	
	public void setCallBack(VisualizarVenda lol, int id)
	{
		vv.clear();
		vv.add(lol);
		callBackVisualiza = 1;
		
		if(id > 0)
		{
			receberCliente(todosClientes.getClienteID(id));
		}
	}
	
	class ClienteModel
	{
		protected String nome;
		protected int idUnico;
		
		public ClienteModel(String nome, int idUnico) {
			this.nome = nome;
			this.idUnico = idUnico;
		}
	}
	
	class buscarCEP implements Runnable {
		public void run () {
			buscaCEP pegaDados = new buscaCEP();

			try {
				String resultadoEnd = pegaDados.getEndereco(campoCEP.getText());
				String resultadoBairro = pegaDados.getBairro(campoCEP.getText());

				if(!resultadoEnd.equals(campoCEP.getText()))
				{
					campoEndereco.setText(resultadoEnd);
					campoBairro.setText(resultadoBairro);
				}

			} catch (IOException e1) {
				e1.printStackTrace();
				new PainelErro(e1);
			} finally {
				labelLoad.setVisible(false);
			}	
		}
	}
	
	private void setarAtivado(boolean set)
	{		
		labelNome.setEnabled(set);
		labelApelido.setEnabled(set);
		labelTelefone.setEnabled(set);
		labelCPF.setEnabled(set);
		labelCEP.setEnabled(set);
		labelEndereco.setEnabled(set);
		labelNumero.setEnabled(set);
		labelBairro.setEnabled(set);
		labelComplemento.setEnabled(set);
		labelDivida.setEnabled(set);
		
		campoNome.setEnabled(set);
		campoApelido.setEnabled(set);
		campoTelefone.setEnabled(set);
		campoCPF.setEnabled(set);
		campoCEP.setEnabled(set);
		campoEndereco.setEnabled(set);
		campoNumero.setEnabled(set);
		campoBairro.setEnabled(set);
		campoComplemento.setEnabled(set);
		bDivida.setEnabled(set);
		bVenda.setEnabled(set);
		scrolltabela.setEnabled(set);
		
		if(!set)
		{
			clienteSelecionado = null;
			campoNome.setText("");
			campoApelido.setText("");
			campoTelefone.setText("");
			campoCPF.setText("");
			campoCEP.setText("");
			campoEndereco.setText("");
			campoNumero.setText("");
			campoBairro.setText("");
			campoComplemento.setText("");
			bDivida.setText("0,00");
			tabela.setRowCount(0);		
		}
	}
	
	private void buscarCliente(String arg)
	{
		setarAtivado(false);
		modeloLista.removeAllElements();
		
		if(arg.equals("allclients"))
		{
			for(int i = 0; i < todosClientes.getListaClientes().size(); i++)
			{
				modeloLista.addElement(
					new ClienteModel(todosClientes.getListaClientes().get(i).getNome(), 
							todosClientes.getListaClientes().get(i).getIdUnico()));
			}			
		}
		else
		{
			int numAchados = 0;
			for(int i = 0; i < todosClientes.getListaClientes().size(); i++)
			{
				if(todosClientes.getListaClientes().get(i).containCliente(arg))
				{
					numAchados++;
					modeloLista.addElement(
							new ClienteModel(todosClientes.getListaClientes().get(i).getNome(), 
									todosClientes.getListaClientes().get(i).getIdUnico()));						
				}
				
				if(numAchados == 1)
				{
					jlist.setSelectedIndex(0);
	        		ClienteModel cLista = modeloLista.getElementAt(0);
	        		receberCliente(todosClientes.getClienteID(cLista.idUnico));
				}					
			}
		}
	}
	
	private void receberCliente(Clientes cliente)
	{
		if(cliente != null)
		{
			clienteSelecionado = cliente;
			setarAtivado(true);
			
			campoNome.setText(cliente.getNome());
			campoApelido.setText(cliente.getApelido());
			campoCPF.setText(cliente.getCpf());
			campoTelefone.setText(cliente.getTelefone());
			campoCEP.setText(cliente.getCep());
			campoEndereco.setText(cliente.getEndereco());
			campoNumero.setText(cliente.getNumero());
			campoBairro.setText(cliente.getBairro());
			campoComplemento.setText(cliente.getComplemento());
			campoNome.requestFocus();
			if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
			{
				try {
					tabela.setNumRows(0);
					double totalDivida = 0.0;
					Query pega2 = new Query();
					pega2.executaQuery("SELECT * FROM vendas WHERE fiado_id = " + cliente.getIdUnico() + " ORDER BY vendas_id DESC limit 0, 12");
					
					while(pega2.next())
					{
						Vector<String> linha = new Vector<String>();		
						
						linha.add("" + pega2.getInt("vendas_id"));
						linha.add(pega2.getString("horario"));
						linha.add(pega2.getString("total"));
						
						if((Double.parseDouble(pega2.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega2.getString("valor_pago").replaceAll(",", "."))))
						{
							if(pega2.getString("forma_pagamento").equals("Fiado"))
							{
								linha.add("0");
								totalDivida += (Double.parseDouble(pega2.getString("total").replaceAll(",", ".")) - Double.parseDouble(pega2.getString("valor_pago").replaceAll(",", ".")));
							}
							else {
								linha.add("1");
							}
						}
						else {
							linha.add("1");
						}
						
						linha.add(pega2.getString("atendente"));
						tabela.addRow(linha);
					}
					
					pega2.fechaConexao();
					bDivida.setText(UtilCoffe.doubleToPreco(totalDivida));
				} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					new PainelErro(e);
				}			
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == bVenda)
		{
			if(clienteSelecionado == null)
			{
				JOptionPane.showMessageDialog(null, "Escolha um cliente antes!");
			}
			else
			{
				if(callBackVisualiza > 0)
				{
					callBackVisualiza = 0;
					vv.get(0).setarCliente(clienteSelecionado.getIdUnico(), clienteSelecionado.getNome());
					vv.get(0).setVisible(true);
					vv.get(0).setLocationRelativeTo(SwingUtilities.getRoot(this));
				}
				else
				{
					if(callBack > 0)	// retorna para o painel mesas;
					{
						PainelMesas.getInstance().verMesa(callBack-1);
						PainelVendaMesa.getInstance().setFiado(clienteSelecionado.getNome(), clienteSelecionado.getIdUnico());
						callBack = 0;
					}
					else
					{
						PainelVendaRapida.getInstance().setFiado(clienteSelecionado.getNome(), clienteSelecionado.getIdUnico(),
						campoTelefone.getText(), campoEndereco.getText(), campoNumero.getText(), campoComplemento.getText());
						PainelPrincipal.getInstance().AbrirPrincipal(0);
					}					
				}
			}
		}
		else if(e.getSource() == bDeletarCliente)
		{
			if(clienteSelecionado != null)
			{
				if(Configuracao.INSTANCE.getModo() == UtilCoffe.CLIENT)
				{
					JOptionPane.showMessageDialog(null, "Terminais não podem deletar clientes pois os mesmos podem ter dívidas com o restaurante!");
				}
				else
				{
					if(UtilCoffe.precoToDouble(bDivida.getText()) <= 0)
					{
						int opcao = JOptionPane.showConfirmDialog(null, "Deletar o cliente: " + campoNome.getText() + ".\n\nVocê tem certeza?\n\n", "Deletar Cliente", JOptionPane.YES_NO_OPTION);

						if(opcao == JOptionPane.YES_OPTION)
						{
							Clientes clienteDeletar = new Clientes();
							clienteDeletar.setIdUnico(clienteSelecionado.getIdUnico());
							clienteDeletar.setNome(clienteSelecionado.getNome());
							clienteDeletar.setTelefone(clienteSelecionado.getTelefone());
							Bartender.INSTANCE.enviarCliente(new CacheClientes(clienteDeletar, UtilCoffe.CLIENTE_REMOVER, Usuario.INSTANCE.getNome()));
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Não pode deletar cliente com dívida aberta!");
					}	
				}
			}
		}
		else if(e.getSource() == bSalvarCliente)
		{
			if(clienteSelecionado != null)
			{
				campoNome.setText((campoNome.getText().replaceAll("'", "")));
				campoApelido.setText((campoApelido.getText().replaceAll("'", "")));
				campoTelefone.setText((campoTelefone.getText().replaceAll("'", "")));
				campoCPF.setText((campoCPF.getText().replaceAll("'", "")));
				campoCEP.setText((campoCEP.getText().replaceAll("'", "")));
				campoEndereco.setText((campoEndereco.getText().replaceAll("'", "")));
				campoNumero.setText((campoNumero.getText().replaceAll("'", "")));
				campoBairro.setText((campoBairro.getText().replaceAll("'", "")));
				campoComplemento.setText((campoComplemento.getText().replaceAll("'", "")));
				
				if(UtilCoffe.vaziu(campoNome.getText()))
				{
					JOptionPane.showMessageDialog(null, "O campo nome é de preenchimento obrigatório!");
				}
				else if(!UtilCoffe.vaziu(campoCPF.getText()) && !UtilCoffe.validaCPF(campoCPF.getText()))
				{
					JOptionPane.showMessageDialog(null, "CPF inválido!");
				}
				else if(campoNome.getText().length() > 100) {
					JOptionPane.showMessageDialog(null, "Máximo de 100 caracteres no nome!");
				}
				else if(campoApelido.getText().length() > 100) {
					JOptionPane.showMessageDialog(null, "Máximo de 100 caracteres no apelido!");
				}
				else if(campoTelefone.getText().length() > 30) {
					JOptionPane.showMessageDialog(null, "Máximo de 30 caracteres no apelido!");
				}
				else if(campoCPF.getText().length() > 40) {
					JOptionPane.showMessageDialog(null, "Máximo de 40 caracteres no CPF!");
				}
				else if(campoCEP.getText().length() > 40) {
					JOptionPane.showMessageDialog(null, "Máximo de 40 caracteres no CEP!");
				}
				else if(campoEndereco.getText().length() > 400) {
					JOptionPane.showMessageDialog(null, "Máximo de 400 caracteres no Endereço!");
				}
				else if(campoComplemento.getText().length() > 300) {
					JOptionPane.showMessageDialog(null, "Máximo de 300 caracteres no Complemento!");
				}
				else if(campoNumero.getText().length() > 50) {
					JOptionPane.showMessageDialog(null, "Máximo de 50 caracteres no Número!");
				}
				else if(campoBairro.getText().length() > 200) {
					JOptionPane.showMessageDialog(null, "Máximo de 200 caracteres no Bairro!");
				}
				else
				{
					Clientes clienteUpdate = new Clientes();
					clienteUpdate.setIdUnico(clienteSelecionado.getIdUnico());
					clienteUpdate.setNome(campoNome.getText());
					clienteUpdate.setApelido(campoApelido.getText());
					clienteUpdate.setTelefone(campoTelefone.getText());
					clienteUpdate.setCpf(campoCPF.getText());
					clienteUpdate.setCep(campoCEP.getText());
					clienteUpdate.setEndereco(campoEndereco.getText());
					clienteUpdate.setNumero(campoNumero.getText());
					clienteUpdate.setBairro(campoBairro.getText());
					clienteUpdate.setComplemento(campoComplemento.getText());
					
					if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
					{
						if(Bartender.INSTANCE.enviarCliente(new CacheClientes(clienteUpdate, UtilCoffe.CLIENTE_EDITAR, Usuario.INSTANCE.getNome())))
						{
							NotificationManager.setLocation(2);
							NotificationManager.showNotification(this, "Cliente Salvo!", 
									new ImageIcon(getClass().getClassLoader().getResource("imgs/notifications_ok.png"))).setDisplayTime(2000);
						}
					}
					else
					{
						Bartender.INSTANCE.enviarCliente(new CacheClientes(clienteUpdate, UtilCoffe.CLIENTE_EDITAR, Usuario.INSTANCE.getNome()));
					}
				}
			}
		}		
		else if(e.getSource() == bNovoCliente)
		{
			Clientes novoCliente = new Clientes();
			novoCliente.setNome("Novo Cliente");			
			
			if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
			{
				if(Bartender.INSTANCE.enviarCliente(new CacheClientes(novoCliente, UtilCoffe.CLIENTE_ADICIONAR, Usuario.INSTANCE.getNome())))
				{
					jlist.setSelectedIndex(0);
	        		ClienteModel cLista = modeloLista.getElementAt(0);
	        		receberCliente(todosClientes.getClienteID(cLista.idUnico));
					
					campoNome.requestFocus();
					TooltipManager.showOneTimeTooltip(bSalvarCliente, null, "Lembre-se de salvar depois!", TooltipWay.up);
					DiarioLog.add(Usuario.INSTANCE.getNome(), "Cadastrou um novo cliente.", 5);							
				}
			}
			else
			{
				Bartender.INSTANCE.enviarCliente(new CacheClientes(novoCliente, UtilCoffe.CLIENTE_ADICIONAR, Usuario.INSTANCE.getNome()));
			}
		}
		else if(e.getSource() == bDivida)
		{
		  String bla = "Escreva o valor a ser deduzido da dívida.\n";
   		  bla += "Importante:\n\n1- Não é possível aumentar a dívida, apenas reduzí-la.\n";
   		  bla += "2 - Não é possível desfazer essa ação.\n3- Se o valor digitado for maior ou igual que o da dívida, a mesma será quitada.\n\n";
   		  String pegaResposta = JOptionPane.showInputDialog(null, bla, "Reduzir Dívida", JOptionPane.QUESTION_MESSAGE);
   		  
   		  if(pegaResposta == null)
   			  pegaResposta = "";
   		  
   		  if(!"".equals(pegaResposta.trim()))
   		  {
   			  pegaResposta = pegaResposta.replaceAll("[^0-9.,]+","");
   			  pegaResposta = pegaResposta.replaceAll(",",".");
   			  
   			  double resposta = Double.parseDouble(pegaResposta);
   			  double deduzindo = resposta;
   			  
   			  if(resposta > 0 && clienteSelecionado != null)
   			  {
   				  	try {
						DiarioLog.add(Usuario.INSTANCE.getNome(), "Reduziu R$" + pegaResposta + " da dívida do " + campoNome.getText() + " (Telefone: " + campoTelefone.getText() + " ) de R$" + bDivida.getText() + ".", 6);
						Query pega = new Query();
						pega.executaQuery("SELECT * FROM vendas WHERE `fiado_id` = " + clienteSelecionado.getIdUnico() + "");
							
							while(pega.next())
							{
								if((Double.parseDouble(pega.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))))
								{
									double conta = Double.parseDouble(pega.getString("total").replaceAll(",", ".")) - Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."));
									Query manda = new Query();
									
									if(conta >= deduzindo)
									{
										String atualizado = String.format("%.2f", (deduzindo + Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))));
										manda.executaUpdate("UPDATE vendas SET `valor_pago` = '" + atualizado + "' WHERE `vendas_id` = " + pega.getInt("vendas_id"));				
										manda.fechaConexao();
										break;
									}
									else
									{
										deduzindo = (deduzindo + Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))) - Double.parseDouble(pega.getString("total").replaceAll(",", "."));
										manda.executaUpdate("UPDATE vendas SET `valor_pago` = '" + pega.getString("total") + "' WHERE `vendas_id` = " + pega.getInt("vendas_id"));
										manda.fechaConexao();
									}
								}
							}
						
						pega.fechaConexao();
						receberCliente(clienteSelecionado);
					} catch (NumberFormatException | ClassNotFoundException | SQLException e1) {
						e1.printStackTrace();
						new PainelErro(e1);
					}
   			  }
   		  }
		}
	}

	public void adicionarCliente(CacheClientes clientesAtualizado) 
	{
		todosClientes.getListaClientes().add(clientesAtualizado.getListaClientes().get(0));
		modeloLista.add(0, new ClienteModel(clientesAtualizado.getListaClientes().get(0).getNome()
				, clientesAtualizado.getListaClientes().get(0).getIdUnico()));
	}

	public void editarClientes(CacheClientes clientesAtualizado) 
	{
		for(int i = 0; i < todosClientes.getListaClientes().size(); i++)
		{
			if(todosClientes.getListaClientes().get(i).getIdUnico() == clientesAtualizado.getListaClientes().get(0).getIdUnico())
			{
				todosClientes.getListaClientes().set(i, clientesAtualizado.getListaClientes().get(0));
				break;
			}
		}
		
		for(int i = 0; i < modeloLista.size(); i++)
		{
			if(modeloLista.getElementAt(i).idUnico == clientesAtualizado.getListaClientes().get(0).getIdUnico())
			{
				modeloLista.set(i, 
						new ClienteModel(clientesAtualizado.getListaClientes().get(0).getNome(), 
						clientesAtualizado.getListaClientes().get(0).getIdUnico()));
				break;
			}
		}
		if(clienteSelecionado != null)
		{
			if(clientesAtualizado.getListaClientes().get(0).getIdUnico() == clienteSelecionado.getIdUnico())
			{
				receberCliente(clientesAtualizado.getListaClientes().get(0));		
			}	
		}
	}

	public void removerClientes(CacheClientes clientesAtualizado) 
	{
		for(int i = 0; i < todosClientes.getListaClientes().size(); i++)
		{
			if(todosClientes.getListaClientes().get(i).getIdUnico() == clientesAtualizado.getListaClientes().get(0).getIdUnico())
			{
				todosClientes.getListaClientes().remove(i);
				break;
			}
		}
		
		for(int i = 0; i < modeloLista.size(); i++)
		{
			if(modeloLista.getElementAt(i).idUnico == clientesAtualizado.getListaClientes().get(0).getIdUnico())
			{
				modeloLista.remove(i);
				break;
			}
		}
		if(clienteSelecionado != null)
		{
			if(clientesAtualizado.getListaClientes().get(0).getIdUnico() == clienteSelecionado.getIdUnico())
			{
				buscarCliente("allclients");
				campoBusca.requestFocus();
				NotificationManager.setLocation(2);
				NotificationManager.showNotification(this, "Cliente Deletado!", 
						new ImageIcon(getClass().getClassLoader().getResource("imgs/notifications_ok.png"))).setDisplayTime(2000);
			}	
		}
	}
	
	public void receberAviso(CacheAviso aviso)
	{
		if(aviso.getTipo() == UtilCoffe.CLIENTE_ADICIONAR)
		{
			jlist.setSelectedIndex(0);
    		ClienteModel cLista = modeloLista.getElementAt(0);
    		receberCliente(todosClientes.getClienteID(cLista.idUnico));
			
			campoNome.requestFocus();
			TooltipManager.showOneTimeTooltip(bSalvarCliente, null, "Lembre-se de salvar depois!", TooltipWay.up);			
		}
		else if(aviso.getTipo() == UtilCoffe.CLIENTE_EDITAR)
		{
			NotificationManager.setLocation(2);
			NotificationManager.showNotification(this, "Cliente Salvo!", 
					new ImageIcon(getClass().getClassLoader().getResource("imgs/notifications_ok.png"))).setDisplayTime(2000);		
		}
	}
	
	private class TabelaVendasRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		private Color alternate = new Color(206, 220, 249);
		private ImageIcon iconLabel = new ImageIcon(getClass().getClassLoader().getResource("imgs/documento.png"));
		private ImageIcon iconOK = new ImageIcon(getClass().getClassLoader().getResource("imgs/pago.png"));
		private ImageIcon iconNO = new ImageIcon(getClass().getClassLoader().getResource("imgs/npago.png"));

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel cellComponent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if(column == 3)
			{
				if(Integer.parseInt(value.toString()) == 1) {
					setIcon(iconOK);
					setText("");
				}
				else {
					setIcon(iconNO);
					setText("");
				}
			}
			else if(column == 2) {
				setIcon(iconLabel);
				if(isSelected) {
					try {
						String formataTip = "<html>";
						formataTip += "<b>Venda #" + table.getValueAt(row,0) + "</b>  (<i>" + table.getValueAt(row,1) +")</i><br>";
						Query pega = new Query();
						pega.executaQuery("SELECT * FROM vendas_produtos WHERE `id_link` = " + table.getValueAt(row,0) + "");

						while(pega.next())
						{
							formataTip += pega.getInt("quantidade_produto") + "x .......... <b>" + pega.getString("nome_produto") + "</b>";
							if(!"".equals(pega.getString("adicionais_produto").trim()))
								formataTip += " com " + pega.getString("adicionais_produto");

							formataTip += " - R$" +  pega.getString("preco_produto") + "<br>";
						}

						pega.fechaConexao();
						formataTip += "</html>";
						setToolTipText(formataTip);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						setToolTipText("Erro ao receber banco de dados.");
					}
				}
			}
			else {
				setIcon(null);
				setToolTipText(null);
			}
			
			if(isSelected) {
				cellComponent.setBackground(tabelaUltimasVendas.getSelectionBackground());
			}
			else if(row % 2 == 0) {
				cellComponent.setBackground(alternate);
			}
			else {
				cellComponent.setBackground(Color.WHITE);
			}
			
			setHorizontalAlignment(JLabel.CENTER);
			return cellComponent;
		}
	}
}