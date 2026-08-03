package com.v2.liveavatar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@lombok.Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Link {
	private String url;

    private String faq;

    private String id;
}
