        package tn.fermista.models;

        import java.util.Objects;
        // Constructors

        public class Collier {
            private int id;
            private String reference;
            private String taille;
            private double valeurTemperature;
            private double valeurAgitation;
            private Vache vache;

            public Collier() {
            }


            public Collier(String reference, String taille, double valeurTemperature, double valeurAgitation, Vache vache) {
                this.reference = reference;
                this.taille = taille;
                this.valeurTemperature = valeurTemperature;
                this.valeurAgitation = valeurAgitation;
                this.vache = vache;
            }

            // Getters and Setters

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getReference() {
                return reference;
            }

            public void setReference(String reference) {
                this.reference = reference;
            }

            public String getTaille() {
                return taille;
            }

            public void setTaille(String taille) {
                this.taille = taille;
            }

            public double getValeurTemperature() {
                return valeurTemperature;
            }

            public void setValeurTemperature(double valeurTemperature) {
                this.valeurTemperature = valeurTemperature;
            }

            public double getValeurAgitation() {
                return valeurAgitation;
            }

            public void setValeurAgitation(double valeurAgitation) {
                this.valeurAgitation = valeurAgitation;
            }

            public Vache getVache() {
                return vache;
            }

            public void setVache(Vache vache) {
                this.vache = vache;
            }

            // Equals & hashCode
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Collier)) return false;
                Collier collier = (Collier) o;
                return id == collier.id &&
                        Double.compare(collier.valeurTemperature, valeurTemperature) == 0 &&
                        Double.compare(collier.valeurAgitation, valeurAgitation) == 0 &&
                        Objects.equals(reference, collier.reference) &&
                        Objects.equals(taille, collier.taille) &&
                        Objects.equals(vache, collier.vache);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, reference, taille, valeurTemperature, valeurAgitation, vache);
            }

            @Override
            public String toString() {
                return "Collier{" +
                        "id=" + id +
                        ", reference='" + reference + '\'' +
                        ", taille='" + taille + '\'' +
                        ", valeurTemperature=" + valeurTemperature +
                        ", valeurAgitation=" + valeurAgitation +
                        ", vache=" + vache +
                        '}';
            }
        }
