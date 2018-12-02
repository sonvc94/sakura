package vn.geardao.sakura.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import vn.geardao.sakura.domain.Phone;
import vn.geardao.sakura.domain.*; // for static metamodels
import vn.geardao.sakura.repository.PhoneRepository;
import vn.geardao.sakura.service.dto.PhoneCriteria;
import vn.geardao.sakura.service.dto.PhoneDTO;
import vn.geardao.sakura.service.mapper.PhoneMapper;

/**
 * Service for executing complex queries for Phone entities in the database.
 * The main input is a {@link PhoneCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PhoneDTO} or a {@link Page} of {@link PhoneDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PhoneQueryService extends QueryService<Phone> {

    private final Logger log = LoggerFactory.getLogger(PhoneQueryService.class);

    private final PhoneRepository phoneRepository;

    private final PhoneMapper phoneMapper;

    public PhoneQueryService(PhoneRepository phoneRepository, PhoneMapper phoneMapper) {
        this.phoneRepository = phoneRepository;
        this.phoneMapper = phoneMapper;
    }

    /**
     * Return a {@link List} of {@link PhoneDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PhoneDTO> findByCriteria(PhoneCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Phone> specification = createSpecification(criteria);
        return phoneMapper.toDto(phoneRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PhoneDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PhoneDTO> findByCriteria(PhoneCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Phone> specification = createSpecification(criteria);
        return phoneRepository.findAll(specification, page)
            .map(phoneMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PhoneCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Phone> specification = createSpecification(criteria);
        return phoneRepository.count(specification);
    }

    /**
     * Function to convert PhoneCriteria to a {@link Specification}
     */
    private Specification<Phone> createSpecification(PhoneCriteria criteria) {
        Specification<Phone> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Phone_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Phone_.name));
            }
            if (criteria.getBrand() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBrand(), Phone_.brand));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), Phone_.price));
            }
            if (criteria.getReleaseDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReleaseDate(), Phone_.releaseDate));
            }
        }
        return specification;
    }
}
