/**
 * 
 */
package serveur.interaction;

import java.rmi.RemoteException;

import serveur.Arene;
import serveur.vuelement.VuePersonnage;

/**
 * @author val
 * Prend le controle d'un personnage
 */
public class Hypnose extends Interaction<VuePersonnage> {

	public Hypnose(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}

	@Override
	public void interagit() {
		try {
			arene.setPhrase(defenseur.getRefRMI(), attaquant.getElement().getGroupe());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
