import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.event.*;

public class VendaRapida 
{
	private JFrame janela;
	private JPanel principalPainel1, principalPainel, adicionarPainel, todosPainel;
	private JButton botaoAdicionar;
	private JLabel total, quantidade, labelProduto, labelValor, labelCodigo;
	private JTextField campoQuantidade, campoValor, campoCodigo;
	
	private AutoSuggest addProduto;
	
	public VendaRapida()
	{
		janela = new JFrame("Pastelaço - Venda Rápida");
		principalPainel1 = new JPanel();
		adicionarPainel = new JPanel();
		
		janela.setSize(800,600);
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
		principalPainel1.setMaximumSize(new Dimension(800, 600));
		
		adicionarPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Adicionar"));
		adicionarPainel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
		adicionarPainel.setMaximumSize(new Dimension(400, 200));
		adicionarPainel.setMinimumSize(new Dimension(400, 200));
		adicionarPainel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		labelProduto = new JLabel("Produto:");
		adicionarPainel.add(labelProduto);
		
		addProduto = new AutoSuggest();
		adicionarPainel.add(addProduto);		
		
		quantidade = new JLabel("Qtd:");
		adicionarPainel.add(quantidade);
		campoQuantidade = new JTextField("1", 2);
		adicionarPainel.add(campoQuantidade);
		
		labelValor = new JLabel("Valor:");
		adicionarPainel.add(labelValor);
		
		campoValor = new JTextField("0.00", 5);
		adicionarPainel.add(campoValor);
		
		labelCodigo = new JLabel("Codigo:");
		adicionarPainel.add(labelCodigo);
		
		campoCodigo = new JTextField("0", 4);
		campoCodigo.setEditable(false);
		adicionarPainel.add(campoCodigo);		
		
		botaoAdicionar = new JButton("Adicionar");
		botaoAdicionar.setPreferredSize(new Dimension(170, 50));
		adicionarPainel.add(botaoAdicionar);
		
		principalPainel1.add(adicionarPainel);
		
		janela.add(principalPainel1);
		janela.setVisible(true);		
	}
	
	public static void main(String[] args)
	{
		VendaRapida Vendinha = new VendaRapida();
	}
}