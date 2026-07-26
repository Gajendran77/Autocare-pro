package com.autocarepro.controller;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.ChecklistItem;
import com.autocarepro.entity.ServiceImage;
import com.autocarepro.entity.User;
import com.autocarepro.repository.ChecklistItemRepository;
import com.autocarepro.repository.ServiceImageRepository;
import com.autocarepro.security.CustomUserDetails;
import com.autocarepro.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/mechanic")
@RequiredArgsConstructor
public class MechanicController {

    private final BookingService bookingService;
    private final ChecklistItemRepository checklistItemRepository;
    private final ServiceImageRepository serviceImageRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User mechanic = principal.getUser();
        List<Booking> jobs = bookingService.getForMechanic(mechanic);
        model.addAttribute("jobs", jobs);
        model.addAttribute("activeJobs", jobs.stream().filter(b -> b.getStatus().name().equals("IN_PROGRESS") || b.getStatus().name().equals("ASSIGNED")).toList());
        model.addAttribute("completedJobs", jobs.stream().filter(b -> b.getStatus().name().equals("COMPLETED")).toList());
        return "mechanic/dashboard";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getById(id);
        model.addAttribute("booking", booking);
        model.addAttribute("checklist", checklistItemRepository.findByBooking(booking));
        model.addAttribute("images", serviceImageRepository.findByBooking(booking));
        return "mechanic/job-detail";
    }

    @PostMapping("/jobs/{id}/checklist")
    public String addChecklistItem(@PathVariable Long id, @RequestParam String description) {
        Booking booking = bookingService.getById(id);
        ChecklistItem item = ChecklistItem.builder().booking(booking).description(description).completed(false).build();
        checklistItemRepository.save(item);
        return "redirect:/mechanic/jobs/" + id;
    }

    @PostMapping("/checklist/{itemId}/toggle")
    public String toggleChecklistItem(@PathVariable Long itemId, @RequestParam Long bookingId) {
        checklistItemRepository.findById(itemId).ifPresent(item -> {
            item.setCompleted(!item.isCompleted());
            checklistItemRepository.save(item);
        });
        return "redirect:/mechanic/jobs/" + bookingId;
    }

    @PostMapping("/jobs/{id}/progress")
    public String updateProgress(@PathVariable Long id, @RequestParam int percent, @RequestParam(required = false) String notes) {
        bookingService.updateProgress(id, percent, notes);
        return "redirect:/mechanic/jobs/" + id;
    }

    @PostMapping("/jobs/{id}/upload")
    public String uploadImage(@PathVariable Long id, @RequestParam MultipartFile file, @RequestParam String type) throws IOException {
        Booking booking = bookingService.getById(id);

        File dir = new File(uploadDir, "service-images");
        if (!dir.exists()) dir.mkdirs();

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path target = Path.of(dir.getAbsolutePath(), filename);
        Files.copy(file.getInputStream(), target);

        ServiceImage image = ServiceImage.builder()
                .booking(booking)
                .imageUrl("/uploads/service-images/" + filename)
                .type(type.toUpperCase())
                .build();
        serviceImageRepository.save(image);

        return "redirect:/mechanic/jobs/" + id;
    }
}
