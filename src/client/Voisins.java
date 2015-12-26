/**
 * 
 */
package client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;


import serveur.IArene;
import serveur.element.*;
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
	 * Le personnage dont on cherche les voisins
	 */
	private Personnage p;
	
	/**
	 * ce hashmap permet le parcours
	 */
	private HashMap<Integer, Point> voisinsTemp;
	
	/**
	 * @param voisins
	 */
	public Voisins(HashMap<Integer, Point> voisins, Point maPosition, Personnage p) {
		this.voisins = voisins;
		this.maPosition = maPosition;
		this.p = p;
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

	public void init(){
		voisinsTemp = new HashMap<>();
		voisinsTemp.putAll(voisins);
	}
	
	/**
	 * Donne l'ennemis le plus proche de notre position, 0 si aucun
	 * @param arene l'arene dans laquelle on a les voisins
	 * @return la reference de l'ennemis le plus proche, 0 si aucun
	 * @throws RemoteException 
	 */
	public Integer ennemisLePlusProche(IArene arene) throws RemoteException{
		init();
		Integer ref;
		Element e;
		while(hasNext()){
			ref = next();
			 e = arene.elementFromRef(ref);
			if(e instanceof Personnage){
				if(!e.getGroupe().equals(p.getGroupe())){
					return ref;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Donne l'amis le plus proche de notre position, 0 si aucun
	 * @param arene l'arene dans laquelle on a les voisin
	 * @return la reference de l'ennemis le plus proche, 0 si aucun
	 * @throws RemoteException
	 */
	public Integer amisLePlusProche(IArene arene) throws RemoteException{
		init();
		Integer ref;
		Element e;
		while(hasNext()){
			ref = next();
			e = arene.elementFromRef(ref);
			if(e instanceof Personnage){
				if(e.getGroupe().equals(p.getGroupe())){
					return ref;
				}
			}
		}
		return 0;
	}
	
}
