package com.fedicode.recruiterservice.Controller;

import com.fedicode.recruiterservice.Dto.ChangeInfoRequest;
import com.fedicode.recruiterservice.Dto.ChangePasswordRequest;
import com.fedicode.recruiterservice.Dto.RecruiterResponse;
import com.fedicode.recruiterservice.Dto.RecruiterStatsResponse;
import com.fedicode.recruiterservice.Entity.Recruiter;
import com.fedicode.recruiterservice.Service.RecruiterService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruiter")
@AllArgsConstructor
public class RecruiterController {

    private RecruiterService recruiterService;

    //----OpenFeign-------(start)
    @GetMapping("/exists/{recruiterEmail}")
    public Boolean existsByEmail(@PathVariable String recruiterEmail) {
        return recruiterService.existByEmail(recruiterEmail);
    }

    @GetMapping("/email/{email}")
    public Optional<Recruiter> findByEmail(@PathVariable String email) {
        return recruiterService.findByEmail(email);
    }

    @PostMapping("/create")
    public Recruiter createRecruiter(@RequestBody Recruiter recruiter) {
        return recruiterService.createRecruiter(recruiter);
    }

    @GetMapping("/recruiters/{id}")
    public Recruiter findRecruiterById(@PathVariable int id) {
        return recruiterService.findRecruiterById(id);
    }
    @GetMapping("/recruiters")
    public List<Recruiter> findRecruitersByIds(@RequestParam("ids") List<Integer> ids){
        return recruiterService.findRecruitersByIds(ids);

    }


    @GetMapping("/search/byName")
    public ResponseEntity<List<Recruiter>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(recruiterService.findByName(name));
    }

    //----OpenFeign-------(end)

    //(Manage Account((start)))
    @PatchMapping("/account-info")
    public ResponseEntity<?> changeAccountInfo(
        @RequestBody ChangeInfoRequest request,
        @RequestHeader("X-User-Email") String recruiterEmail
    ) {
        recruiterService.changeInformation(request, recruiterEmail);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(
        @RequestBody ChangePasswordRequest request,
        @RequestHeader("X-User-Email") String recruiterEmail
    ) {
        recruiterService.changePassword(request, recruiterEmail);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<?> deleteAccount(
        @RequestHeader("X-User-Email") String recruiterEmail
    ) {
        recruiterService.deleteAccount(recruiterEmail);
        return ResponseEntity.noContent().build();
    }

    //(Manage Account((end)))

    //----(admin)---
    @GetMapping("/search")
    public ResponseEntity<Page<RecruiterResponse>> searchJobs(
        @RequestParam(required = false) String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
        );
        return ResponseEntity.ok(
            recruiterService.searchRecruiter(
                query,
                query,
                query,
                query,
                pageable
            )
        );
    }

    @DeleteMapping("/account/{recruiterId}/admin-delete")
    public ResponseEntity<?> deleteAccountFromAdmin(
        @PathVariable int recruiterId
    ) {
        recruiterService.deleteAccountFromAdmin(recruiterId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{recruiterId}/suspend")
    public ResponseEntity<?> suspendRecruiter(
        @PathVariable int recruiterId,
        @RequestBody(required = false) String reason,
        @RequestHeader("X-User-Email") String adminEmail
    ) {
         recruiterService.suspendRecruiter(
            recruiterId,
            reason,
            adminEmail
        );
        return ResponseEntity.ok(
                Map.of("message","Compte recruteur suspendu avec succès")
        );
    }

    @PutMapping("/{recruiterId}/activate")
    public ResponseEntity<?> activateRecruiter(@PathVariable int recruiterId) {
        recruiterService.activateRecruiter(recruiterId);
        return ResponseEntity.ok(Map.of("message","recruiter activated with success"));
    }

    @GetMapping(params = "sort=createdAt,desc")
    public Page<RecruiterResponse> recruitersOrderByCreatedAtDesc(
        @RequestHeader("X-User-Email") String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return recruiterService.recruitersOrderByCreatedAtDesc(
            PageRequest.of(page, size)
        );
    }

    @GetMapping("/number")
    public long numberRecruiters() {
        return recruiterService.numberRecruiter();
    }

    @GetMapping("/activatedRecruiters")
    public Page<RecruiterResponse> getActivatedRecruiters(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return recruiterService.getActivatedRecruiter(
            PageRequest.of(page, size)
        );
    }

    @GetMapping("/suspendedRecruiters")
    public Page<RecruiterResponse> getSuspendedRecruiters(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return recruiterService.getSuspendedRecruiter(
            PageRequest.of(page, size)
        );
    }

    @GetMapping("/stats")
    public RecruiterStatsResponse getStats(){
        return recruiterService.getStats();
    }
}
