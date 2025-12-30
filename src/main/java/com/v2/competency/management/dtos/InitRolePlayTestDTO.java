package com.v2.competency.management.dtos;

import java.util.List;

public class InitRolePlayTestDTO {
	
	private QuestionTextDTO questionText;
    private ProductInfoDTO productInfo;
    private List<CompetitionInfoDTO> competitionInfo;

    public QuestionTextDTO getQuestionText() {
        return questionText;
    }

    public void setQuestionText(QuestionTextDTO questionText) {
        this.questionText = questionText;
    }

    public ProductInfoDTO getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(ProductInfoDTO productInfo) {
        this.productInfo = productInfo;
    }

    public List<CompetitionInfoDTO> getCompetitionInfo() {
        return competitionInfo;
    }

    public void setCompetitionInfo(List<CompetitionInfoDTO> competitionInfo) {
        this.competitionInfo = competitionInfo;
    }

    // ---------- Inner DTO classes ----------

    public static class QuestionTextDTO {
        private String yourRole;
        private String productToSell;
        private String yourGoal;

        public String getYourRole() {
            return yourRole;
        }

        public void setYourRole(String yourRole) {
            this.yourRole = yourRole;
        }

        public String getProductToSell() {
            return productToSell;
        }

        public void setProductToSell(String productToSell) {
            this.productToSell = productToSell;
        }

        public String getYourGoal() {
            return yourGoal;
        }

        public void setYourGoal(String yourGoal) {
            this.yourGoal = yourGoal;
        }
    }

    public static class ProductInfoDTO {
        private String title;
        private String link;
        private String desc;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public static class CompetitionInfoDTO {
        private String title;
        private String link;
        private String desc;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
