package codecoffe.restaurantes.interfaceGrafica;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.ActivationDataFlavor;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Categoria;
import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.produtos.AbaCategorias;
import codecoffe.restaurantes.produtos.AbaProdutos;
import codecoffe.restaurantes.produtos.ProdutosTreeModel;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

public class PainelProdutos extends JPanel implements ActionListener{

	private WebPanel painelProdutos;
	private WebButton bNovoProduto, bNovaCategoria;
	private JPanel painelDireita, painelEscolha;
	private AbaCategorias painelCategoria;
	private AbaProdutos painelProduto;
	private JTree arvoreProdutos;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PainelProdutos()
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(15, 15, 15, 15));
		painelProdutos = new WebPanel(new MigLayout("fill", "[]20[]"));
		painelProdutos.setUndecorated(false);
		painelProdutos.setRound(StyleConstants.largeRound);
		painelProdutos.setMargin(10);

		arvoreProdutos = new JTree();
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));
		renderer.setOpenIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_categoria_open.png")));
		renderer.setClosedIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_categoria_closed.png")));
		arvoreProdutos.setCellRenderer(renderer);
		arvoreProdutos.setModel(new ProdutosTreeModel(carregarProdutos()));
		arvoreProdutos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvoreProdutos.setOpaque(false);
		arvoreProdutos.setFocusable(false);
		arvoreProdutos.setExpandsSelectedPaths(true);
		arvoreProdutos.setScrollsOnExpand(true);
		arvoreProdutos.setDragEnabled(true);
		arvoreProdutos.setDropMode(DropMode.ON_OR_INSERT);
		arvoreProdutos.setTransferHandler(new TreeTransferHandler());
		
		WebScrollPane scrollArvore = new WebScrollPane(arvoreProdutos);
		scrollArvore.setViewportView(arvoreProdutos);
		scrollArvore.setOpaque(false);
		scrollArvore.getViewport().setOpaque(false);
		scrollArvore.setRound(StyleConstants.largeRound);
		scrollArvore.setFocusable(false);

		painelDireita = new JPanel(new CardLayout());
		painelDireita.setOpaque(false);

		painelCategoria = new AbaCategorias();
		painelProduto = new AbaProdutos(getModel().getTodosProdutos());
		painelEscolha = new JPanel(new MigLayout("fill, align center"));
		painelEscolha.setOpaque(false);
		painelEscolha.add(new JLabel("Escolha um produto ou categoria para começar."), "align center");

		painelDireita.add(painelEscolha, "Painel Escolha");
		painelDireita.add(painelCategoria, "Painel Categoria");
		painelDireita.add(painelProduto, "Painel Produto");

		painelProdutos.add(scrollArvore, "grow, w 30%, h 80%, wrap");
		painelProdutos.add(painelDireita, "grow, w 70%, east");

		bNovoProduto = new WebButton("Produto");
		bNovoProduto.setRolloverShine(true);
		bNovoProduto.setPreferredSize(new Dimension(105, 40));
		bNovoProduto.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/produtos_add.png")));
		bNovoProduto.addActionListener(this);
		painelProdutos.add(bNovoProduto, "align center, split 2, gapright 20px");

		bNovaCategoria = new WebButton("Categoria");
		bNovaCategoria.setRolloverShine(true);
		bNovaCategoria.setPreferredSize(new Dimension(105, 40));
		bNovaCategoria.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/categorias_add.png")));
		bNovaCategoria.addActionListener(this);
		painelProdutos.add(bNovaCategoria);

		add(painelProdutos);

		arvoreProdutos.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if(arvoreProdutos.getLastSelectedPathComponent() instanceof Categoria)
				{
					painelCategoria.carregarCategoria((Categoria) arvoreProdutos.getLastSelectedPathComponent());
					CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
					cardLayout.show(painelDireita, "Painel Categoria");
				}
				else if(arvoreProdutos.getLastSelectedPathComponent() instanceof Produto)
				{
					TreePath t = arvoreProdutos.getSelectionPath();
					painelProduto.carregarProduto((Produto) arvoreProdutos.getLastSelectedPathComponent(), (Categoria) t.getPath()[1]);
					CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
					cardLayout.show(painelDireita, "Painel Produto");
				}
				else if(arvoreProdutos.getLastSelectedPathComponent() instanceof String)
				{
					CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
					cardLayout.show(painelDireita, "Painel Escolha");	
				}
			}
		});
	}

	private static class ProdutosSingletonHolder { 
		public static final PainelProdutos INSTANCE = new PainelProdutos();
	}

	public static PainelProdutos getInstance() {
		return ProdutosSingletonHolder.INSTANCE;
	}

	public ProdutosTreeModel getModel()
	{
		return (ProdutosTreeModel) arvoreProdutos.getModel();
	}

	public List<Categoria> carregarProdutos()
	{
		List<Categoria> categorias = new ArrayList<Categoria>();
		try {
			categorias.clear();
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM categorias ORDER BY titulo");

			while(pega.next())
			{
				Categoria c = new Categoria(pega.getInt("id"), pega.getString("titulo"), pega.getString("imagem"));
				Query pega2 = new Query();
				pega2.executaQuery("SELECT * FROM produtos_new WHERE categoria = " + c.getIdCategoria() + " ORDER BY nome");

				while(pega2.next())
				{
					c.addProduto(new Produto(pega2.getString("nome"), pega2.getString("referencia"),
							UtilCoffe.precoToDouble(pega2.getString("preco")), 
							pega2.getInt("id"), pega2.getInt("codigo")));
				}

				categorias.add(c);
				pega2.fechaConexao();
			}

			pega.fechaConexao();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
		}

		return categorias;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == bNovoProduto)
		{
			painelProduto.carregarProduto(new Produto(), new Categoria());

			CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
			cardLayout.show(painelDireita, "Painel Produto");
		}
		else
		{
			painelCategoria.carregarCategoria(new Categoria(0, "", "none"));

			CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
			cardLayout.show(painelDireita, "Painel Categoria");
		}
	}

	public void novoProduto(Produto p, int categoriaID)
	{
		Categoria cache = getModel().getCategoria(categoriaID);
		getModel().adicionarProduto(cache, p);
		painelProduto.carregarProduto(getModel().getProduto(p.getIdUnico(), cache), cache);
	}

	public void salvarProduto(Produto p, int categoriaIDnova, Categoria categoriaAntiga)
	{
		Categoria cache = getModel().getCategoria(categoriaIDnova);
		getModel().atualizarProduto(cache, categoriaAntiga, p);

		if(cache.getIdCategoria() != categoriaAntiga.getIdCategoria())
		{
			painelProduto.carregarProduto(p, cache);
		}
	}

	public void novaCategoria(Categoria c) 
	{
		getModel().adicionarCategoria(new Categoria(c.getIdCategoria(), c.getTitulo(), c.getImagem()));
		painelProduto.atualizarCategorias(getModel().getTodosProdutos());
		painelCategoria.carregarCategoria(getModel().getCategoria(c.getIdCategoria()));
	}

	public void removerCategoria(Categoria c) 
	{
		getModel().removerCategoria(c);
		painelProduto.atualizarCategorias(getModel().getTodosProdutos());
		CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
		cardLayout.show(painelDireita, "Painel Escolha");
	}

	public void salvarCategoria(Categoria c) 
	{
		getModel().atualizarCategoria(c);
		painelProduto.atualizarCategorias(getModel().getTodosProdutos());
	}

	public void removerProduto(Produto p, Categoria c) 
	{
		getModel().removerProduto(c, p);
		CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
		cardLayout.show(painelDireita, "Painel Escolha");
	}

	////////////////////////////////////////////////////////////////////////////////////

	private class TreeTransferHandler extends TransferHandler 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		DataFlavor nodesFlavor;
		DataFlavor[] flavors = new DataFlavor[1];
		Produto produtoRemover;
		Categoria categoriaAntiga;
		final JLabel ghost = new JLabel();
		final JWindow windowGhost = new JWindow();

		public TreeTransferHandler() {
			//String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Produto.class.getName() + "\"";
			nodesFlavor = new ActivationDataFlavor(Produto.class, DataFlavor.javaJVMLocalObjectMimeType, "Produto");
			flavors[0] = nodesFlavor;
			
			ghost.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));
			windowGhost.add(ghost);
			windowGhost.setAlwaysOnTop(true);
			windowGhost.setBackground(new Color(0,true));
			DragSource.getDefaultDragSource().addDragSourceMotionListener(
					new DragSourceMotionListener() {
						@Override public void dragMouseMoved(DragSourceDragEvent dsde) {
							Point pt = dsde.getLocation();
							pt.translate(5, 5); // offset
							windowGhost.setLocation(pt);
						}
					});
		}

		public boolean canImport(TransferHandler.TransferSupport support) 
		{
			if(!support.isDrop()) {
				return false;
			}
			//support.setShowDropLocation(true);
			/*if(!support.isDataFlavorSupported(nodesFlavor)) {
				return false;
			}*/
			
			// Do not allow a drop on the drag source selections.
			/*JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
			JTree tree = (JTree)support.getComponent();
			int dropRow = tree.getRowForPath(dl.getPath());
			int[] selRows = tree.getSelectionRows();
			for(int i = 0; i < selRows.length; i++) {
				if(selRows[i] == dropRow) {
					return false;
				}
			}*/
			return true;
		}

		protected Transferable createTransferable(JComponent c) {
			JTree tree = (JTree)c;
			TreePath[] paths = tree.getSelectionPaths();
			if(paths != null && getModel().isLeaf(paths[0].getLastPathComponent())) 
			{
				Produto copia = copy((Produto) paths[0].getLastPathComponent());
				produtoRemover = (Produto) paths[0].getLastPathComponent();
				categoriaAntiga = getModel().getCategoria(produtoRemover);
				
				ghost.setText(copia.getNome());    
				windowGhost.pack();
				Point pt = c.getLocation();
				SwingUtilities.convertPointToScreen(pt, c);
				windowGhost.setLocation(pt);
				windowGhost.setVisible(true);
				
				return new NodesTransferable(copia);
			}
			return null;
		}

		/** Defensive copy used in createTransferable. */
		private Produto copy(Produto node)  {
			return new Produto(node.getNome(), node.getReferencia(), node.getPreco(), node.getIdUnico(), node.getCodigo());
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			if(!canImport(support)) {
				return false;
			}

			try {
				Transferable t = support.getTransferable();
				Produto nodes = (Produto)t.getTransferData(nodesFlavor);
				
				JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
				TreePath dest = dl.getPath();
				Categoria parent = (Categoria)dest.getLastPathComponent();
				
				if(parent.getIdCategoria() != categoriaAntiga.getIdCategoria())
				{
					arvoreProdutos.expandPath(dest);
					getModel().adicionarProduto(parent, nodes);
					getModel().removerProduto(categoriaAntiga, produtoRemover);
					
					painelProduto.carregarProduto(nodes, parent);
					CardLayout cardLayout = (CardLayout) painelDireita.getLayout();
					cardLayout.show(painelDireita, "Painel Produto");
					
					try {
						Query envia = new Query();
						envia.executaUpdate("UPDATE produtos_new SET categoria = " 
						+ parent.getIdCategoria() + " WHERE id = " + nodes.getIdUnico());
						envia.fechaConexao();
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						new PainelErro(e);
					}
				}
				
				return true;
			} catch(UnsupportedFlavorException ufe) {
				ufe.printStackTrace();
				new PainelErro(ufe);
				return false;
			} catch(java.io.IOException ioe) {
				ioe.printStackTrace();
				new PainelErro(ioe);
				return false;
			}
		}
		
		public void exportDone(JComponent c, Transferable data, int action) {
			windowGhost.setVisible(false);
		}

		public String toString() {
			return getClass().getName();
		}

		public class NodesTransferable implements Transferable {
			Produto nodes;

			public NodesTransferable(Produto nodes) {
				this.nodes = nodes;
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
				if(!isDataFlavorSupported(flavor))
					throw new UnsupportedFlavorException(flavor);
				
				return nodes;
			}

			public DataFlavor[] getTransferDataFlavors() {
				return flavors;
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return nodesFlavor.equals(flavor);
			}
		}
	}
}