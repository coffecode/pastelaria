package codecoffe.restaurantes.produtos;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.alee.laf.WebLookAndFeel;

import codecoffe.restaurantes.interfaceGrafica.PainelProdutos;
import net.miginfocom.swing.MigLayout;

public class Teste {
	
	private ProdutosComboBox combo;
	
	public Teste()
	{
		WebLookAndFeel.install();
		PainelProdutos.getInstance();
		
		JFrame frame = new JFrame("Teste");
		JPanel panel = new JPanel(new MigLayout("align center"));
		panel.setPreferredSize(new Dimension(600, 300));
		panel.setMaximumSize(new Dimension(600, 300));
		
		combo = new ProdutosComboBox(PainelProdutos.getInstance().getTodosProdutosArvore());
		combo.setPreferredSize(new Dimension(350, 35));
		panel.add(combo, "wrap");
		
		JButton hoi = new JButton("haha");
		hoi.setPreferredSize(new Dimension(120, 40));
		
		ActionListener li = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Selecionado: " + combo.getProdutoSelecionado().toString());
			}
		};
		
		hoi.addActionListener(li);
		
		panel.add(hoi, "span");
		
		frame.add(panel);
		frame.setSize(new Dimension(600, 300));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new Teste();
	}
}
