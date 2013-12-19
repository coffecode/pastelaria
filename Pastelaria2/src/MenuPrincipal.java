import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MenuPrincipal 
{
	private JFrame janela;
	private JPanel mesasPainel;
	private JButton[] mesas;
	
	public MenuPrincipal()
	{
		janela = new JFrame("Pastelaço humhum");
		mesasPainel = new JPanel();
		
		janela.setSize(800,600);							// Largura, Altura
		janela.setLocationRelativeTo(null);					// Abre no centro da tela
		janela.setLayout(null);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mesasPainel.setSize(600,400);	
		mesasPainel.setLayout(null);
		mesasPainel.setBackground(Color.BLUE);
		
		mesas = new JButton[30];
		
		int quebra_linha = 0;
		int linha = 15;
		int coluna = 15;
		
		for(int i = 0; i < 30; i++)
		{
			String nomeMesa = "Mesa ";
			mesas[i] = new JButton(nomeMesa + (i+1));
			mesas[i].setBounds(15 + (quebra_linha*110),linha,100,40); 		// Coluna, Linha, Largura, Altura!
			mesasPainel.add(mesas[i]);
			
			quebra_linha++;
			if(quebra_linha >= 5)
			{
				linha += 60;
				coluna = 15;
				quebra_linha = 0;
			}
		}
		
		janela.add(mesasPainel);
		janela.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		MenuPrincipal Principal = new MenuPrincipal();
	}	
}
