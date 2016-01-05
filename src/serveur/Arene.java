package serveur;

import java.awt.Point;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;

import client.controle.IConsole;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.DotHot;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.interaction.Deplacement;
import serveur.interaction.Duel;
import serveur.interaction.Interaction;
import serveur.interaction.Ramassage;
import serveur.interaction.Teleportation;
import serveur.vuelement.VueElement;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Definit l'arene, qui joue le role de serveur. 
 */
public class Arene extends UnicastRemoteObject implements IAreneIHM, Runnable {

	private static final long serialVersionUID = -354976419811607146L;

	/**
	 * Port a utiliser pour la connexion.
	 */
	protected int port;

	/**
	 * Adresse IP de la machine hebergeant l'arene.
	 */
	private String adresseIP;

	/**
	 * Duree de vie du serveur, en tours de jeu (sachant qu'un tour dure environ
	 * une seconde).
	 */
	private final int NB_TOURS;

	/**
	 * Numero de tour de jeu courant.
	 */
	private int tour = 0;

	/**
	 * Nombre d'elements connectes au serveur.
	 */
	private int compteur = 0;

	/**
	 * Associe les references RMI et les personnages connectes ou ayant ete 
	 * connectes au serveur, vivants ou morts. 
	 */	
	protected Hashtable<Integer, VuePersonnage> personnages = null;
	
	/**
	 * Liste des personnages morts. Permet de garder une trace des personnages
	 * qui ont joué et qui sont maintenant deconnectes. 
	 */
	protected List<VuePersonnage> personnagesMorts = null;

	/**
	 * Associe les references RMI et les potions connectees au serveur.
	 */
	protected Hashtable<Integer, VuePotion> potions = null;

	/**
	 * Vrai si la partie est terminee.
	 * Hors tournoi, cela signifie que le dernier tour a ete effectue.
	 * Pendant un tournoi, la partie se termine aussi lorsqu'il n'y qu'un seul
	 * survivant. 
	 */
	protected boolean partieFinie = false;

	/**
	 * Gestionnaire des logs.
	 */
	protected LoggerProjet logger;

	/**
	 * Constructeur de l'arene.
	 * @param port le port de connexion
	 * @param adresseIP nom de la machine qui heberge l'arene
	 * @param nbTours duree de vue du serveur en nombre de tours de jeu 
	 * (si negatif, duree illimitee)
	 * @param logger gestionnaire de log 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	public Arene(int port, String adresseIP, int nbTours, LoggerProjet logger) 
			throws RemoteException, MalformedURLException  {
		
		super();
		this.port = port;
		this.adresseIP = adresseIP;
		this.NB_TOURS = nbTours;

		personnages = new Hashtable<Integer, VuePersonnage>();
		potions = new Hashtable<Integer, VuePotion>();
		personnagesMorts = new ArrayList<VuePersonnage>();
		
		this.logger = logger;

		// ajout de l'arene au registre RMI
		Naming.rebind(adrToString(), this);
		
		logger.info(Constantes.nomClasse(this), "Arene cree a l'adresse " + adrToString());

		new Thread(this).start();
	}	

	/**
	 * Construit l'adresse complete de l'arene sous forme de chaine de 
	 * caracteres.
	 * @return adresse complete de l'arene
	 */
	private String adrToString() {
		return Constantes.nomRMI(adresseIP, port, "Arene");
	}

	@Override
	public void run() {
		// thread executant la strategie de chaque personnage
		ThreadStrategie ts;
		
		// liste qui va contenir les references RMI des personnages, 
		// ordonnees par leur initiative
		List<Integer> listRef;
		
		while(!partieFinie) {
			// moment de debut du tour
			long begin = System.currentTimeMillis();
			
			// on verouille le serveur durant un tour de jeu pour ne pas avoir 
			// de connexion/deconnexion
			synchronized(this) {
				// tri des references par initiative
				listRef = getSortedRefs();
				// applique buff/debuff
				for (VuePersonnage personnage : personnages.values()) {
					((Personnage)personnage.getElement()).appliquerBuffs();
				}
				// pour chaque personnage, on joue sa strategie
				for(int refRMI : listRef) {
					try {						
						// si pas deconnecte
						if(!verifieDeconnexion(refRMI)) {
							
							// lancement de la strategie (dans un thread separe)
							ts = new ThreadStrategie(consoleFromRef(refRMI));
							
							// attente de la fin de la strategie (temps d'attente max 1 seconde)
							ts.join(1000);
							
							// finit le tour pour ce client
							personnages.get(refRMI).termineTour();
							
							if(ts.isAlive()) {
								// si le thread est toujours vivant apres une 
								// seconde (la strategie n'est pas terminee),
								// on l'ejecte
								logger.info(Constantes.nomClasse(this), 
										"Execution de la strategie de " + 
										nomRaccourciClient(refRMI) + 
										" trop longue ! Client ejecte");
								
								deconnecte(refRMI, 
										"Execution de strategie trop longue. Degage !", 
										"trop lent");
								
							} else {
								// on reteste apres l'execution de la strategie
								verifieDeconnexion(refRMI);
							}
						}
					
					} catch (RemoteException e) {
						logger.severe(Constantes.nomClasse(this), "Erreur dans le run "
								+ "avec la console de reference " + refRMI + "\n" + e.toString());
						e.printStackTrace();
						ejectePersonnage(refRMI);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				} 
			}
			
			tour++;
			verifierPartieFinie();
			
			try {
				long dureeTour = System.currentTimeMillis() - begin;
				
				// dormir au plus 1 seconde pour permettre 
				// la connexion/deconnexion des consoles
				long time = 1000 - dureeTour;
				
				if (time > 0){
					Thread.sleep(time);
				}
	
			} catch(InterruptedException e) {
				logger.severe(Constantes.nomClasse(this), "Erreur : run\n" + e.toString());
				e.printStackTrace();
			}
		}
		
		fermerServeur();
	}
	
	/**
	 * Teste si un personnage doit etre connecte : il est mort, il triche, ou
	 * son temps sur l'arene est ecoule. Si oui, le deconnecte.
	 * @param refRMI reference du personnage
	 * @return vrai si le personnage a ete deconnecte, faux sinon
	 * @throws RemoteException
	 */
	private boolean verifieDeconnexion(int refRMI) throws RemoteException {
		boolean res = true;
		Personnage personnage = (Personnage) elementFromRef(refRMI);
		
		if(!personnage.estVivant()) {
			// on teste si le client est vivant
			logger.info(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
					" est mort... Client ejecte");
			deconnecte(refRMI, 
					"Vous etes mort...", 
					"mort naturelle");
			
		} else if(!verifieCaracts(personnage)) { 
			// on teste la triche
			logger.info(Constantes.nomClasse(this),
					nomRaccourciClient(refRMI) + 
					" est un tricheur... Client ejecte");
			
			deconnecte(refRMI, 
					"Vous etes mort pour cause de triche...", 
					"SHAME!");
			
		} else if(!personnages.get(refRMI).resteTours()) {
			// on teste le nombre de tours
			logger.info(Constantes.nomClasse(this), "Fin du nombre de tours de " + 
					nomRaccourciClient(refRMI) + 
					"... Client ejecte");
			
			deconnecte(refRMI, 
					"Temps autorise dans l'arene ecoule, vous etes elimine !", 
					"temps ecoule");
			
		} else {
			res = false;
		}
		
		return res;
	}

	/**
	 * Renvoie la liste des references triees par ordre de passage en fonction
	 * de l'initiative.
	 * @return liste de toutes les references des personnages de la partie,
	 * ordonnee par initiative (decroissante)
	 */
	private List<Integer> getSortedRefs() {
		// on cree une priority queue de paires reference RMI/initiative du 
		// personnage
		// en ajoutant des paires dans la queue, elles automatiquement seront 
		// classees en suivant leur methode compareTo
		Queue<PaireRefRMIIntitiative> queueRefsInitiative = new PriorityQueue<PaireRefRMIIntitiative>();
		
		for(VuePersonnage client : personnages.values()) {			
			queueRefsInitiative.offer(new PaireRefRMIIntitiative(
					client.getRefRMI(), 
					client.getElement().getCaract(Caracteristique.INITIATIVE)));
		}
	
		
		// on recupere juste les references
		List<Integer> listeRefsTriees = new ArrayList<Integer>();
		
		while(!queueRefsInitiative.isEmpty()) {
			listeRefsTriees.add(queueRefsInitiative.poll().getRef());
		}
	
		return listeRefsTriees;
	}

	/**
	 * Verifie que les caracteristiques du personnage sont valides (pour eviter
	 * la triche).
	 * @param personnage personnage
	 * @return vrai si les caracteristiques sont valides (entre min et max)
	 */
	private boolean verifieCaracts(Personnage personnage) {
		boolean res = true;
		HashMap<Caracteristique, Integer> caracts = personnage.getCaracts();
		
		int valeurTemp;
		
		for (Caracteristique c : caracts.keySet()) {
			valeurTemp = caracts.get(c);
			
			if (valeurTemp < c.getMin() || valeurTemp > c.getMax()) {
				res = false;
			}
		}
		
		return res;
	}

	/**
	 * Ferme le serveur a la fin de la partie. 
	 */
	private void fermerServeur() {
		// afficher le classement dans le log 
		logClassement();
	
		// a la fin de la partie, on ferme la "porte" RMI
		try {
			// deconnection des clients
			List<Integer> listRef = getSortedRefs();
			
			for (int refRMI : listRef) {
				deconnecte(refRMI, "Fermeture du serveur", "");
			}
	
			logger.info(Constantes.nomClasse(this),
					"Fin de la partie ! Fermeture du serveur");
			
			unexportObject(this, true);
			
			logger.info(Constantes.nomClasse(this), "Serveur ferme");
			
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Le mot cle "synchronized" permet de garantir que cette methode est 
	 * accedee par un seul thread a la fois.
	 */
	@Override
	public synchronized int alloueRefRMI() throws RemoteException {
		compteur++;
		return compteur;
	}

	@Override
	public synchronized boolean connecte(int refRMI, String ipConsole, 
			Personnage personnage, int nbTours, Point position) throws RemoteException {
		
		// assignation d'un port unique a chaque personnage
		int portConsole = port + refRMI;
		String adr = Constantes.nomRMI(ipConsole, portConsole, "Console" + refRMI);
		
		boolean res = true;
	
		try {
			logger.info(Constantes.nomClasse(this), "Demande de connexion (" + adr + ")");
			
			// ajout du personnage a la liste
			personnages.put(refRMI, new VuePersonnage(ipConsole, personnage, nbTours, position, refRMI));
	
			logger.info(Constantes.nomClasse(this), "Connexion acceptee (" + adr + ")");
			logElements();
			
		} catch (Exception e) {
			logger.severe(Constantes.nomClasse(this), "Echec de connexion (" + adr + ")");
			e.printStackTrace();
			
			ejectePersonnage(refRMI);
			
			res = false;
		}
		
		return res;
	}

	@Override
	public void deconnecte(int refRMI, String cause, String phrase) throws RemoteException {
		// enregistrement des infos de la console lors de sa deconnexion,
		// le but etant de garder des informations sur les deconnectes		
		VuePersonnage vuePersonnage = personnages.get(refRMI);
		
		vuePersonnage.getElement().tue(); // au cas ou ce ne serait pas une mort "naturelle"
		vuePersonnage.setTourMort(tour);
		setPhrase(refRMI, "MORT >_< (" + phrase + ")");
		
		// ajout a la liste des morts
		personnagesMorts.add(vuePersonnage);
		
		try {
			// fermeture de la console en donnant la raison
			consoleFromRef(refRMI).deconnecte(cause);
			
		} catch (UnmarshalException e) {
			e.printStackTrace();
		}
		
		// suppression de la liste des vivants
		ejectePersonnage(refRMI);
		
	}

	@Override
	protected void finalize() throws Throwable {
		List<Integer> listRef = getSortedRefs();
		
		for (int refRMI: listRef) {
			deconnecte(refRMI, "Fermeture du serveur (Arret force ou crash...)", "");
		}
	
		super.finalize();
	}	

	/**
	 * Recupere le nombre de tours restants. 
	 */
	public int getNbToursRestants() throws RemoteException {
		return (int) (NB_TOURS - tour);
	}

	/**
	 * Recupere le numero de tour courant. 
	 */
	public int getTour() throws RemoteException {
		return tour;
	}

	@Override
	public boolean estPartieCommencee() throws RemoteException {
		return true;
	}

	/**
	 * Renvoie la console correspondant a la reference RMI donnee.
	 * @param refRMI reference RMI
	 * @return console correspondante
	 * @throws RemoteException
	 */
	protected IConsole consoleFromRef(int refRMI) throws RemoteException {
		int p = port + refRMI;
		String ip = null;
		String adr = null;
		IConsole console = null;
	
		try {			
			ip = personnages.get(refRMI).getAdresseIp();
			adr = Constantes.nomRMI(ip, p, "Console" + refRMI);
			console = (IConsole) Naming.lookup(adr);
			
		} catch (MalformedURLException e) {
			console = null;
			logger.severe(Constantes.nomClasse(this), "Erreur : acces a " + adr + "\n" + e.toString());
			e.printStackTrace();
			
		} catch (NotBoundException e) {
			console = null;
			logger.severe(Constantes.nomClasse(this), "Erreur : acces a " + adr + "\n" + e.toString());
			e.printStackTrace();
			
		} catch (NullPointerException e) {
			console = null;
		}
	
		return console;
	}
	
	/**
	 * Renvoie la console correspondant a la vue donnee.
	 * @param vue vue
	 * @return console correspondante
	 * @throws RemoteException
	 */
	public IConsole consoleFromVue(VueElement<?> vue) throws RemoteException {
		return consoleFromRef(vue.getRefRMI());
	}


	@Override
	public VueElement<?> vueFromRef(int refRMI) throws RemoteException {
		VueElement<?> vueElement = personnages.get(refRMI);
		
		if (vueElement == null) {
			vueElement = potions.get(refRMI);
		}
		
		return vueElement;
	}


	@Override
	public VueElement<?> vueFromConsole(IConsole console) throws RemoteException {
		return vueFromRef(console.getRefRMI());
	}
	
	/**
	 * Renvoie l'element correspondant a la reference RMI donnee.
	 * @param refRMI reference RMI
	 * @return element correspondant
	 * @throws RemoteException
	 */
	public Element elementFromRef(int refRMI) throws RemoteException {
		return vueFromRef(refRMI).getElement();
	}
	
	/**
	 * Renvoie l'element correspondant a la console donnee.
	 * @param console console
	 * @return element correspondant
	 * @throws RemoteException
	 */
	public Element elementFromConsole(IConsole console) throws RemoteException {
		return vueFromConsole(console).getElement();
	}
	
	@Override
	public Point getPosition(int refRMI) throws RemoteException {
		return vueFromRef(refRMI).getPosition();
	}

	/**
	 * Verifie les conditions de fin de partie.
	 */
	protected void verifierPartieFinie() {
		partieFinie = NB_TOURS > 0 && tour > NB_TOURS;
	}
	
	@Override
	public boolean estPartieFinie() throws RemoteException {
		return partieFinie;
	}

	@Override
	public List<VuePersonnage> getPersonnages() throws RemoteException {
		ArrayList<VuePersonnage> temp =  new ArrayList<VuePersonnage>(personnages.values());
		ArrayList<VuePersonnage> persVisibles = new ArrayList<>();
		for (VuePersonnage vuePersonnage : temp) {
			if(vuePersonnage.isVisible()){
				persVisibles.add(vuePersonnage);
			}
		}
		return persVisibles;
	}

	@Override
	public List<VuePersonnage> getPersonnagesMorts() throws RemoteException {
		return personnagesMorts;
	}

	@Override
	public List<VuePotion> getPotions() throws RemoteException {
		return new ArrayList<VuePotion>(potions.values());
	}

	@Override
	public HashMap<Integer, Point> getVoisins(int refRMI) throws RemoteException {
		HashMap<Integer, Point> res = new HashMap<Integer, Point>();
	
		VuePersonnage courant = personnages.get(refRMI);
		VuePersonnage tempPers;
		
		// personnages
		for(int refVoisin : personnages.keySet()) {
			tempPers = personnages.get(refVoisin);
			VuePersonnage p = (VuePersonnage) vueFromRef(refVoisin);
			if(estVoisin(courant, tempPers) && p.isVisible()) {
				res.put(refVoisin, tempPers.getPosition());
			}
		}
		
		VuePotion tempPot;
		
		// potions
		for(int refVoisin : potions.keySet()) {
			tempPot = potions.get(refVoisin);
						
			if(estVoisin(courant, tempPot)) {
				res.put(refVoisin, tempPot.getPosition());
			}
		}
		
		return res;
	}

	/**
	 * Verifie que les deux elements sont voisins, l'element courant etant un 
	 * personnage.
	 * Pour etre un voisin : 
	 * ils doivent etre differents,
	 * ils doivent etre a une distance inferieure ou egale a la vision,
	 * il doivent etre actifs. 
	 * @param courant personnage courant
	 * @param voisin element voisin
	 * @return vrai si l'element donne est bien un voisin
	 */
	private boolean estVoisin(VuePersonnage courant, VueElement<?> voisin) throws RemoteException {
		boolean res = courant.getElement().estVivant() &&
				Calculs.distanceChebyshev(voisin.getPosition(), courant.getPosition()) <= Constantes.VISION;
		
		if(voisin instanceof VuePersonnage) { // potion
			res = res && 
					((VuePersonnage) voisin).getElement().estVivant() &&
					voisin.getRefRMI() != courant.getRefRMI();
					
		}
		
		return res;
	}

	@Override
	public List<VuePersonnage> getClassement() throws RemoteException {
		return getPersonnagesClassement();
	}

	/**
	 * Renvoie la liste des personnages tries pour le classement final : 
	 * les vivants ne sont pas classes et les morts sont classes par ordre du
	 * tour ou ils sont morts.
	 * @return liste des personnages ordonnes pour le classement final
	 */
	private List<VuePersonnage> getPersonnagesClassement() {
		PriorityQueue<VuePersonnage> classement = new PriorityQueue<VuePersonnage>();
		
		// recuperation des personnages en vie
		for(VuePersonnage vuePers : personnages.values()) {
			classement.offer(vuePers);
		}
		
		// recuperation des personnages elimines
		for(VuePersonnage vuePers : personnagesMorts) {
			classement.offer(vuePers);
		}
		
		// retour sous forme de liste pour les futures utilisations
		return new ArrayList<VuePersonnage>(classement); 
	}

	@Override
	public VuePersonnage getGagnant() throws RemoteException{
		VuePersonnage res = null;
		List<VuePersonnage> classement = getPersonnagesClassement();
		
		if(!classement.isEmpty()) { // au moins un personnage a joue
			if(classement.size() == 1 || // un seul personnage ou
					!classement.get(1).getElement().estVivant()) // un seul vivant (le 2eme classe est mort)
			res = classement.get(0);
		}
		
		return res;
	}

	@Override
	public void setPhrase(int refRMI, String s) throws RemoteException {
		vueFromRef(refRMI).setPhrase(s);
	}

	/**
	 * Ejecte un personnage : le retire de la liste des vivants et ajoute 
	 * une information dans le log. 
	 * @param refRMI reference du personnage a ejecter
	 */
	protected void ejectePersonnage(int refRMI) {
		if(personnages.remove(refRMI) != null) {
			logger.info(Constantes.nomClasse(this), "Console " + refRMI + " ejectee du registre !");
		}
	}		
	
	/**
	 * Ejecte une potion : la retire de la liste des potions.
	 * @param refRMI reference de la potion a ejecter
	 */
	public void ejectePotion(int refRMI) {
		potions.remove(refRMI);
	}	

	
	@Override
	public synchronized void ajoutePotion(Potion potion, Point position) throws RemoteException {		
		int refRMI = alloueRefRMI();
		VuePotion vuePotion = new VuePotion(potion, position, refRMI);
		
		// ajout de la potion a la liste
		potions.put(refRMI, vuePotion);
		
		logger.info(Constantes.nomClasse(this), "Ajout de la potion " + 
				Constantes.nomCompletClient(vuePotion) + " (" + refRMI + ")");
		
		logElements();
	}

	@Override
	public boolean ramassePotion(int refRMI, int refPotion) throws RemoteException {
		boolean res = false;
		
		VuePersonnage vuePersonnage = personnages.get(refRMI);
		VuePotion vuePotion = potions.get(refPotion);
		
		if (vuePersonnage.isActionExecutee()) {
			// si une action a deja ete executee
			logActionDejaExecutee(refRMI);
			
		} else {
			// sinon, on tente de jouer l'interaction
			int distance = Calculs.distanceChebyshev(vuePersonnage.getPosition(), vuePotion.getPosition());
			
			// on teste la distance entre le personnage et la potion
			if (distance <= Constantes.DISTANCE_MIN_INTERACTION) {
				new Ramassage(this, vuePersonnage, vuePotion).interagit();
				personnages.get(refRMI).executeAction();
				
				res = true;
			} else {
				logger.warning(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
						" a tente d'interagir avec " + vuePotion.getElement().getNom() + 
						", alors qu'il est trop eloigne !\nDistance = " + distance);
			}
		}
		
		return res;
	}
	
	@Override
	public void appliquerDotHot(int refRMI, DotHot buff) throws RemoteException {
		Personnage personnage = (Personnage)elementFromRef(refRMI);
		personnage.ajouterBuffs(buff);
	}

	@Override
	public boolean lanceAttaque(int refRMI, int refRMIAdv) throws RemoteException {
		boolean res = false;
		
		VuePersonnage client = personnages.get(refRMI);
		VuePersonnage clientAdv = personnages.get(refRMIAdv);
		
		if (personnages.get(refRMI).isActionExecutee()) {
			// si une action a deja ete executee
			logActionDejaExecutee(refRMI);
			
		} else {
			// sinon, on tente de jouer l'interaction
			IConsole console = consoleFromRef(refRMI);
			IConsole consoleAdv = consoleFromRef(refRMIAdv);
			
			int distance = Calculs.distanceChebyshev(personnages.get(refRMI).getPosition(), 
					personnages.get(refRMIAdv).getPosition());

			// on teste la distance entre les personnages
			if (distance <= Constantes.DISTANCE_MIN_INTERACTION) {
				Personnage pers = (Personnage) elementFromRef(refRMI);
				Personnage persAdv = (Personnage) elementFromRef(refRMIAdv);
				
				// on teste que les deux personnages soient en vie
				if (pers.estVivant() && persAdv.estVivant()) {
					console.log(Level.INFO, Constantes.nomClasse(this), 
							"J'attaque " + nomRaccourciClient(refRMIAdv));
					consoleAdv.log(Level.INFO, Constantes.nomClasse(this), 
							"Je me fait attaquer par " + nomRaccourciClient(refRMI));
					
					logger.info(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
							" attaque " + nomRaccourciClient(consoleAdv.getRefRMI()));
			
					new Duel(this, client, clientAdv).interagit();
					personnages.get(refRMI).executeAction();
					
					// si l'adversaire est mort
					if (!persAdv.estVivant()) {
						setPhrase(refRMI, "Je tue " + nomRaccourciClient(consoleAdv.getRefRMI()));
						console.log(Level.INFO, Constantes.nomClasse(this), 
								"Je tue " + nomRaccourciClient(refRMI));
						
						logger.info(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
								" tue " + nomRaccourciClient(consoleAdv.getRefRMI()));
					}
					
					res = true;
				} else {
					logger.warning(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
							" a tente d'interagir avec "+nomRaccourciClient(refRMIAdv)+", alors qu'il est mort...");
					
					console.log(Level.WARNING, Constantes.nomClasse(this), 
							nomRaccourciClient(refRMIAdv) + " est deja mort !");
				}
			} else {
				logger.warning(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
						" a tente d'interagir avec "+nomRaccourciClient(refRMIAdv) + 
						", alors qu'il est trop eloigne... Distance de chebyshev = " + distance);
				
				console.log(Level.WARNING, "AVERTISSEMENT ARENE", 
						nomRaccourciClient(refRMIAdv) + " est trop eloigne !\nDistance = " + distance);
			}
		}
		
		return res;
	}
	
	public boolean interagir(int refRMI, int refRMIAdv, Class<? extends Interaction<VuePersonnage>> inter) throws RemoteException{
		boolean res = false;
		
		VuePersonnage client = personnages.get(refRMI);
		VuePersonnage clientAdv = personnages.get(refRMIAdv);
		
		if (personnages.get(refRMI).isActionExecutee()) {
			// si une action a deja ete executee
			logActionDejaExecutee(refRMI);
			
		} else {
			// sinon, on tente de jouer l'interaction
			IConsole console = consoleFromRef(refRMI);
			IConsole consoleAdv = consoleFromRef(refRMIAdv);
			
			int distance = Calculs.distanceChebyshev(personnages.get(refRMI).getPosition(), 
					personnages.get(refRMIAdv).getPosition());

			// on teste la distance entre les personnages
			if (distance <= Constantes.DISTANCE_MIN_INTERACTION) {
				Personnage pers = (Personnage) elementFromRef(refRMI);
				Personnage persAdv = (Personnage) elementFromRef(refRMIAdv);
				
				// on teste que les deux personnages soient en vie
				if (pers.estVivant() && persAdv.estVivant()) {
					console.log(Level.INFO, Constantes.nomClasse(this), 
							"J'attaque " + nomRaccourciClient(refRMIAdv));
					consoleAdv.log(Level.INFO, Constantes.nomClasse(this), 
							"Je me fait attaquer par " + nomRaccourciClient(refRMI));
					
					logger.info(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
							" attaque " + nomRaccourciClient(consoleAdv.getRefRMI()));
			
					try {
						Interaction<VuePersonnage> i = 
								(Interaction<VuePersonnage>) inter.newInstance();
						i.instancier(this, client, clientAdv);
						i.interagit();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					
					
					personnages.get(refRMI).executeAction();
					
					// si l'adversaire est mort
					if (!persAdv.estVivant()) {
						setPhrase(refRMI, "Je tue " + nomRaccourciClient(consoleAdv.getRefRMI()));
						console.log(Level.INFO, Constantes.nomClasse(this), 
								"Je tue " + nomRaccourciClient(refRMI));
						
						logger.info(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
								" tue " + nomRaccourciClient(consoleAdv.getRefRMI()));
					}
					
					res = true;
				} else {
					logger.warning(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
							" a tente d'interagir avec "+nomRaccourciClient(refRMIAdv)+", alors qu'il est mort...");
					
					console.log(Level.WARNING, Constantes.nomClasse(this), 
							nomRaccourciClient(refRMIAdv) + " est deja mort !");
				}
			} else {
				logger.warning(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
						" a tente d'interagir avec "+nomRaccourciClient(refRMIAdv) + 
						", alors qu'il est trop eloigne... Distance de chebyshev = " + distance);
				
				console.log(Level.WARNING, "AVERTISSEMENT ARENE", 
						nomRaccourciClient(refRMIAdv) + " est trop eloigne !\nDistance = " + distance);
			}
		}
		
		return res;
	}
	
	public void seCamouflerOuDeCamoufler(int refRMI) throws RemoteException{
		VuePersonnage p = (VuePersonnage)vueFromRef(refRMI);
		p.setVisible(!p.isVisible());
	}
	
	@Override
	public boolean deplace(int refRMI, int refCible) throws RemoteException {		
		boolean res = false;
		
		VuePersonnage client = personnages.get(refRMI);
		
		if (client.isActionExecutee()) {
			// si une action a deja ete executee
			logActionDejaExecutee(refRMI);
			
		} else {
			// sinon, on tente de jouer l'interaction
			new Deplacement(client, getVoisins(refRMI)).seDirigeVers(refCible);
			client.executeAction();
			
			res = true;
		}
		
		return res;
	}

	@Override
	public boolean deplace(int refRMI, Point objectif) throws RemoteException {
		boolean res = false;
		
		VuePersonnage client = personnages.get(refRMI);
		
		if (client.isActionExecutee()) {
			// si une action a deja ete executee
			logActionDejaExecutee(refRMI);
		} else {
			// sinon, on tente de jouer l'interaction
			new Deplacement(client, getVoisins(refRMI)).seDirigeVers(objectif);
			client.executeAction();

			res = true;
		}
		
		return res;
	}
	
	@Override
	public boolean teleporte(int refRMI, Point objectif) throws RemoteException {
		boolean res = false;
		
		VuePersonnage client = personnages.get(refRMI);
		
		if(client.isActionExecutee()){
			logActionDejaExecutee(refRMI);
		}else{
			new Teleportation(client, getVoisins(refRMI)).seDirigeVers(objectif);
			client.executeAction();
			res = true;
		}
		return res;
	}

	/**
	 * Ajoute l'increment donne a la caracteristique donne de l'element 
	 * correspondant a la vue donnee. 
	 * L'increment peut etre positif ou negatif. 
	 * @param vuePersonnage client a mettre a jour
	 * @param carac caracteristique a mettre a jour
	 * @param increment increment a ajouter a la valeur de la caracteristique 
	 * courante
	 * @throws RemoteException
	 */
	public void incrementeCaractElement(VuePersonnage vuePersonnage, Caracteristique carac, 
			int increment) throws RemoteException {
		
		int refRMI = vuePersonnage.getRefRMI();
		IConsole console = consoleFromRef(refRMI);
		Personnage pers = vuePersonnage.getElement();
		
		// increment de la caracteristique
		pers.incrementeCaract(carac, increment);
		
		if(pers.estVivant()) {
			if (increment < 0) {
				console.log(Level.INFO, Constantes.nomClasse(this), "J'ai perdu " + -increment + " points de " + carac);
				
				if (carac == Caracteristique.VIE) {
					setPhrase(refRMI, "Ouch, j'ai perdu " + increment + " points de vie.");
				}
				
			} else {
				console.log(Level.INFO, Constantes.nomClasse(this), 
						"J'ai gagne " + increment + " points de " + carac);
			}
		}
	}
	
	public LoggerProjet getLogger() {
		return logger;
	}

	/**
	 * Ajoute une ligne au log d'un client.
	 * @param vueElement vue du client
	 * @param level niveau du log
	 * @param prefixe prefixe au message
	 * @param msg message
	 * @throws RemoteException
	 */
	public void logClient(VueElement<?> vueElement, Level level, String prefixe, 
			String msg) throws RemoteException {
		IConsole cons = consoleFromRef(vueElement.getRefRMI());
		
		if (cons != null) {
			cons.log(level, prefixe, msg);
		}
	}

	/**
	 * Affiche tous les elements present dans l'arene dans le logger.
	 */
	public void logElements() {
		String msg = getPrintElementsMessage();
		logger.info(Constantes.nomClasse(this), "Compte-rendu :"+msg);
	}

	/**
	 * Affiche le classement de la partie dans le logger.
	 */
	private void logClassement() {
		List<VuePersonnage> classement = getPersonnagesClassement();
		
		String msg = "";
		int i = 1;
		
		for (VuePersonnage vue : classement) {
			msg += "\n" + i + " : " + vue.getElement().getNomGroupe() + " " + vue.getPhrase();
			i++;
		}
		
		logger.info(Constantes.nomClasse(this), "Classement :" + msg);		
	}

	/**
	 * Retourne une chaine de caracteres avec les noms complets de tous les 
	 * clients connectes (et personnages ayant ete deconnectes).
	 * @return chaine representant les clients du serveur
	 */
	protected String getPrintElementsMessage() {
		String msg = "";
		
		for(VuePersonnage vuePers : personnages.values()) {
			msg += "\n" + Constantes.nomCompletClient(vuePers);
		}
		
		for(VuePersonnage vuePers : personnagesMorts) {
			msg += "\n" + Constantes.nomCompletClient(vuePers);
		}
		
		for(VuePotion vuePot : potions.values()) {
			msg += "\n" + Constantes.nomCompletClient(vuePot);
		}
		
		return msg;
	}

	/**
	 * Ajoute les logs dans le cas ou le personnage correspondant a la console 
	 * donnee a tenter d'executer plusieurs fois une action dans le meme tour.
	 * @param refRMI console
	 * @throws RemoteException
	 */
	private void logActionDejaExecutee(int refRMI) throws RemoteException {
		consoleFromRef(refRMI).log(Level.WARNING, "AVERTISSEMENT ARENE", 
				"Une action a deja ete executee ce tour-ci !");
		
		logger.warning(Constantes.nomClasse(this), nomRaccourciClient(refRMI) + 
				" a tente de jouer plusieurs actions dans le meme tour");
	}

	/**
	 * Retourne le nom raccourci du client correspondant a la reference RMI
	 * donnee.
	 * @param refRMI reference RMI
	 * @return nom raccourci du client
	 */
	public String nomRaccourciClient(int refRMI) throws RemoteException {
		return Constantes.nomRaccourciClient(vueFromRef(refRMI));
	}

	
	


	/**************************************************************************
	 * Specifique a l'arene tournoi.
	 **************************************************************************/

	@Override
	public boolean verifieMotDePasse(char[] motDePasse) throws RemoteException {
		return false;
	}
	
	@Override
	public void commencePartie(String motDePasse) throws RemoteException {}

	@Override
	public void ejectePersonnage(int refRMI, String motDePasse) throws RemoteException {}

	@Override
	public void lancePotion(Potion potion, Point position, String motDePasse) throws RemoteException {}
	
}
