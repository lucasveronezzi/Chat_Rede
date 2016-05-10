package Painel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Janela_Servidor extends JFrame {
	private JPanel container;
	private JPanel panelTop;
	private JScrollPane panelCenter;
	private JScrollPane panelRight;
	private JPanel panelBot;
	private JButton iniciar;
	private JButton pausar;
	private JLabel labelPorta;
	private JTextField textPorta;
	private JTextArea textTeminal;
	private JTextArea textClientes;
	private Servidor server;
	private Thread tServer;
	
	public static void main (String[] args){
		try{
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
			UIManager.put("RootPane.setupButtonVisible", false);
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	new Janela_Servidor().setVisible(true);
            }
        });
	}
	public Janela_Servidor(){
		setTitle("Terminal do Chat de Rede");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 850, 400);
		setResizable(false);
		Janela_Servidor janela = this;
		
		container = new JPanel();
		this.setContentPane(container);
		container.setLayout(new BorderLayout(0, 0));
		
		panelTop = new JPanel();
		container.add(panelTop,BorderLayout.PAGE_START);
		
		iniciar = new JButton("Iniciar Servidor");
		panelTop.add(iniciar);
		pausar = new JButton("Pausar Servidor");
		panelTop.add(pausar);
		pausar.setEnabled(false);
		
		panelBot = new JPanel();
		container.add(panelBot, BorderLayout.PAGE_END);
		
		labelPorta = new JLabel("Porta do Servidor");
		panelBot.add(labelPorta);
		
		textPorta = new JTextField("555");
		panelBot.add(textPorta);
		textPorta.setColumns(10);
		
		panelCenter = new JScrollPane();
		container.add(panelCenter, BorderLayout.CENTER);
		panelRight = new JScrollPane();
		panelRight.setPreferredSize(new Dimension(250, 100));
		container.add(panelRight, BorderLayout.EAST);
		
		textTeminal = new JTextArea();
		textTeminal.setEditable(false);
		panelCenter.setViewportView(textTeminal);
		
		textClientes = new JTextArea();
		textClientes.setEditable(false);
		panelRight.setViewportView(textClientes);
		
		iniciar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Integer porta = Integer.parseInt(textPorta.getText());
				server = new Servidor(porta,janela);
				tServer = new Thread(server);
				tServer.start();
				iniciar.setEnabled(false);
				pausar.setEnabled(true);
			}
		});
		pausar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
	}
	public void addMsgTerminal(String msg){
		textTeminal.append(msg);
		ajustaScroll();
	}
	public void addErroTerminal(String msg){
		textTeminal.append(msg);
		ajustaScroll();
	}
	public void addCliente(String cliente, String ip, int porta, String data){
		textClientes.append(cliente+ "(" +ip+ ", " +String.valueOf(porta)+ ") "+data+"\n");
		ajustaScroll();
	}
	public void ajustaScroll(){
		panelCenter.getVerticalScrollBar().setValue(panelCenter.getVerticalScrollBar().getMaximum());
		panelRight.getVerticalScrollBar().setValue(panelRight.getVerticalScrollBar().getMaximum());
	}
}