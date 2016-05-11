package Chat;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jcarrierpigeon.WindowPosition;
import net.sf.jtelegraph.Telegraph;
import net.sf.jtelegraph.TelegraphQueue;
import net.sf.jtelegraph.TelegraphType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class Janela_Chat extends JFrame {
	private JPanel pChatRecebe;
	private JLayeredPane panelRight; 
	private JScrollPane panelLeft;
	private JScrollPane scrollPainel;
	private JScrollPane scrollPainel2;
	private JTextField textArquivo;
	private JTextField txtNomeGrupoAdd;
	private JTextArea areaTextSend;
	private JDialog dgrupo_add;
	private JDialog dgrupo_criar;
	private JDialog darquivo_enviar;
	private JButton btnEnviar;
	private JMenu menuGrupo;
	private DefaultListModel<InfoChat> chat = new DefaultListModel<InfoChat>();
	private DefaultListModel<String> clienteAll = new DefaultListModel<String>();
	private DefaultListModel<String> clienteGrupo = new DefaultListModel<String>();
	private DefaultListCellRenderer jlistIcon;
	private JList<InfoChat> listChat;
	private Cliente client;
	private Telegraph grupoAdd;
	private TelegraphQueue  queue = new TelegraphQueue ();
	private ImageIcon iconAnexo = new ImageIcon("C:\\Chat\\img\\icon-anexo.png");
	private ImageIcon iconEmoticon = new ImageIcon("C:\\Chat\\img\\icon-emoticon.png");
	private ImageIcon iconOn = new ImageIcon("C:\\Chat\\img\\icon-on.png");
	private ImageIcon iconSendFile = new ImageIcon(Janela_Chat.class.getResource("/org/jb2011/lnf/beautyeye/ch16_tree/imgs/treeDefaultOpen1.png"));
	private ImageIcon iconLoading;
	private final String pathDownload = "C:\\Chat\\Files\\";
	private int indexGrupo = 0;
	private TitledBorder tituloChat;
	private JScrollPane scrollEmoticon;

	public Janela_Chat(Cliente client) {
		this.client = client;
		this.client.setChat(this);
		Thread tclient = new Thread(this.client);
		tclient.start();
		ini_CompGraficos();
		try {
			iconLoading = new ImageIcon(new URL("file:///C:/Chat/img/icon-loading.gif"));
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void addLabelClientON(String nome, String ip){
		chat.addElement(new InfoChat(nome, ip,0));
		chat.lastElement().setIconON();
	}
	public void addLabelGrupo(String grupo, String usuarios){
		chat.add(indexGrupo,new InfoChat(grupo, "",1));
		StringTokenizer splitMsg = new StringTokenizer(usuarios);
		if(indexGrupo == 0){
			chat.get(indexGrupo).addUserToGrupo("Todos usuários conectados");
		}else{
			while(splitMsg.hasMoreTokens()){
				chat.get(indexGrupo).addUserToGrupo(splitMsg.nextToken(";"));
			}
		}
		chat.get(indexGrupo).setIconON();
		indexGrupo++;
	}
	public void removeLabelClientON(String chatNome){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(chatNome)){
				chat.remove(x);
				if(listChat.isSelectionEmpty()){
					listChat.setSelectedIndex(0);
					pChatRecebe = listChat.getSelectedValue().getPanelChat();
					chat.get(listChat.getSelectedIndex()).msgNaoLida = false;
					scrollPainel2.setViewportView(pChatRecebe);
					tituloChat.setTitle(listChat.getSelectedValue().getNome());
					scrollPainel2.repaint();
				}
				break;
			}
		}
	}
	public void incluirMsg(String chatNome,String emitente, String msg){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(chatNome)){
				InfoChat chatTemp = chat.get(x);
				chatTemp.addMsgToChat(msg,emitente);
				scrollPainel2.revalidate();
				if(!listChat.isSelectedIndex(x)){
					chatTemp.msgNaoLida = true;
				}
				ajustaScroll();
				break;
			}
		}
	}
	public void incluirFile(String emitente, String chatNome,String nomeFile, String tamanhoFile){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(chatNome)){
				InfoChat chatTemp = chat.get(x);
				String path = pathDownload + nomeFile;
				chatTemp.addFileToChat(path,emitente,"recebendo",tamanhoFile);
				int index = (chatTemp.arquivos.size() -1);
				chatTemp.arquivos.get(index).button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent  e) {
						File pathCheck = new File(pathDownload);
						if(!pathCheck.exists() && !pathCheck.mkdir()){
							showNotfication("Falha","Não foi possível criar o diretório: "+pathDownload);
						}else{
							chatTemp.arquivos.get(index).button.removeActionListener(this);
							chatTemp.arquivos.get(index).button.setToolTipText("Baixando...");
							chatTemp.arquivos.get(index).button.setIcon(iconLoading);
							client.startConexao_file("receber",  emitente, chatNome, chatTemp.arquivos.get(index), chatTemp.tipo);
						}
					}
				});
				scrollPainel2.revalidate();
				if(!listChat.isSelectedIndex(x)){
					chatTemp.msgNaoLida = true;
				}
				ajustaScroll();
				break;
			}
		}
	}
	public void addUserNoGrupo(String grupo, String user){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(grupo)){
				chat.get(x).addUserToGrupo(user);
			}
		}
	}
	public void excUserDoGrupo(String grupo, String user){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(grupo)){
				chat.get(x).delUser(user);
			}
		}
	}
	public void showNotfication(String titulo, String msg){
		grupoAdd = new Telegraph(titulo, msg, TelegraphType.NOTIFICATION_ADD, WindowPosition.BOTTOMRIGHT, 3000);
		queue.add(grupoAdd);
	}
	
	public void ini_CompGraficos(){
		setTitle("Conectado | Usuario: " + client.nome);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(400, 100, 800, 670);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(false);
		
		listChat = new JList<InfoChat>(chat);
		listChat.setBorder(new TitledBorder(new LineBorder(null), "Chats"));
		listChat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		panelLeft = new JScrollPane(listChat);
		getContentPane().add(panelLeft);
		panelLeft.setPreferredSize(new Dimension(100,650));
		panelLeft.setLayout(new ScrollPaneLayout());
		
		panelRight = new JLayeredPane();
		getContentPane().add(panelRight);
		panelRight.setOpaque(true);
		panelRight.setBackground(new Color(250,250,250));
		panelRight.setPreferredSize(new Dimension(320, 650));
		
	    scrollEmoticon = new JScrollPane();
	    scrollEmoticon.setBounds(52, 325, 150, 138);
	    scrollEmoticon.setBackground(new Color(250,250,250));
	    scrollEmoticon.setOpaque(true);
	    scrollEmoticon.setVisible(false);
		panelRight.add(scrollEmoticon, new Integer(5), 0);
		try {
			insertIcons();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		scrollPainel2 = new JScrollPane(pChatRecebe);
		tituloChat = new TitledBorder(new LineBorder(null));
		tituloChat.setTitleFont( new Font("Century Gothic", Font.BOLD, 14));
		scrollPainel2.setBorder(tituloChat);
		scrollPainel2.getVerticalScrollBar().setUnitIncrement(20);
		scrollPainel2.setBounds(0, 0, 480, 460);
		scrollPainel2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelRight.add(scrollPainel2,  new Integer(1), 0);
		
		JPanel panelAtalho = new JPanel();
		panelAtalho.setBounds(0, 460, 485, 30);
		panelAtalho.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
		panelRight.add(panelAtalho, new Integer(2), 0);
		
		JButton labelAnexo = new JButton();
		labelAnexo.setIcon(iconAnexo);
		labelAnexo.setPreferredSize(new Dimension(22,22));
		labelAnexo.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent  e) {
			if(!listChat.isSelectionEmpty()){
				InfoChat destino = listChat.getSelectedValue();

					String localFile = getPath();
					if(localFile != null){
						destino.addFileToChat(localFile, "Eu", "enviando", "0");
						destino.arquivos.get(destino.arquivos.size()-1).button.setIcon(iconLoading);
						client.startConexao_file("enviar", client.nome,destino.getNome(), destino.arquivos.get(destino.arquivos.size()-1), destino.tipo);
						scrollPainel2.revalidate();
						ajustaScroll();
					}
			}else
				showNotfication("Alerta", "Selecione um chat para enviar um arquivo.");
		}
		});
		panelAtalho.add(labelAnexo);
		JButton labelEmoticon = new JButton();
		labelEmoticon.setIcon(iconEmoticon);
		labelEmoticon.setPreferredSize(new Dimension(22,22));
		panelAtalho.add(labelEmoticon);
		labelEmoticon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent  e) {
				scrollEmoticon.setVisible(true);
				scrollEmoticon.requestFocus();
			}
		});
		scrollEmoticon.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusLost(FocusEvent arg0) {
		    	scrollEmoticon.setVisible(false);
		    }
		});  
		
		JPanel panelBotton = new JPanel();
		panelBotton.setBounds(0, 490, 485, 90);
		panelRight.add(panelBotton,  new Integer(2), 0);
		areaTextSend = new JTextArea();
		areaTextSend.setColumns(49);
		areaTextSend.setRows(2);
		areaTextSend.setLineWrap(true);
		areaTextSend.setWrapStyleWord(true);
		
		areaTextSend.addKeyListener(new KeyListener(){
		    @Override
		    public void keyTyped(KeyEvent e) {
		    }
		    @Override
		    public void keyReleased(KeyEvent e) {
		    	if (e.isShiftDown()) {  
		    		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    			areaTextSend.setText(areaTextSend.getText()+"\n");
		    		}	
		    	}
		    }
			@Override
			public void keyPressed(KeyEvent e) {
				if(!e.isShiftDown()){
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				    	e.consume();
			    		btnEnviar.doClick();
					}
				}
			}
		});
		
		scrollPainel = new JScrollPane(areaTextSend);
		scrollPainel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelBotton.add(scrollPainel);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setPreferredSize(new Dimension(55,55));
		btnEnviar.setEnabled(false);
		panelBotton.add(btnEnviar);
		btnEnviar.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent  e) {
				if(btnEnviar.isEnabled()){
					String msg = areaTextSend.getText().trim();
					if(!listChat.isSelectionEmpty()){
						InfoChat chatSelect = listChat.getSelectedValue();
						if(chatSelect.tipo == 1){
							client.enviar("GROUP_MSG", chatSelect.getNome(), msg);
						}else{
							client.enviar("SINGLE_MSG",chatSelect.getNome(),msg);
						}
						areaTextSend.setText("");
						chatSelect.addMsgToChat(msg, "Eu");
						ajustaScroll();
						areaTextSend.requestFocus();
					}else
						showNotfication("Alerta", "Selecione um chat para enviar sua mensagem.");
				}
			}
		});
		areaTextSend.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(areaTextSend.getText().trim().equals("")){
		    		btnEnviar.setEnabled(false);
			     }else{
			    	 btnEnviar.setEnabled(true);
			     }
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(areaTextSend.getText().trim().equals("")){
		    		btnEnviar.setEnabled(false);
			     }else{
			    	 btnEnviar.setEnabled(true);
			     }
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if(areaTextSend.getText().trim().equals("")){
		    		btnEnviar.setEnabled(false);
			     }else{
			    	 btnEnviar.setEnabled(true);
			     }
			}
			
		});
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		menuGrupo = new JMenu("Grupo");
		menuBar.add(menuGrupo);

		JMenuItem g_opt1 = new JMenuItem(new AbstractAction("Criar um Grupo") {
			 public void actionPerformed(ActionEvent ae) {
				 clienteAll.clear();
				 clienteGrupo.clear();
				 for(int x=0; chat.size() > x;x++){
					 if(chat.get(x).tipo == 0){
						 clienteAll.addElement(chat.get(x).getNome());
					 }
				 }
				 dgrupo_criar.setVisible(true);
			 }
		});
		JSeparator separator = new JSeparator();
		menuGrupo.add(separator);
		menuGrupo.add(g_opt1);
		JMenuItem g_opt2 = new JMenuItem(new AbstractAction("Adicionar") {
			 public void actionPerformed(ActionEvent ae) {
				 clienteGrupo.clear();
				 for(int x=0; chat.size() > x;x++){
					 if(chat.get(x).tipo == 0){
						 if(!listChat.getSelectedValue().getUsuarios().contains(chat.get(x).getNome())){
							 clienteGrupo.addElement(chat.get(x).getNome());
						 }
					 }
				 }
				 txtNomeGrupoAdd.setText(listChat.getSelectedValue().getNome());
				 dgrupo_add.setVisible(true);
			 }
		});
		menuGrupo.add(g_opt2);
		g_opt2.setEnabled(false);
		JMenuItem g_opt3 = new JMenuItem(new AbstractAction("Sair do Grupo") {
			 public void actionPerformed(ActionEvent ae) {
				 Object[] options = { "Sim", "Não" }; 
	            	int confirm = JOptionPane.showOptionDialog(null, "Deseja sair do grupo'"+listChat.getSelectedValue().getNome()+"'?", "Confirmação", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE , null, options, options);
	                if(confirm == 0){
	                    client.sairGrupo(listChat.getSelectedValue().getNome());
	                    chat.remove(listChat.getSelectedIndex());
	                    indexGrupo--;
	                    if(listChat.isSelectionEmpty()){
		                    listChat.setSelectedIndex(0);
		                    pChatRecebe = listChat.getSelectedValue().getPanelChat();
		    				chat.get(listChat.getSelectedIndex()).msgNaoLida = false;
		    				scrollPainel2.setViewportView(pChatRecebe);
		    				tituloChat.setTitle(listChat.getSelectedValue().getNome());
							scrollPainel2.repaint();
	                    }
	                }
			 }
		});
		menuGrupo.add(g_opt3);
		g_opt3.setEnabled(false);
		
		JMenu menuArquivo = new JMenu("Arquivos");
		menuBar.add(menuArquivo);
		JMenuItem a_opt1 = new JMenuItem(new AbstractAction("Enviar arquivo..") {
			 public void actionPerformed(ActionEvent ae) {
				 if(!listChat.isSelectionEmpty()){
					 textArquivo.setText("");
					 darquivo_enviar.setVisible(true);
				 }
			 }
		});
		menuArquivo.add(a_opt1);
		a_opt1.setEnabled(true);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
            	 Object[] options = { "Sim", "Não" }; 
            	int confirm = JOptionPane.showOptionDialog(null, "Deseja sair do chat?", "Confirmação", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE , null, options, options);
                if(confirm == 0){
                    client.fechar();
                    System.exit(0);
                }
            }
        });
		listChat.addListSelectionListener(new ListSelectionListener(){
			  public void valueChanged(ListSelectionEvent listSelectionEvent) {
				  if(!listChat.isSelectionEmpty()){
					  if (listSelectionEvent.getValueIsAdjusting()){
						  pChatRecebe = listChat.getSelectedValue().getPanelChat();
						  chat.get(listChat.getSelectedIndex()).msgNaoLida = false;
						  scrollPainel2.setViewportView(pChatRecebe);
						  tituloChat.setTitle(listChat.getSelectedValue().getNome());
						  scrollPainel2.repaint();
						  ajustaScroll();
						  areaTextSend.requestFocus();
					  }
					  if(listChat.getSelectedValue().tipo == 1 && listChat.getSelectedIndex() > 0){
						  g_opt3.setEnabled(true);
						  g_opt2.setEnabled(true);
						
					  }else{
						  g_opt3.setEnabled(false);
						  g_opt2.setEnabled(false);
						
					  }
					 
				  }
			  }
		});
		listChat.setFixedCellHeight(30);

		listChat.setCellRenderer(new DefaultListCellRenderer() {
	          @Override
	          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        	  	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        	  	InfoChat temp = (InfoChat)value;
	          		setText(temp.getNome());
	          		setIcon(temp.icon);

	          		JPanel panel = new JPanel(new BorderLayout(0, 0));
	        	  	panel.setBackground(Color.WHITE);
	        	  	JLabel labMsgNLida = temp.labMsgNLida;
	        	  	labMsgNLida.setVisible(temp.msgNaoLida);
	          		panel.add(labMsgNLida, BorderLayout.EAST);
	          		panel.add(this,BorderLayout.CENTER);
	          		
	          		if(temp.tipo == 0){
	          			panel.setToolTipText("IP: "+temp.getIp());
	          		}else{	          			
	          			panel.setToolTipText(temp.textToolTip);
	          		}
	          		LookAndFeel.installBorder(this, "BorderFactory.createEmptyBorder()"); 
	          	return panel;
	          }
			});
		jlistIcon = new DefaultListCellRenderer() {
	          @Override
	          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        	  	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        	  	if (value instanceof String) {
		        	  	setText((String)value);
		        	  	setIcon(iconOn); 
	        	  	}
	          	return this;
	          }
			};
		menuGrupoCria();
		menuGrupoAddUser();
		menuEnviarArquivo();
	}
	
	public void menuEnviarArquivo(){
		SpringLayout springLayout = new SpringLayout();
		darquivo_enviar = new JDialog(this, "Enviar Arquivo",JDialog.ModalityType.DOCUMENT_MODAL);
		darquivo_enviar.setBounds(450, 220, 415, 180);
		darquivo_enviar.setResizable(false);

		JPanel panelBg = new JPanel();
		darquivo_enviar.add(panelBg);
		panelBg.setLayout(springLayout);
		panelBg.setBackground(Color.white);
		
		JLabel labArquivo = new JLabel("Arquivo: ");
		springLayout.putConstraint(SpringLayout.NORTH, labArquivo, 20, SpringLayout.NORTH, panelBg);
		springLayout.putConstraint(SpringLayout.WEST, labArquivo, 20, SpringLayout.WEST, panelBg);
		panelBg.add(labArquivo);
		
		textArquivo = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textArquivo, 0, SpringLayout.NORTH, labArquivo);
		springLayout.putConstraint(SpringLayout.WEST, textArquivo, 6, SpringLayout.EAST, labArquivo);
		panelBg.add(textArquivo);
		textArquivo.setEnabled(false);
		textArquivo.setColumns(30);
		
		JButton btnSelArquivo = new JButton();
		btnSelArquivo.setIcon(iconSendFile);
		btnSelArquivo.setPreferredSize(new Dimension(20,20));
		springLayout.putConstraint(SpringLayout.NORTH, btnSelArquivo, 0, SpringLayout.NORTH, textArquivo);
		springLayout.putConstraint(SpringLayout.WEST, btnSelArquivo, 6, SpringLayout.EAST, textArquivo);
		panelBg.add(btnSelArquivo);
		
		btnSelArquivo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				 String arquivo = getPath();
				 if(arquivo != null){
					 textArquivo.setText(arquivo);
				 }
			}
		});
		
		JButton btnEnviarArq = new JButton("Enviar Arquivo");
		springLayout.putConstraint(SpringLayout.SOUTH, btnEnviarArq, -10, SpringLayout.SOUTH, panelBg);
		springLayout.putConstraint(SpringLayout.EAST, btnEnviarArq, 240, SpringLayout.WEST, panelBg);
		panelBg.add(btnEnviarArq);

		btnEnviarArq.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!textArquivo.getText().trim().equals("")){
					if(new File(textArquivo.getText()).exists()){
						InfoChat destino = listChat.getSelectedValue();
						destino.addFileToChat(textArquivo.getText(), "Eu", "enviando", "0");
						destino.arquivos.get(destino.arquivos.size()-1).button.setIcon(iconLoading);
						client.startConexao_file("enviar",client.nome, destino.getNome(), destino.arquivos.get(destino.arquivos.size()-1), destino.tipo);
						darquivo_enviar.setVisible(false);
						scrollPainel2.revalidate();
						ajustaScroll();
					}else{
						showNotfication("Arquivo Inválido", "O arquivo selecionado não existe!");
					}
				}
			}
		});
	}
	
	public void menuGrupoAddUser(){
		SpringLayout springLayout = new SpringLayout();
		dgrupo_add = new JDialog(this, "Adicionar Usuário", JDialog.ModalityType.DOCUMENT_MODAL);
		dgrupo_add.setBounds(450, 220, 385, 350);
		dgrupo_add.setResizable(false);
		
		JPanel panelBg = new JPanel();
		dgrupo_add.add(panelBg);
		panelBg.setLayout(springLayout);
		panelBg.setBackground(Color.white);
		
		JLabel labNomeGrupo = new JLabel("Nome do Grupo: ");
		springLayout.putConstraint(SpringLayout.NORTH, labNomeGrupo, 21, SpringLayout.NORTH, panelBg);
		springLayout.putConstraint(SpringLayout.WEST, labNomeGrupo, 50, SpringLayout.WEST, panelBg);
		panelBg.add(labNomeGrupo);
		
		txtNomeGrupoAdd = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtNomeGrupoAdd, 0, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, txtNomeGrupoAdd, 6, SpringLayout.EAST, labNomeGrupo);
		panelBg.add(txtNomeGrupoAdd);
		txtNomeGrupoAdd.setEnabled(false);
		txtNomeGrupoAdd.setColumns(10);
		
		JScrollPane scrollList = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, scrollList, 20, SpringLayout.WEST, labNomeGrupo);
		scrollList.setBorder(new TitledBorder(new LineBorder(null), "Usuários"));
		panelBg.add(scrollList);
		scrollList.setPreferredSize(new Dimension(170,170));
		JList<String> listChatAddUser = new JList<String>(clienteGrupo);
		listChatAddUser.setCellRenderer(jlistIcon);
		listChatAddUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollList.setViewportView(listChatAddUser);
		
		JButton btnAdicionar = new JButton("Adicionar");
		springLayout.putConstraint(SpringLayout.SOUTH, btnAdicionar, -10, SpringLayout.SOUTH, panelBg);
		springLayout.putConstraint(SpringLayout.EAST, btnAdicionar, 210, SpringLayout.WEST, dgrupo_add);
		panelBg.add(btnAdicionar);
		
		btnAdicionar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(listChatAddUser.isSelectionEmpty())
					JOptionPane.showMessageDialog(null, "Selecione o usuário que deseja adicionar ao grupo", "Alerta", JOptionPane.WARNING_MESSAGE);
				else{
					client.enviar("ADD_USER_GRUPO", txtNomeGrupoAdd.getText(), listChatAddUser.getSelectedValue());
					dgrupo_add.setVisible(false);
				}
			}
		});
	}
	
	public void menuGrupoCria(){
		SpringLayout springLayout = new SpringLayout();		
		dgrupo_criar = new JDialog(this, "Adicionar Grupo", JDialog.ModalityType.DOCUMENT_MODAL);
		dgrupo_criar.setBounds(450, 220, 500, 350);
		dgrupo_criar.setResizable(false);
		
		JPanel panelBg = new JPanel();
		dgrupo_criar.add(panelBg);
		panelBg.setLayout(springLayout);
		panelBg.setBackground(Color.white);
		
		JLabel labNomeGrupo = new JLabel("Nome do Grupo: ");
		springLayout.putConstraint(SpringLayout.NORTH, labNomeGrupo, 21, SpringLayout.NORTH, panelBg);
		springLayout.putConstraint(SpringLayout.WEST, labNomeGrupo, 130, SpringLayout.WEST, panelBg);
		panelBg.add(labNomeGrupo);
		
		JTextField txtNomeGrupo = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtNomeGrupo, 0, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, txtNomeGrupo, 6, SpringLayout.EAST, labNomeGrupo);
		panelBg.add(txtNomeGrupo);
		txtNomeGrupo.setColumns(10);
		
		JScrollPane scrollList1 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList1, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, scrollList1, 20, SpringLayout.WEST, panelBg);
		scrollList1.setBorder(new TitledBorder(new LineBorder(null), "Todos"));
		panelBg.add(scrollList1);
		scrollList1.setPreferredSize(new Dimension(170,170));
		JList<String> listChat2 = new JList<String>(clienteAll);
		listChat2.setCellRenderer(jlistIcon);
		listChat2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollList1.setViewportView(listChat2);
		
		JScrollPane scrollList2 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList2, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, scrollList2, 60, SpringLayout.EAST, scrollList1);
		scrollList2.setBorder(new TitledBorder(new LineBorder(null), "Grupo"));
		panelBg.add(scrollList2);
		scrollList2.setPreferredSize(new Dimension(170,170));
		JList<String> listChat3 = new JList<String>(clienteGrupo);
		listChat3.setCellRenderer(jlistIcon);
		listChat3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollList2.setViewportView(listChat3);
		
		JButton btnAddToGrupo = new JButton(">>");
		springLayout.putConstraint(SpringLayout.NORTH, btnAddToGrupo, 30, SpringLayout.NORTH, scrollList1);
		springLayout.putConstraint(SpringLayout.WEST, btnAddToGrupo, 10, SpringLayout.EAST, scrollList1);
		panelBg.add(btnAddToGrupo);
		
		JButton btnRemovToGrupo = new JButton("<<");
		springLayout.putConstraint(SpringLayout.NORTH, btnRemovToGrupo, 10, SpringLayout.SOUTH, btnAddToGrupo);
		springLayout.putConstraint(SpringLayout.WEST, btnRemovToGrupo, 10, SpringLayout.EAST, scrollList1);
		panelBg.add(btnRemovToGrupo);
		
		JButton btnAdicionar = new JButton("Adicionar");
		springLayout.putConstraint(SpringLayout.SOUTH, btnAdicionar, -10, SpringLayout.SOUTH, panelBg);
		springLayout.putConstraint(SpringLayout.EAST, btnAdicionar, 254, SpringLayout.WEST, dgrupo_criar);
		panelBg.add(btnAdicionar);
		
		btnAddToGrupo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!listChat2.isSelectionEmpty()){
					clienteGrupo.addElement(listChat2.getSelectedValue());
					clienteAll.remove(listChat2.getSelectedIndex());
				}
			}
		});
		
		btnRemovToGrupo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!listChat3.isSelectionEmpty()){
					clienteAll.addElement(listChat3.getSelectedValue());
					clienteGrupo.remove(listChat3.getSelectedIndex());
				}
			}
		});
		
		btnAdicionar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!txtNomeGrupo.getText().isEmpty() && clienteGrupo.size() > 0){
					client.criarGrupo(txtNomeGrupo.getText(), clienteGrupo);
					dgrupo_criar.setVisible(false);
					txtNomeGrupo.setText("");
				}else{
					JOptionPane.showMessageDialog(null, "Preencha o nome do grupo e inclua no minimo 1 usuário ao grupo", "Alerta", JOptionPane.WARNING_MESSAGE);
				}		
			}
		});
	}
	
	public String getPath(){
		JFileChooser fc = new JFileChooser();
		fc.setBackground(Color.WHITE);
		fc.setAcceptAllFileFilterUsed(true);
		fc.setDialogTitle("Selecione o Arquivo");
        int res = fc.showOpenDialog(null);
        if(res == JFileChooser.APPROVE_OPTION){
            return fc.getSelectedFile().getPath();
         }
        return null;
	}
	public void ajustaScroll(){
		scrollPainel2.getVerticalScrollBar().setValue(scrollPainel2.getVerticalScrollBar().getMaximum());
	}
	
	public void insertIcons() throws MalformedURLException{
		String e1 = "file:///C:/Chat/img/emoticon/e1.png";
		String e2 = "file:///C:/Chat/img/emoticon/e2.png";
		String e3 = "file:///C:/Chat/img/emoticon/e3.png";
		String e4 = "file:///C:/Chat/img/emoticon/e4.png";
		String e5 = "file:///C:/Chat/img/emoticon/e5.png";
		String e6 = "file:///C:/Chat/img/emoticon/e6.png";
		String e7 = "file:///C:/Chat/img/emoticon/e7.png";
		String e8 = "file:///C:/Chat/img/emoticon/e8.png";
		String e9 = "file:///C:/Chat/img/emoticon/e9.gif";
		String e10 = "file:///C:/Chat/img/emoticon/e10.gif";
		String e11 = "file:///C:/Chat/img/emoticon/e11.gif";
		String e12 = "file:///C:/Chat/img/emoticon/e12.gif";
		String e13 = "file:///C:/Chat/img/emoticon/e13.png";
		String e14 = "file:///C:/Chat/img/emoticon/e14.png";
		String e15 = "file:///C:/Chat/img/emoticon/e15.gif";
		String e16 = "file:///C:/Chat/img/emoticon/e16.png";
		String e17 = "file:///C:/Chat/img/emoticon/e17.png";
		String e18 = "file:///C:/Chat/img/emoticon/ee18.gif";
		String e19 = "file:///C:/Chat/img/emoticon/ee19.gif";
		String e20 = "file:///C:/Chat/img/emoticon/ee20.gif";
		String e21 = "file:///C:/Chat/img/emoticon/e21.png";
		String e22 = "file:///C:/Chat/img/emoticon/e22.png";
		String e23 = "file:///C:/Chat/img/emoticon/e23.gif";
		String e24 = "file:///C:/Chat/img/emoticon/e24.gif";
		String e25 = "file:///C:/Chat/img/emoticon/e25.png";
		Object[][]dataEmoticons ={
			{e1,e2,e3,e4,e5},
			{e6,e7,e8,e9,e10},
			{e11,e12,e13,e14,e15},
			{e16,e17,e18,e19,e20},
			{e21,e22,e23,e24,e25}
			};
		Object[][]stringEmoticons = {
			{":L",":R",">.<",":D",":O",},
			{":)",":(","sniff","/sono","eq%"},
			{"pirata","o.O","/medo","/raiva","/facepalm"},
			{"emo16","emo17","emo18","emo19","emo20"},
			{"emo21","emo22","emo23","emo24","emo25"}
		};
		Object[] colunName = new Object[5];
		DefaultTableModel modelo = new DefaultTableModel(dataEmoticons, colunName);
		JTable tableEmoticon = new JTable(modelo);
		
		tableEmoticon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableEmoticon.setCellSelectionEnabled(true);
		tableEmoticon.setTableHeader(null);
		tableEmoticon.setFillsViewportHeight(true);
		scrollEmoticon.setViewportView(tableEmoticon);

		tableEmoticon.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        JTable table =(JTable) e.getSource();
		        Point ponto = e.getPoint();
		        int row = table.rowAtPoint(ponto);
		        int col = table.columnAtPoint(ponto);
		        areaTextSend.setText(areaTextSend.getText()+" "+stringEmoticons[row][col]);
		        areaTextSend.requestFocus();
		    }
		});
		for(int x=0;x<5;x++){
			tableEmoticon.getColumnModel().getColumn(x).setCellRenderer(new DefaultTableCellRenderer(){
				JButton but = new JButton();
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				    boolean hasFocus, int row, int column) {
					ImageIcon icon;
					try {
						icon = new ImageIcon(new URL(value.toString()));
						but.setIcon(icon);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				    return but;
				  }
			});
		}
	}
}