package com.autocarepro.controller;

import com.autocarepro.dto.AdminStatsDTO;
import com.autocarepro.entity.Booking;
import com.autocarepro.entity.BookingStatus;
import com.autocarepro.repository.InventoryItemRepository;
import com.autocarepro.repository.PaymentRepository;
import com.autocarepro.service.AdminService;
import com.autocarepro.service.BookingService;
import com.autocarepro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final BookingService bookingService;
    private final UserService userService;
    private final InventoryItemRepository inventoryItemRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AdminStatsDTO stats = adminService.getStats();
        model.addAttribute("stats", stats);
        model.addAttribute("recentBookings", bookingService.getAll().stream().limit(8).toList());
        return "admin/dashboard";
    }

    @GetMapping("/bookings")
    public String bookings(@RequestParam(required = false) String status, Model model) {
        if (status != null && !status.isBlank()) {
            model.addAttribute("bookings", bookingService.getByStatus(BookingStatus.valueOf(status.toUpperCase())));
        } else {
            model.addAttribute("bookings", bookingService.getAll());
        }
        model.addAttribute("mechanics", userService.findAllMechanics());
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/assign")
    public String assignMechanic(@PathVariable Long id, @RequestParam Long mechanicId) {
        bookingService.assignMechanic(id, mechanicId);
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        bookingService.updateStatus(id, BookingStatus.valueOf(status.toUpperCase()));
        return "redirect:/admin/bookings";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", userService.findAllCustomers());
        return "admin/customers";
    }

    @GetMapping("/mechanics")
    public String mechanics(Model model) {
        model.addAttribute("mechanics", userService.findAllMechanics());
        return "admin/mechanics";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, @RequestParam(required = false) String redirect) {
        userService.toggleEnabled(id);
        return "redirect:" + (redirect != null ? redirect : "/admin/customers");
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("items", inventoryItemRepository.findAll());
        return "admin/inventory";
    }

    @GetMapping("/payments")
    public String payments(Model model) {
        model.addAttribute("payments", paymentRepository.findAll());
        return "admin/payments";
    }
}
