# Installation
- git clone https://github.com/CarolineBonislawski/simplifia.git
- mvn test

# Description
![Architecture hexagonale du projet](https://github.com/user-attachments/assets/08ce80cc-81a5-487c-9b0e-18df3bc27766)

# Pistes d'amélioration :
- Utiliser des PreAuthorize pour valider l'existence d'un compte en banque, plutôt que de le vérifier dans le service
- Plutôt que de stocker les données d'un événement dans un jsonb, faire des tables séparées 
- Pour de l'optimisation, il serait possible de mettre en place une vue matérialisée afin d'avoir la dernière valeur du compte au lieu de rejouer 
  tous les événéments