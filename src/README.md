# Pistes d'amélioration :
- Utiliser des PreAuthorize pour valider l'existence d'un compte en banque, plutôt que de le vérifier dans le service
- Plutôt que de stocker les données d'un événement dans un jsonb, faire des tables séparées 
- Pour de l'optimisation, il serait possible de mettre en place une vue matérialisée afin d'avoir la dernière valeur du compte au lieu de rejouer 
  tous les événéments