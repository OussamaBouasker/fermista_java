package tn.fermista.tests;

import tn.fermista.models.*;
import tn.fermista.services.*;
import tn.fermista.utils.MyDbConnexion;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Main {
//    public static void main(String[] args) {

//
//        ServiceUser us = new ServiceUser();
//        ServiceReservation sr = new ServiceReservation();
//        ServiceWorkshop sw = new ServiceWorkshop();

//        // ✨ 1. INSERT
//        User u1 = new User("yosr.jemli@gmail.com", "pass123", "Yosr", "Jemli", "12345678", "image.jpg");
//        try {
//            us.insert(u1);
//            System.out.println("✅ Utilisateur inséré !");
//        } catch (SQLException e) {
//            System.err.println("❌ Erreur d'insertion : " + e.getMessage());
//        }
//
//        // 📋 2. SHOW ALL USERS
//        try {
//            List<User> users = us.showAll();
//            System.out.println("📋 Liste des utilisateurs :");
//            users.forEach(System.out::println);
//        } catch (SQLException e) {
//            System.err.println("❌ Erreur d'affichage : " + e.getMessage());
//        }
//
//        // ✏️ 3. UPDATE (supposons que l'ID du dernier utilisateur est 1)
//        User uUpdated = new User();
//        uUpdated.setId(4); // Remplace par un ID existant dans ta DB
//        uUpdated.setEmail("updated.email@gmail.com");
//        uUpdated.setPassword("newpass456");
//        uUpdated.setFirstName("Yosra");
//        uUpdated.setLastName("JemliUpdated");
//        uUpdated.setNumber("87654321");
//        uUpdated.setState(true);
//        uUpdated.setVerified(true);
//        uUpdated.setImage("updated.jpg");
//
//        try {
//            us.update(uUpdated);
//            System.out.println("🔄 Utilisateur mis à jour !");
//        } catch (SQLException e) {
//            System.err.println("❌ Erreur de mise à jour : " + e.getMessage());
//        }


////         🗑️ 4. DELETE (supposons ID = 1 aussi)
//        User uDelete = new User();
//        uDelete.setId(6); // Remplace aussi par un ID existant à supprimer
////
//        try {
////           us.delete(uDelete);
//            us.deleteuser(uDelete);
//            sr.deleteUserReservations(uDelete); // Delete all reservations of the user
//            us.delete(uDelete);
//
//            System.out.println("🗑️ Utilisateur supprimé !");
//        } catch (SQLException e) {
//            System.err.println("❌ Erreur de suppression : " + e.getMessage());
//        }
//
//
//
//        // ✅ 5. Show again to confirm deletion
//        try {
//            List<User> usersAfter = us.showAll();
//            System.out.println("📋 Liste après suppression :");
//            usersAfter.forEach(System.out::println);
//        } catch (SQLException e) {
//            System.err.println("❌ Erreur finale d'affichage : " + e.getMessage());
//        }
//    }

//        ServiceUser serviceUser = new ServiceUser();
//        ServiceReclamation serviceReclamation = new ServiceReclamation();
//
//        try {
//            // 1. Créer un user si nécessaire
//            User user = new User("yosraaaa@gmail.com", "123456", "Yosr", "Jemli", "12345678", "img.png");
//            serviceUser.insert(user);
//
//            // Récupérer le user depuis la base pour avoir son ID (ou tu peux le retrouver via showAll)
//            List<User> users = serviceUser.showAll();
//            User existingUser = users.get(users.size() - 1); // le dernier inséré
//
//            // 2. Créer une réclamation
//            Reclamation r1 = new Reclamation();
//            r1.setTitre("Problème d’accès");
//            r1.setDescription("Je ne peux pas accéder à mon compte.");
//            r1.setStatus(Reclamation.STATUS_PENDING);
//            r1.setDateSoumission(LocalDateTime.now());
//            r1.setUser(existingUser);
//            serviceReclamation.insert(r1);
//
//            // 3. Afficher toutes les réclamations
//            System.out.println("📋 Liste des réclamations :");
//            List<Reclamation> reclamations = serviceReclamation.showAll();
//            reclamations.forEach(System.out::println);
//
//            // 4. Update de la 1ère réclamation
//            Reclamation recToUpdate = reclamations.get(0);
//            recToUpdate.setStatus(Reclamation.STATUS_CONFIRMED);
//            recToUpdate.setDescription("Accès rétabli avec succès.");
//            serviceReclamation.update(recToUpdate);
//
//            // 5. Supprimer la réclamation
//            serviceReclamation.delete(recToUpdate);
//
//        } catch (SQLException e) {
//            System.err.println("Erreur SQL : " + e.getMessage());
//        }
//    }


//        ServiceWorkshop service = new ServiceWorkshop();
//
//        try {
//            // 1. Création d'un nouvel utilisateur (supposons que l'utilisateur avec ID 1 existe déjà)
//            User user = new User(7);
//
//            // 2. Création d'un Workshop
//            Workshop newWorkshop = new Workshop();
//            newWorkshop.setTitre("Test Atelier");
//            newWorkshop.setDescription("Ceci est un test.");
//            newWorkshop.setDate(LocalDateTime.now().plusDays(1));
//            newWorkshop.setPrix("99.99");
//            newWorkshop.setTheme("Informatique");
//            newWorkshop.setDuration(LocalTime.of(2, 30));
//            newWorkshop.setNbrPlacesMax(20);
//            newWorkshop.setNbrPlacesRestantes(20);
//            newWorkshop.setType(Workshop.TYPE_LIVE_WORKSHOP);
//            newWorkshop.setImage("image.jpg");
//            newWorkshop.setMeetlink("https://meet.jit.si/testatelier");
//            newWorkshop.setUser(user);
//            newWorkshop.setKeywords("Java,Live");
//
//            // 3. Insertion
//            service.insert(newWorkshop);
//
//            // 4. Affichage de tous les workshops
//            List<Workshop> workshops = service.showAll();
//            System.out.println("=== Liste des workshops ===");
//            for (Workshop w : workshops) {
//                System.out.println(w);
//            }
//
//            // 5. Mise à jour du premier workshop récupéré
//            if (!workshops.isEmpty()) {
//                Workshop first = workshops.get(13);
//                first.setTitre("Atelier Mis à Jour");
//                first.setPrix("999.99");
//                service.update(first);
//                System.out.println("Workshop mis à jour avec succès !");
//            }
//
////             6. Suppression du dernier workshop
//            if (!workshops.isEmpty()) {
//                Workshop last = workshops.get(workshops.size() - 1);
//                service.delete(last);
//                System.out.println("Workshop supprimé avec succès !");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


//        try {
//            ServiceReservation service = new ServiceReservation();
//
//            // Création d’un utilisateur fictif (doit exister dans la BDD avec l'id 1)
//            User user = new User();
//            user.setId(1);
//
//            // Création d’un atelier fictif (doit exister dans la BDD avec l'id 1)
//            Workshop workshop = new Workshop();
//            workshop.setId(1);
//
//            // Création d’une réservation
//            Reservation newReservation = new Reservation();
//            newReservation.setReservationDate(LocalDateTime.now().plusDays(1));
//            newReservation.setStatus(Reservation.STATUS_PENDING);
//            newReservation.setPrix("59.99");
//            newReservation.setCommentaire("Test unitaire de réservation");
//            newReservation.setConfirmation(false);
//            newReservation.setWorkshop(workshop);
//            newReservation.setEmail("test@example.com");
//            newReservation.setNumTel(12345678);
//            newReservation.setNumCarteBancaire("1234-5678-9012-3456");
//            newReservation.setUser(user);
//
//            // Test insertion
//            service.insert(newReservation);
//            System.out.println("Insertion réussie.");
//
//            // Récupération de toutes les réservations pour retrouver l'ID de celle qu'on vient d'insérer
//            List<Reservation> reservations = service.showAll();
//            Reservation lastInserted = reservations.get(reservations.size() - 1); // dernière insérée
//
//            // Test update
//            lastInserted.setCommentaire("Commentaire mis à jour !");
//            lastInserted.setConfirmation(true);
//            service.update(lastInserted);
//            System.out.println("Mise à jour réussie.");
//
//            // Test affichage
//            System.out.println("Liste des réservations :");
//            for (Reservation r : service.showAll()) {
//                System.out.println(r);
//            }
//
//            // Test suppression
//            service.delete(lastInserted);
//            System.out.println("Suppression réussie.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//        ServiceCollier service = new ServiceCollier();
//
//        // Créer une vache fictive (elle doit exister dans la base de données !)
//        Vache vache = new Vache();
//        vache.setId(11);  // Met l'ID d'une vache existante
//
//        // 1. Test Insert
//        Collier collier = new Collier("50", "M", 38.5, 12.0, vache);
//        service.insert(collier);
//
//        // 2. Test showAll
//        List<Collier> colliers = service.showAll();
//        System.out.println("Liste des colliers :");
//        for (Collier c : colliers) {
//            System.out.println(c);
//        }
//
//        // 3. Test update (on modifie le premier collier récupéré)
//        if (!colliers.isEmpty()) {
//            Collier collierToUpdate = colliers.get(0);
//            collierToUpdate.setTaille("L");
//            collierToUpdate.setValeurTemperature(39.0);
//            service.update(collierToUpdate);
//            System.out.println("Collier mis à jour : " + collierToUpdate);
//        }
//
//        // 4. Test delete (on supprime le même collier)
//        if (!colliers.isEmpty()) {
//            Collier collierToDelete = colliers.get(0);
//            service.delete(collierToDelete);
//            System.out.println("Collier supprimé : " + collierToDelete.getId());
//        }
//
//        // 5. Re-vérification de la liste
//        System.out.println("Liste finale des colliers :");
//        for (Collier c : service.showAll()) {
//            System.out.println(c);
//        }
//    }


//

//    Consultation:

//    public static void main(String[] args) {
//        System.out.println("Hello and welcome!");
//        MyDbConnexion c1 = MyDbConnexion.getInstance();
//        // Création des services
//        ServiceConsultation serviceConsultation = new ServiceConsultation();
//        ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
//        ServiceVache serviceVache = new ServiceVache();
//
//        try {
//            // Création de l'objet RapportMedical
//
//            RapportMedical rapportMedical = new RapportMedical(2);
//
//            // Création de l'objet Vache
//            Vache vache = new Vache(1);
//            // Création de la consultation
//            Consultation consultation = new Consultation();
//            consultation.setRapportMedical(rapportMedical);
//            consultation.setVache(vache);
//            consultation.setNom("Consultation 1");
//            consultation.setDate(Date.valueOf("2025-05-01"));
//            consultation.setHeure(Time.valueOf("10:30:00"));
//            consultation.setLieu("Salle 1");
//            serviceConsultation.insert(consultation);
//            System.out.println("Insertion réussie.");
//
//
//            // Récupération de toutes les réservations pour retrouver l'ID de celle qu'on vient d'insérer
//            List<Consultation> consultations = serviceConsultation.showAll();
//            Consultation lastInserted = consultations.get(consultations.size() - 1);
//
//            // Test update
//            lastInserted.setNom("Nom mis à jour !");
//            serviceConsultation.update(lastInserted);
//            System.out.println("Mise à jour réussie.");
//
//            // Test affichage
//            System.out.println("Liste des réservations :");
//            for (Consultation c : serviceConsultation.showAll()) {
//                System.out.println(c);
//            }
//            // Test suppression
//            serviceConsultation.delete(lastInserted);
//            System.out.println("Suppression réussie.");
//
//
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//     Rapport medical:

//    public static void main(String[] args) {
//        System.out.println("Hello and welcome!");
//        MyDbConnexion c1 = MyDbConnexion.getInstance();
//        // Création des services
//        ServiceConsultation serviceConsultation = new ServiceConsultation();
//        ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
//        ServiceVache serviceVache = new ServiceVache();
//
//        try {
//
//            // Création de la consultation
//            RapportMedical rp = new RapportMedical();
//            rp.setNum(2);
//            rp.setRace("race 1");
//            rp.setHistoriqueDeMaladie("historique 1");
//            rp.setCasMedical("cas 1");
//            rp.setSolution("solution 1");
//
//            serviceRapportMedical.insert(rp);
//            System.out.println("Insertion réussie.");
//
//
//            // Récupération de toutes les réservations pour retrouver l'ID de celle qu'on vient d'insérer
//            List<RapportMedical> rps = serviceRapportMedical.showAll();
//            RapportMedical lastInserted = rps.get(rps.size() - 1);
//
//            // Test update
//            lastInserted.setRace("Race mis à jour !");
//            serviceRapportMedical.update(lastInserted);
//            System.out.println("Mise à jour réussie.");
//
//            // Test affichage
//            System.out.println("Liste des réservations :");
//            for (RapportMedical r: serviceRapportMedical.showAll()) {
//                System.out.println(r);
//            }
//            // Test suppression
//            serviceRapportMedical.delete(lastInserted);
//            System.out.println("Suppression réussie.");
//
//
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
// Rendez vous:

//    public static void main(String[] args) {
//        System.out.println("Hello and welcome!");
//        MyDbConnexion c1 = MyDbConnexion.getInstance();
//        // Création des services
//        ServiceConsultation serviceConsultation = new ServiceConsultation();
//        ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
//        ServiceRendezVous serviceRendezVous = new ServiceRendezVous();
//
//
//        try {
//            // Création de l'objet AGRI
//
//           Agriculteur agriculteur = new Agriculteur(1);
//
//            // Création de l'objet VET
//           Veterinaire veterinaire = new Veterinaire(3);
//            // Création de la consultation
//            RendezVous rV = new RendezVous();
//            rV.setVeterinaire(veterinaire);
//            rV.setAgriculteur(agriculteur);
//            rV.setDate(Date.valueOf("2025-05-01"));
//            rV.setHeure(Time.valueOf("10:30:00"));
//            rV.setSex("sex 1");
//            rV.setCause("cause 1");
//            rV.setStatus("status 1");
//
//            serviceRendezVous.insert(rV);
//            System.out.println("Insertion réussie.");
//
//
//            // Récupération de toutes les réservations pour retrouver l'ID de celle qu'on vient d'insérer
//            List<RendezVous> rVs = serviceRendezVous.showAll();
//            RendezVous lastInserted = rVs.get(rVs.size() - 1);
//
//            // Test update
//            lastInserted.setSex("Sex mis à jour !");
//            serviceRendezVous.update(lastInserted);
//            System.out.println("Mise à jour réussie.");
//
//            // Test affichage
//            System.out.println("Liste des réservations :");
//            for (RendezVous r: serviceRendezVous.showAll()) {
//                System.out.println(r);
//            }
//            // Test suppression
//            serviceRendezVous.delete(lastInserted);
//            System.out.println("Suppression réussie.");
//
//
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}






