package Chat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import Painel.TESTE;

public class InfoChat {
	private String nome;
	private String ip;
	private JTextArea areaTextRecebe;
	public int tipo;
	public ImageIcon icon;
	public boolean msgNaoLida = false;
	public JLabel labMsgNLida=new JLabel();
	private List<String> usuarios;
	public String textToolTip = "Participantes: ";
	
	public InfoChat(String nome, String ip, int tipo){
		this.nome = nome;
		this.ip = ip;	
		this.tipo = tipo;
		if(tipo == 1) usuarios = new ArrayList<String>();
		labMsgNLida.setIcon(new ImageIcon("img\\chat.png"));
		labMsgNLida.setVisible(false);
		areaTextRecebe = new JTextArea();
		areaTextRecebe.setLineWrap(true);
		areaTextRecebe.setWrapStyleWord(true);
		areaTextRecebe.setEditable(false);
	}
	public String getNome(){
		return this.nome;
	}
	public String getIp(){
		return this.ip;
	}
	public JTextArea getJText(){
		return this.areaTextRecebe;
	}
	public void setIconON(){
		if(tipo == 0)
			icon = new ImageIcon("img\\user-icon-on.png");
		else
			icon = new ImageIcon("img\\user-group-icon.png");
	}
	public void setIconOff(){
		icon = new ImageIcon("img\\user-icon-off.png");
	}
	public void addUserToGrupo(String user){
		usuarios.add(user);
		textToolTip = textToolTip +user+", ";
	}
	public List<String> getUsuarios(){
		return usuarios;
	}
	public void delUser(String user){
		usuarios.remove(user);
		 textToolTip = "Participantes: ";
		for(int x=0; usuarios.size() > x;x++){
			textToolTip = textToolTip +usuarios.get(x)+", ";
		}
	}
}
