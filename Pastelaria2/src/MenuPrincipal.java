import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.event.*;

public class MenuPrincipal implements MouseListener
{
	private JFrame janela;
	private JPanel principalPainel1, principalPainel, mesasPainel, mesasPainel1, menuPainel, footerPainel;
	private JButton[] mesas;
	private JButton vendaRapida, consulta, fiados, produtos, funcionarios;
	private JLabel legenda;
	private int qntdMesas;
	
	public MenuPrincipal()
	{
		qntdMesas = 60;
		
		janela = new JFrame("Pastelaço - Controle de Caixa");
		principalPainel1 = new JPanel();
		mesasPainel1 = new JPanel();
		mesasPainel = new JPanel();
		menuPainel = new JPanel();
		footerPainel = new JPanel();
		
		janela.setSize(800,600);
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
		principalPainel1.setMaximumSize(new Dimension(800, 600));
		
		mesasPainel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Mesas"));
		mesasPainel1.setLayout(new BoxLayout(mesasPainel1, BoxLayout.Y_AXIS));
		mesasPainel1.setMinimumSize(new Dimension(800, 430));
		mesasPainel1.setMaximumSize(new Dimension(800, 430));		
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		mesasPainel.setLayout(new GridBagLayout());
		mesasPainel.setMinimumSize(new Dimension(800, 390));
		mesasPainel.setMaximumSize(new Dimension(800, 390));
		
		int colunas = 0;
		int linhas  = 0;
		int quebra_linhas = 0;
		
		mesas = new JButton[qntdMesas];
		ImageIcon[] iconeMesas = new ImageIcon[qntdMesas];
		for(int i = 0; i < qntdMesas; i++)
		{
			String nomeMesa = "Mesa ";
			mesas[i] = new JButton(nomeMesa + (i+1));
			mesas[i].setPreferredSize(new Dimension(100, 52));
			iconeMesas[i] = new ImageIcon("imgs/mesa.png");
			mesas[i].setIcon(iconeMesas[i]);
			mesas[i].setHorizontalTextPosition(AbstractButton.CENTER);
			mesas[i].setVerticalTextPosition(AbstractButton.BOTTOM);
			mesas[i].addMouseListener(this);

			gbc.gridx = colunas;
			gbc.gridy = linhas;
			gbc.insets = new Insets(10,10,0,0);  //top padding
			
			mesasPainel.add(mesas[i], gbc);
			
			quebra_linhas++;
			colunas++;
			
			if(quebra_linhas >= 6)
			{
				colunas = 0;
				linhas++;
				quebra_linhas = 0;
			}
		}
		
		JScrollPane scroll = new JScrollPane(mesasPainel);
		scroll.setMinimumSize(new Dimension(800, 390));
		scroll.setMaximumSize(new Dimension(800, 390));
		scroll.setBorder(BorderFactory.createEmptyBorder());
		mesasPainel1.add(scroll);
		
		menuPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Menu"));
		menuPainel.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));
		menuPainel.setMaximumSize(new Dimension(800, 100));
		menuPainel.setMinimumSize(new Dimension(800, 100));
		
		vendaRapida = new JButton("Venda Rápida");
		ImageIcon iconeRapida = new ImageIcon("imgs/vrapida.png");
		vendaRapida.setIcon(iconeRapida);
		vendaRapida.setPreferredSize(new Dimension(180, 60));
		vendaRapida.addMouseListener(this);
		menuPainel.add(vendaRapida);
		
		produtos = new JButton("Produtos");
		produtos.setPreferredSize(new Dimension(140, 60));
		ImageIcon iconeProdutos = new ImageIcon("imgs/produtos.png");
		produtos.setIcon(iconeProdutos);
		produtos.addMouseListener(this);
		menuPainel.add(produtos);
		
		funcionarios = new JButton("Funcionários");
		funcionarios.setPreferredSize(new Dimension(170, 60));
		ImageIcon iconeFuncionarios = new ImageIcon("imgs/funcionarios.png");
		funcionarios.setIcon(iconeFuncionarios);
		funcionarios.addMouseListener(this);
		menuPainel.add(funcionarios);		
		
		consulta = new JButton("Vendas");
		consulta.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeVendas = new ImageIcon("imgs/consultar.png");
		consulta.setIcon(iconeVendas);
		consulta.addMouseListener(this);
		menuPainel.add(consulta);
		
		fiados = new JButton("Fiados");
		fiados.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeFiados = new ImageIcon("imgs/fiados.png");
		fiados.setIcon(iconeFiados);
		fiados.addMouseListener(this);
		menuPainel.add(fiados);	
		
		principalPainel1.add(menuPainel);
		principalPainel1.add(mesasPainel1);
		
		footerPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Legenda"));
		footerPainel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		footerPainel.setMaximumSize(new Dimension(800, 45));
		footerPainel.setMinimumSize(new Dimension(800, 45));
		
		legenda = new JLabel("Desenvolvido por CodeCoffe (C) - 2013");
		legenda.setFont(new Font("Verdana", Font.PLAIN, 14));
		footerPainel.add(legenda);
		
		principalPainel1.add(footerPainel);
		
		janela.add(principalPainel1);
		janela.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		MenuPrincipal Principal = new MenuPrincipal();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(e.getSource() == vendaRapida)
		{
			legenda.setText("Venda Rápida, direto no balcão.");
		}
		if(e.getSource() == produtos)
		{
			legenda.setText("Gerenciamento de produtos/adicionais.");
		}
		if(e.getSource() == funcionarios)
		{
			legenda.setText("Gerenciamento de funcionários.");
		}
		if(e.getSource() == consulta)
		{
			legenda.setText("Consulte as vendas de determinada data.");
		}
		if(e.getSource() == fiados)
		{
			legenda.setText("Clientes que ainda não pagaram.");
		}				
		else
		{
			for(int i = 0; i < qntdMesas; i++)
			{
				if(e.getSource() == mesas[i])
				{
					legenda.setText("Mesa " + (i+1));
					break;
				}
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		legenda.setText("Desenvolvido por CodeCoffe (C) - 2013");
	}
}