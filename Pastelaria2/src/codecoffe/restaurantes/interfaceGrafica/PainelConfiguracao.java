package codecoffe.restaurantes.interfaceGrafica;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.slider.WebSlider;
import com.alee.laf.text.WebTextField;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;

import net.miginfocom.swing.MigLayout;

public class PainelConfiguracao extends JTabbedPane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PainelConfiguracao()
	{
		setFocusable(false);
		
		WebPanel configPrincipal = new WebPanel(new MigLayout("aligny center, alignx center", "[][][]70[]"));
		configPrincipal.setMargin(10, 10, 10, 10);
		
		final WebTextField campoRestaurante = new WebTextField("");
		campoRestaurante.setMargin(5, 5, 5, 5);
		campoRestaurante.setPreferredSize(new Dimension(200, 35));
		TooltipManager.addTooltip(campoRestaurante, "Clique na engrenagem para salvar!", TooltipWay.up, 250);
		
		final WebTextField campoNumero = new WebTextField("");
		campoNumero.setMargin(5, 5, 5, 5);
		campoNumero.setHorizontalAlignment(SwingConstants.CENTER);
		campoNumero.setPreferredSize(new Dimension(50, 35));
		TooltipManager.addTooltip(campoNumero, "Clique na engrenagem para salvar!", TooltipWay.up, 250);
		
		final WebTextField campoDelivery = new WebTextField("");
		campoDelivery.setMargin(5, 5, 5, 5);
		campoDelivery.setHorizontalAlignment(SwingConstants.CENTER);
		campoDelivery.setPreferredSize(new Dimension(70, 35));
		TooltipManager.addTooltip(campoDelivery, "Clique na engrenagem para salvar!", TooltipWay.up, 250);
		
		final JCheckBox campoOpcional = new JCheckBox("10% Opcional (mesas/comanda)");
		campoOpcional.setPreferredSize(new Dimension(150, 35));
		
		final JCheckBox campoOpcionalRapida = new JCheckBox("10% Opcional (venda rápida)");
		campoOpcionalRapida.setPreferredSize(new Dimension(160, 35));
		
		final JCheckBox campoRecibo = new JCheckBox("Imprimir recibo automaticamente");
		campoRecibo.setPreferredSize(new Dimension(150, 35));
		
		final WebTextField campoMensagemSuperior = new WebTextField("");
		campoMensagemSuperior.setMargin(5, 5, 5, 5);
		campoMensagemSuperior.setPreferredSize(new Dimension(300, 35));
		TooltipManager.addTooltip(campoMensagemSuperior, "Clique na engrenagem para salvar!", TooltipWay.up, 250);
		
		final WebTextField campoMensagemInferior = new WebTextField("");
		campoMensagemInferior.setMargin(5, 5, 5, 5);
		campoMensagemInferior.setPreferredSize(new Dimension(300, 35));
		TooltipManager.addTooltip(campoMensagemInferior, "Clique na engrenagem para salvar!", TooltipWay.up, 250);
		
		String[] tipos = {"Mesa", "Comanda"};
		final JComboBox<String> campoTipo = new JComboBox<String>(tipos);
		campoTipo.setPreferredSize(new Dimension(75, 35));
		campoTipo.setMinimumSize(new Dimension(75, 35));
		
		configPrincipal.add(new JLabel("Restaurante:"));
		configPrincipal.add(campoRestaurante, "gapleft 15px, wrap");
		configPrincipal.add(new JLabel("N. Mesas/Comandas:"));
		configPrincipal.add(campoNumero, "gapleft 15px, wrap");
		configPrincipal.add(new JLabel("Taxa Delivery:"));
		configPrincipal.add(campoDelivery, "gapleft 15px, wrap");
		configPrincipal.add(new JLabel("Usar:"));
		configPrincipal.add(campoTipo, "gapleft 15px, wrap");
		
		configPrincipal.add(campoOpcional, "span 2, wrap");
		configPrincipal.add(campoOpcionalRapida, "span 2, wrap");
		configPrincipal.add(campoRecibo, "span 2, wrap");
		
		configPrincipal.add(new JLabel("<html><b>Mensagens do Recibo</b><html>"), "gaptop 15px, span 2, wrap");
		
		configPrincipal.add(new JLabel("Mensagem Superior:"), "gaptop 15px");
		configPrincipal.add(campoMensagemSuperior, "gapleft 15px, wrap");
		configPrincipal.add(new JLabel("Mensagem Inferior:"));
		configPrincipal.add(campoMensagemInferior, "gapleft 15px, wrap");
		
		configPrincipal.add(new JLabel("É necessário que o programa seja reiniciado para atualizar algumas configurações."), "gaptop 30px, span");
		
		final JCheckBox campoSom = new JCheckBox("Som ao receber novo pedido");
		campoSom.setPreferredSize(new Dimension(150, 35));
		
		final WebSlider campoTempo = new WebSlider(WebSlider.HORIZONTAL);
		campoTempo.setMinimum(10);
		campoTempo.setMaximum(130);
		campoTempo.setMinorTickSpacing(5);
		campoTempo.setMajorTickSpacing(20);
		campoTempo.setPaintTicks(true);
		campoTempo.setPaintLabels(true);
		campoTempo.setPreferredSize(new Dimension(300, 80));
		
		configPrincipal.add(new JLabel("<html><b>Configurações da Cozinha</b><html>"), "cell 4 0, span 2, wrap");
		configPrincipal.add(new JLabel("Remover os pedidos automaticamente (minutos):"), "cell 4 1, span 2, wrap");
		configPrincipal.add(campoTempo, "cell 4 2, span 2 2, wrap");
		configPrincipal.add(campoSom, "gaptop 15px, cell 4 4, span 2, wrap");
		
		final JLabel imagemConfig = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/opcoes_full.png")));
		imagemConfig.setEnabled(false);
		imagemConfig.setPreferredSize(new Dimension(128, 128));
		TooltipManager.addTooltip(imagemConfig, "Clique aqui para salvar!", TooltipWay.up, 100);
		
		configPrincipal.add(imagemConfig, "gaptop 30px, align center, cell 4 5, span 2 5");
		
		campoRestaurante.setText(Configuracao.INSTANCE.getRestaurante());
		campoNumero.setText("" + Configuracao.INSTANCE.getMesas());
		campoTipo.setSelectedIndex(Configuracao.INSTANCE.getTipoPrograma());
		campoDelivery.setText(UtilCoffe.doubleToPreco(Configuracao.INSTANCE.getTaxaEntrega()));
		
		campoMensagemSuperior.setText(Configuracao.INSTANCE.getMensagemSuperior());
		campoMensagemInferior.setText(Configuracao.INSTANCE.getMensagemInferior());
		
		campoOpcional.setSelected(Configuracao.INSTANCE.getDezPorcento());
		campoOpcionalRapida.setSelected(Configuracao.INSTANCE.isDezPorcentoRapida());
		campoRecibo.setSelected(Configuracao.INSTANCE.getReciboFim());
		campoSom.setSelected(Configuracao.INSTANCE.isSomCozinha());
		campoTempo.setValue(Configuracao.INSTANCE.getIntervaloPedido());
		
		ItemListener il = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getItemSelectable() == campoTipo)
				{
					if(e.getStateChange() == ItemEvent.SELECTED)
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET tipoprograma = " + campoTipo.getSelectedIndex());
							envia.fechaConexao();
							Configuracao.INSTANCE.setTipoPrograma(campoTipo.getSelectedIndex());
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
				}
				else if(e.getItemSelectable() == campoOpcional)
				{
					if(campoOpcional.isSelected())
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET dezporcento = 1");
							envia.fechaConexao();
							Configuracao.INSTANCE.setDezPorcento(true);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
					else
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET dezporcento = 0");
							envia.fechaConexao();
							Configuracao.INSTANCE.setDezPorcento(false);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
				}
				else if(e.getItemSelectable() == campoOpcionalRapida)
				{
					if(campoOpcionalRapida.isSelected())
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET dezporcentorapida = 1");
							envia.fechaConexao();
							Configuracao.INSTANCE.setDezPorcentoRapida(true);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
					else
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET dezporcentorapida = 0");
							envia.fechaConexao();
							Configuracao.INSTANCE.setDezPorcentoRapida(false);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
				}
				else if(e.getItemSelectable() == campoRecibo)
				{
					if(campoRecibo.isSelected())
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET recibofim = 1");
							envia.fechaConexao();
							Configuracao.INSTANCE.setReciboFim(true);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
					else
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET recibofim = 0");
							envia.fechaConexao();
							Configuracao.INSTANCE.setReciboFim(false);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
				}
				else if(e.getItemSelectable() == campoSom)
				{
					if(campoSom.isSelected())
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET somcozinha = 1");
							envia.fechaConexao();
							Configuracao.INSTANCE.setSomCozinha(true);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
					else
					{
						try {
							Query envia = new Query();
							envia.executaUpdate("UPDATE opcoes SET somcozinha = 0");
							envia.fechaConexao();
							Configuracao.INSTANCE.setSomCozinha(false);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}
					}
				}
			}
		};
		
		MouseListener ml = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						campoRestaurante.setText((campoRestaurante.getText().replaceAll("'", "")));
						campoMensagemSuperior.setText((campoMensagemSuperior.getText().replaceAll("'", "")));
						campoMensagemInferior.setText((campoMensagemInferior.getText().replaceAll("'", "")));
						
						campoNumero.setText(UtilCoffe.limpaNumero(campoNumero.getText()));
						campoDelivery.setText(UtilCoffe.limpaNumeroDecimal(campoDelivery.getText()));
						
						if(UtilCoffe.vaziu(campoNumero.getText()))
							campoNumero.setText("0");
						
						if(UtilCoffe.vaziu(campoDelivery.getText()))
							campoDelivery.setText("0,00");
						else
							campoDelivery.setText(UtilCoffe.doubleToPreco(UtilCoffe.precoToDouble(campoDelivery.getText())));
						
						if(UtilCoffe.vaziu(campoRestaurante.getText())) {
							JOptionPane.showMessageDialog(null, "Preencha o nome do restaurante!");
						}
						else if(campoRestaurante.getText().length() > 60) {
							JOptionPane.showMessageDialog(null, "Nome do restaurante no máximo com 60 caracteres!");
						}
						else if(campoMensagemSuperior.getText().length() > 200) {
							JOptionPane.showMessageDialog(null, "Mensagem Superior no máximo com 200 caracteres!");
						}
						else if(campoMensagemInferior.getText().length() > 200) {
							JOptionPane.showMessageDialog(null, "Mensagem Inferior no máximo com 200 caracteres!");
						}
						else if(Integer.parseInt(campoNumero.getText()) > 9999) {
							JOptionPane.showMessageDialog(null, "Máximo de 9999 mesas/comandas!");
						}
						else if(Integer.parseInt(campoNumero.getText()) < 0) {
							JOptionPane.showMessageDialog(null, "Número de mesas/comandas inválido!");
						}
						else if(UtilCoffe.precoToDouble(campoDelivery.getText()) > 999) {
							JOptionPane.showMessageDialog(null, "Taxa de Entrega inválida!");
						}
						else if(UtilCoffe.precoToDouble(campoDelivery.getText()) < 0) {
							JOptionPane.showMessageDialog(null, "Taxa de Entrega inválida!");
						}
						else
						{
							try {
								Query envia = new Query();
								envia.executaUpdate("UPDATE opcoes SET restaurante = '" + campoRestaurante.getText() + "', "
										+ "mesas = " + campoNumero.getText() + ", "
										+ "msuperior = '" + campoMensagemSuperior.getText() + "', "
										+ "minferior = '" + campoMensagemInferior.getText() + "', "
										+ "taxaentrega = '" + campoDelivery.getText() + "', "
										+ "intervalopedido = " + campoTempo.getValue());
								envia.fechaConexao();
								
								Configuracao.INSTANCE.setMesas(Integer.parseInt(campoNumero.getText()));
								Configuracao.INSTANCE.setMensagemSuperior(campoMensagemSuperior.getText());
								Configuracao.INSTANCE.setMensagemInferior(campoMensagemInferior.getText());
								Configuracao.INSTANCE.setRestaurante(campoRestaurante.getText());
								Configuracao.INSTANCE.setTaxaEntrega(UtilCoffe.precoToDouble(campoDelivery.getText()));
								Configuracao.INSTANCE.setIntervaloPedido(campoTempo.getValue());
								
								NotificationManager.setLocation(2);
								NotificationManager.showNotification(PainelPrincipal.getInstance().getJanela(), 
										"Configurações Salvas!").setDisplayTime(2000);
								
							} catch (ClassNotFoundException | SQLException e1) {
								e1.printStackTrace();
								new PainelErro(e1);
							}
						}
					}
				});
			}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						imagemConfig.setEnabled(true);
					}
				});
			}

			@Override
			public void mouseExited(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						imagemConfig.setEnabled(false);
					}
				});
			}
		};
		
		imagemConfig.addMouseListener(ml);
		
		campoOpcional.addItemListener(il);
		campoOpcionalRapida.addItemListener(il);
		campoTipo.addItemListener(il);
		campoRecibo.addItemListener(il);
		campoSom.addItemListener(il);
		
		addTab("Configurações Gerais", new ImageIcon(getClass().getClassLoader().getResource("imgs/opcoes_med.png")), configPrincipal, "Configurações principais.");
	}
	
	private static class ConfiguracaoSingletonHolder { 
		public static final PainelConfiguracao INSTANCE = new PainelConfiguracao();
	}

	public static PainelConfiguracao getInstance() {
		return ConfiguracaoSingletonHolder.INSTANCE;
	}
}