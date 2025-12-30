package com.v2.competency.management.webservices;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.v2.competency.management.dtos.CompetencyWithProficiencyForRole;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.Proficiency;
import com.v2.competency.management.dtos.RoleUploadDto;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.Role;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.RoleService;
import com.v2.competency.management.service.TenantService;

@RestController
@CrossOrigin
public class RoleController {
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	CompetencyService competencyService;
	
	XmlMapper xmlMapper = new XmlMapper();
	
	static Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	
	@RequestMapping(value="fetchRoleByName",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchRoleByName(  @RequestParam String roleName,@RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 Role role = 	roleService.findByRoleNameAndCompanyId(roleName, companyId);
	 
	 System.out.println(role.getRoleName());
	 role = populateCompWithProf(role);
	 return ResponseEntity.ok(role);
	}
	
	
	@RequestMapping(value="fetchRolesByCompanyId",method=RequestMethod.GET)  
    public ResponseEntity<?> fetcCompetenciesByPageForLevel( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<Role> roles = roleService.findRolesByCompanyId(companyId, PageRequest.of(pageNumber, 15));
	 
	 List<Role> rls =  roles.getContent();
	 	for(Role r : rls) {
	 		r = populateCompWithProf(r);
	 		r.setCompetenciesWithProficiency(null);
	 	}
	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(roles.getNumber());
	 res.setRecordsTo(roles.getNumberOfElements());
	 res.setTotalNumberOfPages(roles.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 res.setList(roles.getContent());
	 return ResponseEntity.ok(res);
	}
	
	
	
	private Role populateCompWithProf(Role role) throws Exception {
		String xml = role.getCompetenciesWithProficiency();
		 Set<CompetencyWithProficiencyForRole> set =   xmlMapper.readValue(xml, new TypeReference<Set<CompetencyWithProficiencyForRole>>() {
			});
		// System.out.println(xml);
		for(Competency competency : role.getCompetencies()) {
			 CompetencyWithProficiencyForRole p = getFromCompetency(competency, set);
			 competency.setProficiency(p.getProficiency());
			 System.out.println("p is "+p.getCompetency()+" lev "+p.getProficiency()+""+competency.getProficiency() +" "+competency.getCompetency()+" - "+role.getRoleName());
		 }
		return role;
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
	
	
	@RequestMapping(value="assignCompetenciesToRole",method=RequestMethod.POST)  
    public ResponseEntity<?> assignCompetenciesToRole(@RequestBody List<Competency> competencies,  @RequestParam String roleName, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		Role role = roleService.findByRoleNameAndCompanyId(roleName, companyId);
			if(role == null) {
				return ResponseEntity.ok("Role "+roleName+" does not exist for "+companyId+" company");
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
			
			if(role.getCompetencies().contains(competency2)) {
				return ResponseEntity.ok("Competency to be added "+competency.getCompetency()+" - "+(competency.getParentCompetency() == null?"na":competency.getParentCompetency()) +" already exists for the given role "+role.getRoleName());
			}
			competency2.setProficiency(competency.getProficiency());
			role.getCompetencies().add(competency2);
			Set<CompetencyWithProficiencyForRole> set = calculateAddition(role, competency2);
			role.setCompetenciesWithProficiency(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
		}
		
		role = roleService.saveOrUpdate(role);
		
		return ResponseEntity.ok(role);
				
		
	}
	
	private Set<CompetencyWithProficiencyForRole> calculateAddition(Role role, Competency competency) throws JsonParseException, JsonMappingException, IOException{
		String xml = role.getCompetenciesWithProficiency();
		Set<CompetencyWithProficiencyForRole> set=  xmlMapper.readValue(xml, new TypeReference<Set<CompetencyWithProficiencyForRole>>() {});
		CompetencyWithProficiencyForRole p = new CompetencyWithProficiencyForRole();
		p.setCompetency(competency.getCompetency());
		p.setParentCompetency(competency.getParentCompetency());
		p.setCompetencyId(competency.getId());
		p.setProficiency(competency.getProficiency());
		set.add(p);
		return set;
	}
	
	private Set<CompetencyWithProficiencyForRole> calculateRemoval(Role role, Competency competency) throws JsonParseException, JsonMappingException, IOException{
		String xml = role.getCompetenciesWithProficiency();
		Set<CompetencyWithProficiencyForRole> set=  xmlMapper.readValue(xml, new TypeReference<Set<CompetencyWithProficiencyForRole>>() {});
		CompetencyWithProficiencyForRole p = new CompetencyWithProficiencyForRole();
		p.setCompetency(competency.getCompetency());
		p.setParentCompetency(competency.getParentCompetency());
		p.setCompetencyId(competency.getId());
		p.setProficiency(competency.getProficiency());
		set.remove(p);
		return set;
	}
	
	
	
	@RequestMapping(value="removeCompetenciesToRole",method=RequestMethod.POST)  
    public ResponseEntity<?> removeCompetenciesToRole(@RequestBody List<Competency> competencies,  @RequestParam String roleName, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		Role role = roleService.findByRoleNameAndCompanyId(roleName, companyId);
			if(role == null) {
				return ResponseEntity.ok("Role "+roleName+" does not exist for "+companyId+" company");
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
			
			if(!role.getCompetencies().contains(competency2)) {
				return ResponseEntity.ok("Competency to be removed "+competency.getCompetency()+" - "+(competency.getParentCompetency() == null?"na":competency.getParentCompetency()) +" does not exist for the given role "+role.getRoleName());
			}
			
			role.getCompetencies().remove(competency2);
			
		}
		role.setCompetenciesWithProficiency(getXmlForCompetenciesWithProficiency(role));
		role = roleService.saveOrUpdate(role);
		
		return ResponseEntity.ok(role);
				
		
	}
	
	
	@RequestMapping(value="uploadRoles",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadRoles( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
	 try {
		 
		 if(!tenantService.isCompanyIdExisting(companyId)) {
			 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
		 }
		 
		List<RoleUploadDto> roles = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, RoleUploadDto.class);
			System.out.println("Printing List Data: " +roles);
			for(RoleUploadDto r : roles) {
				r.setRoleName(r.getRoleName().trim());
				r.setCompetency(r.getCompetency().trim());
					if(r.getParentCompetency() != null) {
						r.setParentCompetency(r.getParentCompetency().trim());
					}
				
				
				if(!companyId.equals(r.getCompanyId())) {
					throw new RuntimeException("Invalid Company Id "+r.getCompanyId());
				}
				r.setCompanyId(companyId);
			}
			
			Set<Role> roles2 = merge(roles);
			
				for(Role role : roles2) {
					role.setCompetenciesWithProficiency(getXmlForCompetenciesWithProficiency(role));
					roleService.saveOrUpdate(role);
				}
			
			 return ResponseEntity.ok("ok");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		logger.error("Problem in saving role", e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
	}
 }
	
	private String getXmlForCompetenciesWithProficiency(Role role) throws JsonProcessingException {
		Set<CompetencyWithProficiencyForRole> set = new HashSet<>();
			for(Competency competency : role.getCompetencies()) {
				CompetencyWithProficiencyForRole p = new CompetencyWithProficiencyForRole();
				p.setCompetency(competency.getCompetency());
				p.setParentCompetency(competency.getParentCompetency());
				p.setCompetencyId(competency.getId());
				p.setProficiency(competency.getProficiency());
				set.add(p);
			}
		String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set);
		return xml;
	}
	
	private Set<Role> merge(List<RoleUploadDto> list){
		Map<Role, Set<Competency>> map = new HashMap<>();
			for(RoleUploadDto dto : list) {
				Role role = new Role();
				role.setCompanyId(dto.getCompanyId());
				role.setRoleName(dto.getRoleName());
				role.setRoleDesc(dto.getRoleDesc());
				Competency comp =  competencyService.findByCompetencyAndParentCompetency(dto.getCompetency(), dto.getParentCompetency(), dto.getCompanyId());
				//Competency comp = Competency.builder().competency(dto.getCompetency()).parentCompetency(dto.getParentCompetency()).build();
					if(comp == null) {
						throw new RuntimeException("Competency "+dto.getCompetency()+" parent "+dto.getParentCompetency()+" does not exist for company "+dto.getCompanyId());
					}
				comp.setCompanyId(dto.getCompanyId());
					if(Proficiency.valueOf(dto.getProficiency()) != null) {
						comp.setProficiency(dto.getProficiency());
					}
				
					if(map.get(role) == null) {
						Set<Competency> set = new HashSet<>();
						set.add(comp);
						map.put(role, set);
						
					}
					else {
						map.get(role).add(comp);
					}
				
					//role.getCompetencies().add(comp);
			}
			
			for(Role role : map.keySet()) {
				role.setCompetencies(map.get(role));
				Role roleFromDb = roleService.findByRoleNameAndCompanyId(role.getRoleName(), role.getCompanyId());
					if(roleFromDb != null) {
						role.getCompetencies().addAll(roleFromDb.getCompetencies());
					}
				
			}
			return map.keySet();
	}

}
