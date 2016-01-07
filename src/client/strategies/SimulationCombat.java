/**
 * 
 */
package client.strategies;

import java.util.Random;

import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;

/**
 * @author Solen
 *
 */
public class SimulationCombat {
	
	private Personnage personnage ;
	private Personnage ennemi ;

	/**
	 * Constructeur
	 * @param personnage au tour de jouer
	 * @param ennemi en vue
	 */
	public SimulationCombat(Element personnage, Element ennemi) {
		this.personnage = (Personnage) personnage ;
		this.ennemi = (Personnage) ennemi ;
	}
	
	/**
	 * On simule l'attaque du personnage
	 */
	public void tourPersonnage() {
		personnage.incrementeCaract(Caracteristique.INITIATIVE, -10);
		ennemi.incrementeCaract(Caracteristique.INITIATIVE, 10);
		ennemi.incrementeCaract(Caracteristique.VIE, -(personnage.getCaract(Caracteristique.FORCE)));
	}
	
	/**
	 * On simule l'attque de l'ennemi
	 */
	public void tourEnnemi() {
		ennemi.incrementeCaract(Caracteristique.INITIATIVE, -10);
		personnage.incrementeCaract(Caracteristique.INITIATIVE, 10);
		personnage.incrementeCaract(Caracteristique.VIE, -(personnage.getCaract(Caracteristique.FORCE)));
	}
	
	public boolean gagneDuel() {
		while (personnage.estVivant() && ennemi.estVivant()) {
			if (personnage.getCaract(Caracteristique.INITIATIVE) > ennemi.getCaract(Caracteristique.INITIATIVE)) {
				tourPersonnage();
				tourEnnemi();
			} else if (personnage.getCaract(Caracteristique.INITIATIVE) < ennemi.getCaract(Caracteristique.INITIATIVE)) {
				tourEnnemi();
				tourPersonnage();
			} else {
				int rand = new Random().nextInt(1);
				if (rand == 0) {
					tourPersonnage();
					tourEnnemi();
				} else {
					tourEnnemi();
					tourPersonnage();
				}
			}
		}
		return personnage.estVivant() ;
	}

}
