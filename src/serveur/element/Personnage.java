/**
 * 
 */
package serveur.element;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import client.StrategiePersonnage;
import logger.LoggerProjet;
import utilitaires.Calculs;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie.
 * 
 */
public class Personnage extends Element {
	
	private static final long serialVersionUID = 1L;

	private List<DotHot> buffs = new ArrayList<>();
	
	/**
	 * Cree un personnage avec un nom et un groupe.
	 * @param nom du personnage
	 * @param groupe d'etudiants du personnage
	 * @param caracts caracteristiques du personnage
	 */
	public Personnage(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
	}
	
	/**
	 * Incremente la caracteristique donnee de la valeur donnee.
	 * Si la caracteristique n'existe pas, elle sera cree avec la valeur 
	 * donnee.
	 * @param c caracteristique
	 * @param inc increment (peut etre positif ou negatif)
	 */
	public void incrementeCaract(Caracteristique c, int inc) {		
		if(caracts.containsKey(c)) {
			caracts.put(c, Calculs.restreintCarac(c, caracts.get(c) + inc));
		} else {
			caracts.put(c, Calculs.restreintCarac(c, inc));
		}
	}
	
	/**
	 * Tue ce personnage en mettant son nombre de poins de vie a 0.
	 */
	public void tue() {
		caracts.put(Caracteristique.VIE, 0);
	}

	/**
	 * Teste si le personnage est vivant, i.e., son nombre de points de vie
	 * est strictement superieur a 0.
	 * @return vrai si le personnage est vivant, faux sinon
	 */
	public boolean estVivant() {
		Integer vie = caracts.get(Caracteristique.VIE);
		return vie != null && vie > 0;
	}
	
	public void strategie(String ipArene, int port, String ipConsole, 
			int nbTours, Point position, LoggerProjet logger){
		new StrategiePersonnage(ipArene, port, ipConsole, this, nbTours, position, logger);
	}

	public List<DotHot> getBuffs() {
		return buffs;
	}
	
	/**
	 * Ajoute un buff au tableau
	 * @param dh le buff
	 */
	public void ajouterBuffs(DotHot dh){
		buffs.add(dh);
	}

	/**
	 * Applique les buff au personnage pour ce tour et met a jour le nombre de tour restant
	 */
	public synchronized void appliquerBuffs() {
		HashMap<Caracteristique, Integer> total = Caracteristique.mapCaracteristiquesNul();
		for (DotHot dotHot : buffs) {
			for (Caracteristique car : dotHot.caracts.keySet()) {
				total.put(car, dotHot.getCaract(car)+total.get(car));
			}
			dotHot.decrementerNbTour();
		}
		DotHot dotHot;
		for (int i = buffs.size()-1; i>=0; i--) {
			dotHot = buffs.get(i);
			if(dotHot.getNbTours() <= 0){
				buffs.remove(dotHot);
			}
		}
		for (Caracteristique car : total.keySet()) {
			incrementeCaract(car, total.get(car));
		}
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
}
