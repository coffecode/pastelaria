package codecoffe.restaurantes.produtos;

import java.awt.Color;
import java.awt.Component;
//import java.awt.Container;
//import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
//import javax.swing.JScrollBar;
//import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import com.alee.laf.panel.WebPanel;

import net.miginfocom.swing.MigLayout;
import codecoffe.restaurantes.primitivas.Categoria;
import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

public class ProdutosComboBox extends JComboBox<Object> implements KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProdutosComboModel comboModelCompleto;
	private ProdutosComboEditor comboEditor;
	private boolean flag_aciona = true;
	
	public ProdutosComboBox(List<Categoria> categorias)
	{
		comboModelCompleto = new ProdutosComboModel(categorias);		
		makeComboBox();
	}
	
	public ProdutosComboBox(Categoria c)
	{
		comboModelCompleto = new ProdutosComboModel(c);
		makeComboBox();
	}
	
	public ProdutosComboBox()
	{
		comboModelCompleto = new ProdutosComboModel();
		makeComboBox();
	}
	
	public void makeComboBox()
	{
		setModel(comboModelCompleto);
		setEditable(true);
		
		comboEditor = new ProdutosComboEditor();
		setEditor(comboEditor);
		
		setMaximumRowCount(6);
		setFocusTraversalKeysEnabled(true);
		
		/*Object popup = this.getUI().getAccessibleChild(this , 0);
		Component c = ((Container) popup).getComponent(0);
		if(c instanceof JScrollPane)
		{
			JScrollPane spane = (JScrollPane) c;
			JScrollBar scrollBar = spane .getVerticalScrollBar();
			Dimension scrollBarDim = new Dimension(25, scrollBar.getPreferredSize().height);
			scrollBar.setPreferredSize(scrollBarDim);
		}*/
		
		setRenderer(new ListCellRenderer<Object>() {
			private ProdutoCellPainel painelRender = new ProdutoCellPainel();
			
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Produto produto = (Produto) value;
				
				painelRender.setLabelNome(produto.getNome());
				painelRender.setLabelCodigo("" + produto.getCodigo());
				painelRender.setLabelPreco(UtilCoffe.doubleToPreco(produto.getPreco()));
				
				if(isSelected) {
					painelRender.setUndecorated(false);
				}
				else
					painelRender.setUndecorated(true);
				
				return painelRender;
			}
		});
		
		addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					comboEditor.setProduto((Produto) e.getItem());
					
					/*for(ActionListener a: getThis().getActionListeners()) {
					    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {
					    });
					}*/
				}
			}
        });
		
		comboEditor.getTextField().addKeyListener(this);
	}
	
	public void zerarSelecao()
	{
		// pensar em algo
	}
	
	public void atualizaProdutosCombo(List<Categoria> categorias)
	{
		comboModelCompleto.atualizaProdutosCombo(categorias);
	}
	
	public void atualizaAdicionaisCombo(Categoria c)
	{
		comboModelCompleto.atualizaAdicionaisCombo(c);
	}
	
	public List<Produto> getListaProdutos()
	{
		return comboModelCompleto.getListaProdutos();
	}
	
	/*public ProdutosComboBox getThis()
	{
		return this;
	}*/
	
	public Produto getProdutoSelecionado()
	{
		return (Produto) comboEditor.getItem();
	}
	
	public ProdutosComboModel getModelPossivel(String texto)
	{
		String comparar = UtilCoffe.removeAcentos(texto.toLowerCase());
		ProdutosComboModel modelMutavel = new ProdutosComboModel();
		
		for(int i = 0; i < comboModelCompleto.getSize(); i++)
		{
			Produto p = (Produto) comboModelCompleto.getElementAt(i);
			if(p.contains(comparar)) modelMutavel.addElement(p);
		}
		
		return modelMutavel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				if(flag_aciona)
				{
					String text = comboEditor.getTextField().getText();
	                if(text.length() == 0)
	                {
	                	setModel(comboModelCompleto);
	                	showPopup();
	                }
	                else
	                {
	                	ProdutosComboModel m = getModelPossivel(text);
	                	
	                	if(m.getSize() == 0)
	                	{
	                		hidePopup();
	                	}
	                	else
	                	{
	                		setModel(m);
	                		showPopup();
	                	}
	                }	
				}
				else
					flag_aciona = true;
           }
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code==KeyEvent.VK_ENTER)
		{
			if(getModel().getSize() == 1)
			{
				comboEditor.setProduto((Produto) getModel().getElementAt(0));
			}
			
			comboEditor.getTextField().setText("");
			setModel(comboModelCompleto);
			
			flag_aciona = false;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	class ProdutoCellPainel extends WebPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel labelNome, labelCodigo, labelPreco;
		
		public ProdutoCellPainel()
		{
			setUndecorated(false);
			setLayout(new MigLayout());
			setBorder(new EmptyBorder(2, 2, 2, 2));
			labelNome = new JLabel();
			labelNome.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));
			labelNome.setFont(new Font("Verdana", Font.BOLD, 12));
			
			labelCodigo = new JLabel();
			labelCodigo.setFont(new Font("Verdana", Font.PLAIN, 10));
			labelCodigo.setForeground(Color.RED);
			
			labelPreco = new JLabel();
			labelPreco.setFont(new Font("Verdana", Font.PLAIN, 10));
			labelPreco.setForeground(Color.BLUE);
			
			add(labelNome, "span, wrap");
			add(labelCodigo);
			add(labelPreco, "gapleft 20px");
		}
		
		public void setLabelNome(String texto) {
			labelNome.setText(" " + texto);
		}
		
		public void setLabelPreco(String texto) {
			labelPreco.setText("Preço: R$" + texto);
		}
		
		public void setLabelCodigo(String texto) {
			labelCodigo.setText("Código: " + texto);
		}
	}
}
