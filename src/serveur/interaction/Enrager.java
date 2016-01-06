package serveur.interaction;

import java.rmi.RemoteException;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.interaction.Interaction;
import serveur.vuelement.VuePersonnage;

public class Enrager extends Interaction<VuePersonnage> {

	public Enrager(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
		// TODO Auto-generated constructor stub
	}
	
	public Enrager(){
		//
	}
	public void interagit() {
		try {
			arene.incrementeCaractElement(attaquant, Caracteristique.FORCE, 4);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
