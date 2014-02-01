package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

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
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PainelClientes extends JPanel implements ActionListener
{
	/**
	 * 
	 */
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
	private CacheClientes todosClientes;
	private Clientes clienteSelecionado;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PainelClientes()
	{		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		divisaoPainel = new JTabbedPane();			
		
		painelClientes = new JPanel();
		painelClientes.setLayout(new GridBagLayout());
		
		todosClientes = new CacheClientes();
		
		clienteSelecionado = null;
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;	
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
		campoBusca.setPreferredSize(new Dimension(150, 30));
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
		campoNome.setPreferredSize(new Dimension(210, 30));
		
		campoApelido = new JTextField("");
		campoApelido.setPreferredSize(new Dimension(130, 30));
		
		campoTelefone = new JTextField("");
		campoTelefone.setPreferredSize(new Dimension(130, 30));
	
		campoCPF = new JTextField("");
		campoCPF.setPreferredSize(new Dimension(140, 30));		
		
		campoCEP = new WebTextField("");
		campoCEP.setPreferredSize(new Dimension(100, 30));
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
        				labelLoad.setVisible(true);
        				
        				buscarCEP buscar = new buscarCEP();
        				Thread iniciaBuscaCep = new Thread(buscar);
        				iniciaBuscaCep.start();
        			}
        			else
        			{
        				JOptionPane.showMessageDialog(null, "Digite o CEP, exemplo: 13040050");
        			}
        		}
            }
        });				
		
		campoEndereco = new JTextField("");
		campoEndereco.setPreferredSize(new Dimension(300, 30));
		
		campoNumero = new JTextField("");
		campoNumero.setPreferredSize(new Dimension(70, 30));
		
		campoBairro = new JTextField("");
		campoBairro.setPreferredSize(new Dimension(120, 30));		
		
		campoComplemento = new JTextField("");
		campoComplemento.setPreferredSize(new Dimension(300, 30));
		
		verClientes = new JPanel();
		verClientes.setLayout(new BoxLayout(verClientes, BoxLayout.Y_AXIS));
		verClientes.setMinimumSize(new Dimension(320, 360));
		verClientes.setMaximumSize(new Dimension(320, 360));
		
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
		        	label.setPreferredSize(new Dimension(300, 30));
		        }
		        else
		        {
		        	label.setForeground(Color.BLACK);
		        	label.setFont(new Font("Verdana", Font.PLAIN, 11));
		        	label.setPreferredSize(new Dimension(300, 30));
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
		JScrollPane scrollClientes = new JScrollPane(verClientes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollClientes.setMinimumSize(new Dimension(310,320));
		scrollClientes.setMaximumSize(new Dimension(310,320));
		scrollClientes.setPreferredSize(new Dimension(310,320));
		WebPanel verClientes1 = new WebPanel();	
		
        DashedBorderPainter bp4 = new DashedBorderPainter(new float[]{3f, 3f});
        bp4.setWidth(2);
        bp4.setColor(new Color(205, 205, 205));
        verClientes1.setPainter(bp4);
		verClientes1.add(scrollClientes);	
		
		gbc.insets = new Insets(3,3,3,50);
		gbc.gridwidth = 3;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas		
		painelClientes.add(campoBusca, gbc);		
		
		gbc.insets = new Insets(3,3,3,50);
		gbc.gridheight = 8;
		gbc.gridwidth = 3;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas		
		painelClientes.add(verClientes1, gbc);
		
		gbc.insets = new Insets(3,3,3,3);
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 1;	// linhas
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.CLIENT)
			gbc.gridy++;
		
		painelClientes.add(labelNome, gbc);
		
		gbc.gridwidth = 3;
		gbc.gridx = 5;	// colunas
		//gbc.gridy = 1;	// linhas		
		painelClientes.add(campoNome, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 8;	// colunas
		//gbc.gridy = 1;	// linhas		
		painelClientes.add(labelApelido, gbc);
		
		gbc.gridx = 9;	// colunas
		//gbc.gridy = 1;	// linhas		
		painelClientes.add(campoApelido, gbc);
		
		gbc.gridx = 4;	// colunas
		gbc.gridy++;	// linhas		
		painelClientes.add(labelCPF, gbc);
		
		gbc.gridwidth = 2;
		gbc.gridx = 5;	// colunas
		//gbc.gridy = 2;	// linhas		
		painelClientes.add(campoCPF, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 8;	// colunas
		//gbc.gridy = 2;	// linhas		
		painelClientes.add(labelTelefone, gbc);
		
		gbc.gridx = 9;	// colunas
		//gbc.gridy = 2;	// linhas		
		painelClientes.add(campoTelefone, gbc);
		
		gbc.gridx = 4;	// colunas
		gbc.gridy++;	// linhas		
		painelClientes.add(labelCEP, gbc);
		
		gbc.gridx = 5;	// colunas
		//gbc.gridy = 3;	// linhas		
		painelClientes.add(campoCEP, gbc);
		
		labelLoad = new JLabel();
		labelLoad.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/loadcep.gif")));
		labelLoad.setVisible(false);
		
		gbc.gridx = 6;	// colunas
		//gbc.gridy = 3;	// linhas		
		painelClientes.add(labelLoad, gbc);		
		
		gbc.gridx = 4;	// colunas
		gbc.gridy++;	// linhas		
		painelClientes.add(labelEndereco, gbc);
		
		gbc.gridwidth = 5;
		gbc.gridx = 5;	// colunas
		//gbc.gridy = 4;	// linhas		
		painelClientes.add(campoEndereco, gbc);			
		
		gbc.gridwidth = 1;
		gbc.gridx = 4;	// colunas
		gbc.gridy++;	// linhas		
		painelClientes.add(labelNumero, gbc);
		
		gbc.gridx = 5;	// colunas
		//gbc.gridy = 5;	// linhas		
		painelClientes.add(campoNumero, gbc);
		
		gbc.gridx = 8;	// colunas
		//gbc.gridy = 5;	// linhas		
		painelClientes.add(labelBairro, gbc);
		
		gbc.gridx = 9;	// colunas
		//gbc.gridy = 5;	// linhas		
		painelClientes.add(campoBairro, gbc);		
		
		gbc.gridx = 4;	// colunas
		gbc.gridy++;	// linhas		
		painelClientes.add(labelComplemento, gbc);
		
		gbc.gridwidth = 5;
		gbc.gridx = 5;	// colunas
		//gbc.gridy = 6;	// linhas		
		painelClientes.add(campoComplemento, gbc);
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			tabela = new DefaultTableModel() {

			    /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
			    public boolean isCellEditable(int row, int column) {
			       if(column == 4)
			    	   return true;
			       
			       return false;
			    }
			};
			
			tabela.addColumn("ID");
			tabela.addColumn("Data");
			tabela.addColumn("Total");
			tabela.addColumn("Status");
			
			tabelaUltimasVendas = new JTable() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				Color alternate = new Color(206, 220, 249);
			    
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
			
			tabelaUltimasVendas.setModel(tabela);
			tabelaUltimasVendas.getColumnModel().getColumn(0).setMinWidth(0);
			tabelaUltimasVendas.getColumnModel().getColumn(0).setMaxWidth(0);
			tabelaUltimasVendas.getColumnModel().getColumn(1).setMinWidth(100);
			tabelaUltimasVendas.getColumnModel().getColumn(1).setMaxWidth(200);
			tabelaUltimasVendas.getColumnModel().getColumn(2).setMinWidth(100);
			tabelaUltimasVendas.getColumnModel().getColumn(2).setMaxWidth(200);		
			tabelaUltimasVendas.getColumnModel().getColumn(3).setMinWidth(100);
			tabelaUltimasVendas.getColumnModel().getColumn(3).setMaxWidth(200);		
			tabelaUltimasVendas.setRowHeight(25);
			tabelaUltimasVendas.getTableHeader().setReorderingAllowed(false);
			tabelaUltimasVendas.getColumn("Total").setCellRenderer(new JLabelRenderer());
			tabelaUltimasVendas.getColumn("Data").setCellRenderer(new CustomRenderer());
			tabelaUltimasVendas.getColumn("Status").setCellRenderer(new CustomRenderer());
			tabelaUltimasVendas.setPreferredScrollableViewportSize(new Dimension(350, 100));
			
			scrolltabela = new JScrollPane(tabelaUltimasVendas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			gbc.gridwidth = 6;
			gbc.gridheight = 1;
			gbc.gridx = 4;	// colunas
			gbc.gridy = 8;	// linhas		
			painelClientes.add(scrolltabela, gbc);			
		}
		else
		{
			tabela = new DefaultTableModel();
			scrolltabela = new JScrollPane();			
		}
		
		bVenda = new WebButton("Venda");
		bVenda.setRolloverShine(true);
		bVenda.setPreferredSize(new Dimension(90, 35));
		bVenda.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/plus2.png")));
		bVenda.addActionListener(this);
		
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 4;	// colunas
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
			gbc.gridy = 10;	// linhas
		else
			gbc.gridy = 8;	// linhas
		
		painelClientes.add(bVenda, gbc);
		
		bDivida = new WebButton("0,00");
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			gbc.gridx = 8;	// colunas
			gbc.gridy = 10;	// linhas		
			painelClientes.add(labelDivida, gbc);
			
			bDivida.setRolloverShine(true);
			bDivida.setPreferredSize(new Dimension(70, 30));
			bDivida.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/fiados1.png")));
			bDivida.addActionListener(this);
			
			gbc.gridx = 9;	// colunas
			gbc.gridy = 10;	// linhas		
			painelClientes.add(bDivida, gbc);				
		}	
		
		JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		
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
		
		gbc.insets = new Insets(0,0,0,50);
		gbc.gridwidth = 3;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 10;	// linhas		
		painelClientes.add(painelBotoes, gbc);
		
		divisaoPainel.addTab("Clientes", new ImageIcon(getClass().getClassLoader().getResource("imgs/report_user_mini.png")), painelClientes, "Gerenciar Clientes.");			
		add(divisaoPainel);
		setarAtivado(false);
		callBack = 0;		
	}
	
	private static class ClientesSingletonHolder { 
		public static final PainelClientes INSTANCE = new PainelClientes();
	}
 
	public static PainelClientes getInstance() {
		return ClientesSingletonHolder.INSTANCE;
	}
	
	public void atualizarClientes()
	{		
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
	
	public void atualizarClientes(CacheClientes cc)
	{
		todosClientes = cc;
		modeloLista.clear();
		for(int i = 0; i < todosClientes.getListaClientes().size(); i++)
		{
			modeloLista.addElement(new ClienteModel(todosClientes.getListaClientes().get(i).getNome(),
					todosClientes.getListaClientes().get(i).getIdUnico()));
		}
		
		clienteSelecionado = null;
	}
	
	public CacheClientes getTodosClientes()
	{
		return todosClientes;
	}
	
	public void setCallBack(int menu)
	{
		callBack = menu;
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
					JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e1.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
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
			
			if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
			{
				try {
					tabela.setNumRows(0);
					double totalDivida = 0.0;
					Query pega2 = new Query();
					pega2.executaQuery("SELECT * FROM vendas WHERE fiado_id = " + cliente.getIdUnico() + " ORDER BY vendas_id DESC limit 0, 10");
					
					while(pega2.next())
					{
						Vector<Serializable> linha = new Vector<Serializable>();		
						
						linha.add(pega2.getInt("vendas_id"));
						linha.add(pega2.getString("horario"));
						linha.add(pega2.getString("total"));
						
						if((Double.parseDouble(pega2.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega2.getString("valor_pago").replaceAll(",", "."))))
						{
							if(pega2.getString("forma_pagamento").equals("Fiado"))
							{
								linha.add("Não Pago");
								totalDivida += (Double.parseDouble(pega2.getString("total").replaceAll(",", ".")) - Double.parseDouble(pega2.getString("valor_pago").replaceAll(",", ".")));
							}
							else
							{
								linha.add("Pago");
							}
						}
						else
						{
							linha.add("Pago");
						}
						
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
	
	class JLabelRenderer implements TableCellRenderer {

		private JLabel label;

		public JLabelRenderer() {
			label = new JLabel();
			label.setForeground(Color.BLACK);
			label.setOpaque(true);
		}  

		@SuppressWarnings("finally")
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {	  

			label.setHorizontalTextPosition(AbstractButton.LEFT);
			label.setHorizontalAlignment( JLabel.CENTER );
			label.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/documento.png")));
			label.setForeground(Color.BLACK);

			String formataTip = "<html>";
			try {
				formataTip += "<b>Venda #" + table.getValueAt(row,0) + "</b>  (<i>" + table.getValueAt(row,1) +")</i><br>";
				Query pega = new Query();
				pega.executaQuery("SELECT * FROM vendas_produtos WHERE `id_link` = " + table.getValueAt(row,0) + "");

				while(pega.next())
				{
					formataTip += pega.getInt("quantidade_produto") + "x .......... <b>" + pega.getString("nome_produto") + "</b>";

					if(!"".equals(pega.getString("adicionais_produto").trim()))
					{
						formataTip += " com " + pega.getString("adicionais_produto");
					}

					formataTip += " - R$" +  pega.getString("preco_produto") + "<br>";
				}

				pega.fechaConexao();
				formataTip += "</html>";
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				new PainelErro(e);
				formataTip = "Erro ao receber banco de dados.";
			} finally {
				label.setToolTipText(formataTip);

				if (isSelected) {
					label.setForeground(new Color(72, 61, 139));
					label.setBackground(table.getSelectionBackground());
				} else {
					label.setForeground(Color.BLACK);
					label.setBackground(table.getSelectionBackground());
				}

				label.setText((value == null) ? "" : value.toString());
				return label;					
			}
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

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == bVenda)
		{
			if(clienteSelecionado != null)
			{
				JOptionPane.showMessageDialog(null, "Escolha um cliente antes!");
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
					MenuPrincipal.getInstance().AbrirPrincipal(0);
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
				if(UtilCoffe.vaziu(campoNome.getText()))
				{
					JOptionPane.showMessageDialog(null, "O campo nome é de preenchimento obrigatório!");
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
							NotificationManager.showNotification(this, "Cliente Salvado!").setDisplayTime(2000);							
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
				NotificationManager.showNotification(this, "Cliente Deletado!").setDisplayTime(2000);
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
			NotificationManager.showNotification(this, "Cliente Salvado!").setDisplayTime(2000);			
		}
	}
}