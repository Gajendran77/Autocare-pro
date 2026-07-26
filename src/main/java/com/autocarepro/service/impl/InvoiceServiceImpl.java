package com.autocarepro.service.impl;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.Invoice;
import com.autocarepro.exception.BusinessException;
import com.autocarepro.repository.InvoiceRepository;
import com.autocarepro.service.InvoiceService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private static final DeviceRgb BRAND_BLUE = new DeviceRgb(14, 99, 255);

    @Override
    @Transactional
    public Invoice generateInvoice(Booking booking) {
        return invoiceRepository.findByBooking(booking).orElseGet(() -> {
            BigDecimal subtotal = booking.getFinalCost() != null ? booking.getFinalCost()
                    : (booking.getEstimatedCost() != null ? booking.getEstimatedCost() : BigDecimal.valueOf(1500));
            BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(tax);

            Invoice invoice = Invoice.builder()
                    .booking(booking)
                    .subtotal(subtotal)
                    .tax(tax)
                    .totalAmount(total)
                    .build();

            return invoiceRepository.save(invoice);
        });
    }

    @Override
    public byte[] generatePdf(Invoice invoice) {
        try {
            Booking booking = invoice.getBooking();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdfDoc);

            // Header
            Paragraph title = new Paragraph("AutoCare Pro")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(BRAND_BLUE);
            document.add(title);
            document.add(new Paragraph("Premium Vehicle Service Management").setFontSize(10).setFontColor(ColorConstants.GRAY));
            document.add(new Paragraph("Tax Invoice").setFontSize(14).setBold().setMarginTop(10));
            document.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()).setMarginBottom(10));

            // Invoice meta table
            Table metaTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth();
            metaTable.addCell(borderless("Invoice No: " + invoice.getInvoiceNumber()));
            metaTable.addCell(borderless("Booking Code: " + booking.getBookingCode()));
            metaTable.addCell(borderless("Issued: " + invoice.getIssuedAt().toLocalDate()));
            metaTable.addCell(borderless("Customer: " + booking.getOwnerName()));
            document.add(metaTable);

            document.add(new Paragraph(" "));

            // Line items table
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2}))
                    .useAllAvailableWidth();
            table.addHeaderCell(headerCell("Description"));
            table.addHeaderCell(headerCell("Vehicle"));
            table.addHeaderCell(headerCell("Amount (₹)"));

            table.addCell(bodyCell(booking.getBrand() + " " + booking.getModel() + " — Service & Repair"));
            table.addCell(bodyCell(booking.getVehicleNumber()));
            table.addCell(bodyCell(invoice.getSubtotal().toString()));

            document.add(table);

            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{4, 2}))
                    .useAllAvailableWidth().setMarginTop(10);
            totalsTable.addCell(borderless("Subtotal"));
            totalsTable.addCell(borderless("₹ " + invoice.getSubtotal()));
            totalsTable.addCell(borderless("GST (18%)"));
            totalsTable.addCell(borderless("₹ " + invoice.getTax()));
            totalsTable.addCell(borderless("Total Amount").setBold());
            totalsTable.addCell(borderless("₹ " + invoice.getTotalAmount()).setBold());
            document.add(totalsTable);

            document.add(new Paragraph("\nThank you for choosing AutoCare Pro.")
                    .setFontSize(10).setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Failed to generate invoice PDF: " + e.getMessage());
        }
    }

    private Cell headerCell(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(BRAND_BLUE).setPadding(6);
    }

    private Cell bodyCell(String text) {
        return new Cell().add(new Paragraph(text)).setPadding(6);
    }

    private Cell borderless(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER).setPadding(3);
    }

    @Override
    public byte[] generateQrCode(String content, int size) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return baos.toByteArray();
        } catch (WriterException | java.io.IOException e) {
            throw new BusinessException("Failed to generate QR code: " + e.getMessage());
        }
    }
}
