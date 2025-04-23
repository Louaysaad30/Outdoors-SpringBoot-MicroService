package tn.esprit.spring.marketplaceservice.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.entity.Commande;
import tn.esprit.spring.marketplaceservice.entity.LigneCommande;
import tn.esprit.spring.marketplaceservice.entity.Status;
import tn.esprit.spring.marketplaceservice.services.IMPL.LigneCommandeServiceIMPL;
import tn.esprit.spring.marketplaceservice.services.interfaces.ICommandeService;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import com.itextpdf.text.pdf.Barcode128;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.pdf.BarcodeQRCode;

@Tag(name = "Gestion Commande")
@RestController
@AllArgsConstructor
@RequestMapping("/Commande")
public class CommandeController {

    private final ICommandeService iCommandeService;
    private final LigneCommandeServiceIMPL ligneCommandeService;

    @GetMapping("/getAllCommandes")
    public List<Commande> retrieveCommandes() {
        return iCommandeService.retrieveCommandes();
    }

    @PostMapping("/addCommande")
    public Commande addCommande(@RequestBody Commande commande) {
        return iCommandeService.addCommande(commande);
    }

    @PutMapping("/update")
    public Commande updateCommande(Commande commande) {
        return iCommandeService.updateCommande(commande);
    }

    @GetMapping("/get/{idCommande}")
    public Commande retrieveCommande(@PathVariable long idCommande) {
        return iCommandeService.retrieveCommande(idCommande);
    }

    @DeleteMapping("/delete/{idCommande}")
    public void removeCommande(@PathVariable long idCommande) {
        iCommandeService.removeCommande(idCommande);
    }

    @GetMapping("/getByUserIdAndStatus/{userId}/{etat}")
    public List<Commande> getByUserIdAndStatus(@PathVariable Long userId, @PathVariable Status etat) {
        return iCommandeService.findByUserIdAndEtat(userId, etat);
    }


    @GetMapping("/downloadInvoice/{commandeId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long commandeId) {
        try {
            // Fetch the Commande details
            Commande commande = iCommandeService.retrieveCommande(commandeId);
            if (commande == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Get order items
            List<LigneCommande> items = ligneCommandeService.findByCommandeId(commandeId);

            // Create PDF document
            Document document = new Document(PageSize.A4, 36, 36, 54, 36); // Better margins
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // Add metadata
            document.addAuthor("Outdoor Shop");
            document.addCreator("Outdoor Invoice System");
            document.open();

            // Set up fonts and colors
            BaseColor primaryColor = new BaseColor(41, 98, 255); // A nice blue color
            BaseColor secondaryColor = new BaseColor(79, 79, 79); // Dark gray for text
            BaseColor lightGray = new BaseColor(240, 240, 240); // Light gray for backgrounds

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, primaryColor);
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, secondaryColor);
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, secondaryColor);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, secondaryColor);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
            Font highlightFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, primaryColor);

            // ===== HEADER SECTION =====
            // Create a header table with 2 columns
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});
            headerTable.setSpacingAfter(20);

            // Logo and company info (Left column)
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setPaddingBottom(10);

            // Add logo image (if available)
            try {
                ClassPathResource resource = new ClassPathResource("static/images/logo.png");
                byte[] logoBytes = StreamUtils.copyToByteArray(resource.getInputStream());
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(150, 70);
                logoCell.addElement(logo);
            } catch (Exception e) {
                // If logo not available, use text instead
                Paragraph companyName = new Paragraph("OUTDOOR", headerFont);
                logoCell.addElement(companyName);
            }

            Paragraph tagline = new Paragraph("Your adventure starts here", smallFont);
            logoCell.addElement(tagline);

            headerTable.addCell(logoCell);

            // Invoice details (Right column)
            PdfPCell invoiceDetailsCell = new PdfPCell();
            invoiceDetailsCell.setBorder(Rectangle.NO_BORDER);
            invoiceDetailsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            Paragraph invoiceTitle = new Paragraph("INVOICE", titleFont);
            invoiceTitle.setAlignment(Element.ALIGN_RIGHT);
            invoiceDetailsCell.addElement(invoiceTitle);

            // Format the date safely
            Object dateCommande = commande.getDateCommande();
            String formattedDate;

            if (dateCommande instanceof Date) {
                formattedDate = new SimpleDateFormat("dd/MM/yyyy").format((Date) dateCommande);
            } else if (dateCommande instanceof java.time.LocalDate) {
                formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(java.sql.Date.valueOf((java.time.LocalDate) dateCommande));
            } else if (dateCommande instanceof java.time.LocalDateTime) {
                formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(java.sql.Timestamp.valueOf((java.time.LocalDateTime) dateCommande));
            } else {
                formattedDate = "N/A";
            }

            Paragraph invoiceInfo = new Paragraph();
            invoiceInfo.add(new Chunk("Invoice #: ", boldFont));
            invoiceInfo.add(new Chunk(commandeId.toString(), normalFont));
            invoiceInfo.setAlignment(Element.ALIGN_RIGHT);
            invoiceDetailsCell.addElement(invoiceInfo);

            Paragraph invoiceDate = new Paragraph();
            invoiceDate.add(new Chunk("Date: ", boldFont));
            invoiceDate.add(new Chunk(formattedDate, normalFont));
            invoiceDate.setAlignment(Element.ALIGN_RIGHT);
            invoiceDetailsCell.addElement(invoiceDate);

            Paragraph invoiceStatus = new Paragraph();
            invoiceStatus.add(new Chunk("Status: ", boldFont));
            invoiceStatus.add(new Chunk(String.valueOf(commande.getEtat()), highlightFont));
            invoiceStatus.setAlignment(Element.ALIGN_RIGHT);
            invoiceDetailsCell.addElement(invoiceStatus);

            headerTable.addCell(invoiceDetailsCell);

            document.add(headerTable);

            // Add horizontal line
            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell(new Paragraph(" "));
            lineCell.setBorderWidthBottom(2f);
            lineCell.setBorderColorBottom(lightGray);
            lineCell.setBorderWidthTop(0);
            lineCell.setBorderWidthLeft(0);
            lineCell.setBorderWidthRight(0);
            lineCell.setPaddingBottom(5);
            lineTable.addCell(lineCell);
            document.add(lineTable);

            // ===== CUSTOMER INFORMATION SECTION =====
            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            customerTable.setSpacingBefore(15);
            customerTable.setSpacingAfter(20);

            // Bill To Column
            PdfPCell billToCell = new PdfPCell();
            billToCell.setBorder(Rectangle.NO_BORDER);

            Paragraph billToTitle = new Paragraph("BILL TO:", subtitleFont);
            billToTitle.setSpacingAfter(5);
            billToCell.addElement(billToTitle);

            billToCell.addElement(new Paragraph(commande.getNom(), boldFont));
            billToCell.addElement(new Paragraph(commande.getAdresse(), normalFont));
            billToCell.addElement(new Paragraph(commande.getCity() + ", " + commande.getGouvernement(), normalFont));
            billToCell.addElement(new Paragraph("Email: " + commande.getEmail(), normalFont));
            billToCell.addElement(new Paragraph("Phone: " + commande.getPhone(), normalFont));

            customerTable.addCell(billToCell);

            // Shipping Details Column
            PdfPCell shippingCell = new PdfPCell();
            shippingCell.setBorder(Rectangle.NO_BORDER);

            Paragraph shippingTitle = new Paragraph("SHIPPING DETAILS:", subtitleFont);
            shippingTitle.setSpacingAfter(5);
            shippingCell.addElement(shippingTitle);

            // Create a stylish shipping method box
            PdfPTable methodTable = new PdfPTable(1);
            methodTable.setWidthPercentage(100);
            PdfPCell methodCell = new PdfPCell();
            methodCell.setBackgroundColor(lightGray);
            methodCell.setPadding(5);

            String displayMethod = commande.getShippingMethod().equals("ExpressDelivery") ?
                    "Express Delivery" : "Standard Delivery";

            Paragraph methodPara = new Paragraph(displayMethod, boldFont);
            methodPara.setAlignment(Element.ALIGN_CENTER);
            methodCell.addElement(methodPara);
            methodTable.addCell(methodCell);

            shippingCell.addElement(methodTable);
            shippingCell.addElement(new Paragraph(" ", smallFont)); // Spacer

            shippingCell.addElement(new Paragraph("Status: " + commande.getEtat(), normalFont));
            shippingCell.addElement(new Paragraph("Order #: " + commande.getOrderNumber(), normalFont));

            customerTable.addCell(shippingCell);

            document.add(customerTable);

            // ===== ITEMS TABLE SECTION =====
            Paragraph itemsTitle = new Paragraph("ORDER DETAILS", subtitleFont);
            itemsTitle.setSpacingAfter(10);
            document.add(itemsTitle);

            PdfPTable itemsTable = new PdfPTable(5);
            itemsTable.setWidthPercentage(100);
            // Increased width of first column from 5 to 10
            itemsTable.setWidths(new float[]{10, 30, 20, 10, 20});
            itemsTable.setSpacingAfter(20);

            // Style for table headers
            PdfPCell headerCell1 = new PdfPCell(new Paragraph("Item", boldFont));
            PdfPCell headerCell2 = new PdfPCell(new Paragraph("Description", boldFont));
            PdfPCell headerCell3 = new PdfPCell(new Paragraph("Unit Price", boldFont));
            PdfPCell headerCell4 = new PdfPCell(new Paragraph("Quantity", boldFont));
            PdfPCell headerCell5 = new PdfPCell(new Paragraph("Amount", boldFont));

            // Style all header cells
            PdfPCell[] headerCells = {headerCell1, headerCell2, headerCell3, headerCell4, headerCell5};
            for (PdfPCell cell : headerCells) {
                cell.setBackgroundColor(primaryColor);
                cell.setBorderColor(primaryColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                cell.setPaddingBottom(10);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                // Set text color to white for better contrast
                Font whiteFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
                cell.setPhrase(new Phrase(cell.getPhrase().getContent(), whiteFont));
            }

            // Add header cells to table
            itemsTable.addCell(headerCell1);
            itemsTable.addCell(headerCell2);
            itemsTable.addCell(headerCell3);
            itemsTable.addCell(headerCell4);
            itemsTable.addCell(headerCell5);

            // Add items
            double subtotal = 0;
            if (items != null && !items.isEmpty()) {
                int i = 1;
                boolean alternateColor = false;

                for (LigneCommande item : items) {
                    // Alternate row colors for better readability
                    BaseColor rowColor = alternateColor ? lightGray : BaseColor.WHITE;

                    PdfPCell itemNoCell = new PdfPCell(new Paragraph(String.valueOf(i++), normalFont));
                    PdfPCell descCell = new PdfPCell(new Paragraph(item.getProduit().getNomProduit(), normalFont));
                    PdfPCell priceCell = new PdfPCell(new Paragraph(String.format("%.2f TND", item.getPrix()), normalFont));
                    PdfPCell qtyCell = new PdfPCell(new Paragraph(String.valueOf(item.getQuantite()), normalFont));
                    PdfPCell amountCell = new PdfPCell(new Paragraph(String.format("%.2f TND", item.getPrix() * item.getQuantite()), boldFont));

                    // Style all cells in the row
                    PdfPCell[] rowCells = {itemNoCell, descCell, priceCell, qtyCell, amountCell};
                    for (PdfPCell cell : rowCells) {
                        cell.setBackgroundColor(rowColor);
                        cell.setBorderColor(lightGray);
                        cell.setPadding(8);
                    }

                    // Alignment adjustments
                    itemNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    // Add cells to table
                    itemsTable.addCell(itemNoCell);
                    itemsTable.addCell(descCell);
                    itemsTable.addCell(priceCell);
                    itemsTable.addCell(qtyCell);
                    itemsTable.addCell(amountCell);

                    // Calculate subtotal
                    subtotal += item.getPrix() * item.getQuantite();

                    // Toggle for next row
                    alternateColor = !alternateColor;
                }
            } else {
                PdfPCell noItemsCell = new PdfPCell(new Paragraph("No items found", normalFont));
                noItemsCell.setColspan(5);
                noItemsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                noItemsCell.setBackgroundColor(lightGray);
                noItemsCell.setPadding(10);
                itemsTable.addCell(noItemsCell);
            }

            document.add(itemsTable);

            // ===== SUMMARY SECTION =====
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.setSpacingAfter(20);

            // Subtotal
            PdfPCell subtotalLabelCell = new PdfPCell(new Paragraph("Subtotal:", boldFont));
            subtotalLabelCell.setBorder(Rectangle.NO_BORDER);
            subtotalLabelCell.setPadding(5);
            subtotalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell subtotalValueCell = new PdfPCell(new Paragraph(String.format("%.2f TND", subtotal), normalFont));
            subtotalValueCell.setBorder(Rectangle.NO_BORDER);
            subtotalValueCell.setPadding(5);
            subtotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            summaryTable.addCell(subtotalLabelCell);
            summaryTable.addCell(subtotalValueCell);

            // Shipping
            double shippingCost = commande.getShippingMethod().equals("ExpressDelivery") ? 10.0 : 0.0;
            PdfPCell shippingLabelCell = new PdfPCell(new Paragraph("Shipping:", boldFont));
            shippingLabelCell.setBorder(Rectangle.NO_BORDER);
            shippingLabelCell.setPadding(5);
            shippingLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell shippingValueCell = new PdfPCell(new Paragraph(String.format("%.2f TND", shippingCost), normalFont));
            shippingValueCell.setBorder(Rectangle.NO_BORDER);
            shippingValueCell.setPadding(5);
            shippingValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            summaryTable.addCell(shippingLabelCell);
            summaryTable.addCell(shippingValueCell);

            // Service Fee
            PdfPCell feeLabelCell = new PdfPCell(new Paragraph("Service Fee:", boldFont));
            feeLabelCell.setBorder(Rectangle.NO_BORDER);
            feeLabelCell.setPadding(5);
            feeLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell feeValueCell = new PdfPCell(new Paragraph("1.00 TND", normalFont));
            feeValueCell.setBorder(Rectangle.NO_BORDER);
            feeValueCell.setPadding(5);
            feeValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            summaryTable.addCell(feeLabelCell);
            summaryTable.addCell(feeValueCell);

            // Add a line before total
            PdfPCell lineCell2 = new PdfPCell(new Paragraph(""));
            lineCell2.setColspan(2);
            lineCell2.setBorderWidthBottom(1f);
            lineCell2.setBorderColorBottom(BaseColor.BLACK);
            lineCell2.setBorderWidthTop(0);
            lineCell2.setBorderWidthLeft(0);
            lineCell2.setBorderWidthRight(0);
            lineCell2.setPaddingBottom(5);
            summaryTable.addCell(lineCell2);

            // Total (with special styling)
            PdfPCell totalLabelCell = new PdfPCell(new Paragraph("TOTAL:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, primaryColor)));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setPadding(5);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell totalValueCell = new PdfPCell(new Paragraph(String.format("%.2f TND", commande.getMontantCommande()),
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, primaryColor)));
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setPadding(5);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            summaryTable.addCell(totalLabelCell);
            summaryTable.addCell(totalValueCell);

            document.add(summaryTable);

            // ===== PAYMENT DETAILS AND NOTES =====
// Create a bordered box for payment info
            PdfPTable paymentBoxTable = new PdfPTable(1);
            paymentBoxTable.setWidthPercentage(100);

            PdfPCell paymentBoxCell = new PdfPCell();
            paymentBoxCell.setBorderColor(lightGray);
            paymentBoxCell.setBorderWidth(1);
            paymentBoxCell.setPadding(10);

            Paragraph paymentInfo = new Paragraph("PAYMENT INFORMATION", subtitleFont);
            paymentInfo.setAlignment(Element.ALIGN_CENTER);
            paymentInfo.setSpacingAfter(10);
            paymentBoxCell.addElement(paymentInfo);

            Paragraph paymentMethod = new Paragraph("Payment Method: Cash On Delivery", normalFont);
            paymentMethod.setAlignment(Element.ALIGN_CENTER);
            paymentBoxCell.addElement(paymentMethod);

// Create a table with two columns for barcode and QR code
            PdfPTable codesTable = new PdfPTable(2);
            codesTable.setWidthPercentage(100);
            codesTable.setSpacingBefore(15);
            codesTable.setSpacingAfter(5);

// ===== Left column - Barcode =====
            PdfPCell barcodeColumnCell = new PdfPCell();
            barcodeColumnCell.setBorder(Rectangle.NO_BORDER);
            barcodeColumnCell.setPadding(5);

// Generate barcode
            Barcode128 barcode = new Barcode128();
            barcode.setCode("INV-" + commandeId + "-" + commande.getOrderNumber());
            barcode.setCodeType(Barcode128.CODE128);
            Image barcodeImage = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);
            barcodeImage.scalePercent(100);
            barcodeImage.setAlignment(Element.ALIGN_CENTER);

// Add barcode to cell
            barcodeColumnCell.addElement(barcodeImage);

// Add explanatory text
            Paragraph barcodeLabel = new Paragraph("Scan barcode to track order", smallFont);
            barcodeLabel.setAlignment(Element.ALIGN_CENTER);
            barcodeColumnCell.addElement(barcodeLabel);

// ===== Right column - QR code =====
            PdfPCell qrCodeColumnCell = new PdfPCell();
            qrCodeColumnCell.setBorder(Rectangle.NO_BORDER);
            qrCodeColumnCell.setPadding(5);

// Create QR code content with detailed invoice information
            String qrCodeContent = String.format(
                    "Order #: %s\nDate: %s\nCustomer: %s\nAddress: %s, %s, %s\nAmount: %.2f TND\nStatus: %s",
                    commande.getOrderNumber(),
                    formattedDate,
                    commande.getNom(),
                    commande.getAdresse(),
                    commande.getCity(),
                    commande.getGouvernement(),
                    commande.getMontantCommande(),
                    commande.getEtat()
            );

// Generate QR code
            BarcodeQRCode qrCode = new BarcodeQRCode(qrCodeContent, 150, 150, null);
            Image qrCodeImage = qrCode.getImage();
            qrCodeImage.setAlignment(Element.ALIGN_CENTER);
            qrCodeImage.scalePercent(100);

// Add QR code to cell
            qrCodeColumnCell.addElement(qrCodeImage);

// Add explanatory text
            Paragraph qrCodeLabel = new Paragraph("Scan QR code for invoice details", smallFont);
            qrCodeLabel.setAlignment(Element.ALIGN_CENTER);
            qrCodeColumnCell.addElement(qrCodeLabel);

// Add both cells to the codes table
            codesTable.addCell(barcodeColumnCell);
            codesTable.addCell(qrCodeColumnCell);

// Add the codes table to the payment box
            paymentBoxCell.addElement(codesTable);

            paymentBoxTable.addCell(paymentBoxCell);
            document.add(paymentBoxTable);

            // Thank you note
            Paragraph thankYou = new Paragraph("Thank you for your purchase!",
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, primaryColor));
            thankYou.setAlignment(Element.ALIGN_CENTER);
            thankYou.setSpacingBefore(20);
            thankYou.setSpacingAfter(10);
            document.add(thankYou);

            // Footer
            Paragraph footer = new Paragraph("For questions about this invoice, please contact our customer service at support@outdoor-shop.com", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();

            // Configure the HTTP headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=facture_" + commandeId + ".pdf");
            headers.add("Content-Type", "application/pdf");

            // Return the PDF content as a response
            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getProductNamesByCommandeId/{commandeId}")
    public List<String> getProductNamesByCommandeId(@PathVariable Long commandeId) {
        return iCommandeService.getProductNamesByCommandeId(commandeId);
    }
}