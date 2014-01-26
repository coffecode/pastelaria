package codecoffe.restaurantes.sockets;
import java.io.Serializable;
import java.util.ArrayList;

import codecoffe.restaurantes.primitivas.Venda;

public class CacheTodasMesas implements Serializable 
{
	private ArrayList<Venda> vendaMesas;
	
	public CacheTodasMesas(ArrayList<Venda> v)
	{
		this.vendaMesas = v;
	}
	
	public ArrayList<Venda> getTodasMesas()
	{
		return this.vendaMesas;
	}
}
