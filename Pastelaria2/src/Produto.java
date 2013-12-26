import java.util.ArrayList;

public class Produto
{
	private String nome;
	private double preco;
	private double totalProduto;
	private int quantidade;
	private boolean pago;
	private ArrayList<Adicionais> adicionais;
	
	public Produto()
	{
		this.adicionais = new ArrayList();
		this.totalProduto = 0;
		this.quantidade = 1;
	}
	
	public Adicionais getAdicional(int index)
	{
		return this.adicionais.get(index);
	}
	
	public String getAllAdicionais()
	{
		String todosAdicionais = "";
		
		for(int i = 0 ; i < adicionais.size() ; i++)
		{
			todosAdicionais += adicionais.get(i).nomeAdicional;
			todosAdicionais += ", ";
		}
		
		return todosAdicionais;
	}
	
	public int getTotalAdicionais()
	{
		return this.adicionais.size();
	}
	
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public int getQuantidade() {
		return this.quantidade;
	}
	
	public void setPago(boolean setar)
	{
		this.pago = setar;
	}
	
	public boolean getPago()
	{
		return this.pago;
	}
	
	public void adicionrAdc(Adicionais adc)
	{
		this.adicionais.add(adc);
		this.calcularPreco();
	}
	
	public void calcularPreco()
	{
		this.totalProduto = 0;
		if(this.adicionais.size() > 0)
		{
			for(int i = 0; i < this.adicionais.size() ; i++)
			{
				Adicionais adc = new Adicionais();
				adc = this.adicionais.get(i);
				
				this.totalProduto += adc.precoAdicional;
			}
		}
	}
	
	public double getTotalProduto()
	{
		return (this.preco + this.totalProduto);
	}

	public void setQuantidade(int quantidade, int tipo) {
		//O = Substitue
		//1 = Soma
		//2 = subtrai
		//3 = multiplica
		//4 = divide
		
		switch (tipo) {
		case 0:
			this.quantidade = quantidade;
			break;

		case 1:
			this.quantidade += quantidade;
			break;
		case 2:
			this.quantidade -= quantidade;
			break;
		case 3:
			this.quantidade = this.quantidade* quantidade;
			break;
		case 4:
			this.quantidade = this.quantidade/ quantidade;
			break;	
		default:
			break;
		}
	}

	public double getPreco() {
		return this.preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}
}
