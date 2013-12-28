import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.*;


public class PainelMenu extends JPanel implements MouseListener
{
	private JButton vendaRapida, consulta, fiados, produtos, funcionarios;
	private int funcionario;
	
	public PainelMenu(int level)
	{
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Menu"));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));
		this.setMaximumSize(new Dimension(800, 100));
		this.setMinimumSize(new Dimension(800, 100));
		
		this.vendaRapida = new JButton("Venda R�pida");
		ImageIcon iconeRapida = new ImageIcon("imgs/vrapida.png");
		this.vendaRapida.setIcon(iconeRapida);
		this.vendaRapida.setPreferredSize(new Dimension(170, 60));
		this.vendaRapida.addMouseListener(this);
		this.add(vendaRapida);
		
		this.produtos = new JButton("Produtos");
		this.produtos.setPreferredSize(new Dimension(140, 60));
		ImageIcon iconeProdutos = new ImageIcon("imgs/produtos.png");
		this.produtos.setIcon(iconeProdutos);
		this.produtos.addMouseListener(this);
		this.add(produtos);
		
		this.funcionarios = new JButton("Funcion�rios");
		this.funcionarios.setPreferredSize(new Dimension(170, 60));
		ImageIcon iconeFuncionarios = new ImageIcon("imgs/funcionarios.png");
		this.funcionarios.setIcon(iconeFuncionarios);
		this.funcionarios.addMouseListener(this);
		this.add(funcionarios);		
		
		this.consulta = new JButton("Vendas");
		this.consulta.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeVendas = new ImageIcon("imgs/consultar.png");
		this.consulta.setIcon(iconeVendas);
		this.consulta.addMouseListener(this);
		this.add(consulta);
		
		this.fiados = new JButton("Fiados");
		this.fiados.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeFiados = new ImageIcon("imgs/fiados.png");
		this.fiados.setIcon(iconeFiados);
		this.fiados.addMouseListener(this);
		this.add(fiados);
		
		this.funcionario = level;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(e.getSource() == vendaRapida) // apenas teste
		{
			MenuPrincipal.AbrirPrincipal(0, true);
		}
		else if(e.getSource() == produtos)
		{
			if(this.funcionario > 1)
				MenuPrincipal.AbrirPrincipal(1, false);
			else
				JOptionPane.showMessageDialog(null, "Voc� n�o tem permiss�o para ver isso.");
		}
		else if(e.getSource() == funcionarios)
		{
			if(this.funcionario > 1)
				MenuPrincipal.AbrirPrincipal(2, false);
			else
				JOptionPane.showMessageDialog(null, "Voc� n�o tem permiss�o para ver isso.");
		}
		else if(e.getSource() == consulta)
		{
			if(this.funcionario > 1)
				MenuPrincipal.AbrirPrincipal(3, false);
			else
				JOptionPane.showMessageDialog(null, "Voc� n�o tem permiss�o para ver isso.");
		}
		else if(e.getSource() == fiados)
		{
			MenuPrincipal.AbrirPrincipal(4, false);
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