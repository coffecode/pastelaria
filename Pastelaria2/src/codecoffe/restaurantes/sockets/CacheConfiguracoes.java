package codecoffe.restaurantes.sockets;
import java.io.Serializable;

public class CacheConfiguracoes implements Serializable 
{
	private static final long serialVersionUID = 1L;
	private double taxaEntrega;
	private String nomeRest, mensagemSuperior, mensagemInferior;
	private boolean dezPorcento, dezPorcentoRapida, reciboFim, somCozinha;
	private int tipoPrograma;
	
	public CacheConfiguracoes(String nome, boolean dez, boolean dezrapida, boolean recibo, double taxa, 
			int tipo, String mSuperior, String mInferior, boolean som)
	{
		this.nomeRest = nome;
		this.dezPorcento = dez;
		this.dezPorcentoRapida = dezrapida;
		this.reciboFim = recibo;
		this.taxaEntrega = taxa;
		this.tipoPrograma = tipo;
		this.mensagemSuperior = mSuperior;
		this.mensagemInferior = mInferior;
		this.somCozinha = som;
	}

	public double getTaxaEntrega() {
		return taxaEntrega;
	}

	public void setTaxaEntrega(double taxaEntrega) {
		this.taxaEntrega = taxaEntrega;
	}

	public String getNomeRest() {
		return nomeRest;
	}

	public void setNomeRest(String nomeRest) {
		this.nomeRest = nomeRest;
	}

	public String getMensagemSuperior() {
		return mensagemSuperior;
	}

	public void setMensagemSuperior(String mensagemSuperior) {
		this.mensagemSuperior = mensagemSuperior;
	}

	public String getMensagemInferior() {
		return mensagemInferior;
	}

	public void setMensagemInferior(String mensagemInferior) {
		this.mensagemInferior = mensagemInferior;
	}

	public boolean isDezPorcento() {
		return dezPorcento;
	}

	public void setDezPorcento(boolean dezPorcento) {
		this.dezPorcento = dezPorcento;
	}

	public boolean isReciboFim() {
		return reciboFim;
	}

	public void setReciboFim(boolean reciboFim) {
		this.reciboFim = reciboFim;
	}

	public boolean isSomCozinha() {
		return somCozinha;
	}

	public void setSomCozinha(boolean somCozinha) {
		this.somCozinha = somCozinha;
	}

	public int getTipoPrograma() {
		return tipoPrograma;
	}

	public void setTipoPrograma(int tipoPrograma) {
		this.tipoPrograma = tipoPrograma;
	}

	public boolean isDezPorcentoRapida() {
		return dezPorcentoRapida;
	}

	public void setDezPorcentoRapida(boolean dezPorcentoRapida) {
		this.dezPorcentoRapida = dezPorcentoRapida;
	}
}
