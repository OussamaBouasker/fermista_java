package tn.fermista.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import tn.fermista.models.Reservation;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static File generateReservationPDF(Reservation reservation) throws IOException {
        // Create PDF file
        String fileName = "reservation_" + reservation.getId() + ".pdf";
        File file = new File(fileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add content
        addHeader(document);
        addReservationDetails(document, reservation);
        addFooter(document);

        document.close();
        return file;
    }

    private static void addHeader(Document document) throws IOException {
        // Create header with logo and title
        Paragraph header = new Paragraph("Fermista - Confirmation de Réservation")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);

        // Add a decorative line using a table with a single cell
        Table lineTable = new Table(1);
        lineTable.setWidth(UnitValue.createPercentValue(100));
        Cell lineCell = new Cell()
                .setHeight(1)
                .setBackgroundColor(ColorConstants.GRAY)
                .setBorder(null);
        lineTable.addCell(lineCell);

        document.add(header);
        document.add(lineTable);
    }

    private static void addReservationDetails(Document document, Reservation reservation) throws IOException {
        // Create a table for reservation details
        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(20)
                .setMarginBottom(20);

        // Add reservation details
        addTableRow(table, "Numéro de réservation", "#" + reservation.getId());
        addTableRow(table, "Atelier", reservation.getWorkshop().getTitre());
        addTableRow(table, "Date", reservation.getReservationDate().format(DATE_FORMATTER));
        addTableRow(table, "Statut", reservation.getStatus());
        addTableRow(table, "Prix", reservation.getPrix() + " DT");

        if (reservation.getUser() != null) {
            addTableRow(table, "Client",
                    reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName());
            addTableRow(table, "Email", reservation.getUser().getEmail());
        }

        document.add(table);

        // Add additional information
        if (reservation.getCommentaire() != null && !reservation.getCommentaire().isEmpty()) {
            Paragraph comment = new Paragraph("Commentaire:")
                    .setBold()
                    .setMarginTop(20);
            document.add(comment);
            document.add(new Paragraph(reservation.getCommentaire()));
        }
    }

    private static void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(10)
                .add(new Paragraph(label).setBold());

        Cell valueCell = new Cell()
                .setPadding(10)
                .add(new Paragraph(value));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static void addFooter(Document document) throws IOException {
        // Add footer with contact information
        Paragraph footer = new Paragraph()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(50)
                .setFontSize(10)
                .add("Fermista - Votre partenaire agricole\n")
                .add("Contact: support@fermista.com\n")
                .add("Tél: +216 XX XXX XXX");

        document.add(footer);
    }
} 