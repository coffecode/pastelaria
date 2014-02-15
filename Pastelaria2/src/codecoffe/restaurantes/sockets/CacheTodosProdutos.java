package codecoffe.restaurantes.sockets;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Categoria;
import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

public class CacheTodosProdutos implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Categoria> categorias;
	
	public CacheTodosProdutos() {
		this.categorias = new ArrayList<Categoria>();
	}

	public CacheTodosProdutos(List<Categoria> todosProdutos) {
		this.categorias = todosProdutos;
	}

	public List<Categoria> getCategorias() {
		return this.categorias;
	}
	
	public void atualizarProdutos()
	{
		this.categorias.clear();
		
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM categorias ORDER BY titulo");

			while(pega.next())
			{
				Categoria c = new Categoria(pega.getInt("id"), pega.getString("titulo"), pega.getString("imagem"));
				Query pega2 = new Query();
				pega2.executaQuery("SELECT * FROM produtos_new WHERE categoria = " + c.getIdCategoria() + " ORDER BY nome");

				while(pega2.next())
				{
					c.addProduto(new Produto(pega2.getString("nome"), pega2.getString("referencia"),
							UtilCoffe.precoToDouble(pega2.getString("preco")), 
							pega2.getInt("id"), pega2.getInt("codigo")));
				}

				categorias.add(c);
				pega2.fechaConexao();
			}

			pega.fechaConexao();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
			System.exit(0);
		}
	}
}