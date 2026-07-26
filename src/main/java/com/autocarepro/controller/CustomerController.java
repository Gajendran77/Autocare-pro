package com.autocarepro.controller;

import com.autocarepro.dto.BookingRequest;
import com.autocarepro.dto.DashboardStatsDTO;
import com.autocarepro.dto.VehicleRequest;
import com.autocarepro.entity.Booking;
import com.autocarepro.entity.User;
import com.autocarepro.entity.Vehicle;
import com.autocarepro.security.CustomUserDetails;
import com.autocarepro.service.BookingService;
import com.autocarepro.service.NotificationService;
import com.autocarepro.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final NotificationService notificationService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User customer = principal.getUser();
        DashboardStatsDTO stats = bookingService.getCustomerStats(customer);
        List<Vehicle> vehicles = vehicleService.getVehiclesForOwner(customer);
        stats.setTotalVehicles(vehicles.size());

        model.addAttribute("stats", stats);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("recentBookings", bookingService.getForCustomer(customer).stream().limit(5).toList());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(customer));
        model.addAttribute("user", customer);
        return "customer/dashboard";
    }

    @GetMapping("/book-service")
    public String bookServicePage(Model model) {
        model.addAttribute("bookingRequest", new BookingRequest());
        return "customer/book-service";
    }

    @PostMapping("/book-service")
    public String bookService(@AuthenticationPrincipal CustomUserDetails principal,
                               @Valid @ModelAttribute("bookingRequest") BookingRequest request,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "customer/book-service";
        }
        Booking booking = bookingService.createBooking(principal.getUser(), request);
        model.addAttribute("success", true);
        model.addAttribute("bookingCode", booking.getBookingCode());
        model.addAttribute("bookingRequest", new BookingRequest());
        return "customer/book-service";
    }

    @GetMapping("/track-service")
    public String trackService(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("bookings", bookingService.getForCustomer(principal.getUser()));
        return "customer/track-service";
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("bookings", bookingService.getForCustomer(principal.getUser()));
        return "customer/history";
    }

    @GetMapping("/vehicles")
    public String vehicles(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("vehicles", vehicleService.getVehiclesForOwner(principal.getUser()));
        model.addAttribute("vehicleRequest", new VehicleRequest());
        return "customer/vehicles";
    }

    @PostMapping("/vehicles")
    public String addVehicle(@AuthenticationPrincipal CustomUserDetails principal,
                              @Valid @ModelAttribute("vehicleRequest") VehicleRequest request,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.getVehiclesForOwner(principal.getUser()));
            return "customer/vehicles";
        }
        vehicleService.addVehicle(principal.getUser(), request);
        return "redirect:/customer/vehicles";
    }

    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("notifications", notificationService.getForUser(principal.getUser()));
        notificationService.markAllRead(principal.getUser());
        return "customer/notifications";
    }
}
