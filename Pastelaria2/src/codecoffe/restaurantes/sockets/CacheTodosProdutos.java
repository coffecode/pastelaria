package codecoffe.restaurantes.sockets;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

public class CacheTodosProdutos implements Serializable 
{
	private ArrayList<CacheProdutos> produtos;
	private ArrayList<CacheAdicionais> adicionais;
	
	public CacheTodosProdutos()
	{
		this.produtos = new ArrayList<CacheProdutos>();
		this.adicionais = new ArrayList<CacheAdicionais>();
	}
	
	public ArrayList<CacheProdutos> getProdutos()
	{
		return this.produtos;
	}
	
	public ArrayList<CacheAdicionais> getAdicionais()
	{
		return this.adicionais;
	}
	
	public void atualizarProdutos()
	{
		this.produtos.clear();
		this.adicionais.clear();
		
		try {
			
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM produtos WHERE tipo = 1 ORDER BY nome");
			
			while(pega.next())
				this.produtos.add(new CacheProdutos(pega.getInt("produtos_id"), pega.getString("nome"), UtilCoffe.precoToDouble(pega.getString("preco"))));
			
			pega.executaQuery("SELECT * FROM produtos WHERE tipo = 2 ORDER BY nome");
			
			while(pega.next())
				this.adicionais.add(new CacheAdicionais(pega.getInt("produtos_id"), pega.getString("nome"), UtilCoffe.precoToDouble(pega.getString("preco"))));		
			
			pega.fechaConexao();			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
			System.exit(0);
		}
	}
}
