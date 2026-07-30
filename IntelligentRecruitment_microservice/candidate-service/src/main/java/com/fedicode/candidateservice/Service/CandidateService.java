package com.fedicode.candidateservice.Service;


import com.fedicode.candidateservice.Dto.CandidateResponse;
import com.fedicode.candidateservice.Dto.CandidateStatsResponse;
import com.fedicode.candidateservice.Dto.ChangeInfoRequest;
import com.fedicode.candidateservice.Dto.ChangePasswordRequest;
import com.fedicode.candidateservice.Entity.Candidate;
import com.fedicode.candidateservice.Entity.Status;
import com.fedicode.candidateservice.Mapper.CandidateMapper;
import com.fedicode.candidateservice.Repository.CandidateRepository;
import com.fedicode.candidateservice.Repository.CandidateSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;


    public boolean existsByEmail(String email){
        return candidateRepository.existsByEmail(email);
    }

    public Optional<Candidate> findByEmail(String email){
        return candidateRepository.findByEmail(email);
    }

    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }


    public void updateCv(MultipartFile cv, String email){
        Candidate candidate = candidateRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Candidate does not exist")
                );
        if(cv != null && !cv.isEmpty()){
            String cvFilePath=fileService.saveFile(cv);
            candidate.setCvFilePath(cvFilePath);
        }
        candidateRepository.save(candidate);
    }
    public void changePassword(
            ChangePasswordRequest request,
            String email
    ) {

        Candidate candidate = candidateRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Candidate not found")
                );

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                candidate.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        candidate.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        candidateRepository.save(candidate);
    }
    public void changeInformation(
            ChangeInfoRequest request,
            String email
    ) {

        Candidate candidate = candidateRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Candidate does not exist")
                );

        // First Name
        if (request.getFirstName() != null &&
                !request.getFirstName().isBlank()) {

            candidate.setFirstName(
                    request.getFirstName().trim()
            );
        }

        // Last Name
        if (request.getLastName() != null &&
                !request.getLastName().isBlank()) {

            candidate.setLastName(
                    request.getLastName().trim()
            );
        }

        // Country
        if (request.getCountry() != null &&
                !request.getCountry().isBlank()) {

            candidate.setCountry(
                    request.getCountry().trim()
            );
        }

        // Phone
        if (request.getPhone() != null &&
                !request.getPhone().isBlank()) {

            candidate.setPhone(
                    request.getPhone().trim()
            );
        }

        // Date of Birth
        if (request.getDateOfBirth() != null) {

            candidate.setDateOfBirth(
                    request.getDateOfBirth()
            );
        }

        // Email
        if (request.getEmail() != null &&
                !request.getEmail().isBlank() &&
                !request.getEmail().equalsIgnoreCase(candidate.getEmail())) {

            boolean emailExists = candidateRepository
                    .existsByEmail(request.getEmail());

            if (emailExists) {

                throw new IllegalArgumentException(
                        "Email already exists"
                );
            }

            candidate.setEmail(
                    request.getEmail().trim()
            );
        }

        candidateRepository.save(candidate);
    }

    public void deleteAccount(String email) {
        Candidate candidate=candidateRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("email not exist"));
        candidateRepository.delete(candidate);
    }


    public void suspendCandidate(
            int candidateId,
            String reason
    ) {
        Candidate candidate = candidateRepository
                .findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate does not exist"));

        if (candidate.getStatus() == Status.SUSPENDED) {
            throw new IllegalStateException("This account is already suspended");
        }
        candidate.setStatus(Status.SUSPENDED);
        candidate.setSuspendedAt(LocalDateTime.now());
        candidate.setSuspensionReason(reason);
        candidateRepository.save(candidate);
    }

    public void activateCandidate(int candidateId) {
        Candidate candidate = candidateRepository
                .findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate does not exist"));
        if (candidate.getStatus() == Status.ACTIVE) {
            throw new IllegalStateException("This account is already active");
        }
        candidate.setStatus(Status.ACTIVE);
        candidate.setSuspendedAt(null);
        candidate.setSuspensionReason(null);
        candidateRepository.save(candidate);
    }

    public Page<CandidateResponse> findAllByStatus(Status status, Pageable pageable){
        Page<Candidate> candidates=candidateRepository.findAllByStatus(status,pageable);
        return candidates.map(CandidateMapper::toResponse);
    }

    public Page<CandidateResponse> candidatesOrderByCreatedAtDesc(Pageable pageable) {
        Page<Candidate> candidates =
                candidateRepository.findAllByOrderByCreatedAtDesc(pageable);
        return candidates.map(CandidateMapper::toResponse);
    }
    public List<Candidate> findByName(String name) {
        return candidateRepository.findAll((root, q, cb) -> {
            String pattern = "%" + name.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern)
            );
        });
    }

    public CandidateStatsResponse getStats() {
        long total=candidateRepository.count();
        long active=candidateRepository.countByStatus(Status.ACTIVE);
        long suspended=candidateRepository.countByStatus(Status.SUSPENDED);
        long thisMonth=candidateRepository.countByCreatedAtAfter(
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));

        Map<String,Long> monthlyData= new LinkedHashMap<>();
        List.of(
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                )
                .forEach(month->monthlyData.put(month,0L));
        try {
            List<Object[]> rawMonthly = candidateRepository.countGroupedByMonth();
            if(rawMonthly !=null){
                rawMonthly.forEach(row->{
                    String month=((String) row[0]).trim();
                    Long count=((Number) row[1]).longValue();
                    monthlyData.put(month,count);
                });
            }
        }catch (Exception e) {
            // log but don't crash — chart will just show all zeros
            log.warn("Failed to fetch monthly chart data: {}", e.getMessage());
        }
        return CandidateStatsResponse.builder()
                .total(total)
                .active(active)
                .suspended(suspended)
                .thisMonth(thisMonth)
                .monthlyData(monthlyData)
                .build();
    }

    public Page<CandidateResponse> searchCandidate(
            String firstName,
            String lastName,
            String email,
            Pageable pageable
    ) {
        return candidateRepository
                .findAll(
                        CandidateSpecification.withFilters(
                                firstName,
                                lastName,
                                email
                        ),
                        pageable
                )
                .map(CandidateMapper::toResponse);
    }
}
