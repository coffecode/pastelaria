package codecoffe.restaurantes.produtos;

import java.util.List;

import codecoffe.restaurantes.primitivas.Categoria;
import codecoffe.restaurantes.primitivas.Produto;

public class ProdutosTreeModel extends AbstractTreeModel 
{
	private List<Categoria> categorias;
	private String fakeRoot = "Produtos";

	public ProdutosTreeModel(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public Object getChild(Object parent, int index) {
		if(parent == fakeRoot) 			// � o n� principal?
			return categorias.get(index); 	// Pegamos da lista de categorias

		if(parent instanceof Categoria) 	// O pai � uma categoria?
		{
			// Devolvemos um produto
			return ((Categoria) parent).getProdutos().get(index);
		}

		throw new IllegalArgumentException("Invalid parent class" + parent.getClass().getSimpleName());
	}

	/**
	 * Retornamos quantos filhos um pai tem. No caso de um livro, � a contagem
	 * de autores. No caso da lista de livros, � a quantidade de livros.
	 */
	
	public int getChildCount(Object parent) {
		if (parent == fakeRoot)
			return categorias.size();

		if (parent instanceof Categoria)
			return ((Categoria) parent).getProdutos().size();

		throw new IllegalArgumentException("Invalid parent class" + parent.getClass().getSimpleName());
	}

	/**
	 * Dado um pai, indicamos qual � o �ndice do filho correspondente.
	 */
	
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == fakeRoot)
			return categorias.indexOf(child);
		if (parent instanceof Categoria)
			return ((Categoria) parent).getProdutos().indexOf(child);

		return 0;
	}
	
	public Object getRoot() {
		return fakeRoot;
	}

	/**
	 * Indicamos se um n� � ou n�o uma folha. Isso �, se ele n�o tem filhos. No
	 * nosso caso, os autores s�o as folhas da �rvore.
	 */
	
	public boolean isLeaf(Object node) {
		return node instanceof Produto;
	}
	
	public List<Categoria> getTodosProdutos()
	{
		return categorias;
	}
	
	public Categoria getCategoria(int id)
	{
		for(int i = 0; i < categorias.size(); i++)
		{
			if(categorias.get(i).getIdCategoria() == id)
			{
				return categorias.get(i);
			}
		}
		return null;
	}
	
	public Categoria getCategoria(Produto p)
	{
		for(int i = 0; i < categorias.size(); i++)
		{
			for(int x = 0; x < categorias.get(i).getProdutos().size(); x++)
			{
				if(categorias.get(i).getProdutos().get(x).getIdUnico() == p.getIdUnico())
				{
					return categorias.get(i);
				}
			}
		}
		return null;
	}
	
	public Produto getProduto(int id, Categoria categoria)
	{
		for(int i = 0; i < categoria.getProdutos().size(); i++)
			if(categoria.getProdutos().get(i).getIdUnico() == id)
				return categoria.getProdutos().get(i);
		
		return null;
	}

	public void adicionarCategoria(Categoria categoria)
	{
		categorias.add(categoria);
		fireLastPathComponentInserted(fakeRoot, categoria);
	}
	
	public void atualizarCategoria(Categoria categoria)
	{
		for(int i = 0; i < categorias.size(); i++)
		{
			if(categoria.getIdCategoria() == categorias.get(i).getIdCategoria())
			{
				categorias.set(i, categoria);
				fireLastPathComponentChanged(fakeRoot, categoria);
				break;
			}
		}
	}
	
	public void atualizarProduto(Categoria categoriaNova, Categoria categoriaAntiga, Produto produto)
	{
		if(categoriaNova.getIdCategoria() == categoriaAntiga.getIdCategoria())	// atualizou mas n�o trocou categoria
		{
			for(int i = 0; i < categoriaAntiga.getProdutos().size(); i++)
			{
				if(categoriaAntiga.getProdutos().get(i).getIdUnico() == produto.getIdUnico())
				{
					categoriaAntiga.getProdutos().set(i, produto);
					fireLastPathComponentChanged(fakeRoot, categoriaAntiga, produto);
					break;
				}
			}
		}
		else
		{
			adicionarProduto(categoriaNova, produto);
			removerProduto(categoriaAntiga, produto);
		}
	}

	public void adicionarProduto(Categoria categoria, Produto produto)
	{
		categoria.addProduto(produto);
		fireLastPathComponentInserted(fakeRoot, categoria, produto);
	}

	public void removerCategoria(Categoria categoria)
	{
		fireLastPathComponentRemoved(fakeRoot, categoria);
		categorias.remove(categoria);
	}
	
	public void removerProduto(Categoria categoria, Produto produto)
	{
		fireLastPathComponentRemoved(fakeRoot, categoria, produto);
		categoria.removeProduto(produto);
	}
	
	public void countTudo()
	{
		int produtos = 0;
		int cat = 0;
		for(int i = 0; i < categorias.size(); i++)
		{
			for(int x = 0; x < categorias.get(i).getProdutos().size(); x++)
			{
				produtos++;
			}
			
			cat++;
		}
		System.out.println("Contagem, Produtos: " + produtos + " - Categorias: " + cat);
	}
}