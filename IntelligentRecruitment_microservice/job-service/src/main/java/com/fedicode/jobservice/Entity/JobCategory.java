package com.fedicode.jobservice.Entity;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;


public enum JobCategory {

    // Tech & Software Engineering
    SOFTWARE_DEVELOPMENT("Software Development", "Tech & Software Engineering"),
    DATA_SCIENCE("Data Science & Analytics", "Tech & Software Engineering"),
    DEVOPS_CLOUD("DevOps & Cloud", "Tech & Software Engineering"),
    CYBERSECURITY("Cybersecurity", "Tech & Software Engineering"),
    AI_MACHINE_LEARNING("Artificial Intelligence & ML", "Tech & Software Engineering"),
    QA_TESTING("Quality Assurance & Testing", "Tech & Software Engineering"),
    EMBEDDED_SYSTEMS("Embedded Systems & IoT", "Tech & Software Engineering"),

    // Business & Finance
    FINANCE_ACCOUNTING("Finance & Accounting", "Business & Finance"),
    BANKING_INSURANCE("Banking & Insurance", "Business & Finance"),
    CONSULTING("Consulting & Strategy", "Business & Finance"),
    AUDIT_RISK("Audit & Risk Management", "Business & Finance"),
    PROJECT_MANAGEMENT("Project Management", "Business & Finance"),

    // Marketing & Sales
    MARKETING_COMMUNICATION("Marketing & Communication", "Marketing & Sales"),
    SALES_BUSINESS_DEV("Sales & Business Development", "Marketing & Sales"),
    DIGITAL_MARKETING("Digital Marketing & SEO", "Marketing & Sales"),
    PUBLIC_RELATIONS("Public Relations", "Marketing & Sales"),
    E_COMMERCE("E-Commerce", "Marketing & Sales"),

    // Healthcare & Medical
    HEALTHCARE_MEDICINE("Healthcare & Medicine", "Healthcare & Medical"),
    PHARMACY("Pharmacy", "Healthcare & Medical"),
    PSYCHOLOGY_THERAPY("Psychology & Therapy", "Healthcare & Medical"),
    NURSING_CARE("Nursing & Patient Care", "Healthcare & Medical"),
    VETERINARY("Veterinary", "Healthcare & Medical"),

    // Education & Research
    EDUCATION_TEACHING("Education & Teaching", "Education & Research"),
    TRAINING_COACHING("Training & Coaching", "Education & Research"),
    RESEARCH_ACADEMIA("Research & Academia", "Education & Research"),
    CHILDCARE("Childcare & Early Education", "Education & Research"),

    // Engineering & Construction
    CIVIL_ENGINEERING("Civil Engineering", "Engineering & Construction"),
    MECHANICAL_ENGINEERING("Mechanical Engineering", "Engineering & Construction"),
    ELECTRICAL_ENGINEERING("Electrical & Electronic Engineering", "Engineering & Construction"),
    ARCHITECTURE("Architecture", "Engineering & Construction"),
    CONSTRUCTION("Construction & Building", "Engineering & Construction"),

    // Legal & HR
    LEGAL_COMPLIANCE("Legal & Compliance", "Legal & Human Resources"),
    HUMAN_RESOURCES("Human Resources", "Legal & Human Resources"),
    RECRUITMENT("Recruitment & Talent Acquisition", "Legal & Human Resources"),
    LABOR_RELATIONS("Labor Relations & Employment Law", "Legal & Human Resources"),

    // Creative & Media
    DESIGN_UX_UI("UX/UI Design", "Creative & Media"),
    GRAPHIC_DESIGN("Graphic Design", "Creative & Media"),
    CONTENT_WRITING("Content Writing & Copywriting", "Creative & Media"),
    JOURNALISM_MEDIA("Journalism & Media", "Creative & Media"),
    PHOTOGRAPHY_VIDEO("Photography & Video", "Creative & Media"),
    ENTERTAINMENT("Entertainment & Performing Arts", "Creative & Media"),

    // Industry & Logistics
    MANUFACTURING("Manufacturing & Industrial Production", "Industry & Logistics"),
    SUPPLY_CHAIN("Supply Chain & Procurement", "Industry & Logistics"),
    LOGISTICS_TRANSPORT("Logistics & Transportation", "Industry & Logistics"),
    QUALITY_CONTROL("Quality Control", "Industry & Logistics"),
    MAINTENANCE("Maintenance & Technical Support", "Industry & Logistics"),

    // Other Fields
    HOSPITALITY_TOURISM("Hospitality & Tourism", "Other Fields"),
    REAL_ESTATE("Real Estate", "Other Fields"),
    AGRICULTURE("Agriculture & Food Industry", "Other Fields"),
    ENVIRONMENT_ENERGY("Environment & Energy", "Other Fields"),
    GOVERNMENT_PUBLIC("Government & Public Administration", "Other Fields"),
    NON_PROFIT("Non-Profit & NGO", "Other Fields"),
    SPORTS_FITNESS("Sports & Fitness", "Other Fields"),
    OTHER("Other", "Other Fields");

    private final String displayName;
    private final String group;

    JobCategory(String displayName, String group) {
        this.displayName = displayName;
        this.group = group;
    }

    public String getDisplayName() { return displayName; }
    public String getGroup() { return group; }
}