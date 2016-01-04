/**
 * 
 */
package serveur.interaction;

import serveur.Arene;
import serveur.vuelement.VuePersonnage;

/**
 * @author val
 * Prend le controle d'un personnage
 */
public class Hypnose extends Interaction<VuePersonnage> {
	
	public Hypnose(){
		//
	}

	@Override
	public void interagit() {
		defenseur.setGroupe(attaquant.getElement().getGroupe());
	}

}
