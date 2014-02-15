package codecoffe.restaurantes.produtos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.alee.laf.text.WebTextField;

import codecoffe.restaurantes.primitivas.Produto;
import net.miginfocom.swing.MigLayout;

public class ProdutosComboEditor implements ComboBoxEditor
{
	private EditorPane editorPane;
	private Produto produtoSelecionado;
	
	public ProdutosComboEditor()
	{
		editorPane = new EditorPane();
	}

	@Override
	public Component getEditorComponent() {
		return editorPane;
	}

	@Override
	public void setItem(Object anObject) {}
	
	public void setProduto(Produto p)
	{
		if(p != null)
		{
			produtoSelecionado = p;
			editorPane.setSelecionado(p);
		}
	}

	@Override
	public Object getItem() {
		return produtoSelecionado;
	}

	@Override
	public void selectAll() {
		editorPane.selectAll();
	}

	@Override
	public void addActionListener(ActionListener l) {
		editorPane.addActionListener(l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		editorPane.removeActionListener(l);
	}
	
	public WebTextField getTextField()
	{
		return editorPane.getTextField();
	}
	
	class EditorPane extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private WebTextField campo;
		private JPanel painelProduto;
		private JLabel labelNome, labelCodigo;
		
		public EditorPane()
		{
			setLayout(new MigLayout());
			campo = new WebTextField();
			campo.setMargin(5, 5, 5, 5);
			
			painelProduto = new JPanel(new MigLayout());
			
			labelNome = new JLabel("Produto: -");
			labelNome.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));
			labelNome.setFont(new Font("Verdana", Font.BOLD, 12));
			
			labelCodigo = new JLabel("Código: -");
			labelCodigo.setFont(new Font("Verdana", Font.PLAIN, 10));
			labelCodigo.setForeground(Color.RED);
			
			painelProduto.add(labelNome, "span, wrap");
			painelProduto.add(labelCodigo, "span");
			
			add(campo, "grow, w 60%");
			add(painelProduto, "grow, w 40%");
		}
		
		public WebTextField getTextField() {
			return campo;
		}

		public void setSelecionado(Produto p)
		{
			labelNome.setText(" " + p.getNome());
			labelCodigo.setText("Código: " + p.getCodigo());
		}
		
		@Override
        public void addNotify() {
            super.addNotify();
            campo.requestFocusInWindow();
        }

        public void selectAll() {
        	campo.selectAll();
        }

        public void setText(String text) {
        	campo.setText(text);
        }

        public String getText() {
            return campo.getText();
        }

        public void addActionListener(ActionListener listener) {
        	campo.addActionListener(listener);
        }

        public void removeActionListener(ActionListener listener) {
        	campo.removeActionListener(listener);
        }
	}
}
