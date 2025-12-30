package com.v2.competency.management.webservices;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.v2.competency.management.dtos.CompetencyWithProficiencyForJobDesc;
import com.v2.competency.management.dtos.CompetencyWithProficiencyForRole;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.JobDescription;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.JobDescriptionService;
import com.v2.competency.management.service.TenantService;
@RestController
@CrossOrigin
public class JobDescController {
	
	@Autowired
	JobDescriptionService jobDescriptionService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	CompetencyService competencyService;
	
	XmlMapper xmlMapper = new XmlMapper();
	
	static Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	
	@RequestMapping(value="fetchJobDescByName",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchJobDescByName(  @RequestParam String jobdesc,@RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
JobDescription jobDescription = 	jobDescriptionService.findByJobDescriptionAndCompanyId(jobdesc, companyId);

	 
	// System.out.println(jobDescription);
jobDescription = populateCompWithProf(jobDescription);
	 return ResponseEntity.ok(jobDescription);
	}
	
	
	@RequestMapping(value="fetchJobDescriptionsByCompanyId",method=RequestMethod.GET)  
    public ResponseEntity<?> fetcCompetenciesByPageForLevel( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<JobDescription> jobDescs = jobDescriptionService.findJobDescriptionsByCompanyId(companyId, PageRequest.of(pageNumber, 15));
	 
	 List<JobDescription> rls =  jobDescs.getContent();
	 	for(JobDescription r : rls) {
	 		r = populateCompWithProf(r);
	 		r.setCompetenciesWithProficiency(null);
	 	}
	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(jobDescs.getNumber());
	 res.setRecordsTo(jobDescs.getNumberOfElements());
	 res.setTotalNumberOfPages(jobDescs.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 res.setList(jobDescs.getContent());
	 return ResponseEntity.ok(res);
	}
	
	
	
	private JobDescription populateCompWithProf(JobDescription jobDescription) throws Exception {
		String xml = jobDescription.getCompetenciesWithProficiency();
		 Set<CompetencyWithProficiencyForRole> set =   xmlMapper.readValue(xml, new TypeReference<Set<CompetencyWithProficiencyForRole>>() {
			});
		// System.out.println(xml);
		for(Competency competency : jobDescription.getCompetencies()) {
			 CompetencyWithProficiencyForRole p = getFromCompetency(competency, set);
			 competency.setProficiency(p.getProficiency());
			 System.out.println("p is "+p.getCompetency()+" lev "+p.getProficiency()+""+competency.getProficiency() +" "+competency.getCompetency()+" - "+jobDescription.getJobDescName());
		 }
		return jobDescription;
	}
	
	private static <E> E getFromCompetency(final Competency competency, Set<E> set) throws Exception {
	    // reflection stuff
	    Field field = set.getClass().getDeclaredField("map");
	    field.setAccessible(true);

	    // get the internal map
	    @SuppressWarnings("unchecked")
	    Map<E, Object> interalMap = (Map<E, Object>) (field.get(set));

	    // attempt to find a key with an identical hashcode
	    for (E elem : interalMap.keySet()) {
	        //if (elem.hashCode() == hashcode) return elem;
	    	CompetencyWithProficiencyForRole c =(CompetencyWithProficiencyForRole) elem;
	    	if(c.getCompetency().equalsIgnoreCase(competency.getCompetency()) && c.getParentCompetency().equalsIgnoreCase(competency.getParentCompetency())) {
	    		return elem;
	    	}
	    }
	    return null;
	}
	
	private static <E> E getFromHashCode(final int hashcode, Set<E> set) throws Exception {
	    // reflection stuff
	    Field field = set.getClass().getDeclaredField("map");
	    field.setAccessible(true);

	    // get the internal map
	    @SuppressWarnings("unchecked")
	    Map<E, Object> interalMap = (Map<E, Object>) (field.get(set));

	    // attempt to find a key with an identical hashcode
	    for (E elem : interalMap.keySet()) {
	        if (elem.hashCode() == hashcode) return elem;
	    }
	    return null;
	}
	
	
	@RequestMapping(value="assignCompetenciesToJobDesc",method=RequestMethod.POST)  
    public ResponseEntity<?> assignCompetenciesToJobDesc(@RequestBody List<Competency> competencies,  @RequestParam String jobDesc, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		JobDescription jobDescription = jobDescriptionService.findByJobDescriptionAndCompanyId(jobDesc, companyId);
			if(jobDescription == null) {
				//return ResponseEntity.ok("Job desc "+jobDesc+" does not exist for "+companyId+" company");
				jobDescription = JobDescription.builder().jobDescName(jobDesc)
								.build();
				jobDescription.setCompanyId(companyId);
					if(jobDescription.getCompetencies() == null) {
						jobDescription.setCompetencies(new HashSet<>());
					}
			}
		
		
			
		for(Competency competency : competencies)	{
			Competency competency2 = null;
			if(competency.getParentCompetency() == null || competency.getParentCompetency().trim().length() == 0) {
				competency2 = competencyService.findByCompetency(competency.getCompanyName(), companyId);
			}
			else {
				competency2 = competencyService.findByCompetencyAndParentCompetency(competency.getCompetency(), competency.getParentCompetency(), competency.getCompanyId() );
			}
			
			if(competency2 == null) {
				return ResponseEntity.ok("Competency to be added "+competency.getCompetency()+" does not exist for "+companyId+" company");
			}
			
			if(jobDescription.getCompetencies().contains(competency2)) {
				return ResponseEntity.ok("Competency to be added "+competency.getCompetency()+" - "+(competency.getParentCompetency() == null?"na":competency.getParentCompetency()) +" already exists for the given jobdesc "+jobDesc);
			}
			competency2.setProficiency(competency.getProficiency());
			jobDescription.getCompetencies().add(competency2);
			Set<CompetencyWithProficiencyForJobDesc> set = calculateAddition(jobDescription, competency2);
			jobDescription.setCompetenciesWithProficiency(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
		}
		
		jobDescription = jobDescriptionService.saveOrUpdate(jobDescription);
		
		return ResponseEntity.ok(jobDescription);
				
		
	}
	
	private Set<CompetencyWithProficiencyForJobDesc> calculateAddition(JobDescription jobdesc, Competency competency) throws JsonParseException, JsonMappingException, IOException{
		String xml = jobdesc.getCompetenciesWithProficiency();
		Set<CompetencyWithProficiencyForJobDesc> set=null;
			if(xml == null || xml.trim().length() == 0) {
				set=new HashSet<CompetencyWithProficiencyForJobDesc>();
			}
			else {
				set=  xmlMapper.readValue(xml, new TypeReference<Set<CompetencyWithProficiencyForJobDesc>>() {});
			}
		CompetencyWithProficiencyForJobDesc p = new CompetencyWithProficiencyForJobDesc();
		p.setCompetency(competency.getCompetency());
		p.setParentCompetency(competency.getParentCompetency());
		p.setCompetencyId(competency.getId());
		p.setProficiency(competency.getProficiency());
		set.add(p);
		return set;
	}
	
	private Set<CompetencyWithProficiencyForJobDesc> calculateRemoval(JobDescription jobdesc, Competency competency) throws JsonParseException, JsonMappingException, IOException{
		String xml = jobdesc.getCompetenciesWithProficiency();
		Set<CompetencyWithProficiencyForJobDesc> set=  xmlMapper.readValue(xml, new TypeReference<Set<CompetencyWithProficiencyForJobDesc>>() {});
		CompetencyWithProficiencyForJobDesc p = new CompetencyWithProficiencyForJobDesc();
		p.setCompetency(competency.getCompetency());
		p.setParentCompetency(competency.getParentCompetency());
		p.setCompetencyId(competency.getId());
		p.setProficiency(competency.getProficiency());
		set.remove(p);
		return set;
	}
	
	
	
	@RequestMapping(value="removeCompetenciesToJobDesc",method=RequestMethod.POST)  
    public ResponseEntity<?> removeCompetenciesToJobDesc(@RequestBody List<Competency> competencies,  @RequestParam String jobdescName, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		JobDescription jobDescription = jobDescriptionService.findByJobDescriptionAndCompanyId(jobdescName, companyId);
			if(jobDescription == null) {
				return ResponseEntity.ok("jobDescription "+jobdescName+" does not exist for "+companyId+" company");
			}
		
		
			
		for(Competency competency : competencies)	{
			Competency competency2 = null;
			if(competency.getParentCompetency() == null || competency.getParentCompetency().trim().length() == 0) {
				competency2 = competencyService.findByCompetency(competency.getCompanyName(), companyId);
			}
			else {
				competency2 = competencyService.findByCompetencyAndParentCompetency(competency.getCompetency(), competency.getParentCompetency(), competency.getCompanyId() );
			}
			
			if(competency2 == null) {
				return ResponseEntity.ok("Competency to be removed "+competency.getCompetency()+" does not exist for "+companyId+" company");
			}
			
			if(!jobDescription.getCompetencies().contains(competency2)) {
				return ResponseEntity.ok("Competency to be removed "+competency.getCompetency()+" - "+(competency.getParentCompetency() == null?"na":competency.getParentCompetency()) +" does not exist for the given jobdesc "+jobdescName);
			}
			
			jobDescription.getCompetencies().remove(competency2);
			
		}
		jobDescription.setCompetenciesWithProficiency(getXmlForCompetenciesWithProficiency(jobDescription));
		jobDescription = jobDescriptionService.saveOrUpdate(jobDescription);
		
		return ResponseEntity.ok(jobDescription);
				
		
	}
	
	
	
	
	private String getXmlForCompetenciesWithProficiency(JobDescription jobDescription) throws JsonProcessingException {
		Set<CompetencyWithProficiencyForJobDesc> set = new HashSet<>();
			for(Competency competency : jobDescription.getCompetencies()) {
				CompetencyWithProficiencyForJobDesc p = new CompetencyWithProficiencyForJobDesc();
				p.setCompetency(competency.getCompetency());
				p.setParentCompetency(competency.getParentCompetency());
				p.setCompetencyId(competency.getId());
				p.setProficiency(competency.getProficiency());
				set.add(p);
			}
		String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set);
		return xml;
	}
	
	
}
