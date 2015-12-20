package serveur.interaction;

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
		Personnage pDefenseur = attaquant.getElement();
		int c = pAttaquant.getCaract(Caracteristique.FORCE);
		pDefenseur.incrementeCaract(Caracteristique.VIE, Calculs.restreintCarac(Caracteristique.VIE, c));
	}

}
