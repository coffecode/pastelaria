import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.*;

import com.alee.laf.WebLookAndFeel;
import com.jtattoo.plaf.smart.SmartLookAndFeel;

public class MenuPrincipal 
{
	private static JFrame janela;
	private static JPanel principalPainel1;
	
	private static PainelMenu menuPainel;
	private static PainelMesas menuMesas;
	private static PainelLegenda menuFooter;
	private static PainelVenda menuVendaMesa;
	private static PainelStatus menuStatus;
	private static PainelVendaRapida menuVendaRapida;
	private static PainelProdutos menuProdutos;
	private static PainelFuncionarios menuFuncionarios;
	private static PainelVendas menuVendas;
	
	private Timer t;
	private int loadingTime = 0;
	private static int qntdMesas = 0;
	
	public MenuPrincipal()
	{
		/*Properties props = new Properties();
		props.put("logoString", "CodeCoffe");
		SmartLookAndFeel.setCurrentTheme(props);			
		
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {//
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		WebLookAndFeel.install();
		
		janela = new JFrame("Pastelaço - Controle de Caixa v1.00"); //{
            /*@Override
            public void paint(Graphics g) {
                Dimension d = getSize();
                Dimension m = getMaximumSize();
                boolean resize = d.width > m.width || d.height > m.height;
                d.width = Math.min(m.width, d.width);
                d.height = Math.min(m.height, d.height);
                if (resize) {
                    Point p = getLocation();
                    setVisible(false);
                    setSize(d);
                    setLocation(p);
                    setVisible(true);
                }
                super.paint(g);
            }*/
		//};
		
		principalPainel1 = new JPanel();
		
   	 	t = new Timer(500, new ActionListener()
   	 	{
   	 		public void actionPerformed(ActionEvent ae)
   	 		{	
   	 			switch(loadingTime)
   	 			{
   	 				case 0:
   	 				{
   	 					Starter.setarProgresso("Carregando menu...", 25);
   	 					menuPainel = new PainelMenu();
   	 					break;
   	 				}
   	 				case 1:
   	 				{
   	 					Starter.setarProgresso("Carregando mesas...", 30);
   	 					
   	 					Query pega2 = new Query();
   	 					pega2.executaQuery("SELECT mesas FROM opcoes");
   	 			
   	 					if(pega2.next())
   	 						qntdMesas = pega2.getInt("mesas");
   	 			
   	 					pega2.fechaConexao();
   	 					menuMesas = new PainelMesas(qntdMesas);
   	 					break;
   	 				}
   	 				case 2:
   	 				{
   	 					Starter.setarProgresso("Carregando legenda...", 35);
   	 					menuFooter = new PainelLegenda();
   	 					break;
   	 				}
   	 				case 3:
   	 				{
   	 					Starter.setarProgresso("Carregando barra de status...", 40);
   	 					menuStatus = new PainelStatus();
   	 					break;
   	 				}
   	 				case 4:
   	 				{
   	 					Starter.setarProgresso("Carregando painel de venda rápida...", 50);
   	 					menuVendaRapida = new PainelVendaRapida(true);
   	 					break;
   	 				}
   	 				case 5:
   	 				{
   	 					Starter.setarProgresso("Carregando produtos & adicionais...", 60);
   	 					menuProdutos = new PainelProdutos();
   	 					break;
   	 				}  
   	 				case 6:
   	 				{
   	 					Starter.setarProgresso("Carregando funcionarios...", 70);
   	 					menuFuncionarios = new PainelFuncionarios();
   	 					break;
   	 				}
   	 				case 7:
   	 				{
   	 					Starter.setarProgresso("Carregando painel de vendas...", 80);
   	 					menuVendas = new PainelVendas();
   	 					break;
   	 				}   	 				
   	 				case 8:
   	 				{
   	 					Starter.setarProgresso("Carregando componentes do sistema...", 95);
   	 					break;
   	 				}    	 				
   	 				case 9:
   	 				{
   	 					janela.setMinimumSize(new Dimension(800, 640));	
   	 					janela.setMaximumSize(new Dimension(1920, 1080));	
   	 					janela.setLocationRelativeTo(null);
   	 					janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 	 					
   	 	        
   	 					janela.addWindowListener(new WindowAdapter()
   	 					{
   	 						public void windowClosing(WindowEvent e)
   	 						{
   	 							int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja sair?", "Logout",JOptionPane.YES_NO_OPTION);
   	 	    			
   	 							if(opcao == JOptionPane.YES_OPTION)
   	 							{
   	 								janela.setVisible(false);
   	 								DiarioLog.add("Saiu do sistema.", 9);
   	 								MenuLogin LoginNovo = new MenuLogin();
   	 								LoginNovo.setVisible(true);	
   	 							}
   	 						}
   	 					});	
   	 			
   	 					principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
   	 					principalPainel1.setMaximumSize(new Dimension(800, 640));		
   	 			
   	 					principalPainel1.add(menuStatus);
   	 					principalPainel1.add(menuPainel);
   	 					principalPainel1.add(menuMesas);
   	 					principalPainel1.add(menuFooter);
   	 			
   	 					janela.add(principalPainel1);
   	 					janela.setResizable(false);
   	 					janela.setIconImage(new ImageIcon(getClass().getResource("imgs/icone_programa.png")).getImage());
   	 					
   	 					Starter.setarProgresso("Carregando login...", 95);
   	 					MenuLogin login = new MenuLogin();
   	 				
   	 					try {
							Thread.sleep(1500);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
   	 					
   	 					Starter.loadingFinalizado();
   	 					login.setVisible(true);
   	 					break;
   	 				}   	 				
   	 				default:
   	 				{
   	 					t.stop();
   	 				}
   	 			}
   	 			
   	 			loadingTime++;
   	 		}
   	 	});
   	 	
   	 	t.start();
	}
	
	static public void setarVisible(boolean set)
	{
		janela.setVisible(set);
	}
	
	static public void setarEnter(JButton j)
	{
		janela.getRootPane().setDefaultButton(j);
	}
	
	static public void DeletarPrincipal()
	{
		janela.dispose();
	}
	
	static public void Ativar(boolean set)
	{
		janela.setEnabled(set);
	}

	static public void AbrirPrincipal(int modelo, boolean refresh)
	{
		if(modelo == 0)			// Abre o menu principal
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			
			PainelVendaRapida menuVendaRapida = new PainelVendaRapida(refresh);
			
			principalPainel1.add(menuVendaRapida);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}	
		
		if(modelo == 1)			// Abre o menu de produtos
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			principalPainel1.add(menuProdutos);
			principalPainel1.add(menuProdutos);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}
		if(modelo == 2)			// Abre o menu de funcionarios
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			principalPainel1.add(menuFuncionarios);
			principalPainel1.add(menuFuncionarios);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}
		if(modelo == 3)			// Abre o menu de vendas
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			UltimasVendas.refresh();
			principalPainel1.add(menuVendas);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}
		if(modelo == 4)			// Abre o inicio.
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			
			PainelMesas.refresh();
			principalPainel1.add(menuMesas);
			principalPainel1.add(menuFooter);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}						
		if(modelo>= 5)
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			
			PainelVenda menuVenda = new PainelVenda(refresh, modelo);
			
			principalPainel1.add(menuVenda);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}
	}
}