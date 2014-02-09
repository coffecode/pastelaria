package codecoffe.restaurantes.interfaceGrafica;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.sockets.Server;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.extended.button.WebSwitch;
import com.alee.extended.filechooser.WebDirectoryChooser;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.slider.WebSlider;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import com.alee.utils.FileUtils;
import com.alee.utils.swing.DialogOptions;

public class PainelStatus extends WebMenuBar implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WebMenu menuConfiguracoes;
	private WebMenuItem itemUserSair, itemConfGerais, itemBackup, itemFuncionarios;
	private OpcoesDialog menuOpcoes;
	private BackupDialog menuBackup;
	protected JLabel labelUltimo;
	private Timer timerBackup;
    
    private PainelStatus()
    {
		setMaximumSize(new Dimension(1920, 30));
		setMinimumSize(new Dimension(980, 30));
		
		itemUserSair = new WebMenuItem("Usuário", new ImageIcon(getClass().getClassLoader().getResource("imgs/usuario.png")));
		itemUserSair.addActionListener(this);
		add(itemUserSair);
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{			
			menuOpcoes = new OpcoesDialog();	
			menuBackup = new BackupDialog();
			
			menuConfiguracoes = new WebMenu("Configurações", new ImageIcon(getClass().getClassLoader().getResource("imgs/opcoes.png")));
			itemConfGerais  = new WebMenuItem("Gerais", new ImageIcon(getClass().getClassLoader().getResource("imgs/opcoes.png")));
			itemConfGerais.addActionListener(this);
			itemBackup  = new WebMenuItem("Backup", new ImageIcon(getClass().getClassLoader().getResource("imgs/backup_mini.png")));
			itemBackup.addActionListener(this);
			itemFuncionarios  = new WebMenuItem("Funcionários", new ImageIcon(getClass().getClassLoader().getResource("imgs/funcionarios_mini.png")));
			itemFuncionarios.addActionListener(this);					
			menuConfiguracoes.add(itemConfGerais);
			menuConfiguracoes.add(itemBackup);
			menuConfiguracoes.add(itemFuncionarios);
			add(menuConfiguracoes);
			
			try {
				InetAddress ipMaquina = InetAddress.getLocalHost();
				JLabel labelIP = new JLabel("<html><b>IP:  </b>" + ipMaquina.getHostAddress() + "</html>");
				labelIP.setPreferredSize(new Dimension(120, 20));
				add(labelIP, ToolbarLayout.END);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				JLabel labelIP = new JLabel("<html><b>IP:  </b>Indisponível</html>");
				labelIP.setPreferredSize(new Dimension(120, 20));
				add(labelIP, ToolbarLayout.END);
			}
		}
    }
    
	private static class StatusSingletonHolder { 
		public static final PainelStatus INSTANCE = new PainelStatus();
	}
 
	public static PainelStatus getInstance() {
		return StatusSingletonHolder.INSTANCE;
	}    
    
	class BackupAutomatico extends TimerTask 
	{
        public void run() 
        {
        	long duration = System.currentTimeMillis() - Configuracao.INSTANCE.getUltimoBackup().getTime();
        	long hours = TimeUnit.MILLISECONDS.toHours(duration);
        	
        	if(hours >= Configuracao.INSTANCE.getBackupAutoIntervalo() && Configuracao.INSTANCE.getBackupAutoIntervalo() >= 1)
        	{
    			if(!UtilCoffe.vaziu(Configuracao.INSTANCE.getBackupAutoCaminho()))
    			{
    				File file = new File(Configuracao.INSTANCE.getBackupAutoCaminho());
    				if(file.exists())
    				{
	                    SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy__HH_mm");
	                    
						Process p = null;
			            try {
			                Runtime runtime = Runtime.getRuntime();
			                p = runtime.exec("C:\\Users\\andre\\Desktop\\Pastelaria\\v1.50\\mysql-5.6.15-win32\\bin\\mysqldump.exe -uroot -ppuc4321 --add-drop-database -B pastelaria -r " 
			                + file.getPath()
			                + "/BackupRest_" + formatter.format(new Date()) +".sql");
			                
			                int processComplete = p.waitFor();
			 
			                if (processComplete == 0) {
			                	System.out.println("Backup automático criado com sucesso!");
			                    
			        			Query envia = new Query();
			        			envia.executaUpdate("UPDATE opcoes SET ultimobackup = '" + formatter.format(new Date()) + "'");	
			        			envia.fechaConexao();
			        			
			        			Configuracao.INSTANCE.setUltimoBackup(new Date());
			        			
			        			SwingUtilities.invokeLater(new Runnable() {
			        				@Override
			        				public void run() {
					        			SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy - HH:mm.");
					        			labelUltimo.setText("Último backup realizado: " + formatter2.format(Configuracao.INSTANCE.getUltimoBackup()));		
			        				}
			        			});			        			
			                    
			                } else {
			                	System.out.println("Erro, não foi possível criar o backup automático.");
			                }
			            } catch (Exception ex) {
			            	System.out.println("Erro, não foi possível criar o backup automático.");
			                new PainelErro(ex);
			            }	    					
    				}
    				else
    					System.out.println("Erro, não foi possível criar o backup automático (diretório inexistente).");
    			}
        	}
        }
    }    
    
    public void setNome(String nomeFuncionario)
    {
    	itemUserSair.setText(nomeFuncionario);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == itemUserSair)
		{
			int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja sair?", "Logout", JOptionPane.YES_NO_OPTION);
			
			if(opcao == JOptionPane.YES_OPTION)
			{
				MenuPrincipal.getInstance().logout();
			}	
		}
		else if(e.getSource() == itemConfGerais)
		{
	        menuOpcoes.pack ();
	        menuOpcoes.setLocationRelativeTo(null);
	        menuOpcoes.setVisible(true);
		}
		else if(e.getSource() == itemBackup)
		{
			menuBackup.pack ();
			menuBackup.setLocationRelativeTo(null);
			menuBackup.setVisible(true);
		}
		else if(e.getSource() == itemFuncionarios)
		{
			if(Usuario.INSTANCE.getLevel() > 1)
				MenuPrincipal.getInstance().AbrirPrincipal(2);
			else
				JOptionPane.showMessageDialog(null, "Você não tem permissão para ver isso.");
		}			
	}
	
	private class OpcoesDialog extends WebDialog
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel labelNumeroMesas, labelRestaurante, labelCreditos, labelEmail, labelImagem, labelEntrega;
		private JTextField campoNumeroMesas, campoRestaurante, campoEntrega;
		private JCheckBox checkDez, checkRecibo;
		
		public OpcoesDialog()
		{
			setTitle("Configurações do Sistema");
			JPanel opcaoPainel = new JPanel();
			opcaoPainel.setLayout(null);			
			
			setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
			setDefaultCloseOperation(WebDialog.DO_NOTHING_ON_CLOSE);
			
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					atualizaCampos();
					dispose();
				}
			});
			
			ItemListener listener = new ItemListener ()
			{
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getItemSelectable() == checkDez)
					{
						if(checkDez.isSelected())
						{
							try {
								Query envia = new Query();
								envia.executaUpdate("UPDATE opcoes SET dezporcento = 1");
								envia.fechaConexao();
							} catch (ClassNotFoundException | SQLException e1) {
								e1.printStackTrace();
								new PainelErro(e1);
							}
							finally{
								Configuracao.INSTANCE.setDezPorcento(true);
							}
						}
						else
						{
							try {
								Query envia = new Query();
								envia.executaUpdate("UPDATE opcoes SET dezporcento = 0");
								envia.fechaConexao();
							} catch (ClassNotFoundException | SQLException e1) {
								e1.printStackTrace();
								new PainelErro(e1);
							}
							finally{
								Configuracao.INSTANCE.setDezPorcento(false);
							}
						}
					}
					
					if(e.getItemSelectable() == checkRecibo)
					{
						if(checkRecibo.isSelected())
						{
							try {
								Query envia = new Query();
								envia.executaUpdate("UPDATE opcoes SET recibofim = 1");
								envia.fechaConexao();
							} catch (ClassNotFoundException | SQLException e1) {
								e1.printStackTrace();
								new PainelErro(e1);
							}
							finally{
								Configuracao.INSTANCE.setReciboFim(true);
							}
						}
						else
						{
							try {
								Query envia = new Query();
								envia.executaUpdate("UPDATE opcoes SET recibofim = 0");
								envia.fechaConexao();
							} catch (ClassNotFoundException | SQLException e1) {
								e1.printStackTrace();
								new PainelErro(e1);
							}
							finally{
								Configuracao.INSTANCE.setReciboFim(false);
							}
						}
					}
				}
			};
			
			labelRestaurante = new JLabel("Restaurante:");
			labelRestaurante.setFont(new Font("Helvetica", Font.BOLD, 15));
			labelRestaurante.setBounds(15,20,250,30); // Coluna, Linha, Largura, Altura!
			opcaoPainel.add(labelRestaurante);
			
			campoRestaurante = new JTextField();
			campoRestaurante.setToolTipText("É necessário reiniciar o programa para fazer efeito.");
			campoRestaurante.setBounds(164,20,250,30);
			opcaoPainel.add(campoRestaurante);		
			
			labelNumeroMesas = new JLabel("Número de Mesas:");
			labelNumeroMesas.setFont(new Font("Helvetica", Font.BOLD, 15));
			labelNumeroMesas.setBounds(15,60,250,30); // Coluna, Linha, Largura, Altura!
			opcaoPainel.add(labelNumeroMesas);
			
			campoNumeroMesas = new JTextField();
			campoNumeroMesas.setToolTipText("É necessário reiniciar o programa para fazer efeito.");
			campoNumeroMesas.setHorizontalAlignment(SwingConstants.CENTER);
			campoNumeroMesas.setBounds(164,60,35,30);
			opcaoPainel.add(campoNumeroMesas);
			
			labelEntrega = new JLabel("Taxa Delivery:");
			labelEntrega.setFont(new Font("Helvetica", Font.BOLD, 15));
			labelEntrega.setBounds(15,100,250,30);
			opcaoPainel.add(labelEntrega);
			
			campoEntrega = new JTextField();
			campoEntrega.setToolTipText("É necessário reiniciar o programa para fazer efeito.");
			campoEntrega.setHorizontalAlignment(SwingConstants.CENTER);
			campoEntrega.setBounds(164,100,65,30);
			opcaoPainel.add(campoEntrega);			
			
			checkDez = new JCheckBox("10% opcional nas mesas");
			checkDez.setFont(new Font("Helvetica", Font.BOLD, 15));
			checkDez.addItemListener(listener);
			checkDez.setBounds(10,150,300,30);
			opcaoPainel.add(checkDez);
			
			checkRecibo = new JCheckBox("Imprimir recibo no fim da venda");
			checkRecibo.setFont(new Font("Helvetica", Font.BOLD, 15));
			checkRecibo.addItemListener(listener);
			checkRecibo.setBounds(10,180,300,30);
			opcaoPainel.add(checkRecibo);
			
			labelImagem = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/opcoes_full.png")));
			labelImagem.setBounds(310,95,128,128);
			labelImagem.setEnabled(false);
			opcaoPainel.add(labelImagem);		
			
			JPanel creditosPainel = new JPanel(new FlowLayout(FlowLayout.CENTER, 45, 20));
			creditosPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "CodeCoffe - Restaurantes " + UtilCoffe.VERSAO));
			creditosPainel.setBounds(5,240,480,130);
			
			labelCreditos = new JLabel("Sistema desenvolvido por André Alves & Fernando Ferreira.");
			labelCreditos.setFont(new Font("Verdana", Font.PLAIN, 12));
			labelCreditos.setBounds(15,20,400,30); // Coluna, Linha, Largura, Altura!
			creditosPainel.add(labelCreditos);
			
			labelEmail = new JLabel("contato@codecoffe.com.br");
			labelEmail.setFont(new Font("Verdana", Font.ITALIC, 12));
			labelEmail.setBounds(15,20,400,30); // Coluna, Linha, Largura, Altura!
			creditosPainel.add(labelEmail);
			
			opcaoPainel.add(creditosPainel);
			add(opcaoPainel);
			
			setPreferredSize(new Dimension(500, 400));
			
			campoRestaurante.setText(Configuracao.INSTANCE.getRestaurante());
			campoNumeroMesas.setText("" + Configuracao.INSTANCE.getMesas());
			campoEntrega.setText(UtilCoffe.doubleToPreco(Configuracao.INSTANCE.getTaxaEntrega()));
			
			if(Configuracao.INSTANCE.getDezPorcento())
				checkDez.setSelected(true);
			else
				checkDez.setSelected(false);
			
			if(Configuracao.INSTANCE.getReciboFim())
				checkRecibo.setSelected(true);
			else
				checkRecibo.setSelected(false);			
			
			setResizable(false);			
		}

		public void atualizaCampos()
		{
			
			try {
				Query envia = new Query();
				
				if(!"".equals(campoRestaurante.getText().trim()))
				{
					envia.executaUpdate("UPDATE opcoes SET restaurante = '" + campoRestaurante.getText() + "'");
					Configuracao.INSTANCE.setRestaurante(campoRestaurante.getText());
				}
				
				String limpeza = campoNumeroMesas.getText().replaceAll("[^0-9]+","");
				
				if(!"".equals(limpeza.trim()))
				{
					envia.executaUpdate("UPDATE opcoes SET mesas = " + limpeza);
					Configuracao.INSTANCE.setMesas(Integer.parseInt(limpeza));
				}
				
				limpeza = campoEntrega.getText().replaceAll("[^0-9.,]+","");
				
				if(!"".equals(limpeza.trim()))
				{
					envia.executaUpdate("UPDATE opcoes SET taxaentrega = '" + (limpeza.replaceAll(",", ".")) + "'");
					Configuracao.INSTANCE.setTaxaEntrega(UtilCoffe.precoToDouble((limpeza.replaceAll(",", "."))));
				}
				
				envia.fechaConexao();
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				new PainelErro(e);
			}
			finally
			{
				
			}
		}		
	}
	
	
	private class BackupDialog extends WebDialog
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel labelSalvarAuto, labelBackupAuto, labelHoras, labelCria, labelRestaura;
		private WebSwitch backupAuto;
		private WebButton backupLocalButton;
		private WebSlider backupHoras;
		private WebDirectoryChooser directoryChooser;
		
		public BackupDialog()
		{
			setTitle("Backup do Banco de Dados");
			JPanel backupPainel = new JPanel() {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
			    protected void paintComponent(Graphics g) {
			        super.paintComponent(g);
			        
			        g.setColor(new Color(196, 194, 183));
			        g.drawLine(0, 165, getWidth(), 165);
			        g.drawLine(255, 190, 255, getHeight()-40);
			    }				
				
			};
			backupPainel.setLayout(null);			
			
			setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
			setDefaultCloseOperation(WebDialog.DO_NOTHING_ON_CLOSE);
			
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					atualizaCampos();
					dispose();
				}
			});
			
			ActionListener actionLi = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getSource() == backupLocalButton)
					{
						directoryChooser = new WebDirectoryChooser(getOwner(), "Escolha uma pasta");
		                directoryChooser.setVisible(true);
		                
		                if(directoryChooser.getResult() == DialogOptions.OK_OPTION)
		                {
		                    File file = directoryChooser.getSelectedDirectory();
		                    backupLocalButton.setIcon(FileUtils.getFileIcon(file));
		                    backupLocalButton.setText(FileUtils.getDisplayFileName(file));
		                    
		        			try {
		        				Query envia = new Query();
								envia.executaUpdate("UPDATE opcoes SET caminhobackupauto = '" + (file.getPath().replace(File.separator, "/")) + "'");
								envia.fechaConexao();
							} catch (ClassNotFoundException | SQLException e1) {
								e1.printStackTrace();
								new PainelErro(e1);
							}
		        			finally
		        			{
		        				Configuracao.INSTANCE.setBackupAutoCaminho(file.getPath());
		        			}
		                }						
					}
					else if(e.getSource() == backupAuto)
					{
						if(backupAuto.isSelected())
						{
				    		timerBackup = new Timer();
				    		timerBackup.schedule(new BackupAutomatico(), 30*1000, 60*1000); 							
						}
						else
						{
							timerBackup.cancel();
						}
					}
				}	
			};
			
			MouseListener mouseLi = new MouseListener()
			{
				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if(e.getSource() == labelCria)
					{
						directoryChooser = new WebDirectoryChooser(getOwner(), "Escolha uma pasta");
		                directoryChooser.setVisible(true);
		                
		                if(directoryChooser.getResult() == DialogOptions.OK_OPTION)
		                {
		                    File file = directoryChooser.getSelectedDirectory();
		                    SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy__HH_mm");
		                    
							Process p = null;
				            try {
				                Runtime runtime = Runtime.getRuntime();
				                p = runtime.exec("C:\\Users\\andre\\Desktop\\Pastelaria\\v1.50\\mysql-5.6.15-win32\\bin\\mysqldump.exe -uroot -ppuc4321 --add-drop-database -B pastelaria -r " 
				                + file.getPath()
				                + "/BackupRest_" + formatter.format(new Date()) +".sql");
				                
				                int processComplete = p.waitFor();
				 
				                if (processComplete == 0) {
				                	TooltipManager.showOneTimeTooltip (labelCria, null, "Backup criado com sucesso!", TooltipWay.up );
				                    
				        			Query envia = new Query();
				        			envia.executaUpdate("UPDATE opcoes SET ultimobackup = '" + formatter.format(new Date()) + "'");	
				        			envia.fechaConexao();
				        			
				        			Configuracao.INSTANCE.setUltimoBackup(new Date());
				        			
				        			SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy - HH:mm.");
				        			labelUltimo.setText("Último backup realizado: " + formatter2.format(Configuracao.INSTANCE.getUltimoBackup()));				        			
				                    
				                } else {
				                	TooltipManager.showOneTimeTooltip (labelCria, null, "Erro, não foi possível criar o backup.", TooltipWay.up );
				                }
				            } catch (Exception ex) {
				            	TooltipManager.showOneTimeTooltip (labelCria, null, "Erro, não foi possível criar o backup.", TooltipWay.up );
				                new PainelErro(ex);
				            }		                    
		                }						
					}
					else if(e.getSource() == labelRestaura)
					{
						int opcao = JOptionPane.showConfirmDialog(null, "<html>Todo o banco de dados atual será substituido na restauração. "
								+ "<b>Não é possível reverter esta ação.</b><br><br>"
								+ "Você tem certeza que quer continuar?</html>", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						
						if(opcao == JOptionPane.YES_OPTION)
						{
							WebFileChooser escolherBackup = new WebFileChooser("Selecione o arquivo de Backup");
							escolherBackup.setAcceptAllFileFilterUsed(false);
							FileFilter sqlType = new FileNameExtensionFilter("Backup Restaurante (.sql)", "sql");
							escolherBackup.addChoosableFileFilter(sqlType);
							escolherBackup.setFileFilter(sqlType);
							
							if(escolherBackup.showOpenDialog(getOwner()) == WebFileChooser.APPROVE_OPTION)
							{
								File file = escolherBackup.getSelectedFile();
								if(file.getPath().contains(".sql"))
								{
									String[] restoreCmd = new String[]{"C:\\Users\\andre\\Desktop\\Pastelaria\\v1.50\\mysql-5.6.15-win32\\bin\\mysql.exe"
											+ " ", "--user=" + "root", "--password=" + "puc4321", "-e", "source " + file.getPath()};
									
							        try {
							        	Process runtimeProcess = Runtime.getRuntime().exec(restoreCmd);
							            int processComplete = runtimeProcess.waitFor();
							 
							            if (processComplete == 0) {
							            	TooltipManager.showOneTimeTooltip(labelRestaura, null, "Banco de dados restaurado com sucesso!", TooltipWay.up );
							            	JOptionPane.showMessageDialog(null, "O programa precisa ser reiniciado para concluir a restauração.");
							            	Server.getInstance().enviaTodos("BYE");
							            	System.exit(0);
							            } else {
							            	TooltipManager.showOneTimeTooltip(labelRestaura, null, "Erro, não foi possível restaurar o banco.", TooltipWay.up );
							            }
							        } catch (Exception ex) {
							        	new PainelErro(ex);
							        }										
								}
								else
								{
									TooltipManager.showOneTimeTooltip(labelRestaura, null, "Arquivo de Backup inválido.", TooltipWay.up );
								}
							}
						}						
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if(e.getSource() == labelCria)
					{
						labelCria.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/criar_backup_in.png")));
					}
					else if(e.getSource() == labelRestaura)
					{
						labelRestaura.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/restaurar_backup_in.png")));
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if(e.getSource() == labelCria)
					{
						labelCria.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/criar_backup_out.png")));
					}
					else if(e.getSource() == labelRestaura)
					{
						labelRestaura.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/restaurar_backup_out.png")));
					}					
				}
			};
			
			timerBackup = new Timer();
			
			labelBackupAuto = new JLabel("Backup Automático:");
			labelBackupAuto.setFont(new Font("Helvetica", Font.BOLD, 14));
			labelBackupAuto.setBounds(20,20,250,30); // Coluna, Linha, Largura, Altura!
			backupPainel.add(labelBackupAuto);
			
			backupAuto = new WebSwitch();
			backupAuto.setBounds(175,20,70,30);
			backupAuto.addActionListener(actionLi);
			backupPainel.add(backupAuto);	
			
			labelSalvarAuto = new JLabel("Salvar em:");
			labelSalvarAuto.setFont(new Font("Helvetica", Font.BOLD, 14));
			labelSalvarAuto.setBounds(20,60,250,30); // Coluna, Linha, Largura, Altura!
			backupPainel.add(labelSalvarAuto);
			
			backupLocalButton = new WebButton("Escolha a pasta");
			backupLocalButton.setBounds(175,60,200,30);
			backupLocalButton.addActionListener(actionLi);
			backupPainel.add(backupLocalButton);
			
			labelHoras = new JLabel("Intervalo em Horas:");
			labelHoras.setFont(new Font("Helvetica", Font.BOLD, 14));
			labelHoras.setBounds(20,100,250,30);
			backupPainel.add(labelHoras);			
			
			backupHoras = new WebSlider(WebSlider.HORIZONTAL);
			backupHoras.setMinimum(0);
			backupHoras.setMaximum(48);
			backupHoras.setMinorTickSpacing(2);
			backupHoras.setMajorTickSpacing(12);
			backupHoras.setPaintTicks(true);
			backupHoras.setPaintLabels(true);
			backupHoras.setBounds(170,110,300,45);
			backupPainel.add(backupHoras);
			
			labelCria = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/criar_backup_out.png")));
			labelCria.setBounds(50, 200, 150, 100); // Coluna, Linha, Largura, Altura
			labelCria.addMouseListener(mouseLi);
			backupPainel.add(labelCria);
			
			labelRestaura = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/restaurar_backup_out.png")));
			labelRestaura.setBounds(280, 200, 210, 100); // Coluna, Linha, Largura, Altura
			labelRestaura.addMouseListener(mouseLi);
			backupPainel.add(labelRestaura);			
			
			labelUltimo = new JLabel("Último backup realizado: nunca");
			labelUltimo.setFont(new Font("Verdana", Font.ITALIC, 10));
			labelUltimo.setBounds(20,330,300,20); // Coluna, Linha, Largura, Altura!
			backupPainel.add(labelUltimo);
			
			add(backupPainel);
			setPreferredSize(new Dimension(510, 390));			
			setResizable(false);
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm.");
			labelUltimo.setText("Último backup realizado: " + formatter.format(Configuracao.INSTANCE.getUltimoBackup()));
			
			if(Configuracao.INSTANCE.getBackupAuto())
				backupAuto.setSelected(true);	// ja inicia o timer aqui.
			
			backupHoras.setValue(Configuracao.INSTANCE.getBackupAutoIntervalo());
			if(!UtilCoffe.vaziu(Configuracao.INSTANCE.getBackupAutoCaminho()))
			{
				File file = new File(Configuracao.INSTANCE.getBackupAutoCaminho());
				if(file.exists())
				{
	                backupLocalButton.setIcon(FileUtils.getFileIcon(file));
	                backupLocalButton.setText(FileUtils.getDisplayFileName(file));				
				}
				else
				{
					Configuracao.INSTANCE.setBackupAutoCaminho("");
					backupLocalButton.setText("Escolha uma pasta");	
				}				
			}
			else
			{
				backupLocalButton.setText("Escolha uma pasta");
			}
		}

		public void atualizaCampos()
		{
			
			try {
				Query envia = new Query();
				if(backupAuto.isSelected())
					envia.executaUpdate("UPDATE opcoes SET backupauto = 1");
				else
					envia.executaUpdate("UPDATE opcoes SET backupauto = 0");
				
				envia.executaUpdate("UPDATE opcoes SET intervalobackupauto = " + backupHoras.getValue());	
				envia.fechaConexao();
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
				new PainelErro(e1);
			}
			finally{
				
			}
		}
	}	
}