package serveur;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.IConsole;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.vuelement.VueElement;

/**
 * Definit les methodes qui pourront s'appliquer a l'arene par le reseau.
 */
public interface IArene extends Remote {	
	
	/**************************************************************************
	 * Connexion et deconnexion, partie non commencee ou finie. 
	 **************************************************************************/
	
	/**
	 * Retourne une reference RMI libre pour un element.
	 * @return reference RMI inutilisee
	 */
	public int alloueRefRMI() throws RemoteException;
	
	/**
	 * Connecte un personnage a l'arene.
	 * @param refRMI reference RMI de l'element a connecter
	 * @param ipConsole ip de la console correspondant au personnage
	 * @param personnage personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position courante
	 * @return vrai si l'element a ete connecte, faux sinon
	 * @throws RemoteException
	 */
	public boolean connecte(int refRMI, String ipConsole, 
			Personnage personnage, int nbTours, Point position) throws RemoteException;
	
	/**
	 * Deconnecte un element du serveur.
	 * @param refRMI reference RMI correspondant au personnage a deconnecter
	 * @param cause cause de la deconnexion
	 * @param phrase phrase a ecrire sur l'interface
	 * @throws RemoteException
	 */
	public void deconnecte(int refRMI, String cause, String phrase) throws RemoteException;

	/**
	 * Teste si la partie est finie.
	 * @return true si la partie est finie, false sinon
	 * @throws RemoteException
	 */
	public boolean estPartieFinie() throws RemoteException;

	/**
	 * Teste si la partie a commence.
	 * @return vrai si la partie a commence, faux sinon
	 * @throws RemoteException
	 */
	boolean estPartieCommencee() throws RemoteException;

	/**
	 * Ajoute une potion dans l'arene a n'importe quel moment en mode arene 
	 * libre.
	 * @param potion potion
	 * @param position position de la potion
	 * @throws RemoteException
	 */
	public void ajoutePotion(Potion potion, Point position) throws RemoteException;
	
	

	/**************************************************************************
	 * Accesseurs sur les elements du serveur. 
	 **************************************************************************/
	/**
	 * Permet de connaitre le nombre de tours restants
	 * @return nombre de tours restant
	 * @throws RemoteException
	 */
	public int getNbToursRestants() throws RemoteException ;

	/**
	 * Permet de savoir le nombre de tours ecoules
	 * @return nombre de tour ecoules
	 * @throws RemoteException
	 */
	public int getTour() throws RemoteException;

	/**
	 * Calcule la liste les voisins d'un element represente par sa reference
	 * RMI.
	 * @param refRMI reference de l'element dont on veut recuperer les voisins
	 * @return map des couples reference/coordonnees des voisins
	 * @throws RemoteException
	 */
	public HashMap<Integer, Point> getVoisins(int refRMI) throws RemoteException;

	/**
	 * Permet de recuperer une copie de l'element correspondant a la reference 
	 * RMI.
	 * @param refRMI reference RMI
	 * @return copie de l'element correspondant a la reference RMI donnee
	 * @throws RemoteException
	 */
	public Element elementFromRef(int refRMI) throws RemoteException;

	/**
	 * Permet de recuperer une copie de l'element correspondant a la console.
	 * @param console console
	 * @return copie de l'element correspondant a la console donnee
	 * @throws RemoteException
	 */
	public Element elementFromConsole(IConsole console) throws RemoteException;
	
	/**
	 * Renvoie la vue correspondant a la reference RMI donnee.
	 * @param refRMI reference RMI
	 * @return vue correspondante
	 */
	public VueElement<?> vueFromRef(int refRMI) throws RemoteException;
	
	/**
	 * Renvoie la vue correspondant a la console donnee.
	 * @param console console
	 * @return vue correspondante
	 * @throws RemoteException
	 */
	public VueElement<?> vueFromConsole(IConsole console) throws RemoteException;

	/**
	 * Permet de savoir la position d'un element
	 * @param refRMI reference de l'element
	 * @return position de l'element
	 * @throws RemoteException
	 */
	public Point getPosition(int refRMI) throws RemoteException;

	/**
	 * Modifie la phrase du personnage correspondant a la console donnee.
	 * @param refRMI reference RMI du personnage dont on doit modifier la phrase
	 * @param s nouvelle phrase
	 * @throws RemoteException
	 */
	public void setPhrase(int refRMI, String s) throws RemoteException;
	

	/**************************************************************************
	 * Gestion des interactions.
	 **************************************************************************/

	/**
	 * Execute le ramassage d'une potion par un personnage, si possible.
	 * Le ramassage echoue si une action a deja ete executee ce tour par ce 
	 * personnage, ou si la potion est trop loin du personnage.
	 * @param refRMI reference RMI du personnage voulant ramasser une potion
	 * @param refPotion reference RMI de la potion qui doit etre ramasse
	 * @return vrai si l'action a ete effectuee, faux sinon
	 * @throws RemoteException
	 */
	public boolean ramassePotion(int refRMI, int refPotion) throws RemoteException;
	
	/**
	 * Execute un duel entre le personnage correspondant a la console donnee 
	 * et l'adversaire correspondant a la reference RMI donnee.
	 * Le duel echoue si une action a deja ete executee a ce tour par 
	 * l'attaquant, si les personnages sont trop eloignes, si l'un des deux 
	 * n'est plus actif (mort)
	 * @param refRMI reference RMI de l'attaquant, qui demande un duel
	 * @param refAdv reference RMI du defenseur
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean lanceAttaque(int refRMI, int refAdv) throws RemoteException;
	
	/**
	 * Execute un soin du personnage correspondant a la console donnée
	 * et l'amis correspondant à la reference RMI donnee
	 * Le duel echoue si une action a deja ete effectue a ce tour par
	 * l'attaquant, si les personnages sont trop eloignes, si l'un des deux
	 * n'est plus actif (mort)
	 * @param refRMI reference rmi du soigneur
	 * @param amis reference RMI du soigne
	 * @return vrai si l'action a eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean soigner(int refRMI, int amis) throws RemoteException;
	
	/**
	 * Deplace le personnage correspondant a la console donne vers l'element 
	 * correspondant a la reference RMI cible.
	 * Le deplacement echoue si une action a deja ete executee a ce tour par 
	 * ce personnage.
	 * @param refRMI reference RMI du personnage voulant se deplacer
	 * @param refCible reference RMI de l'element vers lequel on veut se 
	 * deplacer, ou 0 si on veut se deplacer aleatoirement
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean deplace(int refRMI, int refCible) throws RemoteException;
	
	/**
	 * Deplace le personnage correspondant a la console donne vers le point 
	 * cible.
	 * Le deplacement echoue si une action a deja ete executee a ce tour par 
	 * ce personnage.
	 * @param refRMI reference RMI du personnage voulant se deplacer
	 * @param objectif point vers lequel on veut se deplacer
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean deplace(int refRMI, Point objectif) throws RemoteException;
	
	/**
	 * Deplace le personnage cible a la position donne par le point objectif
	 * Le deplacement echoue si une action a deja ete effectuee a ce tour par
	 * ce personnage
	 * @param refRMI reference RMI du personnage voulant se deplacer
	 * @param objectif point vers lequel sera teleporte le personnage
	 * @return vrai si l'action a eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean teleporte(int refRMI, Point objectif) throws RemoteException;

	

	/**************************************************************************
	 * Specifique au tournoi.
	 **************************************************************************/
	
	/**
	 * Verifie le mot de passe administrateur. 
	 * @param motDePasse mot de passe a verifier
	 * @return true si le mot de passe est ok, false sinon
	 * @throws RemoteException
	 */
	public boolean verifieMotDePasse(char[] motDePasse) throws RemoteException;

	/**
	 * Lance la partie.
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void commencePartie(String motDePasse) throws RemoteException;

	/**
	 * Ejecte un joueur de la partie. 
	 * @param refRMI personnage
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void ejectePersonnage(int refRMI, String motDePasse) throws RemoteException;

	/**
	 * Ajoute une potion dans l'arene a n'importe quel moment en mode tournoi.
	 * @param potion potion
	 * @param position position de la potion
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void lancePotion(Potion potion, Point position, String motDePasse) throws RemoteException;
	
}

