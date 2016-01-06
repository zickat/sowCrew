package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.strategies.Enrage;
import client.strategies.Separation;
import lanceur.LanceurPerso;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

/**
 * Personnage avec 0 initiative, pas de force, qui gagne de la force
 * à chaque coup reçu.
 * @author benjamin
 *
 */
public class Berserker extends Personnage {

	
	private static final long serialVersionUID = -1083407268088377816L;

	/** 
	 * 
	 * @param nom du personnage
	 * @param groupe associé au personnage
	 */
	public Berserker(String nom, String groupe) {
		super(nom, groupe,new HashMap<Caracteristique, Integer>());
		incrementeCaract(Caracteristique.VIE, -25);
	    incrementeCaract(Caracteristique.INITIATIVE, -50);	
	}
	/**
	 * 
	 */
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new Enrage(ipArene, port, ipConsole, this, nbTours, position, logger);
	}
	
	
	
}
