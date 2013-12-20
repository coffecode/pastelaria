import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.event.*;


public class PainelMenu extends JPanel implements MouseListener 
{
	private JButton vendaRapida, consulta, fiados, produtos, funcionarios;
	
	public PainelMenu()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Menu"));
		setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));
		setMaximumSize(new Dimension(800, 100));
		setMinimumSize(new Dimension(800, 100));
		
		vendaRapida = new JButton("Venda Rápida");
		ImageIcon iconeRapida = new ImageIcon("imgs/vrapida.png");
		vendaRapida.setIcon(iconeRapida);
		vendaRapida.setPreferredSize(new Dimension(180, 60));
		vendaRapida.addMouseListener(this);
		add(vendaRapida);
		
		produtos = new JButton("Produtos");
		produtos.setPreferredSize(new Dimension(140, 60));
		ImageIcon iconeProdutos = new ImageIcon("imgs/produtos.png");
		produtos.setIcon(iconeProdutos);
		produtos.addMouseListener(this);
		add(produtos);
		
		funcionarios = new JButton("Funcionários");
		funcionarios.setPreferredSize(new Dimension(170, 60));
		ImageIcon iconeFuncionarios = new ImageIcon("imgs/funcionarios.png");
		funcionarios.setIcon(iconeFuncionarios);
		funcionarios.addMouseListener(this);
		add(funcionarios);		
		
		consulta = new JButton("Vendas");
		consulta.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeVendas = new ImageIcon("imgs/consultar.png");
		consulta.setIcon(iconeVendas);
		consulta.addMouseListener(this);
		add(consulta);
		
		fiados = new JButton("Fiados");
		fiados.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeFiados = new ImageIcon("imgs/fiados.png");
		fiados.setIcon(iconeFiados);
		fiados.addMouseListener(this);
		add(fiados);			
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(e.getSource() == vendaRapida) // apenas teste
		{
			MenuPrincipal.AbrirPrincipal(0);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		if(e.getSource() == vendaRapida)
		{
			PainelLegenda.AtualizaLegenda("Venda Rápida, direto no balcão.");
		}
		if(e.getSource() == produtos)
		{
			PainelLegenda.AtualizaLegenda("Gerenciamento de produtos/adicionais.");
		}
		if(e.getSource() == funcionarios)
		{
			PainelLegenda.AtualizaLegenda("Gerenciamento de funcionários.");
		}
		if(e.getSource() == consulta)
		{
			PainelLegenda.AtualizaLegenda("Consulte as vendas de determinada data.");
		}
		if(e.getSource() == fiados)
		{
			PainelLegenda.AtualizaLegenda("Clientes que ainda não pagaram.");
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		PainelLegenda.AtualizaLegenda("Desenvolvido por CodeCoffe (C) - 2013");
	}
}