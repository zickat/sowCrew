package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.strategies.LifeSteal;
import client.strategies.Resurrect;
import client.strategies.Separation;
import lanceur.LanceurPerso;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

public class Necromancien extends Personnage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public Necromancien(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
		// TODO Auto-generated constructor stub
	}
	
	
	/* (non-Javadoc)
	 * @see serveur.element.Personnage#strategie(java.lang.String, int, java.lang.String, int, java.awt.Point, logger.LoggerProjet)
	 */
	@Override
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new Resurrect(ipArene, port, ipConsole, this, nbTours, position, logger);
	}	

}
