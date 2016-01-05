package serveur.interaction;

import java.rmi.RemoteException;

import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;

public class Soigner extends Interaction<VuePersonnage> {

	public Soigner(){
		super();
	}
	
	@Override
	public void interagit() {
		Personnage pAttaquant = attaquant.getElement();
		//Personnage pDefenseur = attaquant.getElement();
		int c = pAttaquant.getCaract(Caracteristique.FORCE);
		try {
			arene.incrementeCaractElement(defenseur, Caracteristique.VIE, c);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
