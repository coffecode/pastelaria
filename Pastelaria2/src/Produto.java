


public class Produto {

	private String nome;
	
	private int quantidade; //quantidade de produtos vendidos
	
	private Double preco;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	
	
	public int getQuantidade() {
		return quantidade;
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

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}
	
}
