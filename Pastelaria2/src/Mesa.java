public class Mesa {
	
	private int numero;   //Cada mesa terá o seu numero
	private double valorNaoPago;
	private double ValorPago;
	private double valorTotalGasto; //valorTotalGasto = valorPago + valorNaoPago
	
	
	public Mesa(int numero){
		this.numero = numero;
		
		this.valorNaoPago = 0;
		this.ValorPago = 0;
		this.valorTotalGasto =0;
	}
	
	
	
}
