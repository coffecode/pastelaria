package codecoffe.restaurantes.primitivas;
import java.io.Serializable;
import java.util.Date;

public class Pedido implements Serializable 
{
	private Produto p;
	private Date horario, ultimaEdicao;
	private String nomeAtendido, observacao;
	private int local, header, status, idUnico;
	
	public Pedido(Produto prod, String atendimento, String obs, int local)
	{
		this.p = prod;
		this.nomeAtendido = atendimento;
		this.observacao = obs;
		this.local = local;
		this.header = 1;
		this.status = 1;
		this.idUnico = 0;
	}
	
	public Pedido(Produto p, Date horario, String nomeAtendido,
			String observacao, int local, int status, int idUnico) {
		this.p = p;
		this.horario = horario;
		this.nomeAtendido = nomeAtendido;
		this.observacao = observacao;
		this.local = local;
		this.status = status;
		this.idUnico = idUnico;
		this.header = 0;
		this.ultimaEdicao = new Date();
	}

	public Pedido() {}
	
	public int getIdUnico() {
		return idUnico;
	}

	public void setIdUnico(int idUnico) {
		this.idUnico = idUnico;
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
	
	public void setProduto(Produto pp)
	{
		this.p = pp;
	}
}