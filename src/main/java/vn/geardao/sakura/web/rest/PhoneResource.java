package vn.geardao.sakura.web.rest;

import com.codahale.metrics.annotation.Timed;
import vn.geardao.sakura.service.PhoneService;
import vn.geardao.sakura.web.rest.errors.BadRequestAlertException;
import vn.geardao.sakura.web.rest.util.HeaderUtil;
import vn.geardao.sakura.web.rest.util.PaginationUtil;
import vn.geardao.sakura.service.dto.PhoneDTO;
import vn.geardao.sakura.service.dto.PhoneCriteria;
import vn.geardao.sakura.service.PhoneQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Phone.
 */
@RestController
@RequestMapping("/api")
public class PhoneResource {

    private final Logger log = LoggerFactory.getLogger(PhoneResource.class);

    private static final String ENTITY_NAME = "phone";

    private final PhoneService phoneService;

    private final PhoneQueryService phoneQueryService;

    public PhoneResource(PhoneService phoneService, PhoneQueryService phoneQueryService) {
        this.phoneService = phoneService;
        this.phoneQueryService = phoneQueryService;
    }

    /**
     * POST  /phones : Create a new phone.
     *
     * @param phoneDTO the phoneDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new phoneDTO, or with status 400 (Bad Request) if the phone has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/phones")
    @Timed
    public ResponseEntity<PhoneDTO> createPhone(@Valid @RequestBody PhoneDTO phoneDTO) throws URISyntaxException {
        log.debug("REST request to save Phone : {}", phoneDTO);
        if (phoneDTO.getId() != null) {
            throw new BadRequestAlertException("A new phone cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PhoneDTO result = phoneService.save(phoneDTO);
        return ResponseEntity.created(new URI("/api/phones/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /phones : Updates an existing phone.
     *
     * @param phoneDTO the phoneDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated phoneDTO,
     * or with status 400 (Bad Request) if the phoneDTO is not valid,
     * or with status 500 (Internal Server Error) if the phoneDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/phones")
    @Timed
    public ResponseEntity<PhoneDTO> updatePhone(@Valid @RequestBody PhoneDTO phoneDTO) throws URISyntaxException {
        log.debug("REST request to update Phone : {}", phoneDTO);
        if (phoneDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PhoneDTO result = phoneService.save(phoneDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, phoneDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /phones : get all the phones.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of phones in body
     */
    @GetMapping("/phones")
    @Timed
    public ResponseEntity<List<PhoneDTO>> getAllPhones(PhoneCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Phones by criteria: {}", criteria);
        Page<PhoneDTO> page = phoneQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/phones");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /phones/count : count all the phones.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/phones/count")
    @Timed
    public ResponseEntity<Long> countPhones(PhoneCriteria criteria) {
        log.debug("REST request to count Phones by criteria: {}", criteria);
        return ResponseEntity.ok().body(phoneQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /phones/:id : get the "id" phone.
     *
     * @param id the id of the phoneDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the phoneDTO, or with status 404 (Not Found)
     */
    @GetMapping("/phones/{id}")
    @Timed
    public ResponseEntity<PhoneDTO> getPhone(@PathVariable Long id) {
        log.debug("REST request to get Phone : {}", id);
        Optional<PhoneDTO> phoneDTO = phoneService.findOne(id);
        return ResponseUtil.wrapOrNotFound(phoneDTO);
    }

    /**
     * DELETE  /phones/:id : delete the "id" phone.
     *
     * @param id the id of the phoneDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/phones/{id}")
    @Timed
    public ResponseEntity<Void> deletePhone(@PathVariable Long id) {
        log.debug("REST request to delete Phone : {}", id);
        phoneService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
