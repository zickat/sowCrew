/**
 * 
 */
package client;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;

import utilitaires.Calculs;

/**
 * @author val
 * Permet de traiter un hashmap de voisin
 */
public class Voisins implements Iterator<Integer>{

	/**
	 * Le hashmap de voisin a traiter
	 */
	private HashMap<Integer, Point> voisins;
	
	/**
	 * La position du personnage
	 */
	private Point maPosition;
	
	
	/**
	 * ce hashmap permet le parcours
	 */
	private HashMap<Integer, Point> voisinsTemp;
	
	/**
	 * @param voisins
	 */
	public Voisins(HashMap<Integer, Point> voisins, Point maPosition) {
		this.voisins = voisins;
		this.maPosition = maPosition;
		voisinsTemp = new HashMap<>();
		voisinsTemp.putAll(voisins);
	}

	@Override
	public boolean hasNext() {
		return !voisinsTemp.isEmpty();
	}

	@Override
	public Integer next() {
		Integer suivant = Calculs.chercheElementProche(maPosition, voisinsTemp);
		voisinsTemp.remove(suivant);
		return suivant;
	}

	@Override
	public void remove() {
		//Rien a faire
	}

	/**
	 * Initialise au debut l'iterator
	 */
	public void init(){
		voisinsTemp = new HashMap<>();
		voisinsTemp.putAll(voisins);
	}
	

	
}
