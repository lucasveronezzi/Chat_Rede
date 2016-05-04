package Chat;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


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
	private JDialog dgrupo_gerencia;
	private JDialog dgrupo_adicionar;
	private DefaultListModel<String> clienteAll = new DefaultListModel<String>();
	private DefaultListModel<String> clienteGrupo = new DefaultListModel<String>();
	private int indexGrupo = 0;

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
	public void addLabelGrupo(String grupo){
		chat.add(indexGrupo,new InfoChat(grupo, "",1));
		chat.get(indexGrupo).setIconON();
		indexGrupo++;
		JOptionPane.showMessageDialog(null, "Novo grupo: "+grupo, "Grupo Adicionado", JOptionPane.WARNING_MESSAGE);
	}
	public void removeLabelClientON(String chatNome){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(chatNome)){
				chat.remove(x);
				listChat.setSelectedIndex(0);
				break;
			}
		}
	}
	public void incluirMsg(String chatNome, String msg){
		//for(ListaChat temp : chat){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(chatNome)){
				chat.get(x).getText().append(msg);
				if(!listChat.isSelectedIndex(x)){
					chat.get(x).msgNaoLida = true;
				}
				break;
			}
		}
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
					if(msg != ""){
						if(listChat.getSelectedValue().tipo == 1){
							client.enviar("GROUP_MSG", listChat.getSelectedValue().getNome(), msg);
						}else{
							client.enviar("SINGLE_MSG",listChat.getSelectedValue().getNome(),msg);
						}
						areaTextSend.setText("");
						areaTextRecebe.append("Eu: " + msg + "\n");
					}
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
		menuGrupos();
		JMenuItem g_opt1 = new JMenuItem("Gerenciar um Grupo");
		menuGrupo.add(g_opt1);
		JMenuItem g_opt2 = new JMenuItem(new AbstractAction("Criar um Grupo") {
			 public void actionPerformed(ActionEvent ae) {
				 clienteAll.clear();
				 clienteGrupo.clear();
				 for(int x=0; chat.size() > x;x++){
					 if(chat.get(x).tipo == 0){
						 clienteAll.addElement(chat.get(x).getNome());
					 }
				 }
				 dgrupo_adicionar.setVisible(true);
			 }
		});
		menuGrupo.add(g_opt2);
		JMenuItem g_opt3 = new JMenuItem("Sair do Grupo");
		menuGrupo.add(g_opt3);
		
		JMenu menuArquivo = new JMenu("Arquivos");
		menuBar.add(menuArquivo);
		JMenuItem a_opt1 = new JMenuItem("Enviar um arquivo");
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
				  if (listSelectionEvent.getValueIsAdjusting()){
					  areaTextRecebe = listChat.getSelectedValue().getText();
					  chat.get(listChat.getSelectedIndex()).msgNaoLida = false;
					  scrollPainel2.setViewportView(areaTextRecebe);
				  }
			  }
		});
		listChat.setFixedCellHeight(30);
		listChat.setCellRenderer(new DefaultListCellRenderer() {
	          @Override
	          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        	  JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	          	
	          	if (value instanceof InfoChat) {
	          		InfoChat temp = (InfoChat)value;
	          		if(temp.tipo == 0){
	          			label.setToolTipText("IP: "+temp.getNome());
	          		}
	          		if(temp.msgNaoLida){
	          			label.setBackground(new Color(255, 255, 102));
	          		}
	          		label.setText(temp.getNome());
	          		label.setIcon(temp.icon);
	          	}
	          	return this;
	          }
			});
	}
	
	public void menuGrupos(){
		SpringLayout springLayout = new SpringLayout();
		
		dgrupo_gerencia = new JDialog(this, "Gerenciar Grupo", JDialog.ModalityType.DOCUMENT_MODAL);
		dgrupo_gerencia.setBounds(232, 232, 350, 250);
		dgrupo_gerencia.setResizable(false);
		
		dgrupo_adicionar = new JDialog(this, "Adicionar Grupo", JDialog.ModalityType.DOCUMENT_MODAL);
		dgrupo_adicionar.setBounds(232, 232, 350, 250);
		dgrupo_adicionar.setResizable(false);
		dgrupo_adicionar.setLayout(springLayout);
		
		JLabel labNomeGrupo = new JLabel("Nome do Grupo: ");
		springLayout.putConstraint(SpringLayout.NORTH, labNomeGrupo, 21, SpringLayout.NORTH, dgrupo_adicionar);
		springLayout.putConstraint(SpringLayout.EAST, labNomeGrupo, 130, SpringLayout.EAST, dgrupo_adicionar);
		dgrupo_adicionar.add(labNomeGrupo);
		
		JTextField txtNomeGrupo = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtNomeGrupo, 0, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, txtNomeGrupo, 6, SpringLayout.EAST, labNomeGrupo);
		dgrupo_adicionar.add(txtNomeGrupo);
		txtNomeGrupo.setColumns(10);
		
		JScrollPane scrollList1 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList1, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.WEST, scrollList1, -15, SpringLayout.WEST, labNomeGrupo);
		scrollList1.setBorder(new TitledBorder(new LineBorder(null), "Todos"));
		dgrupo_adicionar.add(scrollList1);
		scrollList1.setPreferredSize(new Dimension(100,120));
		JList<String> listChat2 = new JList<String>(clienteAll);
		scrollList1.setViewportView(listChat2);
		
		JScrollPane scrollList2 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollList2, 31, SpringLayout.NORTH, labNomeGrupo);
		springLayout.putConstraint(SpringLayout.EAST, scrollList2, 15, SpringLayout.EAST, txtNomeGrupo);
		scrollList2.setBorder(new TitledBorder(new LineBorder(null), "Grupo"));
		dgrupo_adicionar.add(scrollList2);
		scrollList2.setPreferredSize(new Dimension(100,120));
		JList<String> listChat3 = new JList<String>(clienteGrupo);
		scrollList2.setViewportView(listChat3);
		
		JButton btnAddToGrupo = new JButton(">>>");
		springLayout.putConstraint(SpringLayout.NORTH, btnAddToGrupo, 30, SpringLayout.NORTH, scrollList1);
		springLayout.putConstraint(SpringLayout.EAST, btnAddToGrupo, 40, SpringLayout.EAST, scrollList1);
		dgrupo_adicionar.add(btnAddToGrupo);
		
		JButton btnRemovToGrupo = new JButton("<<<");
		springLayout.putConstraint(SpringLayout.NORTH, btnRemovToGrupo, 10, SpringLayout.SOUTH, btnAddToGrupo);
		springLayout.putConstraint(SpringLayout.EAST, btnRemovToGrupo, 40, SpringLayout.EAST, scrollList1);
		dgrupo_adicionar.add(btnRemovToGrupo);
		
		JButton btnAdicionar = new JButton("Adicionar");
		springLayout.putConstraint(SpringLayout.SOUTH, btnAdicionar, 30, SpringLayout.SOUTH, scrollList1);
		springLayout.putConstraint(SpringLayout.EAST, btnAdicionar, 0, SpringLayout.EAST, labNomeGrupo);
		dgrupo_adicionar.add(btnAdicionar);
		
		JButton btnCancelar = new JButton("Cancelar");
		springLayout.putConstraint(SpringLayout.NORTH, btnCancelar, 0, SpringLayout.NORTH, btnAdicionar);
		springLayout.putConstraint(SpringLayout.WEST, btnCancelar, 90, SpringLayout.WEST, btnAdicionar);
		dgrupo_adicionar.add(btnCancelar);
		
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
					dgrupo_adicionar.setVisible(false);
				}else{
					JOptionPane.showMessageDialog(null, "Preencha o nome do grupo e inclua no minimo 1 usuário ao grupo", "Alerta", JOptionPane.WARNING_MESSAGE);
				}		
			}
		});
		
		btnCancelar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dgrupo_adicionar.setVisible(false);
			}
		});
	}
}