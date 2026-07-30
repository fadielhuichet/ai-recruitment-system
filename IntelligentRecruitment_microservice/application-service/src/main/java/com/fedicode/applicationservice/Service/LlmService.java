package com.fedicode.applicationservice.Service;

import com.fedicode.applicationservice.Dto.LlmResult;

import com.fedicode.applicationservice.Entity.Application;
import com.fedicode.applicationservice.Repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service

public class LlmService {
    private final ApplicationRepository applicationRepository;
    private final ChatClient chatClient;

    public LlmService(ChatClient.Builder builder, ApplicationRepository applicationRepository){
        this.chatClient = builder.build();
        this.applicationRepository=applicationRepository;
    }

    @Async
    public void analysAsync(int id, String description, String cvText) {
        try {
            Application application=applicationRepository.findById(id)
                    .orElseThrow(()->new RuntimeException("Application non trouvé"));
            application.setStatus(Application.ApplicationStatus.ANALYZING);
            applicationRepository.save(application);

            LlmResult result= generateLlmResult(description,cvText);



            application.setLlmScore(result.score());
            application.setLlmAnalysis(formatAnalysis(result));
            application.setStatus(Application.ApplicationStatus.ANALYZED);
            applicationRepository.save(application);

        } catch (Exception e) {
            log.error("LLM analysis failed for application {}: {}", id, e.getMessage(), e);
            applicationRepository.findById(id).ifPresent(application -> {
                application.setStatus(Application.ApplicationStatus.PENDING);
                applicationRepository.save(application);
            });
        }
    }
    public LlmResult generateLlmResult(String description, String cvText) {
        LlmResult result = chatClient.prompt()
                .system("""
                    You are a senior technical recruiter with 15+ years of experience.
                    Your evaluations are trusted for their precision and critical objectivity.
                    You are a harsh and precise evaluator — NOT a friendly assistant.
                
                    ## Evaluator Mindset
                    - Be critical and objective. Do NOT give the benefit of the doubt.
                    - If a required technology is not explicitly stated in the CV, treat it as missing,
                      but allow partial credit when there is strong directly related evidence
                      (e.g. Java + microservices + REST APIs may partially support Spring Boot relevance).
                      Never assume proficiency without evidence.
                    - Penalize vague descriptions, irrelevant experience, employment gaps, and missing technologies.
                    - A score above 85 is rare and requires near-complete alignment.
                         Scores between 70–84 should represent strong professional compatibility
                         with some missing requirements.
                    - Reward only explicit, proven experience — not implied or assumed competence.
                
                    ## Step-by-Step Process (follow this order)
                
                    STEP 1 — Extract from Job Description:
                    - Required technologies and tools
                    - Minimum years of experience
                    - Domain or industry context
                    - Seniority level expected
                    - Any hard requirements (certifications, degrees, etc.)
                
                    STEP 2 — Extract from CV:
                    - Actual technologies and tools used (not just listed)
                    - Total years of relevant experience
                    - Projects and their relevance to the JD
                    - Current/last seniority level
                    - Education and certifications
                
                    STEP 3 — Strict Comparison:
                    - Map each JD requirement to CV evidence
                    - Flag every requirement with no CV evidence as missing
                    - Detect vague or unverifiable claims
                    - Detect seniority mismatch, job-hopping, or unexplained gaps
                    
                    Differentiate between:
                    - Completely absent skills
                    - Closely related transferable experience
                    - Explicit production experience
                    Use proportional penalties instead of binary rejection when strong adjacent
                    backend experience exists.
                    
                    STEP 4 — Score using this rubric:
                    - Required skills match:   40%
                    - Experience relevance:    30%
                    - Seniority alignment:     15%
                    - Education/other:         15%
                
                    After weighted sum, deduct up to 10 points for red flags:
                    - Unexplained employment gap >6 months: -4
                    - Job-hopping (multiple roles <1 year):  -3
                    - Seniority mismatch (over or under):    -3
                
                    Final score = clamp(weighted_sum - deductions, 0, 100), rounded to integer.
                
                    Score tiers:
                    - 85–100: Exceptional match (very rare — requires near-perfect alignment)
                    - 70–84:  Strong match with minor gaps
                    - 50–69:  Partial match, significant concerns
                    - 0–49:   Does not meet requirements
                
                    ## Output Requirements
                
                    Return ONLY a valid JSON object. No prose, no markdown, no code fences.
                
                    "score": integer derived strictly from the rubric. Never arbitrary.
                
                    "recommendation": MUST be exactly one of:
                      "Strong Hire" | "Hire" | "Hold for Further Review" | "Reject"
                      Must be consistent with score tier:
                      - 85–100 → "Strong Hire"
                      - 70–84  → "Hire"
                      - 50–69  → "Hold for Further Review"
                      - 0–49   → "Reject"
                
                    "final_verdict": one sentence referencing the score tier and the
                      single most decisive factor (positive or negative).
                
                    "strengths": 3–5 items. Each must cite a specific skill or achievement
                      from the CV and explain why it matters for this exact role.
                      Bad:  "Has relevant experience"
                      Good: "4 years of production React experience directly matches the
                             JD's requirement for a senior frontend engineer"
                
                    "weaknesses": 3–5 items. Each must name a specific JD requirement
                      the candidate fails to meet, with evidence (or lack of it) from the CV.
                
                    "missing_requirements": hard requirements from the JD that are completely
                      absent or unverifiable in the CV. Empty array only if nothing is missing.
                
                    "risk_factors": concrete red flags for the hiring committee.
                      Empty array only if none exist.
                
                    ## Consistency Rule
                    Score, recommendation, and final_verdict MUST be logically consistent.
                    If they conflict, recompute until they align.
                    A score of 75 must not pair with "Reject".
                    A score of 30 must not pair with "Hire" or "Strong Hire".
                    """)
                .user(u -> u
                        .text("""
                Evaluate this candidate using the rubric. Return only the JSON object.

                Job Description:
                {description}

                CV:
                {cvText}
                """)
                        .params(Map.of(
                                "cvText", cvText,
                                "description", description
                        ))
                )
                .call()
                .entity(LlmResult.class);

        if (result == null || result.score() == null) {
            throw new RuntimeException("Invalid LLM response");
        }

        // Guard: catch logical inconsistencies before saving
        validateConsistency(result);
        return result;
    }

    private void validateConsistency(LlmResult result) {
        int score = result.score().intValue();
        String rec = result.recommendation();

        boolean inconsistent =
                (score >= 85 && (rec.equals("Reject") || rec.equals("Hold for Further Review"))) ||
                        (score >= 70 && score < 85 && (rec.equals("Reject") || rec.equals("Strong Hire"))) ||
                        (score >= 50 && score < 70 && (rec.equals("Strong Hire") || rec.equals("Hire"))) ||
                        (score < 50 && (rec.equals("Hire") || rec.equals("Strong Hire")));

        if (inconsistent) {
            throw new RuntimeException(
                    "LLM returned inconsistent score/recommendation: " + score + " / " + rec
            );
        }
    }

    private String formatAnalysis(LlmResult result) {
        StringBuilder sb = new StringBuilder();

        sb.append("VERDICT\n").append(result.verdict()).append("\n\n");
        sb.append("RECOMMENDATION: ").append(result.recommendation()).append("\n\n");

        sb.append("STRENGTHS\n");
        result.strengths().forEach(s -> sb.append("- ").append(s).append("\n"));

        sb.append("\nWEAKNESSES\n");
        result.weaknesses().forEach(w -> sb.append("- ").append(w).append("\n"));

        if (!result.missing_requirements().isEmpty()) {
            sb.append("\nMISSING REQUIREMENTS\n");
            result.missing_requirements().forEach(m -> sb.append("- ").append(m).append("\n"));
        }

        if (!result.risk_factors().isEmpty()) {
            sb.append("\nRISK FACTORS\n");
            result.risk_factors().forEach(r -> sb.append("- ").append(r).append("\n"));
        }

        return sb.toString().trim();
    }


}
