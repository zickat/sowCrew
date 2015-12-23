package serveur.interaction;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

/**
 * 
 */

/**
 * @author val
 *
 */
public class Teleportation extends Deplacement{

	/**
	 * Cree une teleportation.
	 * @param personnage personnage voulant se teleporter
	 * @param voisins voisins du personnage
	 */
	public Teleportation(VuePersonnage personnage, HashMap<Integer, Point> voisins) {
		super(personnage, voisins);
	}

	/**
	 * Deplace le personnage au point donné s'il est vide, a cote sinon
	 */
	@Override
	public void seDirigeVers(Point objectif) throws RemoteException {
		Point cible = Calculs.restreintPositionArene(objectif);
		personnage.setPosition(cible);
	}
	
	
	
}
