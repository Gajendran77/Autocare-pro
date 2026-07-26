package com.autocarepro.controller;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.Invoice;
import com.autocarepro.entity.User;
import com.autocarepro.security.CustomUserDetails;
import com.autocarepro.service.BookingService;
import com.autocarepro.service.InvoiceService;
import com.autocarepro.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final BookingService bookingService;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;

    @GetMapping("/customer/notifications/unread-count")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal CustomUserDetails principal) {
        return Map.of("count", notificationService.getUnreadCount(principal.getUser()));
    }

    @GetMapping(value = "/invoices/{bookingId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long bookingId) {
        Booking booking = bookingService.getById(bookingId);
        Invoice invoice = invoiceService.generateInvoice(booking);
        byte[] pdf = invoiceService.generatePdf(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + invoice.getInvoiceNumber() + ".pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdf);
    }

    @GetMapping(value = "/bookings/{bookingId}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> bookingQrCode(@PathVariable Long bookingId) {
        Booking booking = bookingService.getById(bookingId);
        String content = "AutoCare Pro Service Record\nBooking: " + booking.getBookingCode()
                + "\nVehicle: " + booking.getVehicleNumber()
                + "\nStatus: " + booking.getStatus();
        byte[] qr = invoiceService.generateQrCode(content, 300);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qr);
    }

    // ---- Admin dashboard chart data ----
    @GetMapping("/admin/charts/bookings-by-status")
    public Map<String, Long> bookingsByStatus() {
        List<Booking> all = bookingService.getAll();
        return Map.of(
                "PENDING", all.stream().filter(b -> b.getStatus().name().equals("PENDING")).count(),
                "ASSIGNED", all.stream().filter(b -> b.getStatus().name().equals("ASSIGNED")).count(),
                "IN_PROGRESS", all.stream().filter(b -> b.getStatus().name().equals("IN_PROGRESS")).count(),
                "COMPLETED", all.stream().filter(b -> b.getStatus().name().equals("COMPLETED")).count(),
                "CANCELLED", all.stream().filter(b -> b.getStatus().name().equals("CANCELLED")).count()
        );
    }

    @GetMapping("/admin/charts/bookings-by-type")
    public Map<String, Long> bookingsByType() {
        List<Booking> all = bookingService.getAll();
        return Map.of(
                "CAR", all.stream().filter(b -> b.getVehicleType().name().equals("CAR")).count(),
                "BIKE", all.stream().filter(b -> b.getVehicleType().name().equals("BIKE")).count(),
                "TRUCK", all.stream().filter(b -> b.getVehicleType().name().equals("TRUCK")).count()
        );
    }
}
