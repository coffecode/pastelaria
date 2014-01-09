import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class PainelMenu extends JPanel implements MouseListener
{
	private JButton vendaRapida, consulta, inicio, produtos, funcionarios;
	static private int funcionario;
	
	public PainelMenu()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER, 6, 20));
		setMaximumSize(new Dimension(1920, 100));
		setMinimumSize(new Dimension(800, 100));
		
		inicio = new JButton("Início");
		inicio.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeInicio = new ImageIcon(getClass().getResource("imgs/inicio.png"));
		inicio.setIcon(iconeInicio);
		inicio.addMouseListener(this);
		add(inicio);		
		
		vendaRapida = new JButton("Venda Rápida");
		ImageIcon iconeRapida = new ImageIcon(getClass().getResource("imgs/vrapida.png"));
		vendaRapida.setIcon(iconeRapida);
		vendaRapida.setPreferredSize(new Dimension(170, 60));
		vendaRapida.addMouseListener(this);
		add(vendaRapida);
		
		produtos = new JButton("Produtos");
		produtos.setPreferredSize(new Dimension(140, 60));
		ImageIcon iconeProdutos = new ImageIcon(getClass().getResource("imgs/produtos.png"));
		produtos.setIcon(iconeProdutos);
		produtos.addMouseListener(this);
		add(produtos);
		
		funcionarios = new JButton("Funcionários");
		funcionarios.setPreferredSize(new Dimension(170, 60));
		ImageIcon iconeFuncionarios = new ImageIcon(getClass().getResource("imgs/funcionarios.png"));
		funcionarios.setIcon(iconeFuncionarios);
		funcionarios.addMouseListener(this);
		add(funcionarios);		
		
		consulta = new JButton("Vendas");
		consulta.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeVendas = new ImageIcon(getClass().getResource("imgs/consultar.png"));
		consulta.setIcon(iconeVendas);
		consulta.addMouseListener(this);
		add(consulta);
		
		funcionario = 1;
		
		ActionMap actionMap = getActionMap();
		actionMap.put("botao1", new SpaceAction(4));
		actionMap.put("botao2", new SpaceAction(0));
		actionMap.put("botao3", new SpaceAction(1));
		actionMap.put("botao4", new SpaceAction(2));
		actionMap.put("botao5", new SpaceAction(3));
		actionMap.put("botao6", new SpaceAction(999));
		setActionMap(actionMap);
		
		InputMap imap = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke("F1"), "botao1");
		imap.put(KeyStroke.getKeyStroke("F2"), "botao2");
		imap.put(KeyStroke.getKeyStroke("F3"), "botao3"); 
		imap.put(KeyStroke.getKeyStroke("F4"), "botao4"); 
		imap.put(KeyStroke.getKeyStroke("F5"), "botao5");
		imap.put(KeyStroke.getKeyStroke("ESCAPE"), "botao6");
	}
	
	private class SpaceAction extends AbstractAction {
		
		private int tipo = 0;
		
		public SpaceAction() {
	        super();
	    }
		
		public SpaceAction(int tipo) {
	        this.tipo = tipo;
	    }		
		
        @Override
        public void actionPerformed(ActionEvent e) {
        	
        	if(this.tipo == 999)
        	{
    			int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja sair?", "Logout", JOptionPane.YES_NO_OPTION);
    			
    			if(opcao == JOptionPane.YES_OPTION)
    			{
    				DiarioLog.add("Saiu do sistema.", 9);
    				MenuPrincipal.setarVisible(false);
    				MenuLogin LoginNovo = new MenuLogin();
    				LoginNovo.setVisible(true);	
    			}        		
        	}
        	else
        	{
        		MenuPrincipal.AbrirPrincipal(this.tipo, false);
        	}
        }
    }	
	
	static public void setarLevel(int lvl)
	{
		funcionario = lvl;
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
			if(funcionario > 1)
				MenuPrincipal.AbrirPrincipal(1, false);
			else
				JOptionPane.showMessageDialog(null, "Você não tem permissão para ver isso.");
		}
		else if(e.getSource() == funcionarios)
		{
			if(funcionario > 1)
				MenuPrincipal.AbrirPrincipal(2, false);
			else
				JOptionPane.showMessageDialog(null, "Você não tem permissão para ver isso.");
		}
		else if(e.getSource() == consulta)
		{
			if(funcionario > 1)
				MenuPrincipal.AbrirPrincipal(3, false);
			else
				JOptionPane.showMessageDialog(null, "Você não tem permissão para ver isso.");
		}
		else if(e.getSource() == inicio)
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
		if(e.getSource() == inicio)
		{
			PainelLegenda.AtualizaLegenda("Início do programa (mesas).");
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		PainelLegenda.AtualizaLegenda("Desenvolvido por CodeCoffe (C) - 2013");
	}
}