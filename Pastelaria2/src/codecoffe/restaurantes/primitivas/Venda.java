package codecoffe.restaurantes.primitivas;
import java.io.Serializable;
import java.util.ArrayList;

public class Venda implements Serializable 
{
		private ArrayList<Produto> produtos;
		private double total;
		
		public Venda() 
		{
			this.produtos = new ArrayList<Produto>();
			this.total = 0;
		}
		
		public void clear()
		{
			this.produtos.clear();
			this.total = 0;
		}
		
		public Produto getProduto(int index)
		{
			return this.produtos.get(index);
		}
		
		public int adicionarProduto(Produto p)
		{
			boolean flag = false;
			int id = 0;
			if(this.produtos.size() > 0)
			{
				for(int i = 0 ; i < this.produtos.size() ; i++)
				{
					if((this.produtos.get(i).getNome().equals(p.getNome())) && (this.produtos.get(i).getTotalProduto() == p.getTotalProduto()))
					{
						if(this.produtos.get(i).getAllAdicionais().equals(p.getAllAdicionais()))
						{
							flag = true;
							this.produtos.get(i).setQuantidade(1, 1);
							id = i;
							break;							
						}
					}
				}
			}
			
			this.calculaTotal();
			
			if(!flag)
			{
				this.produtos.add(p);
				return (this.produtos.size()-1);
			}
			
			return id;
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
			this.total = preco;
		}
}