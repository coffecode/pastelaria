

public class Venda {
		private Produto[] produtos; //Produtos que terão na venda
		
		private int quantidadeVenda; //Quantidade de vendas
		private int quantidadeProdutos; //Quantidade de produtos nessa venda 
		private double preco;  //Preço total dessa venda
		private int pago;
		
		
		public Venda() {
			this.produtos = new Produto[30]; //Inicializo minha venda com 30 produtos(null)
										//Acho um tamanho bom, qualquer coisa é só alterar
			this.quantidadeProdutos = 0;
			this.quantidadeVenda= 0;
			this.setPago(0);
			this.setPreco(0);
			

		}
		
		public void adicionarProduto(String nome) {
			this.produtos[this.quantidadeProdutos] = new Produto();
			this.produtos[this.quantidadeProdutos].setNome(nome);
			this.produtos[this.quantidadeProdutos].setQuantidade(1, 0);
			this.quantidadeProdutos++;
			this.calculaPreco();
		}
		
		
		public void removeProduto(String nome){
			
			int produtoId =0;
			
			//Salvo o offset do produto no array, usando o nome para pesquisar no array
			for(int i=0; i<this.quantidadeProdutos; i++){
				if(this.produtos[i].getNome().equals(nome)){
					produtoId = i;
					break;
				}
			}
			
			//eu excluo todo ele do array de produtos e reorganizo o array 
			this.quantidadeProdutos --;
			for(int i=produtoId; i<this.quantidadeProdutos; i++){
				this.produtos[i] = this.produtos[i+1];
			}
			this.produtos[this.quantidadeProdutos +1] = null;
			
			this.calculaPreco();
		}
		
		public void aumentaQuantidadeProduto(String nome) {
			int produtoId =0;
			
			//Salvo o offset do produto no array, usando o nome para pesquisar no array
			for(int i=0; i<this.quantidadeProdutos; i++){
				if(this.produtos[i].getNome().equals(nome)){
					produtoId = i;
					break;
				}
			}
			
			this.produtos[produtoId].setQuantidade(1, 1);
			this.calculaPreco();
		}
		
		public void diminuiQuantidadeProduto(String nome) {
			int produtoId =0;
			
			//Salvo o offset do produto no array, usando o nome para pesquisar no array
			for(int i=0; i<this.quantidadeProdutos; i++){
				if(this.produtos[i].getNome().equals(nome)){
					produtoId = i;
					break;
				}
			}
			
			this.produtos[produtoId].setQuantidade(1, 2);
			this.calculaPreco();
		}

		public int getQuantidadeVenda() {
			return quantidadeVenda;
		}

		public void setQuantidadeVenda(int quantidade, int tipo) {
			//O = Substitue
			//1 = Soma
			//2 = subtrai
			//3 = multiplica
			//4 = divide
			
			switch (tipo) {
			case 0:
				this.quantidadeVenda = quantidade;
				break;

			case 1:
				this.quantidadeVenda += quantidade;
				break;
			case 2:
				this.quantidadeVenda -= quantidade;
				break;
			case 3:
				this.quantidadeVenda = this.quantidadeVenda* quantidade;
				break;
			case 4:
				this.quantidadeVenda = this.quantidadeVenda/ quantidade;
				break;	
			default:
				break;
			}
			this.calculaPreco();
		}
		private void calculaPreco(){
			this.setPreco(0);
			for(int i=0; i< this.produtos.length; i++){
				this.setPreco(this.getPreco() + this.produtos[i].getPreco() * this.produtos[i].getQuantidade()); 
			}
		}

		public int getPago() {
			return pago;
		}

		public void setPago(int pago) {
			this.pago = pago;
		}

		public double getPreco() {
			return preco;
		}

		public void setPreco(double preco) {
			this.preco = preco;
		}
}