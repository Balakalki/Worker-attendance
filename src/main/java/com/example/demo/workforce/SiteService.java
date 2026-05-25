package com.example.demo.workforce;

import com.example.demo.workforce.dto.CreateSiteRequest;
import com.example.demo.workforce.exception.WorkforceApiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SiteService {
    private final SiteRepository siteRepository;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Transactional(readOnly = true)
    public List<Site> findAllSites() {
        return siteRepository.findAll();
    }

    @Transactional
    public Site createSite(CreateSiteRequest request) {
        Site site = new Site();
        site.setName(request.getName().trim());
        site.setLocation(request.getLocation().trim());
        site.setActive(request.getActive() == null || request.getActive());

        try {
            return siteRepository.save(site);
        } catch (DataIntegrityViolationException exception) {
            throw new WorkforceApiException("SITE_ALREADY_EXISTS", "A site with this name and location already exists", HttpStatus.CONFLICT);
        }
    }
}
