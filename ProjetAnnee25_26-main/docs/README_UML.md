# Diagrammes UML - L'Héritage Maudit

## 📊 Diagramme de Classes UML

### Fichiers disponibles

- **diagramme_classes_uml.puml** : Diagramme complet (simplifié pour GitHub)

### 🎨 Comment générer les images

#### Option 1 : En ligne (recommandé)
1. Allez sur http://www.plantuml.com/plantuml/uml/
2. Copiez le contenu du fichier `.puml`
3. Collez dans l'éditeur
4. Téléchargez en PNG ou SVG

#### Option 2 : VS Code
1. Installez l'extension "PlantUML"
2. Ouvrez le fichier `.puml`
3. Appuyez sur `Alt+D` pour prévisualiser
4. Clic droit → Export → PNG

### 📁 Organisation

```
docs/
├── diagramme_classes_uml.puml     (fichier source PlantUML)
├── images/
│   └── (placez ici les PNG/SVG générés)
└── README_UML.md                   (ce fichier)
```

### 🔗 Liens directs pour la prof

Une fois les images uploadées sur GitHub, les liens seront :
```
https://github.com/math208208/ProjetAnnee25_26/blob/documentation/docs/images/diagramme_classes.png
```

---

## Légende

| Couleur | Signification |
|---------|---------------|
| 🟢 Vert | Code existant (de la prof) |
| 🟡 Jaune | À étendre (ajouter méthodes/attributs) |
| 🔵 Bleu | Nouveau (à créer entièrement) |

### Classes existantes (7)
- Commande (interface)
- Direction (enum)
- CommandeNonDirectionnelle (enum) - à étendre
- Zone - à étendre
- Jeu - à étendre
- GUI - à étendre
- Main

### Nouveaux packages (6)
- `jeu.joueur` : Joueur, Inventaire
- `jeu.environnement` : Manoir, Conteneur, Coffre, Cheminee, Bureau, CorpsBaron
- `jeu.objets` : ObjetJeu, Cle, ObjetMaudit, Allumettes, etc.
- `jeu.enigmes` : Enigme, BanqueEnigmes
- `jeu.sauvegarde` : GestionnaireSauvegardeJSON, EtatPartie
- `jeu.util` : Randomiseur, TestAutomatique

**Total : 39 classes Java**
