package com.fedicode.recruiterservice.Service;

import com.fedicode.recruiterservice.Dto.ChangeInfoRequest;
import com.fedicode.recruiterservice.Dto.ChangePasswordRequest;
import com.fedicode.recruiterservice.Dto.RecruiterResponse;
import com.fedicode.recruiterservice.Dto.RecruiterStatsResponse;
import com.fedicode.recruiterservice.Entity.Recruiter;
import com.fedicode.recruiterservice.Entity.Status;
import com.fedicode.recruiterservice.Repository.RecruiterRepository;
import com.fedicode.recruiterservice.Repository.RecruiterSpecification;
import java.time.LocalDateTime;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RecruiterService {

    private final PasswordEncoder passwordEncoder;
    private RecruiterRepository recruiterRepository;

    //---(admin)---
    public Page<RecruiterResponse> searchRecruiter(
        String firstName,
        String lastName,
        String email,
        String companyName,
        Pageable pageable
    ) {
        return recruiterRepository
            .findAll(
                RecruiterSpecification.withFilters(
                    firstName,
                    lastName,
                    email,
                    companyName
                ),
                pageable
            )
            .map(recruiter ->
                RecruiterResponse.builder()
                    .id(recruiter.getId())
                    .firstName(recruiter.getFirstName())
                    .lastName(recruiter.getLastName())
                    .createdAt(recruiter.getCreatedAt())
                    .companyName(recruiter.getCompanyName())
                    .email(recruiter.getEmail())
                    .phone(recruiter.getPhone())
                    .status(recruiter.getStatus())
                        .profileImage(recruiter.getProfileImage())
                    .build()
            );
    }

    public List<Recruiter> findRecruitersByIds(List<Integer> ids) {
        return recruiterRepository.findAllById(ids);
    }
    @CacheEvict(value = "recruiter_cache", key = "#recruiterId")
    public void deleteAccountFromAdmin(int recruiterId) {
        Recruiter recruiter = recruiterRepository
            .findById(recruiterId)
            .orElseThrow(() ->
                new RuntimeException("Recruiter does not exist")
            );
        recruiterRepository.delete(recruiter);
    }

    @CacheEvict(value = "recruiter_cache", key = "#recruiterId")
    public void suspendRecruiter(
        int recruiterId,
        String reason,
        String adminEmail
    ) {
        Recruiter recruiter = recruiterRepository
            .findById(recruiterId)
            .orElseThrow(() -> new RuntimeException("recruteur non trouvé"));

        if (recruiter.getStatus() == Status.SUSPENDED) {
            throw new IllegalStateException("Ce compte est déjà inactif");
        }
        recruiter.setStatus(Status.SUSPENDED);
        recruiter.setSuspendedAt(LocalDateTime.now());
        recruiter.setSuspendedBy(adminEmail);
        recruiter.setSuspensionReason(reason);
        recruiterRepository.save(recruiter);
    }
    @CacheEvict(value = "recruiter_cache", allEntries = true)
    public void activateRecruiter(int recruiterId) {
        Recruiter recruiter = recruiterRepository
            .findById(recruiterId)
            .orElseThrow(() -> new RuntimeException("recruteur non trouvé"));
        if (recruiter.getStatus() == Status.ACTIVE) {
            throw new IllegalStateException("Ce compte est déjà actif");
        }
        recruiter.setStatus(Status.ACTIVE);
        recruiter.setSuspendedAt(null);
        recruiter.setSuspendedBy(null);
        recruiter.setSuspensionReason(null);
        recruiterRepository.save(recruiter);
    }

    public Page<RecruiterResponse> recruitersOrderByCreatedAtDesc(
        Pageable pageable
    ) {
        Page<Recruiter> recruiters =
            recruiterRepository.findAllByOrderByCreatedAtDesc(pageable);
        return recruiters.map(recruiter ->
            RecruiterResponse.builder()
                .id(recruiter.getId())
                .firstName(recruiter.getFirstName())
                .lastName(recruiter.getLastName())
                .email(recruiter.getEmail())
                .companyName(recruiter.getCompanyName())
                .phone(recruiter.getPhone())
                .createdAt(recruiter.getCreatedAt())
                .status(recruiter.getStatus())
                    .profileImage(recruiter.getProfileImage())
                .build()
        );
    }

    public long numberRecruiter() {
        return recruiterRepository.count();
    }
    @CacheEvict(value = "recruiter_cache", allEntries = true)
    public void changeInformation(
        ChangeInfoRequest request,
        String recruiterEmail
    ) {
        Recruiter recruiter = recruiterRepository
            .findByEmail(recruiterEmail)
            .orElseThrow(() ->
                new UsernameNotFoundException("Recruiter not exist")
            );

        if (
            request.getFirstName() != null && !request.getFirstName().isBlank()
        ) {
            recruiter.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            recruiter.setLastName(request.getLastName());
        }
        if (
            request.getCompanyName() != null &&
            !request.getCompanyName().isBlank()
        ) {
            recruiter.setCompanyName(request.getCompanyName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            recruiter.setPhone(request.getPhone());
        }
        if (
            request.getEmail() != null &&
            !request.getEmail().isBlank() &&
            !request.getEmail().equalsIgnoreCase(recruiter.getEmail())
        ) {
            if (recruiterRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email deja exist");
            }
            recruiter.setEmail(request.getEmail());
        }
        recruiterRepository.save(recruiter);
    }
    public void changePassword(ChangePasswordRequest request, String recruiterEmail) {
        Recruiter recruiter = recruiterRepository
            .findByEmail(recruiterEmail)
            .orElseThrow(() -> new UsernameNotFoundException("Recruiter not exist"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), recruiter.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        recruiter.setPassword(passwordEncoder.encode(request.getNewPassword()));
        recruiterRepository.save(recruiter);
    }

    @CacheEvict(value = "recruiter_cache", allEntries = true)
    public void deleteAccount(String recruiterEmail) {
        Recruiter recruiter = recruiterRepository
            .findByEmail(recruiterEmail)
            .orElseThrow(() ->
                new RuntimeException("Recruiter does not exist")
            );
        recruiterRepository.delete(recruiter);
    }

    //---(OpenFeign)----(start)---
    @CachePut(value="recruiter_cache",key="#result.id")
    public Recruiter createRecruiter(Recruiter recruiter) {
        return recruiterRepository.save(recruiter);
    }
    
    public Optional<Recruiter> findByEmail(String email) {
        return recruiterRepository.findByEmail(email);
    }

    public Boolean existByEmail(String recruiterEmail) {
        return recruiterRepository.existsByEmail(recruiterEmail);
    }

    @Cacheable(value="recruiter_cache",key="#id")
    public Recruiter findRecruiterById(int id) {
        return recruiterRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("recruiter does not exist")
            );
    }

    //---(OpenFeign)----(end)---
    public Page<RecruiterResponse> getActivatedRecruiter(Pageable pageable) {
        return recruiterRepository
            .findAllByStatus(Status.ACTIVE, pageable)
            .map(recruiter ->
                RecruiterResponse.builder()
                    .id(recruiter.getId())
                    .firstName(recruiter.getFirstName())
                    .lastName(recruiter.getLastName())
                    .email(recruiter.getEmail())
                    .status(recruiter.getStatus())
                    .phone(recruiter.getPhone())
                    .companyName(recruiter.getCompanyName())
                    .createdAt(recruiter.getCreatedAt())
                        .profileImage(recruiter.getProfileImage())
                    .build()
            );
    }

    public Page<RecruiterResponse> getSuspendedRecruiter(Pageable pageable) {
        return recruiterRepository
            .findAllByStatus(Status.SUSPENDED, pageable)
            .map(recruiter ->
                RecruiterResponse.builder()
                    .id(recruiter.getId())
                    .firstName(recruiter.getFirstName())
                    .lastName(recruiter.getLastName())
                    .email(recruiter.getEmail())
                    .status(recruiter.getStatus())
                    .phone(recruiter.getPhone())
                    .companyName(recruiter.getCompanyName())
                    .createdAt(recruiter.getCreatedAt())
                        .profileImage(recruiter.getProfileImage())
                    .build()
            );
    }


    public List<Recruiter> findByName(String name) {
        return recruiterRepository.findAll((root, q, cb) -> {
            String pattern = "%" + name.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern)
            );
        });
    }

    public RecruiterStatsResponse getStats() {
        long total=recruiterRepository.count();
        long active=recruiterRepository.countByStatus(Status.ACTIVE);
        long suspended=recruiterRepository.countByStatus(Status.SUSPENDED);
        long thisMonth=recruiterRepository.countByCreatedAtAfter(
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
            List<Object[]> rawMonthly = recruiterRepository.countGroupedByMonth();
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
        return RecruiterStatsResponse.builder()
                .total(total)
                .active(active)
                .suspended(suspended)
                .thisMonth(thisMonth)
                .monthlyData(monthlyData)
                .build();
    }


}
