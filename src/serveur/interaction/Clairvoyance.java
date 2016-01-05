package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class Clairvoyance extends Interaction<VuePersonnage>{

	public Clairvoyance(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
		// TODO Auto-generated constructor stub
	}

	public HashMap<Caracteristique,Integer> clair(){
		Personnage att=attaquant.getElement();
		Personnage def=defenseur.getElement();
		return defenseur.getElement().getCaracts();
		
	}

	@Override
	public void interagit() {
		// TODO Auto-generated method stub
		
	}
}
