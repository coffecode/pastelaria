package codecoffe.restaurantes.sockets;
import java.io.Serializable;

import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.primitivas.Venda;

public class CacheMesaHeader implements Serializable
{
	private Venda vendaMesa;
	private Produto produtoMesa;
	private int mesa_id, header, header_extra;
	
	public CacheMesaHeader(int id, Venda v, int hd)
	{
		this.vendaMesa = v;
		this.mesa_id = id;
		this.header = hd;
	}
	
	public CacheMesaHeader(int id, Produto p, int hd)
	{
		this.produtoMesa = p;
		this.mesa_id = id;
		this.header = hd;
	}
	
	public CacheMesaHeader(int id, Produto p, Venda v, int hd, int hd_extra)
	{
		this.vendaMesa = v;
		this.produtoMesa = p;
		this.mesa_id = id;
		this.header = hd;
		this.header_extra = hd_extra;
	}
	
	public int getHeaderExtra()
	{
		return this.header_extra;
	}
	
	public Produto getProdutoMesa()
	{
		return this.produtoMesa;
	}
	
	public Venda getMesaVenda()
	{
		return this.vendaMesa;
	}
	
	public int getMesaId()
	{
		return this.mesa_id;
	}
	
	public int getHeader()
	{
		return this.header;
	}	
}
