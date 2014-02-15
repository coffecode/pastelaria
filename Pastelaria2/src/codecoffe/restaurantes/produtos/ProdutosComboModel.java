package codecoffe.restaurantes.produtos;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

import codecoffe.restaurantes.primitivas.Categoria;
import codecoffe.restaurantes.primitivas.Produto;

public class ProdutosComboModel extends AbstractListModel<Object> implements MutableComboBoxModel<Object> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Produto> listaProdutos;
	private Produto produtoSelecionado;
	
	public ProdutosComboModel(List<Categoria> categorias)
	{
		this.listaProdutos = new ArrayList<Produto>();
		
		if(categorias.size() > 0)
		{
			for(int i = 0; i < categorias.size(); i++)
			{
				for(int x = 0; x < categorias.get(i).getProdutos().size(); x++)
				{
					this.listaProdutos.add(categorias.get(i).getProdutos().get(x));
				}
			}
			
			if(this.listaProdutos.size() > 0)
			{
				setSelectedItem(this.listaProdutos.get(0));
			}
		}
	}
	
	public ProdutosComboModel(Categoria c)
	{
		this.listaProdutos = new ArrayList<Produto>();
		
		if(c.getProdutos().size() > 0)
		{
			for(int i = 0; i < c.getProdutos().size(); i++)
			{
				this.listaProdutos.add(c.getProdutos().get(i));
			}
			
			if(this.listaProdutos.size() > 0)
			{
				setSelectedItem(this.listaProdutos.get(0));
			}
		}
	}
	
	public ProdutosComboModel()
	{
		this.listaProdutos = new ArrayList<Produto>();
	}
	
	public void atualizaProdutosCombo(List<Categoria> categorias)
	{
		this.listaProdutos.clear();
		
		if(categorias.size() > 0)
			for(int i = 0; i < categorias.size(); i++)
				if(categorias.get(i).getIdCategoria() != 1)		// diferente de Adicionais
					for(int x = 0; x < categorias.get(i).getProdutos().size(); x++)
						this.listaProdutos.add(categorias.get(i).getProdutos().get(x));
	}
	
	public void atualizaAdicionaisCombo(Categoria c)
	{
		this.listaProdutos.clear();
		
		if(c.getProdutos().size() > 0)
			for(int i = 0; i < c.getProdutos().size(); i++)
				this.listaProdutos.add(c.getProdutos().get(i));
	}
	
	public List<Produto> getListaProdutos()
	{
		return this.listaProdutos;
	}
	
	@Override
	public int getSize() {
		return this.listaProdutos.size();
	}

	@Override
	public Object getElementAt(int index) {
		return this.listaProdutos.get(index);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.produtoSelecionado = (Produto) anItem;
	}

	@Override
	public Object getSelectedItem() {
		return this.produtoSelecionado;
	}

	@Override
	public void addElement(Object item) {
		this.listaProdutos.add((Produto) item);
	}

	@Override
	public void removeElement(Object obj) {
		this.listaProdutos.remove((Produto) obj);
	}

	@Override
	public void insertElementAt(Object item, int index) {
		this.listaProdutos.add(index, (Produto) item);
	}

	@Override
	public void removeElementAt(int index) {
		this.listaProdutos.remove(index);
	}
}