import java.util.ArrayList;

public class Venda 
{
		private ArrayList<Produto> produtos;
		private double total;
		
		public Venda() 
		{
			this.produtos = new ArrayList();
			this.total = 0;
		}
		
		public Produto getProduto(int index)
		{
			return this.produtos.get(index);
		}
		
		public void adicionarProduto(Produto p)
		{
			boolean flag = false;
			if(this.produtos.size() > 0)
			{
				for(int i = 0 ; i < this.produtos.size() ; i++)
				{
					if((this.produtos.get(i).getNome().equals(p.getNome())) && (this.produtos.get(i).getTotalProduto() == p.getTotalProduto()))
					{
						flag = true;
						this.produtos.get(i).setQuantidade(1, 1);
						break;
					}
				}
			}
			
			if(!flag)
				this.produtos.add(p);
			
			this.calculaTotal();
		}
		
		public void removerProdutoIndex(int index)
		{
			this.produtos.remove(index);
		}		
		
		public void removerProduto(String nome, double precoTotal)
		{
			if(this.produtos.size() > 0)
			{
				for(int i = 0 ; i < this.produtos.size() ; i++)
				{
					Produto x = new Produto();
					x = this.produtos.get(i);
					
					if(x.getNome() == nome && x.getTotalProduto() == precoTotal)
					{
						this.produtos.remove(i);
						break;
					}
				}
			}
		}
		
		public int getQuantidadeProdutos()
		{
			return this.produtos.size();
		}
		
		public void calculaTotal()
		{
			this.total = 0;
			
			if(this.produtos.size() > 0)
			{
				for(int i = 0 ; i < this.produtos.size() ; i++)
				{
					Produto p = new Produto();
					p = this.produtos.get(i);
					
					this.total += (p.getTotalProduto()*p.getQuantidade());
				}
			}
		}

		public double getTotal() {
			return this.total;
		}

		public void setTotal(double preco) {
			this.total = total;
		}
}