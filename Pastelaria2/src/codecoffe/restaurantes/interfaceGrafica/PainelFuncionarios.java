package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;

public class PainelFuncionarios extends JPanel implements ActionListener, TableModelListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DefaultTableModel tabela;
	private static JTable tabelaFuncionarios;
	private static JLabel adicionarNome, adicionarUser, adicionarSenha;
	private static JTextField campoNome, campoUser, campoSenha;
	private static JComboBox<?> campoLevel;
	private static JButton adicionar;
	private static JPanel addPainel;
	
	PainelFuncionarios()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Gerenciar Funcionários"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//setMinimumSize(new Dimension(1020, 650));
		//setMaximumSize(new Dimension(1920, 910));
		
		//instance table model
		tabela = new DefaultTableModel() {

		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		    	
			       if(column == 1 || column == 2)
			    	   return true;		    	
		    	
		    	String pega = (String) tabela.getValueAt(row, 0);
		    	
		    	if(pega.equals(Usuario.getNome()))
		    		return false;
		    	
		       if(column == 4 || column == 3)
		    	   return true;
		       
		       return false;
		    }	    
		};
		
		tabela.addColumn("Nome");
		tabela.addColumn("Usuário");
		tabela.addColumn("Senha");
		tabela.addColumn("Cargo");
		tabela.addColumn("Deletar");
		
		int linhas = 0;
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM funcionarios ORDER BY nome");
			
			while(pega.next())
			{
				if(pega.getInt("level") != 3)
				{
					Vector<Serializable> linha = new Vector<Serializable>();
					
					linha.add(pega.getString("nome"));
					linha.add(pega.getString("username"));
					linha.add(pega.getString("password"));
					
					if(pega.getInt("level") < 2)
						linha.add("Funcionário");
					else
						linha.add("Gerente");
					
					linha.add("Deletar");
					
					tabela.addRow(linha);
					linhas++;				
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
		}
		
		tabelaFuncionarios = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Color alternate = new Color(206, 220, 249);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 4 && column != 3)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }				
		};
		
		tabelaFuncionarios.setModel(tabela);
		tabelaFuncionarios.getColumnModel().getColumn(0).setMinWidth(270);
		tabelaFuncionarios.getColumnModel().getColumn(0).setMaxWidth(700);
		
		tabelaFuncionarios.getColumnModel().getColumn(1).setMinWidth(250);
		tabelaFuncionarios.getColumnModel().getColumn(1).setMaxWidth(600);
		tabelaFuncionarios.getColumnModel().getColumn(2).setMinWidth(250);
		tabelaFuncionarios.getColumnModel().getColumn(2).setMaxWidth(500);		
		
		tabelaFuncionarios.getColumnModel().getColumn(3).setMinWidth(100);
		tabelaFuncionarios.getColumnModel().getColumn(3).setMaxWidth(140);
		
		tabelaFuncionarios.getColumnModel().getColumn(4).setMinWidth(60);
		tabelaFuncionarios.getColumnModel().getColumn(4).setMaxWidth(60);		
		
		tabelaFuncionarios.setRowHeight(30);
		tabelaFuncionarios.getTableHeader().setReorderingAllowed(false);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );
		
		tabelaFuncionarios.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaFuncionarios.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));
		
		tabelaFuncionarios.getColumn("Nome").setCellRenderer(new CustomRenderer());
		tabelaFuncionarios.getColumn("Usuário").setCellRenderer(new CustomRenderer());
		tabelaFuncionarios.getColumn("Senha").setCellRenderer(new CustomRenderer());
		tabelaFuncionarios.getColumn("Cargo").setCellRenderer(new CustomRenderer());	
		
		String[] tiposFuncionario = { "Funcionário", "Gerente" };
		
		tabelaFuncionarios.getColumn("Cargo").setCellEditor(new MyComboBoxEditor(tiposFuncionario));
		
		MyComboBoxRenderer combo = new MyComboBoxRenderer(tiposFuncionario);
		((JLabel)combo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		tabelaFuncionarios.getColumn("Cargo").setCellRenderer(combo);
		
		tabelaFuncionarios.getModel().addTableModelListener(this);		
		
		if(linhas > 8)
			tabelaFuncionarios.setPreferredScrollableViewportSize(new Dimension(650, 112));
		else
			tabelaFuncionarios.setPreferredScrollableViewportSize(new Dimension(650, linhas*16));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaFuncionarios);
		add(scrolltabela);
		
		addPainel = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Graphics2D g2d = (Graphics2D) g;
		        Color color1 = getBackground();
		        Color color2 = new Color(207, 220, 249);
		        int w = getWidth();
		        int h = getHeight();
		        GradientPaint gp = new GradientPaint(
		            0, 0, color1, 0, h, color2);
		        g2d.setPaint(gp);
		        g2d.fillRect(0, 0, w, h);
		    }
		};
		addPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Adicionar"));
		addPainel.setLayout(new GridBagLayout());
		addPainel.setMinimumSize(new Dimension(975, 300));
		addPainel.setMaximumSize(new Dimension(1920, 400));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		campoLevel = new JComboBox<Object>(tiposFuncionario);
		((JLabel)campoLevel.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		campoLevel.setSelectedIndex(0);
		campoLevel.setPreferredSize(new Dimension(150, 30));
		
		adicionarNome = new JLabel("Nome: ");
		adicionarUser = new JLabel("Usuário: ");
		adicionarSenha = new JLabel("Senha: ");
		
		campoNome = new JTextField();
		campoNome.setPreferredSize(new Dimension(150, 30));
		campoUser = new JTextField();
		campoUser.setPreferredSize(new Dimension(150, 30));
		campoSenha = new JTextField();
		campoSenha.setPreferredSize(new Dimension(150, 30));
		
		ImageIcon iconePlus = new ImageIcon(getClass().getClassLoader().getResource("imgs/plus2.png"));
		adicionar = new JButton("Adicionar");
		adicionar.setIcon(iconePlus);
		adicionar.setPreferredSize(new Dimension(150, 30));
		adicionar.addActionListener(this);
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas
		
		addPainel.add(adicionarNome, gbc);
		
		gbc.gridx = 2;	// colunas
		addPainel.add(campoNome, gbc);
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas		
		
		addPainel.add(adicionarUser, gbc);
		
		gbc.gridx = 2;	// colunas
		
		addPainel.add(campoUser, gbc);
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 3;	// linhas			
		
		addPainel.add(adicionarSenha, gbc);
		gbc.gridx = 2;	// colunas
		addPainel.add(campoSenha, gbc);
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 1;	// linhas
		
		addPainel.add(campoLevel, gbc);
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 3;	// linhas				
		
		addPainel.add(adicionar, gbc);
		add(addPainel);
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
	
	class MyComboBoxRenderer extends JComboBox<Object> implements TableCellRenderer {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyComboBoxRenderer(String[] items) {
		    super(items);
		  }

		  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		      boolean hasFocus, int row, int column) {
		    if (isSelected) {
		    	setForeground(new Color(72, 61, 139));
		      super.setBackground(table.getSelectionBackground());
		    } else {
		    	setForeground(Color.BLACK);
		    	setBackground(table.getBackground());
		    }
		    setSelectedItem(value);
		    return this;
		  }
		}

	class MyComboBoxEditor extends DefaultCellEditor {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyComboBoxEditor(String[] items) {
		    super(new JComboBox<Object>(items));
		  }
		}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		if(e.getSource() == adicionar)
		{
			int level = 0;
			
			if(campoLevel.getSelectedItem() == "Gerente")
				level = 2;
			else
				level = 1;
			
			if("".equals(campoNome.getText().trim()) || "".equals(campoUser.getText().trim()) || "".equals(campoSenha.getText().trim()))
			{
				JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
			}
			else
			{
				try {
					String formatacao;
					Query envia = new Query();
					formatacao = "INSERT INTO funcionarios(username, password, level, nome) VALUES('"
					+ (campoUser.getText().replaceAll("'", "")) +
					"', '" + (campoSenha.getText().replaceAll("'", "")) +
					"', " + level + ", '" + (campoNome.getText().replaceAll("'", "")) + "');";
					
					envia.executaUpdate(formatacao);
					envia.fechaConexao();
					DiarioLog.add(Usuario.getNome(), "Adicionou o funcionário " + (campoUser.getText().replaceAll("'", "")) + ".", 4);
					
					String tipo = "";
					boolean flag = false;
					
					if(level == 1)
						tipo = "Funcionário";
					else
						tipo = "Gerente";
					
					Object[] linha = {campoNome.getText(), campoUser.getText(), campoSenha.getText(), tipo, ""};
					
					for(int i = 0; i < tabela.getRowCount() ; i++)
					{
						if(campoNome.getText().compareTo((String) tabela.getValueAt(i, 0)) <= 0)
						{
							tabela.insertRow(i, linha);
							flag = true;
							break;
						}
					}

					if(!flag)
						tabela.addRow(linha);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
					new PainelErro(e1);
				}
				finally
				{
					campoNome.setText("");
					campoUser.setText("");
					campoSenha.setText("");
					campoNome.requestFocus();	
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
		    //setText((value == null) ? "" : value.toString());
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
		      if(tabelaFuncionarios.getSelectedRowCount() == 1)	//verifico se somente uma linha estÃ¡ selecionada  
		      {
		    	  try {
					String pega = (String) tabelaFuncionarios.getValueAt(tabelaFuncionarios.getSelectedRow(), 1);
					  String formatacao;
					  Query envia = new Query();
					  formatacao = "DELETE FROM funcionarios WHERE `username` = '" + (pega.replaceAll("'", "")) + "';";
					  envia.executaUpdate(formatacao);
					  envia.fechaConexao();
					  DiarioLog.add(Usuario.getNome(), "Deletou o funcionário " + (pega.replaceAll("'", "")) + ".", 4);
					      
					  SwingUtilities.invokeLater(new Runnable() {  
						  public void run() {  
							  tabela.removeRow(tabelaFuncionarios.getSelectedRow());
						  }  
					  });
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					new PainelErro(e);
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

		@Override
		public void tableChanged(TableModelEvent e) {
	        int row = e.getFirstRow();
	        int column = e.getColumn();
	        if(column == 1 || column==2){
	        	String tipo;
	        	if(column==1){
	        		tipo = "username";
	        	}else{
	        		tipo = "password";
	        	}
		        TableModel model = (TableModel)e.getSource();
		        String data = (String) model.getValueAt(row, column);
		        
		        String pega = (String) model.getValueAt(row, 0);
		        
			    try {
					String formatacao;
					Query envia = new Query();
					formatacao = "UPDATE funcionarios SET "+tipo+" = '" + data + "' WHERE nome = '"+ (pega.replaceAll("'", "")) +"' " ;
					envia.executaUpdate(formatacao);
					envia.fechaConexao();
					DiarioLog.add(Usuario.getNome(), "Atualizou o " + tipo + "do funcionário " + (pega.replaceAll("'", "")) + " para " + data + ".", 4);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
					new PainelErro(e1);
				}
			    
	        }else if(column == 3){
	        	TableModel model = (TableModel)e.getSource();
				String data = (String) model.getValueAt(row, column);
				String pega = (String) model.getValueAt(row, 0);
				int tipo =0;
				if(data.equals("Gerente")){
				       tipo =2;
				}else{
				       tipo =1;
				}
				try {
					String formatacao;
					Query envia = new Query();
					formatacao = "UPDATE funcionarios SET level =  " + tipo + " WHERE nome = '"+ (pega.replaceAll("'", "")) +"' " ;
					envia.executaUpdate(formatacao);
					envia.fechaConexao();
					DiarioLog.add(Usuario.getNome(), "Alterou o cargo de " + (pega.replaceAll("'", "")) + " para " + (tipo == 1 ? "funcionário" : "gerente") + ".", 4);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
					new PainelErro(e1);
				}					
	        }
	    }	
}