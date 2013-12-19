

public class Venda {
		private Produto[] produtos; //Produtos que terão na venda
		
		private int quantidadeVenda; //Quantidade de vendas
		private int quantidadeProdutos;
		private double preco;  //Preço total dessa venda
		
		public Venda() {
			this.produtos = new Produto[30]; //Inicializo meus produtos com 30 produtos
										//Acho um tamanho bom, qualquer coisa é só alterar
			this.quantidadeProdutos = 0;
			this.quantidadeVenda = 0;

			this.preco = 0;
			

		}
		
		public void adicionarProduto(String nome, int quantidade) {
			this.produtos[this.quantidadeProdutos] = new Produto();
			this.produtos[this.quantidadeProdutos].setNome(nome);
			this.produtos[this.quantidadeProdutos].setQuantidade(quantidade);
			this.preco +=  this.produtos[this.quantidadeProdutos].getPreco()  * quantidade;
			
			this.quantidadeProdutos++;
		}
}
