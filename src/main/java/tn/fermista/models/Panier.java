package tn.fermista.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Panier {
    private static Panier instance;
    private Map<Produit, Integer> produits;
    private double total;

    private Panier() {
        this.produits = new HashMap<>();
        this.total = 0.0;
    }

    public static Panier getInstance() {
        if (instance == null) {
            instance = new Panier();
        }
        return instance;
    }

    public void ajouterProduit(Produit produit) {
        produits.merge(produit, 1, Integer::sum);
        calculerTotal();
    }

    public void retirerProduit(Produit produit) {
        if (produits.containsKey(produit)) {
            int quantite = produits.get(produit);
            if (quantite > 1) {
                produits.put(produit, quantite - 1);
            } else {
                produits.remove(produit);
            }
            calculerTotal();
        }
    }

    public void supprimerProduit(Produit produit) {
        produits.remove(produit);
        calculerTotal();
    }

    private void calculerTotal() {
        total = produits.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrix() * entry.getValue())
                .sum();
    }

    public void viderPanier() {
        produits.clear();
        total = 0.0;
    }

    public Map<Produit, Integer> getProduits() {
        return new HashMap<>(produits);
    }

    public double getTotal() {
        return total;
    }

    public int getQuantite(Produit produit) {
        return produits.getOrDefault(produit, 0);
    }

    public void decrementerQuantite(Produit produit) {
        if (produits.containsKey(produit)) {
            int quantite = produits.get(produit);
            if (quantite > 1) {
                produits.put(produit, quantite - 1);
            } else {
                produits.remove(produit);
            }
            calculerTotal();
        }
    }
    
    /**
     * Retourne les éléments du panier sous forme de liste d'objets PanierItem
     * @return Une liste de PanierItem
     */
    public List<PanierItem> getItems() {
        List<PanierItem> items = new ArrayList<>();
        for (Map.Entry<Produit, Integer> entry : produits.entrySet()) {
            items.add(new PanierItem(entry.getKey(), entry.getValue()));
        }
        return items;
    }
}