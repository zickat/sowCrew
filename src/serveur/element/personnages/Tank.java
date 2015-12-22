package serveur.element.personnages;
import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Personnage;
import utilitaires.Calculs;

/**
 * Classe tank avec beaucoup de vie mais peut de force
 */

/**
 * @author val
 *
 */
public class Tank extends Personnage {

	private static final long serialVersionUID = -5937757897372164830L;

	/**
	 * @param nom
	 * @param groupe
	 * @param caracts
	 */
	public Tank(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
		incrementeCaract(Caracteristique.VIE, Calculs.restreintCarac(Caracteristique.VIE, 100));
		incrementeCaract(Caracteristique.FORCE, Calculs.restreintCarac(Caracteristique.FORCE, 0));
	}

}
