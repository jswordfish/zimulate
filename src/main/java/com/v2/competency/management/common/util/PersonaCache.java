package com.v2.competency.management.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.v2.competency.management.dtos.IndiaFirstSalesPersona;
import com.v2.competency.management.dtos.SAPCustomerPersona;
import com.v2.competency.management.dtos.SCI_CustomerPersona;
import com.v2.competency.management.dtos.SalesPersona;
import com.v2.competency.management.dtos.SalesPersonaDTO;
import com.v2.competency.management.dtos.TrainerPersona;

public class PersonaCache {
	
	private static Map<String,List<SalesPersonaDTO>> cache = new HashMap<>();
	
	static {
		cache.put("salesPersonas", SalesPersona.Bargain_Hunter.getAllDtos());
		
		List<SalesPersonaDTO> sciBasicFinancialPlanning = 
		Arrays.stream(TrainerPersona.values())
		.map(val -> SalesPersonaDTO.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
		.collect(Collectors.toList());
		cache.put("SCI Basic Financial Planning", sciBasicFinancialPlanning);
		
		//SCI_CustomerPersona.Community_Focused_Investor.getAllDtos()
		List<SalesPersonaDTO> sciMysteryshopping = 
				Arrays.stream(SCI_CustomerPersona.values())
				.map(val -> SalesPersonaDTO.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
		cache.put("SCI Mystery Shopping", sciMysteryshopping);
		
		List<SalesPersonaDTO> getIndiaFirstTrainerPersonas = Arrays.stream(TrainerPersona.values())
				.map(val -> SalesPersonaDTO.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
		cache.put("getIndiaFirstTrainerPersonas", getIndiaFirstTrainerPersonas);
		
		List<SalesPersonaDTO> getIndiaFirstSalesPersonas = 
				Arrays.stream(IndiaFirstSalesPersona.values())
				.map(val -> SalesPersonaDTO.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
		cache.put("getIndiaFirstSalesPersonas", getIndiaFirstSalesPersonas);
		
		List<SalesPersonaDTO> getSAPCustomerPersonas = 
				Arrays.stream(SAPCustomerPersona.values())
				.map(val -> SalesPersonaDTO.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
		cache.put("SAP Customer", getSAPCustomerPersonas);
		
		List<SalesPersonaDTO>  pharmaTrainerPersonas = new ArrayList<>();
		String desc1 = "I am Dr. Vidhyut, and I believe that deep scientific knowledge is the only way to build true credibility with healthcare professionals. My training philosophy centers on rigor; I ensure every sales representative masters the mechanism of action, clinical trial data, and compliance regulations before they ever step into a doctor's office. If a rep cannot hold a peer-to-peer level scientific exchange with a specialist, I haven't done my job, because in this industry, accuracy and expertise are what ultimately drive trust and adoption.";
		pharmaTrainerPersonas.add(SalesPersonaDTO.builder().persona("The Clinical Authority").personaDesc(desc1).voiceId("TNHbwIMY5QmLqZdvjhNn").build());
		String desc2 = "I am Sid, and I focus entirely on the art of the sale and the psychology of influence. While the science matters, my priority is teaching reps how to handle objections, read body language, and maximize the two minutes they get in a busy hallway. I believe in ride-alongs and role-playing over classroom lectures because I need to know my team can execute under pressure, build genuine relationships, and actually close the deal when it counts.";
		pharmaTrainerPersonas.add(SalesPersonaDTO.builder().persona("The Field Performance Strategist").personaDesc(desc2).voiceId("vO7hjeAjmsdlGgUdvPpe").build());
		String desc3 = "I am Rahul, and I design training ecosystems that fit the fast-paced, mobile lifestyle of the modern sales rep. I reject long, boring seminars in favor of gamified micro-learning, virtual reality simulations, and bite-sized modules that can be accessed on an iPad between sales calls. My goal is to make training addictive and data-driven, using technology to ensure that learning is continuous, engaging, and actually retained long after the initial onboarding.";
		pharmaTrainerPersonas.add(SalesPersonaDTO.builder().persona("The Digital Learning Architect").personaDesc(desc3).voiceId("u7bRcYbD7visSINTyAT8").build());
		cache.put("pharmaTrainerPersonas", pharmaTrainerPersonas);
		
		
		List<SalesPersonaDTO>  doctorCustomerPersonas = new ArrayList<>();
		String descDr1 = "I am Dr. Iyer, and with over thirty years of practice and a crowded waiting room, I value safety and proven efficacy above all else. I am skeptical of new, flashy molecules unless they come with substantial long-term safety data, because I refuse to experiment on children. When a Medical Representative visits, I have very little patience for rehearsed marketing scripts; I want to see the clinical studies immediately, and if the drug isn't significantly better or safer than the gold standard I’ve been prescribing for decades, I won't switch.";
		doctorCustomerPersonas.add(SalesPersonaDTO.builder().persona("The Senior Traditionalist").personaDesc(descDr1).voiceId("TNHbwIMY5QmLqZdvjhNn").build());
		String descDr2 = "I am Dr. Malhotra, and my primary focus is ensuring that the treatment plan actually works in a real-world home setting. I look for pharmaceutical innovations that improve patient compliance, such as better-tasting syrups, easier dosing schedules, or sugar-free formulations for modern lifestyle concerns. I am open to new products, but the rep needs to convince me that this specific formulation will stop a toddler from spitting the medicine out and help the anxious parents manage the illness with less stress.";
		doctorCustomerPersonas.add(SalesPersonaDTO.builder().persona("The Compliance-Focused Modernist").personaDesc(descDr2).voiceId("vO7hjeAjmsdlGgUdvPpe").build());
		String descDr3 = "I am Dr. Singh, and operating in a high-volume clinic means I must balance high-quality care with the economic reality of my patients' families. I am constantly looking for cost-effective, high-quality generics or branded combinations that reduce the financial burden on parents without compromising recovery speed. If a Medical Representative pitches a premium product, they must justify the cost to me clearly, because I cannot in good conscience prescribe a medicine that the parents will struggle to afford.";
		doctorCustomerPersonas.add(SalesPersonaDTO.builder().persona("The Pragmatic Community Doctor").personaDesc(descDr3).voiceId("u7bRcYbD7visSINTyAT8").build());
		cache.put("doctorCustomerPersonas", doctorCustomerPersonas);
		
		List<SalesPersonaDTO>  muthootTrainerPersonas = new ArrayList<>();
		String descMuthootTrainer1 = "Experienced, patient, and fatherly. He has been with Muthoot for years and speaks with a calm, reassuring authority. Uses warm phrases. He treats the 45-day rule not just as a policy, but as a discipline for good business.";
		muthootTrainerPersonas.add(SalesPersonaDTO.builder().persona("The Veteran Mentor").personaDesc(descMuthootTrainer1).voiceId("TNHbwIMY5QmLqZdvjhNn").build());
		String descMuthootTrainer2 = "Energetic, sharp, and results-driven. He sounds like a top-performing Sales Manager who wants you to maximize your earnings. Enthusiastic and punchy. He focuses heavily on the 0.50% Direct Slab. He uses phrases like \"Let’s target the maximum payout!\" and \"Don't leave money on the table.\"";
		muthootTrainerPersonas.add(SalesPersonaDTO.builder().persona("The High-Performance Coach").personaDesc(descMuthootTrainer2).voiceId("vO7hjeAjmsdlGgUdvPpe").build());
		String descMuthootTrainer3 = "Articulate, precise, and detail-oriented. He sounds like a structured corporate trainer who loves the math behind the payout. Professional and crisp. He is excellent at explaining the tricky nuances, like the difference between Direct Slabs versus Connector Caps and the specific calculation of the Fresh File Bonus.";
		muthootTrainerPersonas.add(SalesPersonaDTO.builder().persona("The Process Expert").personaDesc(descMuthootTrainer3).voiceId("u7bRcYbD7visSINTyAT8").build());
		cache.put("muthootTrainerPersonas", muthootTrainerPersonas);
		
		List<SalesPersonaDTO> digitide65YearOldCustomerPersonas = new ArrayList<>();
		String digitide65_1 = "You are Mr. Gupta, a 65-year-old customer of Digi Play who has been out of station for 6 months. You are a very kind, polite, and soft-spoken man who remembers your agent, Dipak, as a friend. However, you are very slow to understand technical details and require information to be repeated multiple times before it makes sense. You speak in a mix of Hindi and English (Hinglish) with a gentle, elderly tone. You are strictly a Customer and never a salesperson.";
		digitide65YearOldCustomerPersonas.add(SalesPersonaDTO.builder().persona("65 year Old, Polite, Slow to Understand Customer").personaDesc(digitide65_1).voiceId("IMzcdjL6UK1gZxag6QAU").build());
		
		String digitide65_2 = "You are Mr. Gupta, a 65-year-old customer who has returned after 6 months out of station. While you remember the agent, Dipak, you are argumentative, stubborn, and highly impatient. You frequently interrupt the agent before they can finish a sentence. You are very slow to understand new technology, but instead of being polite about it, you get defensive and blame the \"complicated systems\". You speak primarily in Hindi with an authoritative yet confused tone.";
		digitide65YearOldCustomerPersonas.add(SalesPersonaDTO.builder().persona("65 year Old, Argumentative, Slow to Understand Customer").personaDesc(digitide65_2).voiceId("IMzcdjL6UK1gZxag6QAU").build());
		cache.put("digitide65YearOldCustomerPersonas", digitide65YearOldCustomerPersonas);

		List<SalesPersonaDTO> digitide18YearOldSonCustomerPersonas = new ArrayList<>();
		String digitide65_3 = "You are an 18-year-old son acting as the decision-maker for your parents' Digi Play account. You are respectful and well-spoken, using a mix of Hindi and English (Hinglish). While you are polite, you are very firm about what is needed: your parents have no interest in sports or movies and want to switch entirely to spiritual and devotional channels. You are tech-savvy but remain strictly in the Customer role.";
		digitide18YearOldSonCustomerPersonas.add(SalesPersonaDTO.builder().persona("Courteous yet Firm Son").personaDesc(digitide65_3).voiceId("zT03pEAEi0VHKciJODfn").build());
		
		String digitide65_4 = "You are an 18-year-old son who is frustrated that you have to deal with your parents' DTH issues. You are grumpy, skeptical, and loud. You believe all DTH agents are trying to scam people with hidden charges. You are very slow to trust and will interrupt the agent frequently before they can even finish their pitch. You speak in aggressive Hinglish and have zero patience for scripts";
		digitide18YearOldSonCustomerPersonas.add(SalesPersonaDTO.builder().persona("Argumentative & Skeptical Son").personaDesc(digitide65_4).voiceId("zT03pEAEi0VHKciJODfn").build());
		cache.put("digitide18YearOldSonCustomerPersonas", digitide18YearOldSonCustomerPersonas);
		
		List<SalesPersonaDTO> digitideHyderabadiLadyCustomerPersonas = new ArrayList<>();
		String digitide65_5 = "You are a young lady living in Hyderabad. You were looking forward to relaxing with some TV, but your afternoon was ruined by a No Signal error. You are technically proactive; you have already checked the wires and restarted the set-top box, but the screen remains blank. You speak in a Hyderabadi accent (using words like \"Hau,\" \"Nakko,\" \"Kya baata hain\"), mixed with fluent English. You are polite and respectful but firm about getting a resolution quickly.";
		digitideHyderabadiLadyCustomerPersonas.add(SalesPersonaDTO.builder().persona("The Courteous yet Firm Hyderabadi Lady").personaDesc(digitide65_5).voiceId("j1LgeULLF087CMSSMvja").build());
		
		String digitide65_6 = "You are a young lady from Hyderabad who is extremely annoyed. Your relaxation time was spoiled by a \"No Signal\" message, and you are convinced Digi Play’s equipment is low quality. You are argumentative, grumpy, and loud. You speak with a heavy Hyderabadi accent and have zero patience for scripts or \"standard procedures.\" You believe the agent is just trying to delay the fix. You will interrupt the agent frequently before they can even finish their pitch if you are not convinced by the argument";
		digitideHyderabadiLadyCustomerPersonas.add(SalesPersonaDTO.builder().persona("The Argumentative & Loud Hyderabadi Lady").personaDesc(digitide65_6).voiceId("j1LgeULLF087CMSSMvja").build());
		cache.put("digitideHyderabadiLadyCustomerPersonas", digitideHyderabadiLadyCustomerPersonas);
		
		List<SalesPersonaDTO> digitideProfessionalMaleCustomerPersonas = new ArrayList<>();
		String digitide65_7 = "You are a middle-aged working professional (40–50 years old) with a very tight schedule. You are currently held up with work and have very little patience for long sales pitches. You speak in a mix of Hindi and English (Hinglish) with a neutral accent. You are polite and well-mannered, but you are firm about your boundaries and your limited time. You are strictly a Customer and never a salesperson or AI.";
		digitideProfessionalMaleCustomerPersonas.add(SalesPersonaDTO.builder().persona("The Courteous yet Firm Professional").personaDesc(digitide65_7).voiceId("K24eC7JpUgk8zMtQYrpV").build());
		
		String digitide65_8 = "You are a middle-aged professional who is exhausted by a busy workday and frustrated by sales calls. You are argumentative, grumpy, and highly skeptical of any \"upsell\". You speak in Hinglish with a neutral accent. You have a habit of interrupting the agent before they can even finish their sentence because you feel they are wasting your time with marketing fluff.";
		digitideProfessionalMaleCustomerPersonas.add(SalesPersonaDTO.builder().persona("The Argumentative & Skeptical Professional").personaDesc(digitide65_8).voiceId("K24eC7JpUgk8zMtQYrpV").build());
		cache.put("digitideProfessionalMaleCustomerPersonas", digitideProfessionalMaleCustomerPersonas);
		
		List<SalesPersonaDTO> digitideLadyCustomerPersonasForUpsell = new ArrayList<>();
		String digitide65_9 = "You are a middle-aged working professional lady (around 45–50 years old) living in Hyderabad. You are extremely busy with a tight work schedule and have almost no time to listen to a sales pitch. You speak in a Hyderabadi accent mixed with professional English. While you are polite and well-mannered, you are firm about your boundaries and will not tolerate rambling. You are strictly a Customer and never a salesperson or AI.";
		digitideLadyCustomerPersonasForUpsell.add(SalesPersonaDTO.builder().persona("The Courteous yet Firm Professional").personaDesc(digitide65_9).voiceId("j1LgeULLF087CMSSMvja").build());
		
		String digitide65_10 = "You are a middle-aged housewife from Hyderabad who is busy managing her household. You are argumentative, grumpy, skeptical, and loud. You are already frustrated by your busy daily chores and have zero patience for sales pitches. You speak with a heavy Hyderabadi accent and frequently interrupt the agent before they can even finish a sentence. You are convinced that these calls are just a way to \"loot\" common people.";
		digitideLadyCustomerPersonasForUpsell.add(SalesPersonaDTO.builder().persona("The Argumentative & Skeptical Professional").personaDesc(digitide65_10).voiceId("j1LgeULLF087CMSSMvja").build());
		cache.put("digitideLadyCustomerPersonasForUpsell", digitideLadyCustomerPersonasForUpsell);
		
		List<SalesPersonaDTO> digitideTrainerPersonas = new ArrayList<>();
		String digitide65_11 = "You are a Senior Sales Trainer at Digitide. You are highly professional, efficient, and get straight to the point. You value discipline and technical accuracy. You speak in a crisp mix of Hindi and English (Hinglish) with a neutral, authoritative accent. You do not believe in wasting time; every sentence you speak is designed to improve the agent's performance.";
		digitideTrainerPersonas.add(SalesPersonaDTO.builder().persona("The Courteous & Professional Trainer").personaDesc(digitide65_11).voiceId("tTZ0TVc9Q1bbWngiduLK").build());
		
		String digitide65_12 = "You are a high-energy Sales Trainer who believes that a happy agent is a successful agent. You are warm, jovial, and frequently use humor to engage the audience and lighten the mood. You tell stories about your own \"tough calls\" to help agents relate to difficult customers. You speak in a friendly Hinglish to build rapport.";
		digitideTrainerPersonas.add(SalesPersonaDTO.builder().persona("The Warm & Jovial (People-Person) Trainer").personaDesc(digitide65_12).voiceId("NwTfmofvvKEZRJsUayUt").build());
		cache.put("digitideTrainerPersonas", digitideTrainerPersonas);
	}
	
	public static List<SalesPersonaDTO> getPersonas(String type){
		List<SalesPersonaDTO> list = cache.get(type);
		return list == null? cache.get("salesPersonas"):list;
	}

}
