package codecoffe.restaurantes.sockets;
import java.io.Serializable;

public class CacheAdicionais implements Serializable 
{
	private int id;
	private String nome;
	private double preco;
	
	public CacheAdicionais(int index, String nomeProduto, double valor)
	{
		this.id = index;
		this.nome = nomeProduto;
		this.preco = valor;
	}
	
	public void setIndex(int index)
	{
		this.id = index;
	}
	
	public void setNome(String nomeProduto)
	{
		this.nome = nomeProduto;
	}
	
	public void setPreco(double valor)
	{
		this.preco = valor;
	}
	
	public int getIndex()
	{
		return this.id;
	}
	
	public String getNome()
	{
		return this.nome;
	}
	
	public double getPreco()
	{
		return this.preco;
	}
}
