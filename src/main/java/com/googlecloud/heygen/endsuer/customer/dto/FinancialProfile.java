package com.googlecloud.heygen.endsuer.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinancialProfile {
	@JsonProperty("investment_purpose")
    private String investmentPurpose;

    @JsonProperty("investment_horizon")
    private String investmentHorizon;

    @JsonProperty("monthly_savings_capacity")
    private String monthlySavingsCapacity;

    @JsonProperty("risk_appetite")
    private String riskAppetite;

    @JsonProperty("existing_investments")
    private String existingInvestments;

    public String getInvestmentPurpose() {
        return investmentPurpose;
    }

    public void setInvestmentPurpose(String investmentPurpose) {
        this.investmentPurpose = investmentPurpose;
    }

    public String getInvestmentHorizon() {
        return investmentHorizon;
    }

    public void setInvestmentHorizon(String investmentHorizon) {
        this.investmentHorizon = investmentHorizon;
    }

    public String getMonthlySavingsCapacity() {
        return monthlySavingsCapacity;
    }

    public void setMonthlySavingsCapacity(String monthlySavingsCapacity) {
        this.monthlySavingsCapacity = monthlySavingsCapacity;
    }

    public String getRiskAppetite() {
        return riskAppetite;
    }

    public void setRiskAppetite(String riskAppetite) {
        this.riskAppetite = riskAppetite;
    }

    public String getExistingInvestments() {
        return existingInvestments;
    }

    public void setExistingInvestments(String existingInvestments) {
        this.existingInvestments = existingInvestments;
    }
}
