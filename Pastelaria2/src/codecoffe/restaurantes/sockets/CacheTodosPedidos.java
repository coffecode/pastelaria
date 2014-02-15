package codecoffe.restaurantes.sockets;
import java.io.Serializable;
import java.util.ArrayList;

import codecoffe.restaurantes.primitivas.Pedido;

public class CacheTodosPedidos implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Pedido> pedidos;
	
	public CacheTodosPedidos(ArrayList<Pedido> p)
	{
		this.pedidos = p;
	}
	
	public ArrayList<Pedido> getTodosPedidos()
	{
		return this.pedidos;
	}
}
