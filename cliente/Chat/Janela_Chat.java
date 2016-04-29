package Chat;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Janela_Chat extends JFrame {
	private JScrollPane panelLeft;
	private JPanel panelRight; 
	private JScrollPane scrollPainel;
	private JScrollPane scrollPainel2;
	private JTextArea areaTextRecebe;
	private JTextArea areaTextSend;
	private JButton btnEnviar;
	private DefaultListModel<InfoChat> chat = new DefaultListModel<InfoChat>();
	private JList<InfoChat> listChat;

	public static void main (String[] args){
		try{
			UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
			UIManager.put("RootPane.setupButtonVisible", false);			
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void incluirMsg(String chatNome, String msg){
		//for(ListaChat temp : chat){
		for(int x=0;chat.size() > x;x++){
			if(chat.get(x).getNome().equals(chatNome)){
				chat.get(x).getText().append(msg);
				break;
			}
		}
	}
	
	public Janela_Chat(Cliente client) {
		setTitle("Conectado | Usuario: " + client.nome);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(400, 100, 700, 500);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(false);
		
		chat.addElement(new InfoChat("Chat All", "todos"));
		chat.get(chat.getSize()-1).setIconON();
		listChat = new JList<InfoChat>(chat);
		listChat.setSelectedIndex(0);
		
		panelLeft = new JScrollPane(listChat);
		getContentPane().add(panelLeft);
		panelLeft.setPreferredSize(new Dimension(100,500));
		panelLeft.setLayout(new ScrollPaneLayout());
		
		panelRight = new JPanel();
		getContentPane().add(panelRight);
		panelRight.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelRight.setPreferredSize(new Dimension(270, 500));
		panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
		
		areaTextRecebe = listChat.getSelectedValue().getText();
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
		btnEnviar.addMouseListener(new MouseAdapter() {
			@Override
		public void mouseClicked(MouseEvent e) {
				String msg = areaTextSend.getText();
				if(msg != ""){
					if(listChat.getSelectedValue().getNome().equals("Chat All")){
						client.Enviar("GROUP_MSG", "Chat All", msg);
					}else{
						client.Enviar("SINGLE_MSG",listChat.getSelectedValue().getNome(),msg);
					}
					areaTextSend.setText("");
					areaTextRecebe.append("Eu: " + msg + "\n");
				}
			}
		});
		scrollPainel.setRowHeaderView(btnEnviar);
	
		client.SetChat(this);
		Thread tclient = new Thread(client);
		tclient.start();
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
            	 Object[] options = { "Sim", "Não" }; 
            	int confirm = JOptionPane.showOptionDialog(null, "Deseja sair do chat?", "Confirmação", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE , null, options, options);
                if(confirm == 0){
                    client.Fechar();
                    System.exit(0);
                }
            }
        });
		listChat.addListSelectionListener(new ListSelectionListener(){
			  public void valueChanged(ListSelectionEvent listSelectionEvent) {
				  if (listSelectionEvent.getValueIsAdjusting()){
					  areaTextRecebe = listChat.getSelectedValue().getText();
					  scrollPainel2.setViewportView(areaTextRecebe);
				  }
			  }
		});
		
		listChat.setCellRenderer(new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
          	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          	if (value instanceof InfoChat) {
          		InfoChat temp = (InfoChat)value;
          		setText(temp.getNome()+"("+temp.getIp()+")");
          		this.setIcon(temp.icon);
          	}
          	return this;
          }
		});
	}
	public void addLabelClientON(String nome, String ip){
		chat.addElement(new InfoChat(nome, ip));
		chat.get(chat.getSize()-1).setIconON();
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
}