#Idees
_ Définir stratégies en fonction des caractéristiques des potions qui pop : si manque de vie aller récupérer une potion de vie (si il en voit une aller la chercher OU en chercher une dans l'arène en cas de désespoir ?)
_ Faire une simulation d'un combat en 1v1 pour définir la meilleure stratégie : fuir, combattre ou chercher un autre ennemi (chercher un healer...) simulation sur prise de potion ?
_ Coder exécuter intéractions : permet d'exécuter n'importe quelle intéraction (codage dans arène)
_ Stratégie combo Healer Berseker (le healer cherche en priorité un berseker dans l'équipe)
_ Stratégie regroupement
_ Stratégie mentalist -> définir meilleur ennemi à convertir

_SUITE AUX DEFINITIONS DE REGLES :
Définir les pv, init et force des personnages.

BONUS :
_ Armure : prends comme dégâts force - armure. L'armure baisse.
_ IHM

POTIONS:
_ Immortalité (locale) : potion qui empêche de descendre la vie en dessous de 1 pv pendant un certain nombre de tours. Si ce personnage tue quelqu'un pendant ce délai il récupère X pv ou X% ed vie.
_ Poison (locale) : perte de pv sur X toujours.
X _ Vie : +100 PV -100 init.
X _ Rage : -30 PV +50 force.
_ Duplication (locale) : division d'un personnage et ses caractéristques en deux

PERSONNAGES:
X _ Separator : quand il se fait attaquer, il perd de la vie et se split en deux.
X _ Berseker : quand il perd de la vie il gagne de l'initiative, il part avec 0 initiative.
X _ Healer : se met au corps avec un allié pour le soigner.
_ Voleur (locale) : invisible tant qu'il n'a pas engagé le combat
_ AOE (locale) : c'est dans le nom hehe.
= _ Alchimiste : drop des potions (NE LES RAMASSE PAS) -> fuite sans boire popo
= _ Warrior : balancé en attaque et vie -> n'a que la strat de base
_ Necromancien : quand quelqu'un meurt il fait pop un squelette
X _ Lyche : drain de vie équivalent à sa force
X _ Mentalist (locale) : convertion d'un personnage ennemi en allié 
= _ Tank : -> strat marche pas, attaque pas trop
