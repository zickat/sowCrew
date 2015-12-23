/**
 * 
 */
package lanceur;

import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.personnages.Tank;

/**
 * @author val
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Personnage p1 = new Tank("Val2", "11");
		//Personnage p2 = new Personnage("Sow", "11", new HashMap<Caracteristique,Integer>());
		new LanceurPerso(p1).lancer();
		//new LanceurPerso(p2).lancer();
	}

}
