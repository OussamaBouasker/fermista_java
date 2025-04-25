package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.fermista.models.Consultation;
import tn.fermista.models.RapportMedical;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.time.LocalDateTime;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class SearchResultsPopup {
    @FXML
    private VBox contentVBox;
    @FXML
    private Button exportPdfButton;
    
    private List<Consultation> consultations;
    private Stage stage;

    public void initialize() {
        exportPdfButton.setOnAction(e -> exportToPdf());
    }

    public void setData(List<Consultation> consultations) {
        this.consultations = consultations;
        displayResults();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void displayResults() {
        contentVBox.getChildren().clear();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Consultation consultation : consultations) {
            TitledPane consultationPane = new TitledPane();
            consultationPane.setText("Consultation: " + consultation.getNom());
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            
            // Informations de la consultation
            int row = 0;
            grid.add(new Label("Date:"), 0, row);
            grid.add(new Label(consultation.getDate().toString()), 1, row++);
            
            grid.add(new Label("Heure:"), 0, row);
            grid.add(new Label(consultation.getHeure().toString()), 1, row++);
            
            grid.add(new Label("Lieu:"), 0, row);
            grid.add(new Label(consultation.getLieu()), 1, row++);
            
            // Informations du rapport médical
            if (consultation.getRapportMedical() != null) {
                RapportMedical rapport = consultation.getRapportMedical();
                
                grid.add(new Label("Rapport Médical"), 0, row++, 2, 1);
                grid.add(new Label("Numéro:"), 0, row);
                grid.add(new Label(String.valueOf(rapport.getNum())), 1, row++);
                
                grid.add(new Label("Race:"), 0, row);
                grid.add(new Label(rapport.getRace()), 1, row++);
                
                grid.add(new Label("Historique:"), 0, row);
                TextArea historiqueArea = new TextArea(rapport.getHistoriqueDeMaladie());
                historiqueArea.setWrapText(true);
                historiqueArea.setEditable(false);
                historiqueArea.setPrefRowCount(2);
                grid.add(historiqueArea, 1, row++);
                
                grid.add(new Label("Cas médical:"), 0, row);
                TextArea casArea = new TextArea(rapport.getCasMedical());
                casArea.setWrapText(true);
                casArea.setEditable(false);
                casArea.setPrefRowCount(2);
                grid.add(casArea, 1, row++);
                
                grid.add(new Label("Solution:"), 0, row);
                TextArea solutionArea = new TextArea(rapport.getSolution());
                solutionArea.setWrapText(true);
                solutionArea.setEditable(false);
                solutionArea.setPrefRowCount(2);
                grid.add(solutionArea, 1, row++);
            }
            
            consultationPane.setContent(grid);
            contentVBox.getChildren().add(consultationPane);
        }
    }

    @FXML
    private void exportToPdf() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File file = fileChooser.showSaveDialog(stage);
            
            if (file != null) {
                Document document = new Document(PageSize.A4, 36, 36, 90, 36); // Marges optimisées
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                
                // Ajouter un en-tête à chaque page
                HeaderFooter event = new HeaderFooter();
                writer.setPageEvent(event);
                
                document.open();
                
                // Add title
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(129, 196, 8));
                Paragraph title = new Paragraph("Rapport des Consultations", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(30);
                document.add(title);

                Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(44, 62, 80));
                Font subSectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(52, 73, 94));
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);
                Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

                // Pour chaque consultation
                for (Consultation consultation : consultations) {
                    // Titre de la consultation
                    Paragraph consultationTitle = new Paragraph("Consultation: " + consultation.getNom(), sectionFont);
                    consultationTitle.setSpacingBefore(20);
                    consultationTitle.setSpacingAfter(10);
                    document.add(consultationTitle);

                    // Détails de la consultation
                    PdfPTable consultationTable = new PdfPTable(2);
                    consultationTable.setWidthPercentage(100);
                    consultationTable.setWidths(new float[]{1, 3});
                    consultationTable.getDefaultCell().setPadding(5);
                    consultationTable.getDefaultCell().setBorderWidth(0.5f);

                    // Style pour les cellules d'en-tête
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(new BaseColor(240, 240, 240));
                    headerCell.setPadding(5);

                    // Ajouter les détails de la consultation
                    addTableRow(consultationTable, "Date:", consultation.getDate().toString(), headerCell, normalFont);
                    addTableRow(consultationTable, "Heure:", consultation.getHeure().toString(), headerCell, normalFont);
                    addTableRow(consultationTable, "Lieu:", consultation.getLieu(), headerCell, normalFont);

                    document.add(consultationTable);

                    // Si un rapport médical existe
                    if (consultation.getRapportMedical() != null) {
                        RapportMedical rapport = consultation.getRapportMedical();
                        
                        // Titre du rapport médical
                        Paragraph rapportTitle = new Paragraph("Rapport Médical", subSectionFont);
                        rapportTitle.setSpacingBefore(15);
                        rapportTitle.setSpacingAfter(10);
                        document.add(rapportTitle);

                        // Table pour les informations de base du rapport
                        PdfPTable rapportTable = new PdfPTable(2);
                        rapportTable.setWidthPercentage(100);
                        rapportTable.setWidths(new float[]{1, 3});
                        rapportTable.getDefaultCell().setPadding(5);
                        rapportTable.getDefaultCell().setBorderWidth(0.5f);

                        // Informations de base du rapport
                        addTableRow(rapportTable, "Numéro:", String.valueOf(rapport.getNum()), headerCell, normalFont);
                        addTableRow(rapportTable, "Race:", rapport.getRace(), headerCell, normalFont);

                        document.add(rapportTable);

                        // Sections détaillées du rapport
                        addDetailedSection(document, "Historique de Maladie", rapport.getHistoriqueDeMaladie(), labelFont, normalFont);
                        addDetailedSection(document, "Cas Médical", rapport.getCasMedical(), labelFont, normalFont);
                        addDetailedSection(document, "Solution", rapport.getSolution(), labelFont, normalFont);
                    }

                    // Ajouter une ligne de séparation entre les consultations
                    LineSeparator line = new LineSeparator();
                    line.setLineColor(new BaseColor(200, 200, 200));
                    line.setPercentage(100);
                    line.setOffset(20);
                    document.add(new Chunk(line));
                }

                // Ajouter les informations de bas de page
                Paragraph footer = new Paragraph("Document généré le " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
                footer.setAlignment(Element.ALIGN_RIGHT);
                footer.setSpacingBefore(20);
                document.add(footer);

                document.close();
                
                showAlert("Succès", "Le PDF a été généré avec succès", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la génération du PDF", Alert.AlertType.ERROR);
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, PdfPCell headerCell, Font normalFont) {
        PdfPCell labelCell = new PdfPCell(headerCell);
        labelCell.setPhrase(new Phrase(label, normalFont));
        table.addCell(labelCell);
        table.addCell(new Phrase(value, normalFont));
    }

    private void addDetailedSection(Document document, String title, String content, Font labelFont, Font normalFont) throws DocumentException {
        // Titre de la section
        Paragraph sectionTitle = new Paragraph(title + ":", labelFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(5);
        document.add(sectionTitle);

        // Contenu
        Paragraph sectionContent = new Paragraph(content, normalFont);
        sectionContent.setIndentationLeft(20);
        sectionContent.setSpacingAfter(10);
        document.add(sectionContent);
    }

    // Classe interne pour gérer l'en-tête et le pied de page
    private static class HeaderFooter extends PdfPageEventHelper {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 8);
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            // En-tête
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase("Page " + writer.getPageNumber(), headerFont),
                document.right(), document.top() + 30, 0);
                
            // Ligne de séparation en haut
            cb.setLineWidth(0.5f);
            cb.moveTo(document.left(), document.top() + 20);
            cb.lineTo(document.right(), document.top() + 20);
            cb.stroke();
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Appliquer le style CSS personnalisé
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/styles/StyleCalendar.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-alert");
        
        switch (type) {
            case ERROR:
                dialogPane.getStyleClass().add("error-alert");
                break;
            case INFORMATION:
                dialogPane.getStyleClass().add("info-alert");
                break;
            case WARNING:
                dialogPane.getStyleClass().add("warning-alert");
                break;
        }
        
        alert.showAndWait();
    }
}