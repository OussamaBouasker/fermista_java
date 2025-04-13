    package tn.fermista.models;

    import java.sql.Date;
    import java.sql.Time;
    import java.util.Objects;

    public class RendezVous {

        private Integer id;
        private Date date;  // Utiliser java.sql.Date, mais si vous pouvez, remplacez-le par java.time.LocalDate
        private Time heure; // Utiliser java.sql.Time, mais préférer LocalTime
        private String sex;
        private String cause;

        // Relations
        private Veterinaire veterinaire;
        private Agriculteur agriculteur;

        private String status = "en attente";

        // Constructeurs
        public RendezVous() {}

        public RendezVous(Integer id, Date date, Time heure, String sex, String cause, Veterinaire veterinaire, Agriculteur agriculteur, String status) {
            this.id = id;
            this.date = date;
            this.heure = heure;
            this.sex = sex;
            this.cause = cause;
            this.veterinaire = veterinaire;
            this.agriculteur = agriculteur;
            this.status = status;
        }

        // Getters et Setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Time getHeure() {
            return heure;
        }

        public void setHeure(Time heure) {
            this.heure = heure;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }

        public Veterinaire getVeterinaire() {
            return veterinaire;
        }

        public void setVeterinaire(Veterinaire veterinaire) {
            this.veterinaire = veterinaire;
        }

        public Agriculteur getAgriculteur() {
            return agriculteur;
        }

        public void setAgriculteur(Agriculteur agriculteur) {
            this.agriculteur = agriculteur;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        // Validation personnalisée à appeler manuellement
        public void validate() throws IllegalArgumentException {
            if (date != null) {
                // Comparaison si la date est dans le passé
                if (date.toLocalDate().isBefore(java.time.LocalDate.now())) {
                    throw new IllegalArgumentException("La date du rendez-vous ne peut pas être dans le passé.");
                }

                // Vérification si la date est un week-end
                int dayOfWeek = date.toLocalDate().getDayOfWeek().getValue(); // 6 = samedi, 7 = dimanche
                if (dayOfWeek >= 6) {
                    throw new IllegalArgumentException("Les rendez-vous ne peuvent pas être pris le week-end (samedi ou dimanche).");
                }
            }
        }

        @Override
        public String toString() {
            return "RendezVous{" +
                    "id=" + id +
                    ", date=" + date +
                    ", heure=" + heure +
                    ", sex='" + sex + '\'' +
                    ", cause='" + cause + '\'' +
                    ", veterinaire=" + (veterinaire != null ? veterinaire.getId() : null) +
                    ", agriculteur=" + (agriculteur != null ? agriculteur.getId() : null) +
                    ", status='" + status + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RendezVous that = (RendezVous) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(date, that.date) &&
                    Objects.equals(heure, that.heure) &&
                    Objects.equals(sex, that.sex) &&
                    Objects.equals(cause, that.cause) &&
                    Objects.equals(veterinaire, that.veterinaire) &&
                    Objects.equals(agriculteur, that.agriculteur) &&
                    Objects.equals(status, that.status);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, date, heure, sex, cause, veterinaire, agriculteur, status);
        }
    }