package com.autocarepro.service;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.Invoice;

public interface InvoiceService {
    Invoice generateInvoice(Booking booking);
    byte[] generatePdf(Invoice invoice);
    byte[] generateQrCode(String content, int size);
}
