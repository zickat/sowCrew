package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.strategies.ControleMental;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

/**
 * @author val
 * Le mentalist possede un autre personnage
 */
public class Mentalist extends Personnage {

	private static final long serialVersionUID = 3530939510801359336L;

	public Mentalist(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
	}

	@Override
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new ControleMental(ipArene, port, ipConsole, this, nbTours, position, logger);
	}
	
}
