package Chat;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import Painel.TESTE;
import net.sf.jcarrierpigeon.WindowPosition;
import net.sf.jtelegraph.Telegraph;
import net.sf.jtelegraph.TelegraphQueue;
import net.sf.jtelegraph.TelegraphType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

public class Janela_Chat extends JFrame {
	private JScrollPane panelLeft;
	private JPanel panelRight; 
	private JScrollPane scrollPainel;
	private JScrollPane scrollPainel2;
	private JTextArea areaTextRecebe;
	private JTextArea areaTextSend;
	private JButton btnEnviar;
	private JMenu menuGrupo;
	private DefaultListModel<InfoChat> chat = new DefaultListModel<InfoChat>();
	private JList<InfoChat> listChat;
	private Cliente client;
	private JDialog dgrupo_add;
	private JDialog dgrupo_criar;
	private JDialog darquivo_enviar;
	private JTextField textArquivo;
	private DefaultListModel<String> clienteAll = new DefaultListModel<String>();
	private DefaultListModel<String> clienteGrupo = new DefaultListModel<String>();
	private int indexGrupo = 0;
	private JTextField txtNomeGrupoAdd;
	private Telegraph grupoAdd;
	private TelegraphQueue  queue = new TelegraphQueue ();
	private JProgressBar percentFile;

	public Janela_Chat(Cliente client) {
		this.client = client;
		this.client.setChat(this);
		Thread tclient = new Thread(this.client);
		tclient.start();
		ini_CompGraficos();
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
				listChat.setSelectedIndex(0);
				areaTextRecebe = listChat.getSelectedValue().getJText();
				chat.get(listChat.getSelectedIndex()).msgNaoLida = false;
				scrollPainel2.setViewportView(areaTextRecebe);
				break;
			}
		}
	}
	public void incluirMsg(String chatNome, String msg){
		//for(ListaChat temp : chat){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(chatNome)){
				chat.get(x).getJText().append(msg);
				if(!listChat.isSelectedIndex(x)){
					chat.get(x).msgNaoLida = true;
				}
				break;
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
		grupoAdd = new Telegraph(titulo, msg, TelegraphType.NOTIFICATION_ADD, WindowPosition.BOTTOMRIGHT, 5000);
		queue.add(grupoAdd);
	}
	
	public void ini_CompGraficos(){
		setTitle("Conectado | Usuario: " + client.nome);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(400, 100, 700, 500);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(false);
		
		listChat = new JList<InfoChat>(chat);
		listChat.setBorder(new TitledBorder(new LineBorder(null), "Chats"));
		listChat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		panelLeft = new JScrollPane(listChat);
		getContentPane().add(panelLeft);
		panelLeft.setPreferredSize(new Dimension(100,500));
		panelLeft.setLayout(new ScrollPaneLayout());
		
		panelRight = new JPanel();
		getContentPane().add(panelRight);
		panelRight.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelRight.setPreferredSize(new Dimension(270, 500));
		panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
		
		scrollPainel2 = new JScrollPane(areaTextRecebe);
		scrollPainel2.setPreferredSize(new Dimension(200,320));
		scrollPainel2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelRight.add(scrollPainel2);
		
		areaTextSend = new JTextArea();
		areaTextSend.setColumns(10);
		areaTextSend.setLineWrap(true);
		areaTextSend.setWrapStyleWord(true);
		
		scrollPainel = new JScrollPane(areaTextSend);
		scrollPainel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelRight.add(scrollPainel);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setEnabled(false);
		btnEnviar.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseClicked(MouseEvent e) {
				if(btnEnviar.isEnabled()){
					String msg = areaTextSend.getText().trim();
					if(!listChat.isSelectionEmpty()){
						if(listChat.getSelectedValue().tipo == 1){
							client.enviar("GROUP_MSG", listChat.getSelectedValue().getNome(), msg);
						}else{
							client.enviar("SINGLE_MSG",listChat.getSelectedValue().getNome(),msg);
						}
						areaTextSend.setText("");
						areaTextRecebe.append("Eu: " + msg + "\n");
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
		scrollPainel.setRowHeaderView(btnEnviar);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		menuGrupo = new JMenu("Grupo");
		menuBar.add(menuGrupo);
		menuGrupoCria();
		menuGrupoAddUser();
		menuEnviarArquivo();
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
	                    listChat.setSelectedIndex(0);
	    				areaTextRecebe = listChat.getSelectedValue().getJText();
	    				chat.get(listChat.getSelectedIndex()).msgNaoLida = false;
	    				scrollPainel2.setViewportView(areaTextRecebe);
	                }
			 }
		});
		menuGrupo.add(g_opt3);
		g_opt3.setEnabled(false);
		
		JMenu menuArquivo = new JMenu("Arquivos");
		menuBar.add(menuArquivo);
		JMenuItem a_opt1 = new JMenuItem(new AbstractAction("Enviar arquivo..") {
			 public void actionPerformed(ActionEvent ae) {
				 if(!listChat.isSelectionEmpty() && listChat.getSelectedValue().tipo == 0){
					 textArquivo.setText("");
					 darquivo_enviar.setVisible(true);
				 }
			 }
		});
		menuArquivo.add(a_opt1);
		
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
						  areaTextRecebe = listChat.getSelectedValue().getJText();
						  chat.get(listChat.getSelectedIndex()).msgNaoLida = false;
						  scrollPainel2.setViewportView(areaTextRecebe);
					  }
					  if(listChat.getSelectedValue().tipo == 1 && listChat.getSelectedIndex() > 0){
						  g_opt3.setEnabled(true);
						  g_opt2.setEnabled(true);
						  darquivo_enviar.setEnabled(false);
					  }else{
						  g_opt3.setEnabled(false);
						  g_opt2.setEnabled(false);
						  darquivo_enviar.setEnabled(true);
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
	          			panel.setToolTipText("IP: "+temp.getNome());
	          		}else{	          			
	          			panel.setToolTipText(temp.textToolTip);
	          		}
	          		LookAndFeel.installBorder(this, "BorderFactory.createEmptyBorder()"); 
	          	return panel;
	          }
			});
	}
	
	public void menuEnviarArquivo(){
		SpringLayout springLayout = new SpringLayout();
		darquivo_enviar = new JDialog(this, JDialog.ModalityType.DOCUMENT_MODAL);
		darquivo_enviar.setBounds(450, 220, 500, 250);
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
		btnSelArquivo.setIcon(new ImageIcon(TESTE.class.getResource("/org/jb2011/lnf/beautyeye/ch16_tree/imgs/treeDefaultOpen1.png")));
		btnSelArquivo.setPreferredSize(new Dimension(20,20));
		springLayout.putConstraint(SpringLayout.NORTH, btnSelArquivo, 0, SpringLayout.NORTH, textArquivo);
		springLayout.putConstraint(SpringLayout.WEST, btnSelArquivo, 6, SpringLayout.EAST, textArquivo);
		panelBg.add(btnSelArquivo);
		
		btnSelArquivo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				 String arquivo = GetPath();
				 if(arquivo != null){
					 textArquivo.setText(arquivo);
				 }
			}
		});
		
		JButton btnEnviarArq = new JButton("Enviar Arquivo");
		springLayout.putConstraint(SpringLayout.SOUTH, btnEnviarArq, -10, SpringLayout.SOUTH, panelBg);
		springLayout.putConstraint(SpringLayout.EAST, btnEnviarArq, 275, SpringLayout.WEST, panelBg);
		panelBg.add(btnEnviarArq);

		btnEnviarArq.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(textArquivo.getText() != null){
					client.startConexao_file("enviar", listChat.getSelectedValue().getNome(), textArquivo.getText());
					darquivo_enviar.setVisible(false);
				}
			}
		});
		
		percentFile = new JProgressBar();
		percentFile.setStringPainted(true);
		panelBg.add(percentFile);
		springLayout.putConstraint(SpringLayout.NORTH, percentFile, 40, SpringLayout.NORTH, labArquivo);
		springLayout.putConstraint(SpringLayout.WEST, percentFile, -10, SpringLayout.EAST, labArquivo);
	}
	public void atualizaSt(int n){
		percentFile.setValue(n);
	}
	
	public void menuGrupoAddUser(){
		SpringLayout springLayout = new SpringLayout();
		dgrupo_add = new JDialog(this, "Adicionar Usuário", JDialog.ModalityType.DOCUMENT_MODAL);
		dgrupo_add.setBounds(450, 220, 350, 250);
		dgrupo_add.setResizable(false);
		
		JPanel panelBg = new JPanel();
		dgrupo_add.add(panelBg);
		panelBg.setLayout(springLayout);
		panelBg.setBackground(Color.white);
		
		JLabel labNomeGrupo = new JLabel("Nome do Grupo: ");
		springLayout.putConstraint(SpringLayout.NORTH, labNomeGrupo, 21, SpringLayout.NORTH, panelBg);
		springLayout.putConstraint(SpringLayout.WEST, labNomeGrupo, 25, SpringLayout.WEST, panelBg);
		panelBg.add(labNomeGrupo);
		
		txtNomeGrupoAdd = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtNomeGrupoAdd, 0, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, txtNomeGrupoAdd, 6, SpringLayout.EAST, labNomeGrupo);
		panelBg.add(txtNomeGrupoAdd);
		txtNomeGrupoAdd.setEnabled(false);
		txtNomeGrupoAdd.setColumns(10);
		
		JScrollPane scrollList = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, scrollList, -15, SpringLayout.WEST, labNomeGrupo);
		scrollList.setBorder(new TitledBorder(new LineBorder(null), "Usuários"));
		panelBg.add(scrollList);
		scrollList.setPreferredSize(new Dimension(100,120));
		JList<String> listChat = new JList<String>(clienteGrupo);
		listChat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollList.setViewportView(listChat);
		
		JButton btnAdicionar = new JButton("Adicionar");
		springLayout.putConstraint(SpringLayout.SOUTH, btnAdicionar, -10, SpringLayout.SOUTH, panelBg);
		springLayout.putConstraint(SpringLayout.EAST, btnAdicionar, 210, SpringLayout.WEST, dgrupo_add);
		panelBg.add(btnAdicionar);
		
		btnAdicionar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(listChat.isSelectionEmpty())
					JOptionPane.showMessageDialog(null, "Selecione o usuário que deseja adicionar ao grupo", "Alerta", JOptionPane.WARNING_MESSAGE);
				else{
					client.enviar("ADD_USER_GRUPO", txtNomeGrupoAdd.getText(), listChat.getSelectedValue());
					dgrupo_add.setVisible(false);
				}
			}
		});
	}
	
	public void menuGrupoCria(){
		SpringLayout springLayout = new SpringLayout();		
		dgrupo_criar = new JDialog(this, "Adicionar Grupo", JDialog.ModalityType.DOCUMENT_MODAL);
		dgrupo_criar.setBounds(450, 220, 400, 300);
		dgrupo_criar.setResizable(false);
		
		JPanel panelBg = new JPanel();
		dgrupo_criar.add(panelBg);
		panelBg.setLayout(springLayout);
		panelBg.setBackground(Color.white);
		
		JLabel labNomeGrupo = new JLabel("Nome do Grupo: ");
		springLayout.putConstraint(SpringLayout.NORTH, labNomeGrupo, 21, SpringLayout.NORTH, panelBg);
		springLayout.putConstraint(SpringLayout.WEST, labNomeGrupo, 25, SpringLayout.WEST, panelBg);
		panelBg.add(labNomeGrupo);
		
		JTextField txtNomeGrupo = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtNomeGrupo, 0, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, txtNomeGrupo, 6, SpringLayout.EAST, labNomeGrupo);
		panelBg.add(txtNomeGrupo);
		txtNomeGrupo.setColumns(10);
		
		JScrollPane scrollList1 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList1, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, scrollList1, -15, SpringLayout.WEST, labNomeGrupo);
		scrollList1.setBorder(new TitledBorder(new LineBorder(null), "Todos"));
		panelBg.add(scrollList1);
		scrollList1.setPreferredSize(new Dimension(100,120));
		JList<String> listChat2 = new JList<String>(clienteAll);
		listChat2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollList1.setViewportView(listChat2);
		
		JScrollPane scrollList2 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList2, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.EAST, scrollList2, 15, SpringLayout.EAST, txtNomeGrupo);
		scrollList2.setBorder(new TitledBorder(new LineBorder(null), "Grupo"));
		panelBg.add(scrollList2);
		scrollList2.setPreferredSize(new Dimension(100,120));
		JList<String> listChat3 = new JList<String>(clienteGrupo);
		listChat3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollList2.setViewportView(listChat3);
		
		JButton btnAddToGrupo = new JButton(">>>");
		springLayout.putConstraint(SpringLayout.NORTH, btnAddToGrupo, 30, SpringLayout.NORTH, scrollList1);
		springLayout.putConstraint(SpringLayout.EAST, btnAddToGrupo, 40, SpringLayout.EAST, scrollList1);
		panelBg.add(btnAddToGrupo);
		
		JButton btnRemovToGrupo = new JButton("<<<");
		springLayout.putConstraint(SpringLayout.NORTH, btnRemovToGrupo, 10, SpringLayout.SOUTH, btnAddToGrupo);
		springLayout.putConstraint(SpringLayout.EAST, btnRemovToGrupo, 40, SpringLayout.EAST, scrollList1);
		panelBg.add(btnRemovToGrupo);
		
		JButton btnAdicionar = new JButton("Adicionar");
		springLayout.putConstraint(SpringLayout.SOUTH, btnAdicionar, -10, SpringLayout.SOUTH, panelBg);
		springLayout.putConstraint(SpringLayout.EAST, btnAdicionar, 210, SpringLayout.WEST, dgrupo_criar);
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
					showNotfication("Grupo Adicionado", "Você criou o grupo '"+txtNomeGrupo.getText()+"' com sucesso!");
					dgrupo_criar.setVisible(false);
					txtNomeGrupo.setText("");
				}else{
					JOptionPane.showMessageDialog(null, "Preencha o nome do grupo e inclua no minimo 1 usuário ao grupo", "Alerta", JOptionPane.WARNING_MESSAGE);
				}		
			}
		});
	}
	
	public String GetPath(){
		JFileChooser fc = new JFileChooser(); 
		fc.setAcceptAllFileFilterUsed(true);
        int res = fc.showOpenDialog(null);
        if(res == JFileChooser.APPROVE_OPTION){
            return fc.getSelectedFile().getPath();
         }
        return null;
	}
}