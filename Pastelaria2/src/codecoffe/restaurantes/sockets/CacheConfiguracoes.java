package codecoffe.restaurantes.sockets;
import java.io.Serializable;

public class CacheConfiguracoes implements Serializable 
{
	private double taxaEntrega;
	private String nomeRest;
	private boolean dezPorcento, reciboFim;	
	
	public CacheConfiguracoes(String nome, boolean dez, boolean recibo, double taxa)
	{
		this.nomeRest = nome;
		this.dezPorcento = dez;
		this.reciboFim = recibo;
		this.taxaEntrega = taxa;
	}
	
	public String getNome()
	{
		return this.nomeRest;
	}
	
	public double getTaxa()
	{
		return this.taxaEntrega;
	}
	
	public boolean getDezPorcento()
	{
		return this.dezPorcento;
	}
	
	public boolean getReciboFim()
	{
		return this.reciboFim;
	}	
}
