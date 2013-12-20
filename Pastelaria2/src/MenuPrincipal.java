import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.*;

public class MenuPrincipal
{
	private JFrame janela;
	private static JPanel principalPainel1;
	
	private static PainelMenu menuPainel;
	private static PainelMesas menuMesas;
	private static PainelLegenda menuFooter;
	private static PainelVenda menuVendaMesa;
	
	public MenuPrincipal()
	{
		janela = new JFrame("Pastelaço - Controle de Caixa");//
		principalPainel1 = new JPanel();
		
		menuPainel = new PainelMenu();
		menuMesas = new PainelMesas(35);
		menuFooter = new PainelLegenda();
		
		menuVendaMesa = new PainelVenda();
		
		janela.setSize(800,600);
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
		principalPainel1.setMaximumSize(new Dimension(800, 600));		
		
		principalPainel1.add(menuPainel);
		principalPainel1.add(menuMesas);
		principalPainel1.add(menuFooter);
		
		janela.add(principalPainel1);
		janela.setVisible(true);
	}
	
	static public void AbrirPrincipal(int modelo)
	{
		if(modelo == 0)			// Abre o menu principal
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuPainel);
			principalPainel1.add(menuMesas);
			principalPainel1.add(menuFooter);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}
		if(modelo == 1)			// Abre o menu de vendas da mesa
		{
			principalPainel1.removeAll();
			principalPainel1.add(menuPainel);
			principalPainel1.add(menuVendaMesa);
			principalPainel1.revalidate();
			principalPainel1.repaint();
		}		
	}
	
	public static void main(String[] args)
	{
		MenuPrincipal Principal = new MenuPrincipal();
	}
}