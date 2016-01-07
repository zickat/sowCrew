/**
 * Personnage qui drop des potions
 */
package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.strategies.DropPotions;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

/**
 * @author Solen
 *
 */
public class Alchimiste extends Personnage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur du personnage Alchimiste
	 * @param nom du personnage
	 * @param groupe qui a envoye le personnage
	 */
	public Alchimiste(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
		caracts.put(Caracteristique.VIE, 100) ;
		caracts.put(Caracteristique.FORCE, 2) ;
		caracts.put(Caracteristique.INITIATIVE, 50) ;
	}
	
	@Override
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new DropPotions(ipArene, port, ipConsole, this, nbTours, position, logger);
	}

}
