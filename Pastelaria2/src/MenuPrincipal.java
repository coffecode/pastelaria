import java.awt.*;

import javax.swing.*;

public class MenuPrincipal
{
	private static JFrame janela;
	private static JPanel principalPainel1;
	
	private static PainelMenu menuPainel;
	private static PainelMesas menuMesas;
	private static PainelLegenda menuFooter;
	private static PainelVenda menuVendaMesa;
	private static PainelStatus menuStatus;
	
	public MenuPrincipal(int level, String nome)
	{
		janela = new JFrame("Pastelaço - Controle de Caixa v1.00");
		principalPainel1 = new JPanel();
		
		menuPainel = new PainelMenu(level);
		menuMesas = new PainelMesas(35);
		menuFooter = new PainelLegenda();
		
		menuVendaMesa = new PainelVenda();
		menuStatus = new PainelStatus(nome);
		
		janela.setSize(800,640);
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
		principalPainel1.setMaximumSize(new Dimension(800, 640));		
		
		principalPainel1.add(menuStatus);
		principalPainel1.add(menuPainel);
		principalPainel1.add(menuMesas);
		principalPainel1.add(menuFooter);
		
		janela.add(principalPainel1);
		janela.setResizable(false);
		janela.setVisible(true);
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
		
		if(modelo == 1)			// Abre o menu de vendas da mesa
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			
			PainelProdutos menuProdutos = new PainelProdutos();
			
			principalPainel1.add(menuProdutos);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}
		if(modelo == 2)			// Abre o menu de funcionarios
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuStatus);
			principalPainel1.add(menuPainel);
			
			PainelFuncionarios menuFuncionarios = new PainelFuncionarios();
			
			principalPainel1.add(menuFuncionarios);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}		
	}
}