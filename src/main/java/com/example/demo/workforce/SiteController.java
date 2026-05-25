package com.example.demo.workforce;

import com.example.demo.workforce.dto.CreateSiteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {
    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    public List<Site> findAllSites() {
        return siteService.findAllSites();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Site createSite(@Valid @RequestBody CreateSiteRequest request) {
        return siteService.createSite(request);
    }
}
