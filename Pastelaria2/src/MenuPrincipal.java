import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.*;

public class MenuPrincipal 
{
	private JFrame janela;
	private JPanel principalPainel1, principalPainel, mesasPainel, menuPainel, footerPainel;
	private JButton[] mesas;
	private JButton vendaRapida, consulta, opcoes, produtos, adicionais, funcionarios;
	private JLabel legenda;
	
	public MenuPrincipal()
	{
		janela = new JFrame("Pastelaço");
		principalPainel1 = new JPanel();
		principalPainel = new JPanel();
		mesasPainel = new JPanel();
		menuPainel = new JPanel();
		footerPainel = new JPanel();
		
		janela.setSize(800,600);							// Largura, Altura
		janela.setLocationRelativeTo(null);					// Abre no centro da tela
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
		principalPainel1.setMaximumSize(new Dimension(800, 600));
		
		principalPainel.setLayout(new BoxLayout(principalPainel, BoxLayout.X_AXIS));
		principalPainel.setMaximumSize(new Dimension(800, 530));
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		mesasPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Mesas"));
		mesasPainel.setLayout(new GridBagLayout());
		mesasPainel.setMinimumSize(new Dimension(550, 530));
		mesasPainel.setMaximumSize(new Dimension(550, 530));
		mesasPainel.setBackground(Color.GRAY);
		
		int colunas = 0;
		int linhas  = 0;
		int quebra_linhas = 0;
		
		mesas = new JButton[60];
		for(int i = 0; i < 60; i++)
		{
			String nomeMesa = "Mesa ";
			mesas[i] = new JButton(nomeMesa + (i+1));
			mesas[i].setPreferredSize(new Dimension(100, 40));
			
			gbc.gridx = colunas;
			gbc.gridy = linhas;
			gbc.insets = new Insets(10,10,0,0);  //top padding
			
			mesasPainel.add(mesas[i], gbc);
			
			quebra_linhas++;
			colunas++;
			
			if(quebra_linhas >= 4)
			{
				colunas = 0;
				linhas++;
				quebra_linhas = 0;
			}
		}
		
		mesasPainel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JScrollPane scroll = new JScrollPane(mesasPainel);
		scroll.setMinimumSize(new Dimension(550, 530));
		scroll.setMaximumSize(new Dimension(550, 530));
		scroll.setBorder(BorderFactory.createEmptyBorder());
		
		principalPainel.add(scroll);
		
		menuPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Menu"));
		menuPainel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 11));
		menuPainel.setMaximumSize(new Dimension(245, 530));
		menuPainel.setMinimumSize(new Dimension(245, 530));
		menuPainel.setAlignmentX(Component.LEFT_ALIGNMENT);
		menuPainel.setBackground(Color.GRAY);
		
		vendaRapida = new JButton("Venda Rapida");
		vendaRapida.setPreferredSize(new Dimension(200, 70));
		menuPainel.add(vendaRapida);
		
		produtos = new JButton("Gerenciar Produtos");
		produtos.setPreferredSize(new Dimension(200, 70));
		menuPainel.add(produtos);
		
		adicionais = new JButton("Gerenciar Adicionais");
		adicionais.setPreferredSize(new Dimension(200, 70));
		menuPainel.add(adicionais);
		
		funcionarios = new JButton("Gerenciar Funcionários");
		funcionarios.setPreferredSize(new Dimension(200, 70));
		menuPainel.add(funcionarios);		
		
		consulta = new JButton("Consultar Vendas");
		consulta.setPreferredSize(new Dimension(200, 70));
		menuPainel.add(consulta);
		
		opcoes = new JButton("Opções");
		opcoes.setPreferredSize(new Dimension(200, 70));
		menuPainel.add(opcoes);	
		
		principalPainel.add(menuPainel);
		
		footerPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Legenda"));
		footerPainel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		footerPainel.setMaximumSize(new Dimension(800, 48));
		footerPainel.setMinimumSize(new Dimension(800, 48));
		
		legenda = new JLabel("Desenvolvido por CodeCoffe (C) - 2013");
		legenda.setFont(new Font("Verdana", Font.PLAIN, 14));
		footerPainel.add(legenda);
		footerPainel.setBackground(Color.GRAY);
		
		principalPainel1.add(principalPainel);
		principalPainel1.add(footerPainel);
		
		janela.add(principalPainel1);
		janela.setVisible(true);
	}
	
	/*public static void main(String[] args)
	{
		MenuPrincipal Principal = new MenuPrincipal();
	}	*/
}
