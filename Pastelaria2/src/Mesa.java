public class Mesa {
	
	private int numero;   //Cada mesa terá o seu numero
	private double valorNaoPago;
	private double ValorPago;
	private double valorTotalGasto; //valorTotalGasto = valorPago + valorNaoPago
	private int quantidade; //quantidade de vendas
	
	private Venda[] vendas = new Venda[50];
	
	public Mesa(int numero){
		this.numero = numero;
		
		this.valorNaoPago = 0;
		this.ValorPago = 0;
		this.valorTotalGasto =0;
	}
	
	public void novaVenda(){
		this.vendas[this.quantidade] = new Venda();
		this.quantidade++;
		this.calcularPreco();

	}
	
	public void excluirVenda(int numero, int quantidade){
		//Caso o nomero de vendas a serem excluidos seja maior do que a quantidade do mesmo produto
		//eu excluo toda ela do array de vendas e reorganizo o array 
		if(this.vendas[numero].getQuantidadeVenda()<= quantidade){
			this.quantidade --;
			for(int i=numero; i<this.quantidade; i++){
				this.vendas[i] = this.vendas[i+1];
			}
			this.vendas[this.quantidade +1] = null;
		}else{
			//Caso a quantidade de itens a serem excluidos seja menor,
			//apenas diminuo isso na sua quantidade
			this.vendas[numero].setQuantidadeVenda(quantidade, 2);
			
		}
		this.calcularPreco();

	}
	
	public void excluirProdutoEmUmaVenda(String nome, int nVenda){
		this.vendas[nVenda].removeProduto(nome);
		this.calcularPreco();
	}
	
	public void adicionarProdutoEmUmaVenda(String nome, int nVenda){
		this.vendas[nVenda].aumentaQuantidadeProduto(nome);
		this.calcularPreco();
	}
	
	public void diminuiProdutoEmUmaVenda(String nome, int nVenda){
		this.vendas[nVenda].diminuiQuantidadeProduto(nome);
		this.calcularPreco();

	}
	public void pagarVenda(int nVenda){
		this.vendas[nVenda].setPago(1);		
		this.calcularPreco();
	}
	
	private void calcularPreco(){
		this.valorNaoPago = 0;
		this.ValorPago = 0;
		this.valorTotalGasto = 0;
		for(int i=0; i<this.quantidade; i++){
			if(this.vendas[i].getPago() == 0){
				this.valorNaoPago+= this.vendas[i].getPreco();
			}else{
				this.ValorPago += this.vendas[i].getPreco();
			}
		}
		this.valorTotalGasto = this.valorNaoPago + this.ValorPago;
	}
	
	
	
	
}
