package tn.fermista.services;

import tn.fermista.models.Produit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Panier {
    private static Panier instance;
    private final Map<Produit, Integer> items;

    private Panier() {
        items = new HashMap<>();
    }

    public static Panier getInstance() {
        if (instance == null) {
            instance = new Panier();
        }
        return instance;
    }

    public void addProduct(Produit produit) {
        items.put(produit, items.getOrDefault(produit, 0) + 1);
    }

    public void decreaseQuantity(Produit produit) {
        int quantity = items.getOrDefault(produit, 0);
        if (quantity > 1) {
            items.put(produit, quantity - 1);
        } else {
            items.remove(produit);
        }
    }

    public void removeProduct(Produit produit) {
        items.remove(produit);
    }

    public int getQuantity(Produit produit) {
        return items.getOrDefault(produit, 0);
    }

    public Set<Produit> getProducts() {
        return items.keySet();
    }

    public double getTotal() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrix() * entry.getValue())
                .sum();
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Map<Produit, Integer> getItems() {
        return new HashMap<>(items);
    }
} 