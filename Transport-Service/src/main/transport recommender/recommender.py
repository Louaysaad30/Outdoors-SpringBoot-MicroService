import pandas as pd
import numpy as np
from transformers import DistilBertTokenizer, TFDistilBertModel  # Pour le traitement du texte avec DistilBERT
import tensorflow as tf

class VehicleRecommender:
    def __init__(self, quantize=False):
        # Initialisation du tokenizer à partir du modèle préentraîné 'distilbert-base-uncased'
        self.text_processor = DistilBertTokenizer.from_pretrained('distilbert-base-uncased')

        # Chargement du modèle préentraîné DistilBERT pour l'encodage des textes
        self.model = TFDistilBertModel.from_pretrained('distilbert-base-uncased')

        # Définition des poids pour chaque composante du score : texte, prix, rating
        self.weights = np.array([0.6, 0.25, 0.15])  # 60% texte, 25% prix, 15% note

        # Option (non utilisée ici) pour activer la quantification du modèle (réduction de taille/performance)
        self.quantized = quantize

    def _embed_text(self, texts):
        """
        Fonction interne pour transformer des textes en vecteurs (embeddings) avec DistilBERT.
        Prend une liste de textes en entrée, retourne une matrice d'embeddings.
        """
        # Tokenisation
        inputs = self.text_processor(texts, return_tensors="tf", padding=True, truncation=True, max_length=128)

        # On récupère uniquement le vecteur CLS
        return self.model(**inputs).last_hidden_state[:, 0, :].numpy()

    def recommend_from_list(self, user_input, vehicle_list, top_n=5):
        """
        Fonction principale pour générer une liste de recommandations de véhicules.
        - user_input : la requête utilisateur (ex. "véhicule écologique")
        - vehicle_list : liste des véhicules (dictionnaires avec type, localisation, etc.)
        - top_n : nombre de résultats à retourner (par défaut 5)
        """

        # Conversion de la liste des véhicules en DataFrame
        df = pd.DataFrame(vehicle_list)

        # Remplacement des champs manquants par des chaînes vides
        df.fillna('', inplace=True)

        # Normalisation du prix : on inverse (moins cher = meilleur score)
        df['price_norm'] = 1 - (df['prixParJour'] / df['prixParJour'].max())

        # Normalisation du rating : on suppose que la note max est sur 5
        df['rating_norm'] = df['rating'] / 5

        # Création d'un texte combiné pour chaque véhicule (type + localisation + modèle + description)
        combined_texts = (
                df['type'] + " " +
                df['localisation'] + " " +
                df['modele'] + " " +
                df['description']
        ).tolist()

        # Génération des embeddings textuels pour chaque véhicule
        text_embeds = self._embed_text(combined_texts)

        # Embedding de la requête de l'utilisateur
        query_embed = self._embed_text([user_input.lower()])[0]

        # Calcul du score de similarité pour chaque véhicule :
        # Produit scalaire entre la requête et les véhicules (représentation texte)
        # + pondération du prix et de la note
        df['similarity'] = (
                self.weights[0] * np.array([np.dot(query_embed, embed) for embed in text_embeds]) +
                self.weights[1] * df['price_norm'] +
                self.weights[2] * df['rating_norm']
        )

        # Tri décroissant selon la similarité et retour des top_n meilleurs résultats
        return df.sort_values('similarity', ascending=False).head(top_n).to_dict(orient='records')
