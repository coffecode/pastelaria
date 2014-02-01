package codecoffe.restaurantes.sockets;

import java.io.Serializable;
import java.util.ArrayList;
import codecoffe.restaurantes.primitivas.Clientes;

public class CacheClientes implements Serializable
{
	private int header;
	private String atendente;
	private ArrayList<Clientes> listaClientes;

	public CacheClientes(ArrayList<Clientes> listaClientes) {
		this.listaClientes = listaClientes;
	}
	
	public CacheClientes(Clientes c, int hd, String atendente)
	{
		this.listaClientes = new ArrayList<Clientes>();
		this.listaClientes.add(c);
		this.header = hd;
		this.atendente = atendente;
	}
	
	public CacheClientes(int hd)
	{
		this.listaClientes = new ArrayList<Clientes>();
		this.header = hd;
	}
	
	public CacheClientes(Clientes c)
	{
		this.listaClientes = new ArrayList<Clientes>();
		this.listaClientes.add(c);
	}	
	
	public CacheClientes() {
		this.listaClientes = new ArrayList<Clientes>();
	}

	public ArrayList<Clientes> getListaClientes() {
		return listaClientes;
	}

	public void setListaClientes(ArrayList<Clientes> listaClientes) {
		this.listaClientes = listaClientes;
	}	
	
	public String getAtendente() {
		return atendente;
	}

	public void setAtendente(String atendente) {
		this.atendente = atendente;
	}

	public int getHeader() {
		return header;
	}

	public void setHeader(int header) {
		this.header = header;
	}

	public Clientes getClienteID(int id)
	{
		for(int i = 0; i < this.listaClientes.size(); i++)
		{
			if(this.listaClientes.get(i).getIdUnico() == id)
				return this.listaClientes.get(i);
		}
		
		return null;
	}
}