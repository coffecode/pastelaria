

public class Venda {
		private Produto[] produtos; //Produtos que terão na venda
		
		private int quantidadeVenda; //Quantidade de vendas
		private int quantidadeProdutos;
		private double preco;  //Preço total dessa venda

		private int quantidadeProduto;
		
		public Venda() {
			this.produtos = new Produto[30]; //Inicializo minha venda com 30 produtos(null)
										//Acho um tamanho bom, qualquer coisa é só alterar
			this.quantidadeProdutos = 0;
			this.quantidadeVenda = 0;

			this.preco = 0;
			

		}
		
		public void adicionarProduto(String nome, int quantidade) {
			this.produtos[this.quantidadeProdutos] = new Produto();
			this.produtos[this.quantidadeProdutos].setNome(nome);
			this.produtos[this.quantidadeProdutos].setQuantidade(quantidade, 0);
			this.preco +=  this.produtos[this.quantidadeProdutos].getPreco()  * quantidade;
			
			this.quantidadeProdutos++;
		}
		
		
		public void removeProduto(String nome, int quantidade){
			
			int produtoId =0;
			
			//Salvo o offset do produto no array, usando o nome para pesquisar no array
			for(int i=0; i<this.quantidadeProdutos; i++){
				if(this.produtos[i].getNome().equals(nome)){
					produtoId = i;
					break;
				}
			}
			
			//Caso o nomero de produtos a serem excluidos seja maior do que a quantidade do mesmo produto
			//eu excluo todo ele do array de produtos e reorganizo o array 
			if(this.produtos[produtoId].getQuantidade()<= quantidade){
				this.quantidadeProdutos --;
				for(int i=produtoId; i<this.quantidadeProdutos; i++){
					this.produtos[i] = this.produtos[i+1];
				}
				this.produtos[this.quantidadeProduto +1] = null;
			}else{
				//Caso a quantidade de itens a serem excluidos seja menor,
				//apenas diminuo isso na sua quantidade
				this.produtos[produtoId].setQuantidade(quantidade, 2);
				
			}
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
		}
		
}