# Inventaire des Classes Java - Projet L'Héritage Maudit

## Vue d'ensemble

Après analyse du code fourni par la prof et de notre architecture UML, voici l'inventaire complet des fichiers nécessaires.

**Résumé:**
- 7 fichiers existants (JeuSommaire de la prof)
- 32 fichiers à créer
- Total: 39 fichiers Java

---

## 1. Fichiers existants (à partir du code JeuSommaire)

### Package `jeu` - 7 fichiers

| Fichier | Type | Action |
|---------|------|--------|
| Commande.java | Interface | Garder tel quel |
| Direction.java | Enum | Garder tel quel |
| Main.java | Classe | Garder tel quel |
| CommandeNonDirectionnelle.java | Enum | **Modifier** - ajouter nouvelles commandes |
| Zone.java | Classe | **Modifier** - ajouter gestion objets/éclairage |
| Jeu.java | Classe | **Modifier** - ajouter logique complète |
| GUI.java | Classe | **Modifier** - ajouter panels inventaire/stats |

### Modifications à apporter

**CommandeNonDirectionnelle.java** - Ajouter ces commandes:
- RETOUR, PRENDRE, DEPOSER, INVENTAIRE
- UTILISER, OUVRIR
- ALLUMER_LUMIERE, ALLUMER_FEU, BRULER
- MIROIR, SAUVEGARDER, ABANDON, TEST

**Zone.java** - Ajouter:
- Attribut `nom`
- Liste d'objets présents
- Liste de conteneurs
- Gestion éclairage (boolean eclairee, image sombre)
- Sortie cachée (Map pour la cave)

**Jeu.java** - Ajouter:
- Référence au Joueur et au Manoir
- Historique des zones (Stack pour RETOUR)
- Gestion fragments (liste des 3 à détruire)
- Toutes les méthodes executerXXX pour les commandes
- Vérification victoire/défaite

**GUI.java** - Ajouter:
- PanneauInventaire (affichage sac)
- PanneauStatistiques (vies + fragments)
- Dialogues pour énigmes et téléportation

---

## 2. Nouveaux fichiers à créer

### Package `jeu` - 1 fichier

- **EtatJeu.java** (Enum): EN_COURS, VICTOIRE, DEFAITE, PAUSE

### Package `jeu.environnement` - 9 fichiers

**Classes de base:**
- **Conteneur.java** (abstract): Classe mère pour tous les conteneurs
- **Meuble.java**: Meubles interactifs

**Conteneurs concrets:**
- **Coffre.java**: Verrouillé + piège si mauvaise clé
- **Cheminee.java**: Brûler fragments (vérifie bois + allumettes)
- **Bureau.java**: Verrouillé
- **Armoire.java**: Verrouillé
- **Tiroir.java**: Simple
- **CorpsBaron.java**: Déclenche énigme avant ouverture

**Support:**
- **Manoir.java**: Création et gestion des 10 zones
- **TypeMeuble.java** (Enum): INTERRUPTEUR, MIROIR_MAGIQUE, LIVRE_SECRET

### Package `jeu.joueur` - 3 fichiers

- **Joueur.java**: Gestion vies (3 PV), inventaire, zones visitées
- **Inventaire.java**: Capacité max 5 objets, vérifications
- **ProfilJoueur.java**: Profil avec stats (victoires/défaites)

### Package `jeu.objets` - 9 fichiers

**Classe de base:**
- **ObjetJeu.java** (abstract): Classe mère pour tous les objets

**Objets concrets:**
- **Cle.java**: Déverrouillage conteneurs
- **ObjetMaudit.java**: Fragments à brûler (avec TypeFragment)
- **Allumettes.java**: Nécessaire pour feu
- **MorceauBois.java**: Nécessaire pour feu
- **Echelle.java**: Sortir de la cave
- **Medaillon.java**: Téléportation via miroir
- **ObjetSimple.java**: Objets génériques (notes, livres)

**Support:**
- **TypeFragment.java** (Enum): MONTRE_GOUSSET, PIPE_BOIS, JOURNAL_INTIME, PLUME, MEDAILLON

### Package `jeu.enigmes` - 2 fichiers

- **Enigme.java**: Question, réponse, indice
- **BanqueEnigmes.java**: Collection d'énigmes, choix aléatoire

### Package `jeu.sauvegarde` - 3 fichiers

Note: Format JSON, pas sérialisation Java native

- **GestionnaireSauvegardeJSON.java**: Load/save au format `joueur_nom.json`
- **EtatPartie.java**: État complet à sauvegarder (vies, inventaire, position, etc.)
- **GestionnaireProfils.java**: Gestion profils joueurs (JSON aussi)

### Package `jeu.util` - 2 fichiers

- **Randomiseur.java**: Choisit 3 fragments parmi 5 au démarrage
- **TestAutomatique.java**: Commande TEST (séquence auto-victoire)

### Package `jeu.ui` - 3 fichiers

Composants Swing supplémentaires:

- **PanneauInventaire.java** (JPanel): Affichage inventaire
- **PanneauStatistiques.java** (JPanel): Affichage vies + fragments détruits
- **DialogueEnigme.java** (JDialog): Fenêtre énigme du baron

---

## 3. Organisation par package

```
src/
└── jeu/
    ├── [7 fichiers existants]
    ├── EtatJeu.java
    │
    ├── environnement/
    │   ├── Manoir.java
    │   ├── Conteneur.java (abstract)
    │   ├── Coffre.java
    │   ├── Cheminee.java
    │   ├── Bureau.java
    │   ├── Armoire.java
    │   ├── Tiroir.java
    │   ├── CorpsBaron.java
    │   ├── Meuble.java
    │   └── TypeMeuble.java
    │
    ├── joueur/
    │   ├── Joueur.java
    │   ├── Inventaire.java
    │   └── ProfilJoueur.java
    │
    ├── objets/
    │   ├── ObjetJeu.java (abstract)
    │   ├── Cle.java
    │   ├── ObjetMaudit.java
    │   ├── TypeFragment.java
    │   ├── Allumettes.java
    │   ├── MorceauBois.java
    │   ├── Echelle.java
    │   ├── Medaillon.java
    │   └── ObjetSimple.java
    │
    ├── enigmes/
    │   ├── Enigme.java
    │   └── BanqueEnigmes.java
    │
    ├── sauvegarde/
    │   ├── GestionnaireSauvegardeJSON.java
    │   ├── EtatPartie.java
    │   └── GestionnaireProfils.java
    │
    ├── util/
    │   ├── Randomiseur.java
    │   └── TestAutomatique.java
    │
    └── ui/
        ├── PanneauInventaire.java
        ├── PanneauStatistiques.java
        └── DialogueEnigme.java
```

---

## 4. Ordre d'implémentation suggéré

D'après les dépendances entre classes, voici un ordre logique:

**Étape 1 - Enums et interfaces de base (4 fichiers)**
1. EtatJeu
2. TypeFragment
3. TypeMeuble
4. Modifier CommandeNonDirectionnelle

**Étape 2 - Classes abstraites (2 fichiers)**
5. ObjetJeu
6. Conteneur

**Étape 3 - Objets simples (8 fichiers)**
7. Cle
8. ObjetMaudit
9. Allumettes, MorceauBois, Echelle, Medaillon, ObjetSimple
10. Inventaire

**Étape 4 - Joueur (2 fichiers)**
11. Joueur
12. ProfilJoueur

**Étape 5 - Conteneurs (7 fichiers)**
13. Coffre, Bureau, Armoire, Tiroir
14. Cheminee (dépend de Allumettes, MorceauBois, ObjetMaudit)
15. Meuble
16. Enigme, BanqueEnigmes
17. CorpsBaron (dépend de Enigme)

**Étape 6 - Environnement (2 fichiers)**
18. Modifier Zone (dépend de ObjetJeu, Conteneur, Meuble)
19. Manoir (dépend de Zone)

**Étape 7 - Logique jeu (1 fichier)**
20. Modifier Jeu (dépend de tout le reste)

**Étape 8 - Sauvegarde (3 fichiers)**
21. EtatPartie
22. GestionnaireSauvegardeJSON
23. GestionnaireProfils

**Étape 9 - UI et utils (6 fichiers)**
24. PanneauInventaire, PanneauStatistiques, DialogueEnigme
25. Modifier GUI
26. Randomiseur, TestAutomatique

---

## 5. Statistiques

| Type | Nombre |
|------|--------|
| Interfaces | 1 (Commande - existante) |
| Classes abstraites | 2 (ObjetJeu, Conteneur) |
| Classes concrètes | 28 |
| Enums | 5 |
| **Total classes/interfaces** | **36** |

Estimation lignes de code: environ 3000-3500 lignes au total.

---

Fin de l'inventaire.
