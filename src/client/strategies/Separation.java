/**
 * 
 */
package client.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.StrategiePersonnage;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.personnages.Separator;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

/**
 * @author val
 *
 */
public class Separation extends StrategiePersonnage {

	private int lastVie;
	
	/**
	 * Cree la console associe et la strategie associe a un personnage
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param Le personnage associe a la strategie
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */
	public Separation(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
			LoggerProjet logger) {
		super(ipArene, port, ipConsole, pers, nbTours, position, logger);
		lastVie = pers.getCaract(Caracteristique.VIE);
	}

	/* (non-Javadoc)
	 * @see client.StrategiePersonnage#executeStrategie(java.util.HashMap)
	 */
	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		IArene arene = console.getArene();
		Personnage p = (Personnage)arene.elementFromConsole(console);
		VuePersonnage maVue = (VuePersonnage) arene.vueFromConsole(console);
		Point maPos = maVue.getPosition();
		int newvie = p.getCaract(Caracteristique.VIE);
		if(newvie < lastVie)
			((Separator)p).seSeparer(Calculs.meilleurPoint(maPos, maPos, voisins));
		lastVie = newvie;
		super.executeStrategie(voisins);
	}

}
