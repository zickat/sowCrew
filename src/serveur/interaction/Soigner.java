package serveur.interaction;

import java.rmi.RemoteException;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

public class Soigner extends Interaction<VuePersonnage> {

	public Soigner(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}

	@Override
	public void interagit() {
		Personnage pAttaquant = attaquant.getElement();
		//Personnage pDefenseur = attaquant.getElement();
		int c = pAttaquant.getCaract(Caracteristique.FORCE);
		try {
			arene.incrementeCaractElement(defenseur, Caracteristique.VIE, c);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
