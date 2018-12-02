package vn.geardao.sakura.service.impl;

import vn.geardao.sakura.service.PhoneService;
import vn.geardao.sakura.domain.Phone;
import vn.geardao.sakura.repository.PhoneRepository;
import vn.geardao.sakura.service.dto.PhoneDTO;
import vn.geardao.sakura.service.mapper.PhoneMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Phone.
 */
@Service
@Transactional
public class PhoneServiceImpl implements PhoneService {

    private final Logger log = LoggerFactory.getLogger(PhoneServiceImpl.class);

    private final PhoneRepository phoneRepository;

    private final PhoneMapper phoneMapper;

    public PhoneServiceImpl(PhoneRepository phoneRepository, PhoneMapper phoneMapper) {
        this.phoneRepository = phoneRepository;
        this.phoneMapper = phoneMapper;
    }

    /**
     * Save a phone.
     *
     * @param phoneDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PhoneDTO save(PhoneDTO phoneDTO) {
        log.debug("Request to save Phone : {}", phoneDTO);

        Phone phone = phoneMapper.toEntity(phoneDTO);
        phone = phoneRepository.save(phone);
        return phoneMapper.toDto(phone);
    }

    /**
     * Get all the phones.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PhoneDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Phones");
        return phoneRepository.findAll(pageable)
            .map(phoneMapper::toDto);
    }


    /**
     * Get one phone by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PhoneDTO> findOne(Long id) {
        log.debug("Request to get Phone : {}", id);
        return phoneRepository.findById(id)
            .map(phoneMapper::toDto);
    }

    /**
     * Delete the phone by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Phone : {}", id);
        phoneRepository.deleteById(id);
    }
}
