package codecoffe.restaurantes.primitivas;
import java.io.Serializable;
import java.util.Date;

public class Pedido implements Serializable 
{
	private Produto p;
	private Date horario, ultimaEdicao;
	private String nomeAtendido, observacao;
	private int local, id_local, header, status;
	
	public Pedido(Produto prod, String atendimento, String obs, int local, int idlocal)
	{
		this.p = prod;
		this.nomeAtendido = atendimento;
		this.observacao = obs;
		this.local = local;
		this.id_local = idlocal;
		this.header = 1;
		this.status = 1;
	}
	
	public Produto getProduto()
	{
		return this.p;
	}
	
	public Date getHora()
	{
		return this.horario;
	}
	
	public String getAtendido()
	{
		return this.nomeAtendido;
	}
	
	public String getObs()
	{
		return this.observacao;
	}
	
	public int getLocal()
	{
		return this.local;
	}
	
	public int getIdLocal()
	{
		return this.id_local;
	}
	
	public int getHeader()
	{
		return this.header;
	}
	
	public int getStatus()
	{
		return this.status;
	}
	
	public Date getUltimaEdicao()
	{
		return this.ultimaEdicao;
	}
	
	public void setHora(Date now)
	{
		this.horario = now;
	}
	
	public void setHeader(int hd)
	{
		this.header = hd;
	}
	
	public void setStatus(int ss)
	{
		this.status = ss;
	}
	
	public void setUltimaEdicao(Date tempo)
	{
		this.ultimaEdicao = tempo;
	}
}