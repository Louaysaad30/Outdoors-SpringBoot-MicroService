package tn.esprit.spring.campingservice.Services.IMPL;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.Reservation;
import tn.esprit.spring.campingservice.Services.Interfaces.IEmailService;
import tn.esprit.spring.campingservice.Services.Interfaces.IReservationService;
import tn.esprit.spring.campingservice.Services.Interfaces.IUserService;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class EmailServiceIMPL implements IEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    private final IReservationService reservationService;
    private final IUserService userService;

    @Override
    public void sendPaymentConfirmationEmail(Long reservationId, String email) {
        try {
            // Get reservation details
            Reservation reservation = reservationService.retrieveReservation(reservationId);
            if (reservation == null) {
                throw new RuntimeException("Reservation not found with ID: " + reservationId);
            }
            String customerName = "Customer"; // Default fallback

            // Get user details if email not provided
            if (email == null || email.isEmpty()) {
                UserDto user = userService.getUserById(reservation.getIdClient());

                if (user != null) {
                    email = user.getEmail();

                } else {
                    throw new RuntimeException("User email not found for client ID: " + reservation.getIdClient());
                }
                // Use the first and last name if available
                if (user.getPrenom() != null && !user.getPrenom().isEmpty()
                        && user.getNom() != null && !user.getNom().isEmpty()) {
                    customerName = user.getPrenom() + " " + user.getNom();
                }
            }

            // Format dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String startDate = dateFormat.format(reservation.getDateDebut());
            String endDate = dateFormat.format(reservation.getDateFin());

            String qrCodeContent = "Payment ID: " + reservation.getPaymentIntentId() +
                    "\nClient ID: " + customerName +
                    "\nCenter: " + reservation.getCentre().getName() +
                    "\nDates: " + startDate + " - " + endDate +
                    "\nPersons: " + reservation.getNbrPersonnes() +
                    "\nTotal: TND" + reservation.getPrixTotal();

            // Generate QR code as base64 image
            String qrCodeBase64 = generateQRCodeBase64(qrCodeContent, 300, 300);


            // Create HTML content
            String htmlContent =
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                            "<div style='background-color: #4CAF50; color: white; padding: 20px; text-align: center;'>" +
                            "<h2>Payment Confirmation</h2></div>" +
                            "<div style='padding: 20px; border: 1px solid #ddd;'>" +
                            "<p>Dear " + customerName + ",</p>" +
                            "<p>Thank you for your reservation! Your payment has been successfully processed.</p>" +
                            "<div style='margin: 20px 0; text-align: center;'>" +
                            "<h3>Reservation QR Code</h3>" +
                            "<p>Please present this QR code upon arrival at " + reservation.getCentre().getName() + "</p>" +
                            "<img src='data:image/png;base64," + qrCodeBase64 + "' alt='Reservation QR Code' style='max-width: 300px;'/>" +
                            "<p style='margin-top: 20px;'><strong>Payment ID:</strong> " + reservation.getPaymentIntentId() + "</p>" +
                            "<p><strong>Check-in Date:</strong> " + startDate + " | <strong>Check-out Date:</strong> " + endDate + "</p>" +
                            "<p><strong>Total Amount:</strong> <span style='font-weight: bold; color: #4CAF50;'>TND " + reservation.getPrixTotal() + "</span></p>" +
                            "</div>" +
                            "<p>If you have any questions about your reservation, please contact our customer service.</p>" +
                            "<p>We look forward to your visit!</p>" +
                            "</div>" +
                            "<div style='text-align: center; margin-top: 20px; font-size: 12px; color: #777;'>" +
                            "<p>This is an automated email. Please do not reply to this message.</p>" +
                            "</div></div>";

            // Set up the email
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(email);
            String subject = "Camping Reservation Payment Confirmation";
            Content content = new Content("text/html", htmlContent);

            Mail mail = new Mail(from, subject, to, content);

            // Send the email using SendGrid
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            // Check for errors
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid API error: " + response.getBody());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String generateQRCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return Base64.encodeBase64String(pngData);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }
}