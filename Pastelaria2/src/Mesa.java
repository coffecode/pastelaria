public class Mesa {
	int nMesa;   //Cada mesa terá o seu numero
	double valorNaoPago;
	double ValorPago;
	double valorTotalGasto; //valorTotalGasto = valorPago + valorNaoPago
	public Mesa(int numero){
		this.nMesa = numero;
		
		this.valorNaoPago = 0;
		this.ValorPago = 0;
		this.valorTotalGasto =0;
	}
	
	
	
}
