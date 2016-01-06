package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import lanceur.LanceurPerso;
import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.personnages.Necromancien;
import serveur.element.personnages.Separator;
import serveur.element.personnages.Squelette;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class Necromancie extends Interaction<VuePersonnage>{
	
	/**
	 * Cree une interaction de nécromancie (#squelette)
	 * @param arene arene
	 * @param attaquant necromancien
	 * @param defenseur null
	 * @return 
	 */

	
	public Necromancie(Arene arene, VuePersonnage attaquant, VuePersonnage def) {
		super(arene,attaquant,def);
	}
	
	public Necromancie(){
		
	}

	@Override
	public void interagit(){
		System.err.println("1");
		
		try{
			System.err.println("2");
				Point maPos = attaquant.getPosition();
				System.err.println("3");
				Point p = Calculs.meilleurPoint(maPos, maPos, arene.getVoisins(attaquant.getRefRMI()));
				System.err.println("4");
				new Thread(new LanceurPerso(new Squelette("Squelette", attaquant.getElement().getGroupe(),  
						new HashMap<Caracteristique, Integer>()),p)).start();
				System.err.println("5");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
