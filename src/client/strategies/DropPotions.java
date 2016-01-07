/**
 * 
 */
package client.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import lanceur.LanceurPotion;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.element.personnages.Alchimiste;
import serveur.element.potions.PotionVie;
import serveur.vuelement.VuePersonnage;

/**
 * @author Solen
 *
 */

	/**
	 * Stratégie d'un Alchimiste
	 */

public class DropPotions extends Fuite {	
	
	private int compteur = 5 ;
	
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
	public DropPotions(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
			LoggerProjet logger) {
		super(ipArene, port, ipConsole, pers, nbTours, position, logger);
	}
	
	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		
		IArene arene = console.getArene();
		Alchimiste p = (Alchimiste)arene.elementFromConsole(console);
				
		if (compteur <= 0) {
			VuePersonnage maVue = (VuePersonnage) arene.vueFromConsole(console);
			Point maPos = maVue.getPosition();
			console.setPhrase("Je pose une potion");
			Potion potion1 = new PotionVie(p.getGroupe()) ;
			new Thread(new LanceurPotion(potion1,maPos)).start();
			compteur = 6 ;
		} else super.executeStrategie(voisins);
		compteur -- ;
	}

}
