package tn.fermista.controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.fermista.utils.WebViewUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class MapViewController implements Initializable {

    @FXML private WebView webView;
    @FXML private TextField searchField;
    @FXML private Label locationDetailsLabel;
    
    private WebEngine webEngine;
    private LocationCallback callback;
    private String selectedAddress = "";
    private double selectedLat;
    private double selectedLng;
    private String selectedCity = "";
    private String selectedState = "";
    private String selectedCountry = "";
    private boolean locationSelected = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Vérifier que WebView est disponible
            if (!WebViewUtils.isWebViewAvailable()) {
                showErrorAlert("JavaFX WebView n'est pas disponible. Vérifiez vos dépendances.");
                return;
            }
            
            webEngine = webView.getEngine();
            
            // Assurer que WebView a les dimensions correctes
            webView.setPrefSize(700, 500);
            webView.setMinSize(600, 400);
            
            // Charger la page HTML avec la carte Leaflet
            String mapPageUrl = getClass().getResource("/map.html").toExternalForm();
            webEngine.load(mapPageUrl);
            
            // Définir le callback JavaScript
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == State.SUCCEEDED) {
                    try {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("locationCallback", new JavaScriptCallback());
                        
                        // Définir un marqueur initial pour la Tunisie
                        WebViewUtils.executeScript(webEngine, "centerMap(36.8065, 10.1815, 8);");
                        
                        // Forcer un redimensionnement après le chargement
                        Platform.runLater(() -> {
                            try {
                                WebViewUtils.executeScript(webEngine, "map.invalidateSize(true);");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showErrorAlert("Erreur lors de l'initialisation du callback JavaScript: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors de l'initialisation de la carte: " + e.getMessage());
        }
    }
    
    public void setCallback(LocationCallback callback) {
        this.callback = callback;
    }
    
    @FXML
    private void handleSearch() {
        try {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                // Appel à l'API Nominatim pour rechercher une adresse
                String script = String.format(
                    "fetch('https://nominatim.openstreetmap.org/search?format=json&q=%s')" +
                    ".then(response => response.json())" +
                    ".then(data => {" +
                    "  if (data && data.length > 0) {" +
                    "    var lat = parseFloat(data[0].lat);" +
                    "    var lng = parseFloat(data[0].lon);" +
                    "    centerMap(lat, lng, 16);" +
                    "    updateMarker(lat, lng);" +
                    "  } else {" +
                    "    alert('Aucun résultat trouvé pour: %s');" +
                    "  }" +
                    "}).catch(error => {" +
                    "  console.error('Erreur lors de la recherche:', error);" +
                    "  alert('Erreur lors de la recherche: ' + error);" +
                    "});",
                    searchText.replace("'", "\\'"),
                    searchText.replace("'", "\\'")
                );
                WebViewUtils.executeScript(webEngine, script);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors de la recherche d'adresse: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleConfirm() {
        if (locationSelected && selectedAddress != null && !selectedAddress.isEmpty() && callback != null) {
            callback.onLocationSelected(selectedLat, selectedLng, selectedAddress, selectedCity, selectedState, selectedCountry);
            closeWindow();
        } else {
            // Vérifier si on peut extraire les informations directement depuis le JavaScript
            try {
                // Essayer d'obtenir les coordonnées du marqueur actuel avec différentes approches
                boolean coordsExtracted = false;
                
                // Approche 1: Obtenir les coordonnées via un array
                try {
                    Object result = WebViewUtils.executeScript(webEngine, 
                        "if (marker) {" +
                        "  var pos = marker.getLatLng();" +
                        "  [pos.lat, pos.lng];" +
                        "} else { null; }");
                    
                    if (result != null && result instanceof JSObject) {
                        JSObject latLng = (JSObject) result;
                        try {
                            Object lat = latLng.getMember("0");
                            Object lng = latLng.getMember("1");
                            
                            if (lat != null && lng != null) {
                                selectedLat = Double.parseDouble(lat.toString());
                                selectedLng = Double.parseDouble(lng.toString());
                                coordsExtracted = true;
                            }
                        } catch (Exception e) {
                            // Continuer avec l'approche suivante si celle-ci échoue
                            System.out.println("Approche 1 a échoué: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Approche 1 a échoué: " + e.getMessage());
                }
                
                // Approche 2: Obtenir les coordonnées individuellement
                if (!coordsExtracted) {
                    try {
                        Object latResult = WebViewUtils.executeScript(webEngine, 
                            "marker ? marker.getLatLng().lat : null;");
                        Object lngResult = WebViewUtils.executeScript(webEngine, 
                            "marker ? marker.getLatLng().lng : null;");
                        
                        if (latResult != null && lngResult != null) {
                            selectedLat = Double.parseDouble(latResult.toString());
                            selectedLng = Double.parseDouble(lngResult.toString());
                            coordsExtracted = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Approche 2 a échoué: " + e.getMessage());
                    }
                }
                
                // Approche 3: Obtenir la latitude et longitude séparément avec une conversion en JSON
                if (!coordsExtracted) {
                    try {
                        String jsonCoords = (String) WebViewUtils.executeScript(webEngine,
                            "if (marker) {" +
                            "  var pos = marker.getLatLng();" +
                            "  JSON.stringify({lat: pos.lat, lng: pos.lng});" +
                            "} else { null; }");
                        
                        if (jsonCoords != null && !jsonCoords.equals("null")) {
                            // Analyse manuelle de la chaîne JSON
                            jsonCoords = jsonCoords.replace("{", "").replace("}", "").replace("\"", "");
                            String[] parts = jsonCoords.split(",");
                            
                            if (parts.length == 2) {
                                String latStr = parts[0].split(":")[1].trim();
                                String lngStr = parts[1].split(":")[1].trim();
                                
                                selectedLat = Double.parseDouble(latStr);
                                selectedLng = Double.parseDouble(lngStr);
                                coordsExtracted = true;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Approche 3 a échoué: " + e.getMessage());
                    }
                }
                
                // Si on a réussi à extraire les coordonnées
                if (coordsExtracted) {
                    // Utiliser les coordonnées pour créer une adresse basique
                    selectedAddress = String.format("Latitude: %.6f, Longitude: %.6f", selectedLat, selectedLng);
                    selectedCity = "Ville inconnue";
                    selectedState = "Région inconnue";
                    selectedCountry = "Tunisie";
                    
                    if (callback != null) {
                        callback.onLocationSelected(selectedLat, selectedLng, selectedAddress, selectedCity, selectedState, selectedCountry);
                        closeWindow();
                        return;
                    }
                } else {
                    // Si on arrive ici, c'est qu'on n'a pas pu extraire les données
                    showErrorAlert("Veuillez sélectionner une localisation sur la carte avant de confirmer.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Erreur lors de la confirmation: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void closeWindow() {
        try {
            Stage stage = (Stage) webView.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    // Interface pour le callback
    public interface LocationCallback {
        void onLocationSelected(double lat, double lng, String address, String city, String state, String country);
    }
    
    // Classe pour le callback JavaScript
    public class JavaScriptCallback {
        public void onLocationSelected(double lat, double lng, String address, String city, String state, String country) {
            try {
                System.out.println("JavaScript callback received: " + address);
                selectedLat = lat;
                selectedLng = lng;
                selectedAddress = address;
                selectedCity = city != null ? city : "";
                selectedState = state != null ? state : "";
                selectedCountry = country != null ? country : "Tunisie";
                locationSelected = true;
                
                // Mettre à jour le label avec l'adresse sélectionnée
                Platform.runLater(() -> {
                    locationDetailsLabel.setText(address);
                });
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Erreur lors de la sélection de la localisation: " + e.getMessage());
            }
        }
    }
} 