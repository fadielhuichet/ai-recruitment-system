package com.fedicode.candidateservice.Controller;


import com.fedicode.candidateservice.Dto.ChangeInfoRequest;
import com.fedicode.candidateservice.Dto.ChangePasswordRequest;
import com.fedicode.candidateservice.Dto.CandidateResponse;
import com.fedicode.candidateservice.Dto.CandidateStatsResponse;
import com.fedicode.candidateservice.Entity.Candidate;
import com.fedicode.candidateservice.Entity.Status;
import com.fedicode.candidateservice.Mapper.CandidateMapper;
import com.fedicode.candidateservice.Service.CandidateService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class candidateController {
    private final CandidateService candidateService;

    //--(feign)----

    @GetMapping("/candidate/exist/{email}")
    public boolean existsByEmail(@PathVariable String email){
        return candidateService.existsByEmail(email);
    }
    @GetMapping("/candidate/find/{email}")
    public Optional<Candidate> findByEmail(@PathVariable String email){
        return candidateService.findByEmail(email);
    }
    @PostMapping("/create")
    public Candidate saveCandidate(@RequestBody Candidate candidate){
        return candidateService.saveCandidate(candidate);
    }

    //

    @PatchMapping("/account-info")
    public ResponseEntity<?> changeAccountInfo(
            @RequestBody ChangeInfoRequest request,
            @RequestHeader("X-User-Email") String email
    ) {
        candidateService.changeInformation(request, email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<?> deleteAccount(
            @RequestHeader("X-User-Email") String email
    ) {
        candidateService.deleteAccount(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/candidate/search/byName")
    public ResponseEntity<List<CandidateResponse>> findByName(@RequestParam String name) {
        List<CandidateResponse> responses = candidateService.findByName(name)
                .stream()
                .map(CandidateMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/candidate/search")
    public ResponseEntity<Page<CandidateResponse>> searchCandidate(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(candidateService.searchCandidate(query, query, query, pageable));
    }

    @GetMapping(value = "/candidates", params = "sort=createdAt,desc")
    public Page<CandidateResponse> candidatesOrderByCreatedAtDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return candidateService.candidatesOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @GetMapping("/activatedCandidates")
    public Page<CandidateResponse> getActivatedCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return candidateService.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size));
    }

    @GetMapping("/suspendedCandidates")
    public Page<CandidateResponse> getSuspendedCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return candidateService.findAllByStatus(Status.SUSPENDED, PageRequest.of(page, size));
    }

    @GetMapping("/candidate/stats")
    public CandidateStatsResponse getStats() {
        return candidateService.getStats();
    }

    @PutMapping("/candidate/{candidateId}/suspend")
    public ResponseEntity<?> suspendCandidate(
            @PathVariable int candidateId,
            @RequestBody(required = false) String reason,
            @RequestHeader("X-User-Roles") String role
    ) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        candidateService.suspendCandidate(candidateId, reason);
        return ResponseEntity.ok(Map.of("message", "Candidate suspended with success"));
    }

    @PutMapping("/candidate/{candidateId}/activate")
    public ResponseEntity<?> activateCandidate(
            @PathVariable int candidateId,
            @RequestHeader("X-User-Roles") String role
    ) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        candidateService.activateCandidate(candidateId);
        return ResponseEntity.ok(Map.of("message", "Candidate activated with success"));
    }

    @PatchMapping(value = "/updateCv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCv(
            @RequestParam("cv") MultipartFile cv,
            @RequestHeader("X-User-Email") String email
    ) {
        candidateService.updateCv(cv, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            @RequestHeader("X-User-Email") String email
    ) {
        candidateService.changePassword(request, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(
            @RequestBody ChangeInfoRequest request,
            @RequestHeader("X-User-Email") String email
    ) {
        candidateService.changeInformation(request, email);
        return ResponseEntity.noContent().build();
    }
}
